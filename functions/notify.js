const { onRequest } = require("firebase-functions/v2/https");
const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { onSchedule } = require("firebase-functions/v2/scheduler");
const admin = require("firebase-admin");

const CHANNEL_ID = "todori_default";
const DEFAULT_REGION = "us-central1";
const REFLECTION_DEEPLINK = "todori://app.todori.com/stats";
const TODO_TODAY_DEEPLINK = "todori://app.todori.com/todo";
const goalDetailLink  = (goalId) =>
  `todori://app.todori.com/goal/detail/${encodeURIComponent(goalId)}`;
const studyDetailLink = (studyId, date) =>
  `todori://app.todori.com/study/detail/${encodeURIComponent(studyId)}?date=${date}`;
const communityDetailLink = (postId) =>
  `todori://app.todori.com/community/detail/${encodeURIComponent(postId)}`;

/* ---------- 공통 유틸 ---------- */
const db = admin.firestore();

function todayKST() {
  return new Intl.DateTimeFormat("en-CA", {
    timeZone: "Asia/Seoul", year: "numeric", month: "2-digit", day: "2-digit"
  }).format(new Date());
}
const MS_PER_DAY = 24 * 60 * 60 * 1000;

/** "YYYY-MM-DD" → KST 자정 Date */
function kstDate(ymd) {
  return new Date(`${ymd}T00:00:00+09:00`);
}

/** today(YYYY-MM-DD) 기준 n일 뒤를 "YYYY-MM-DD"(KST)로 반환 */
function addDaysStrKST(ymd, n) {
  const d = kstDate(ymd);
  d.setDate(d.getDate() + n);
  return new Intl.DateTimeFormat("en-CA", {
    timeZone: "Asia/Seoul", year: "numeric", month: "2-digit", day: "2-digit"
  }).format(d);
}

/** endYmd까지 몇 일이 남았는지(KST) 0=당일, 7=일주일 전 */
function daysLeft(endYmd, baseYmd = todayKST()) {
  const diffMs = kstDate(endYmd) - kstDate(baseYmd);
  return Math.floor(diffMs / MS_PER_DAY);
}

/** 중복발송 방지: 최초만 true 반환 */
async function sentLogOnce(uid, key) {
  const ref = db.doc(`users/${uid}/reminderLog/${key}`);
  const s = await ref.get();
  if (s.exists) return false;
  await ref.set({ sentAt: admin.firestore.FieldValue.serverTimestamp() });
  return true;
}

/** 멀티캐스트 전송(시스템 notification 추가: 백그라운드에서도 제목/본문 표시) */
async function sendToUser(uid, { title, body = "", deeplink, collapseKey, ttlSeconds = 3600 }) {
  const snap = await db.collection("users").doc(uid).collection("fcmTokens").get();
  const tokens = snap.docs.map(d => d.id).filter(Boolean);
  if (!tokens.length) {
    console.log(`[sendToUser] uid=${uid} no tokens`);
    return { successCount: 0, failureCount: 0, pruned: 0 };
  }

  const safeTitle = String(title ?? "Todori");
  const safeBody  = String(body ?? "");
  const safeLink  = (typeof deeplink === "string" && deeplink.length > 0)
    ? deeplink
    : "todori://todo";

  const msg = {
    tokens,
    data: { title: safeTitle, body: safeBody, deeplink: safeLink },
    android: {
      priority: "high",
      collapseKey,
      ttl: ttlSeconds * 1000,
    },
  };

  const resp = await admin.messaging().sendEachForMulticast(msg);

  //불량 토큰 정리 로직
  const bad = new Set(["messaging/registration-token-not-registered", "messaging/invalid-registration-token"]);
  const toDelete = [];
  resp.responses.forEach((r, i) => { if (!r.success && r.error && bad.has(r.error.code)) toDelete.push(tokens[i]); });
  await Promise.all(toDelete.map(t => db.collection("users").doc(uid).collection("fcmTokens").doc(t).delete().catch(() => {})));

  console.log(`[sendToUser] uid=${uid} success=${resp.successCount} failure=${resp.failureCount} pruned=${toDelete.length}`);
  return { successCount: resp.successCount, failureCount: resp.failureCount, pruned: toDelete.length };
}

//수동 발송(테스트용)
const sendUserNotification = onRequest({ region: DEFAULT_REGION }, async (req, res) => {
  try {
    if (req.method !== "POST") return res.status(405).json({ error: "POST only" });
    const { uid, title, body, deeplink, collapseKey, ttlSeconds } = req.body || {};
    if (!uid || !title) return res.status(400).json({ error: "uid and title required" });
    const result = await sendToUser(uid, { title, body, deeplink, collapseKey, ttlSeconds });
    res.json({ ok: true, ...result });
  } catch (e) {
    console.error("sendUserNotification error", e);
    res.status(500).json({ error: e?.message || String(e) });
  }
});

//댓글 알림: posts/{postId}/studyPostReply/{commentId}
const onCommentCreated = onDocumentCreated(
  { document: "posts/{postId}/studyPostReply/{commentId}", region: DEFAULT_REGION },
  async (event) => {
  const { postId, commentId } = event.params;
  const cSnap = event.data; if (!cSnap) return;

  const c = cSnap.data() || {};
  const commentAuthor = c.uid || c.authorUid;
  const contentPreview = (c.content || "").toString().slice(0, 60);

  // 글 작성자 찾기: posts/{postId}.createdBy (없으면 uid/authorUid 시도)
  const post = await db.doc(`posts/${postId}`).get();
  if (!post.exists) return;
  const postOwner = post.get("createdBy") || post.get("uid") || post.get("authorUid");

  // 글 주인에게
  if (postOwner && postOwner !== commentAuthor) {
    const k = `comment-${postId}-${commentId}-${postOwner}`;
    if (await sentLogOnce(postOwner, k)) {
      await sendToUser(postOwner, {
        title: "새 댓글",
        body: contentPreview || "댓글을 확인해 보세요",
        deeplink: communityDetailLink(postId),
        collapseKey: `comment-${postId}`,
        ttlSeconds: 3600
      });
    }
  }

  // 대댓글이면 댓글 주인에게도
  const parentId = c.parentCommentId || null;
  if (parentId) {
    const p = await db.doc(`posts/${postId}/studyPostReply/${parentId}`).get();
    if (p.exists) {
      const parentOwner = p.get("uid") || p.get("authorUid");
      if (parentOwner && parentOwner !== commentAuthor && parentOwner !== postOwner) {
        const k = `comment-reply-${postId}-${commentId}-${parentOwner}`;
        if (await sentLogOnce(parentOwner, k)) {
          await sendToUser(parentOwner, {
            title: "내 댓글에 답글",
            body: contentPreview || "답글이 달렸어요",
            deeplink: communityDetailLink(postId),
            collapseKey: `comment-${postId}`,
            ttlSeconds: 3600
          });
        }
      }
    }
  }
});

//2) 스터디 Todo 추가: studyTodos/{docId}
const onStudyTodoCreated = onDocumentCreated(
  { document: "studyTodos/{docId}", region: DEFAULT_REGION },
  async (event) => {
  const { docId } = event.params;
  const sSnap = event.data; if (!sSnap) return;

  const t = sSnap.data() || {};
  const studyId = t.studyId;
  const title = t.title || "새 할 일";
  const creator = t.createdBy || t.uid;
  if (!studyId) return;

  // 멤버 가져오기: studyMembers에서 studyId로 조회 (fallback: studies/{studyId}/members)
  let memberUids = [];
  try {
    const m = await db.collection("studyMembers").where("studyId", "==", studyId).get();
    memberUids = m.docs.map(d => d.get("uid")).filter(Boolean);
  } catch {}
  if (memberUids.length === 0) {
    try {
      const m2 = await db.collection(`studies/${studyId}/members`).get();
      memberUids = m2.docs.map(d => d.id);
    } catch {}
  }
  memberUids = memberUids.filter(u => u && u !== creator);

  const today = todayKST();

  await Promise.all(memberUids.map(async (uid) => {
    const k = `studyTodo-${studyId}-${docId}-${uid}`;
    if (!(await sentLogOnce(uid, k))) return;
    await sendToUser(uid, {
      title: "스터디에 새 Todo 등록",
      body: title,
      deeplink: studyDetailLink(studyId, today),
      collapseKey: `studyTodo-${studyId}`,
      ttlSeconds: 3600
    });
  }));
});

//3) 회고 리마인드(매일 21:00)
const remindReflection = onSchedule(
  { schedule: "00 21 * * *", timeZone: "Asia/Seoul", region: "us-central1" },
  async () => {
    try {
      const today = todayKST();
      const usersSnap = await db.collection("users").get();
      const jobs = [];

      for (const u of usersSnap.docs) {
        const uid = u.id;

        // 당일 dailyRecord 1개만 체크
        const drRef = db.doc(`users/${uid}/dailyRecord/${today}`);
        const drSnap = await drRef.get();

        const hasReflection = drSnap.exists && !!(drSnap.data()?.reflection);

        // 중복 방지
        const logRef = db.doc(`users/${uid}/reminderLog/reflection-${today}`);
        if ((await logRef.get()).exists) continue;

        jobs.push((async () => {
          await sendToUser(uid, {
            title: "Todori",
            body: "한 줄 회고 작성하러 가기.",
            deeplink: REFLECTION_DEEPLINK,
            collapseKey: "reflection-reminder",
            ttlSeconds: 3600
          });
          await logRef.set({ sentAt: admin.firestore.FieldValue.serverTimestamp() });
        })());
      }

      await Promise.all(jobs);
      console.log(`[remindReflection] done for ${today}, jobs=${jobs.length}`);
    } catch (e) {
      console.error("[remindReflection] crash", e);
    }
  }
);


// 4) D-day 리마인드(매일 09:00, D-7 ~ D-0)
const dailyDdayReminder = onSchedule(
  { schedule: "0 9 * * *", timeZone: "Asia/Seoul", region: "us-central1" },
  async () => {
    const today = todayKST();
    const until = addDaysStrKST(today, 7); // 오늘~+7일

    const users = await db.collection("users").get();
    const jobs = [];

    for (const u of users.docs) {
      const uid = u.id;

      const qs = await db.collection(`users/${uid}/goals`)
        .where("endDate", ">=", today)
        .where("endDate", "<=", until)
        .get();

      qs.forEach(doc => {
        const g = doc.data() || {};
        if (g.completed === true) return;

        const left = daysLeft(g.endDate, today);
        if (left < 0 || left > 7) return;

        const title = left === 0 ? "오늘이 D-Day!" : `D-${left}`;
        const body  = g.title ? `‘${g.title}’ 목표가 임박했어요` : "마감이 임박한 목표가 있어요";

        const logKey = `dday-${doc.id}-${today}`;
        jobs.push((async () => {
          if (!(await sentLogOnce(uid, logKey))) return;
          await sendToUser(uid, {
            title,
            body,
            deeplink: goalDetailLink(doc.id),
            collapseKey: `dday-${doc.id}`,
            ttlSeconds: 3600
          });
        })());
      });
    }

    await Promise.all(jobs);
    console.log(`[dailyDdayReminder] ${today} sent=${jobs.length}`);
  }
);


// 5) 오늘 Todo 미완료(매일 21:30)
const todoIncompleteReminder = onSchedule(
  { schedule: "30 21 * * *", timeZone: "Asia/Seoul", region: "us-central1" },
  async () => {
    const today = todayKST();
    const users = await db.collection("users").get();
    const jobs = [];
    for (const u of users.docs) {
      const uid = u.id;
      const qs = await db.collection(`users/${uid}/todos`)
        .where("date", "==", today).where("completed", "==", false).limit(1).get();
      if (qs.empty) continue;
      const k = `todo-incomplete-${today}`;
      jobs.push((async () => {
        if (!(await sentLogOnce(uid, k))) return;
        await sendToUser(uid, {
          title: "Todo가 아직 남아있어요",
          body: "남은 Todo를 마무리해 볼까요?",
          deeplink: TODO_TODAY_DEEPLINK,
          collapseKey: `todo-incomplete-${today}`,
          ttlSeconds: 3600
        });
      })());
    }
    await Promise.all(jobs);
    console.log(`[todoIncompleteReminder] ${today} sent=${jobs.length}`);
  }
);

module.exports = {
  sendUserNotification,
  // outbox 트리거
  onOutboxCreated: onDocumentCreated("users/{uid}/outbox/{msgId}", async (event) => {
    const { uid } = event.params;
    const snap = event.data; if (!snap) return;
    const data = snap.data() || {};
    if (!data.title) {
      await snap.ref.update({ status: "error_no_title", updatedAt: admin.firestore.FieldValue.serverTimestamp() });
      return;
    }
    const result = await sendToUser(uid, {
      title: data.title,
      body: data.body || "",
      deeplink: data.deeplink || TODO_TODAY_DEEPLINK,
      collapseKey: data.collapseKey,
      ttlSeconds: data.ttlSeconds || 3600,
    });
    await snap.ref.update({
      status: result.failureCount === 0 ? "sent" : (result.successCount > 0 ? "partial" : "failed"),
      successCount: result.successCount,
      failureCount: result.failureCount,
      prunedTokens: result.pruned,
      updatedAt: admin.firestore.FieldValue.serverTimestamp(),
    });
  }),
  onCommentCreated,
  onStudyTodoCreated,
  remindReflection,
  dailyDdayReminder,
  todoIncompleteReminder,
};
