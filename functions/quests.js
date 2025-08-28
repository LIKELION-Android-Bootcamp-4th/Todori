const { onRequest } = require("firebase-functions/v2/https");
const admin = require("firebase-admin");

const db = admin.firestore();

/** ============== KST 유틸 ============== */
const KST_TZ = "Asia/Seoul";
const fmtDate = new Intl.DateTimeFormat("en-CA", {
  timeZone: KST_TZ, year: "numeric", month: "2-digit", day: "2-digit",
});
const fmtHour = (d) =>
  Number(new Intl.DateTimeFormat("en-US", { timeZone: KST_TZ, hour: "2-digit", hour12: false }).format(d));
const dateStrKST = (d = new Date()) => fmtDate.format(d);
const addDaysKST = (base, diff) => { const x = new Date(base.getTime()); x.setDate(x.getDate() + diff); return x; };
const lastNDates = (n, from = new Date()) => Array.from({ length: n }, (_, i) => dateStrKST(addDaysKST(from, -i)));

const THREE_HOURS = 3 * 60 * 60 * 1000;

/** ============== 레벨 규칙 ============== */
// Lv1→2:100, Lv2→3:200, Lv3→4:300, Lv4→5:600, Lv5→6:1300
const NEEDS = [null, 100, 200, 300, 600, 1300];
const MAX_LEVEL = 6;
function needPoint(level) {
  const idx = Math.min(level, NEEDS.length - 1);
  return NEEDS[idx] ?? Number.POSITIVE_INFINITY;
}

/** ============== 퀘스트 마스터 ============== */
const RULES = [
  { id:"Q_FIRST_LOGIN",        requiresStudy:false, timeGate:null },

  { id:"Q_CONSISTENT_LOGIN_3", requiresStudy:false, timeGate:null, streak:{type:"login", days:3} },
  { id:"Q_CONSISTENT_LOGIN_7", requiresStudy:false, timeGate:null, streak:{type:"login", days:7} },

  { id:"Q_TIMER_60",           requiresStudy:false, timeGate:null },
  { id:"Q_TIMER_120",          requiresStudy:false, timeGate:null },
  { id:"Q_TIMER_180",          requiresStudy:false, timeGate:null },

  { id:"Q_CONSISTENT_TIMER_3", requiresStudy:false, timeGate:null, streak:{type:"timer", days:3} },
  { id:"Q_CONSISTENT_TIMER_7", requiresStudy:false, timeGate:null, streak:{type:"timer", days:7} },

  { id:"Q_TODO_COMPLETE",      requiresStudy:false, timeGate:null },
  { id:"Q_TODO_3_COMPLETE",    requiresStudy:false, timeGate:null },

  { id:"Q_GOAL_TODO_DONE",     requiresStudy:false, timeGate:null },
  { id:"Q_GOAL_3_TODO_DONE",   requiresStudy:false, timeGate:null },

  { id:"Q_STUDY_TODO_DONE",    requiresStudy:true,  timeGate:null },
  { id:"Q_STUDY_3_TODO_DONE",  requiresStudy:true,  timeGate:null },

  { id:"Q_TODO_ADD",           requiresStudy:false, timeGate:null },

  { id:"Q_TIMER_RECORD",       requiresStudy:false, timeGate:null },
  { id:"Q_TIMER_RECORD_3",     requiresStudy:false, timeGate:null },

  { id:"Q_REFLECTION_WRITE",   requiresStudy:false, timeGate:null },
  { id:"Q_REFLECTION_WRITE_3", requiresStudy:false, timeGate:null, streak:{type:"reflection", days:3} },
  { id:"Q_REFLECTION_WRITE_7", requiresStudy:false, timeGate:null, streak:{type:"reflection", days:7} },

  { id:"Q_MORNING_START",      requiresStudy:false, timeGate:"morning"  }, // 09시 이전
  { id:"Q_LATE_NIGHT_STUDY",   requiresStudy:false, timeGate:"latenight"}  // 23시 이후
];

const DEFAULT_POINTS = {
  Q_FIRST_LOGIN:5, Q_CONSISTENT_LOGIN_3:10, Q_CONSISTENT_LOGIN_7:15,
  Q_TIMER_60:10, Q_TIMER_120:15, Q_TIMER_180:20,
  Q_CONSISTENT_TIMER_3:20, Q_CONSISTENT_TIMER_7:30,
  Q_TODO_COMPLETE:10, Q_TODO_3_COMPLETE:20,
  Q_GOAL_TODO_DONE:10, Q_GOAL_3_TODO_DONE:20,
  Q_STUDY_TODO_DONE:10, Q_STUDY_3_TODO_DONE:20,
  Q_TODO_ADD:5, Q_TIMER_RECORD:10, Q_TIMER_RECORD_3:15,
  Q_REFLECTION_WRITE:5, Q_REFLECTION_WRITE_3:10, Q_REFLECTION_WRITE_7:20,
  Q_MORNING_START:15, Q_LATE_NIGHT_STUDY:15,
};

let QUEST_CACHE = { at: 0, data: null };
const QUEST_CACHE_MS = 10 * 60 * 1000;

async function loadQuestMasterFromDB() {
  if (QUEST_CACHE.data && Date.now() - QUEST_CACHE.at < QUEST_CACHE_MS) return QUEST_CACHE.data;
  const dbMap = new Map();
  try {
    const snap = await db.collection("quest").get();
    snap.forEach(doc => {
      const v = doc.data() || {};
      dbMap.set(doc.id, {
        title: v.title || v.questTitle || doc.id,
        points: typeof v.points === "number" ? v.points : undefined,
      });
    });
  } catch {}
  const merged = RULES.map(r => {
    const o = dbMap.get(r.id) || {};
    return {
      id: r.id,
      requiresStudy: r.requiresStudy,
      timeGate: r.timeGate || null,
      streak: r.streak || null,
      title: o.title || r.id,
      points: (o.points ?? DEFAULT_POINTS[r.id] ?? 0),
    };
  });
  QUEST_CACHE = { at: Date.now(), data: merged };
  return merged;
}

/** ============== 데이터 로딩 ============== */
async function loadDailyRecordMap(uid, dateStrs) {
  const refs = dateStrs.map(d => db.doc(`users/${uid}/dailyRecord/${d}`));
  const snaps = await db.getAll(...refs);
  const map = new Map();
  snaps.forEach((s, i) => map.set(dateStrs[i], s.exists ? s.data() : null));
  return map;
}

async function loadTodosAgg(uid, startDateStr, endDateStr) {
  const q = db.collection(`users/${uid}/todos`)
    .where("date", ">=", startDateStr)
    .where("date", "<=", endDateStr)
    .orderBy("date")
    .select("date", "totalFocusTimeMillis", "completed");
  const snap = await q.get();

  const byDate = new Map();
  snap.docs.forEach(doc => {
    const d = doc.get("date");
    const total = doc.get("totalFocusTimeMillis") || 0;
    const completed = !!doc.get("completed");
    const hasTime = total > 0;

    if (!byDate.has(d)) byDate.set(d, { totalMs: 0, completedCnt: 0, withTimeCnt: 0, todoRefs: [] });
    const obj = byDate.get(d);
    obj.totalMs += total;
    if (completed) obj.completedCnt += 1;
    if (hasTime) obj.withTimeCnt += 1;
    obj.todoRefs.push(doc.ref);
  });
  return byDate;
}

async function loadGoalTodosAgg(uid, startDateStr, endDateStr) {
  const q = db.collection(`users/${uid}/goalTodos`)
    .where("dueDate", ">=", startDateStr)
    .where("dueDate", "<=", endDateStr)
    .orderBy("dueDate")
    .select("dueDate", "completed");
  const snap = await q.get();

  const byDate = new Map();
  snap.docs.forEach(doc => {
    const d = doc.get("dueDate");
    const completed = !!doc.get("completed");
    if (!byDate.has(d)) byDate.set(d, { completedCnt: 0 });
    if (completed) byDate.get(d).completedCnt += 1;
  });
  return byDate;
}

async function loadTodaySessions(uid, todayStr) {
  const q = db.collectionGroup("sessions")
    .where("uid", "==", uid)
    .where("date", "==", todayStr)
    .select("startedAt", "endedAt");
  const snap = await q.get();
  return snap.docs.map(d => d.data());
}

async function isJoinedStudy(uid) {
  try {
    const s1 = await db.collection(`users/${uid}/myStudies`).limit(1).get();
    if (!s1.empty) return true;
  } catch {}
  try {
    const s2 = await db.collectionGroup("members")
      .where(admin.firestore.FieldPath.documentId(), "==", uid)
      .limit(1).get();
    if (!s2.empty) return true;
  } catch {}
  try {
    const s3 = await db.collection("studyMembers").where("uid", "==", uid).limit(1).get();
    if (!s3.empty) return true;
  } catch {}
  return false;
}

async function loadStudyProgressCounts(uid, todayStr) {
  try {
    const q = db.collection("todoProgresses")
      .where("uid", "==", uid)
      .where("date", "==", todayStr);
    const snap = await q.get();
    if (!snap.empty) {
      let total = 0, done = 0;
      snap.forEach(d => { total += 1; if (d.get("isDone") === true) done += 1; });
      return { total, done };
    }
  } catch {}
  try {
    const q2 = db.collectionGroup("progress")
      .where(admin.firestore.FieldPath.documentId(), "==", uid)
      .where("date", "==", todayStr);
    const snap2 = await q2.get();
    let total = 0, done = 0;
    snap2.forEach(d => { total += 1; if (d.get("isDone") === true) done += 1; });
    return { total, done };
  } catch {}
  return { total: 0, done: 0 };
}

async function loadProfile(uid) {
  const ref = db.doc(`users/${uid}`);
  const snap = await ref.get();
  const data = snap.exists ? (snap.data() || {}) : {};
  return {
    ref,
    data: {
      level: data.level ?? 1,
      rewardPoint: (typeof data.rewardPoint === "number" ? data.rewardPoint : 0),
      xpTotal: (typeof data.xpTotal === "number" ? data.xpTotal : null),
    }
  };
}

/** ============== 배정 사전 조건 & 완료 판정 ============== */
function eligibleForAssign(q, ctx) {
  const { now, joinedStudy, last3, last7, dailyMap, todosByDate } = ctx;
  const hour = fmtHour(now);

  if (q.requiresStudy && !joinedStudy) return false;
  if (q.timeGate === "morning")   return hour < 9;
  if (q.timeGate === "latenight") return hour >= 23;

  if (q.streak?.type === "login" && q.streak.days === 3) {
    const two = last3.slice(1); return two.every(d => !!dailyMap.get(d));
  }
  if (q.streak?.type === "login" && q.streak.days === 7) {
    const six = last7.slice(1); return six.every(d => !!dailyMap.get(d));
  }
  if (q.streak?.type === "timer" && q.streak.days === 3) {
    const two = last3.slice(1); return two.every(d => (todosByDate.get(d)?.totalMs || 0) >= THREE_HOURS);
  }
  if (q.streak?.type === "timer" && q.streak.days === 7) {
    const six = last7.slice(1); return six.every(d => (todosByDate.get(d)?.totalMs || 0) >= THREE_HOURS);
  }
  if (q.streak?.type === "reflection" && q.streak.days === 3) {
    const two = last3.slice(1); return two.every(d => !!(dailyMap.get(d)?.reflection));
  }
  if (q.streak?.type === "reflection" && q.streak.days === 7) {
    const six = last7.slice(1); return six.every(d => !!(dailyMap.get(d)?.reflection));
  }
  return true;
}

function checkCompleted(qid, ctx) {
  const { todayStr, last3, last7, dailyMap, todosByDate, goalByDate, sessions, studyProgress } = ctx;
  const todayDaily = dailyMap.get(todayStr);
  const tTodo = todosByDate.get(todayStr) || { totalMs: 0, completedCnt: 0, withTimeCnt: 0, todoRefs: [] };
  const tGoal = goalByDate.get(todayStr) || { completedCnt: 0 };

  switch (qid) {
    case "Q_FIRST_LOGIN":        return !!todayDaily;

    case "Q_CONSISTENT_LOGIN_3": return last3.every(d => !!dailyMap.get(d));
    case "Q_CONSISTENT_LOGIN_7": return last7.every(d => !!dailyMap.get(d));

    case "Q_TIMER_60":           return tTodo.totalMs >=  60 * 60 * 1000;
    case "Q_TIMER_120":          return tTodo.totalMs >= 120 * 60 * 1000;
    case "Q_TIMER_180":          return tTodo.totalMs >= 180 * 60 * 1000;

    case "Q_CONSISTENT_TIMER_3": return last3.every(d => (todosByDate.get(d)?.totalMs || 0) >= THREE_HOURS);
    case "Q_CONSISTENT_TIMER_7": return last7.every(d => (todosByDate.get(d)?.totalMs || 0) >= THREE_HOURS);

    case "Q_TODO_ADD":           return (tTodo.todoRefs?.length || 0) > 0;
    case "Q_TODO_COMPLETE":      return tTodo.completedCnt >= 1;
    case "Q_TODO_3_COMPLETE":    return tTodo.completedCnt >= 3;

    case "Q_GOAL_TODO_DONE":     return tGoal.completedCnt >= 1;
    case "Q_GOAL_3_TODO_DONE":   return tGoal.completedCnt >= 3;

    case "Q_STUDY_TODO_DONE":    return (studyProgress?.done || 0) >= 1;
    case "Q_STUDY_3_TODO_DONE":  {
      const total = studyProgress?.total || 0;
      const done = studyProgress?.done || 0;
      return total >= 1 && total === done;
    }

    case "Q_TIMER_RECORD":       return tTodo.withTimeCnt >= 1;
    case "Q_TIMER_RECORD_3":     return tTodo.withTimeCnt >= 3;

    case "Q_REFLECTION_WRITE":   return !!(todayDaily && todayDaily.reflection);
    case "Q_REFLECTION_WRITE_3": return last3.every(d => !!(dailyMap.get(d)?.reflection));
    case "Q_REFLECTION_WRITE_7": return last7.every(d => !!(dailyMap.get(d)?.reflection));

    case "Q_MORNING_START": {
      for (const s of sessions) {
        const ts = s.startedAt; if (!ts) continue;
        const d = ts.toDate ? ts.toDate() : new Date(ts);
        if (fmtHour(d) < 9) return true;
      }
      return false;
    }
    case "Q_LATE_NIGHT_STUDY": {
      for (const s of sessions) {
        const ts = s.endedAt; if (!ts) continue;
        const d = ts.toDate ? ts.toDate() : new Date(ts);
        if (fmtHour(d) >= 23) return true;
      }
      return false;
    }

    default: return false;
  }
}

/** ============== 샘플링 ============== */
function sampleFiveWithFallback(list, ctx) {
  const eligible = list.filter(q => eligibleForAssign(q, ctx));
  const ineligible = list.filter(q => !eligible.includes(q));
  const shuffle = (arr) => {
    const a = [...arr];
    for (let i = a.length - 1; i > 0; i--) { const j = Math.floor(Math.random() * (i + 1)); [a[i], a[j]] = [a[j], a[i]]; }
    return a;
  };
  const pick = [];
  shuffle(eligible).forEach(q => { if (pick.length < 5) pick.push(q); });
  shuffle(ineligible).forEach(q => { if (pick.length < 5) pick.push(q); });
  return pick.slice(0, 5);
}

/** ============== (옵션2) 과거 문서 보존: archived:true 처리 ============== */
async function archiveOldQuests(uid, todayStr) {
  // date != today 인 문서들에 archived:true, archivedAt만 찍어 보존
  const col = db.collection(`users/${uid}/dailyUserQuests`);
  const snap = await col.where("date", "!=", todayStr).limit(500).get();
  if (snap.empty) return;

  const batch = db.batch();
  snap.docs.forEach(doc => {
    batch.set(doc.ref, {
      archived: true,
      archivedAt: admin.firestore.FieldValue.serverTimestamp(),
    }, { merge: true });
  });
  await batch.commit();
}

/** ============== 핸들러 (ID 토큰 검증 포함) ============== */
exports.updateUserQuest = onRequest(async (req, res) => {
  try {
    // 🔐 Authorization: Bearer <ID_TOKEN>
    const authHeader = req.headers.authorization || "";
    const idToken = authHeader.replace(/^Bearer\s+/, "");
    if (!idToken) return res.status(401).json({ error: "missing Authorization Bearer token" });

    let uid;
    try {
      const decoded = await admin.auth().verifyIdToken(idToken);
      uid = decoded.uid;
    } catch (e) {
      console.error("verifyIdToken failed:", e);
      return res.status(401).json({ error: "invalid id token" });
    }

    const now = new Date();
    const todayStr = dateStrKST(now);
    const last3 = lastNDates(3, now);
    const last7 = lastNDates(7, now);
    const start7 = last7[last7.length - 1];

    const [
      questsMaster,
      joinedStudy,
      { ref: userRef },
      dailyMap,
      todosByDate,
      goalByDate
    ] = await Promise.all([
      loadQuestMasterFromDB(),
      isJoinedStudy(uid),
      loadProfile(uid),
      loadDailyRecordMap(uid, last7),
      loadTodosAgg(uid, start7, todayStr),
      loadGoalTodosAgg(uid, start7, todayStr),
    ]);

    const [sessions, studyProgress] = await Promise.all([
      loadTodaySessions(uid, todayStr),
      loadStudyProgressCounts(uid, todayStr),
    ]);

    const dailyRef = db.collection(`users/${uid}/dailyUserQuests`);
    let todaysSnap = await dailyRef.where("date", "==", todayStr).orderBy("assignedAt").get();

    // 👉 오늘자 없으면: 과거 문서를 archived:true 로 보존 처리
    if (todaysSnap.empty) {
      await archiveOldQuests(uid, todayStr);
      // 주의: 하루 5개가 꼭 존재하도록 샘플링/생성은 아래 트랜잭션에서 set 하며 해결
    }

    const masterById = new Map(questsMaster.map(q => [q.id, q]));
    let assigned = [];
    if (!todaysSnap.empty) {
      const ids = todaysSnap.docs.map(d => d.id);
      assigned = ids.map(id => masterById.get(id)).filter(Boolean);
    }

    const baseList = questsMaster.filter(q => !(q.requiresStudy && !joinedStudy));
    if (assigned.length < 5) {
      const assignCtx = { now, joinedStudy, last3, last7, dailyMap, todosByDate };
      const remains = baseList.filter(q => !assigned.some(a => a.id === q.id));
      const filled = sampleFiveWithFallback(remains, assignCtx);
      assigned = [...assigned, ...filled].slice(0, 5);
    }

    const evalCtx = { todayStr, last3, last7, dailyMap, todosByDate, goalByDate, sessions, studyProgress };
    const evaluated = assigned.map(q => ({
      questId: q.id,
      title: q.title,
      completed: checkCompleted(q.id, evalCtx),
      points: q.points,
    }));

    const result = await db.runTransaction(async (tx) => {
      const userSnap = await tx.get(userRef);
      const userData = userSnap.exists ? (userSnap.data() || {}) : {};

      let level = userData.level ?? 1;
      let rewardPoint = (typeof userData.rewardPoint === "number") ? userData.rewardPoint : 0;

      // 과거 xpTotal 사용하던 프로젝트 호환
      if (typeof userData.xpTotal === "number") {
        const SUM = [0, 100, 300, 600, 1200, 2500];
        let lvl = 1;
        for (let i = SUM.length - 1; i >= 0; i--) {
          if (userData.xpTotal >= SUM[i]) { lvl = i + 1; break; }
        }
        level = lvl;
        rewardPoint = userData.xpTotal - SUM[level - 1];
      }

      // 오늘자 문서 세팅 + 보상 계산
      const qRefs = evaluated.map(ev => dailyRef.doc(ev.questId));
      const qSnaps = await Promise.all(qRefs.map(ref => tx.get(ref)));

      let gained = 0;
      for (let i = 0; i < evaluated.length; i++) {
        const ev = evaluated[i];
        const snap = qSnaps[i];
        const wasTodayCompleted =
          snap.exists && snap.get("date") === todayStr && snap.get("completed") === true;

        if (!wasTodayCompleted && ev.completed) {
          gained += (ev.points || 0);
        }
      }

      // 오늘자 상태로 덮어쓰기 (merge) + archived는 항상 false로 리셋
      for (let i = 0; i < evaluated.length; i++) {
        const ev = evaluated[i];
        const qRef = qRefs[i];
        tx.set(qRef, {
          questId: ev.questId,
          title: ev.title,
          completed: ev.completed,
          points: ev.points,
          date: todayStr,
          archived: false, // ✅ 보존 플래그 리셋
          assignedAt: admin.firestore.FieldValue.serverTimestamp(),
        }, { merge: true });
      }

      // 레벨/포인트 갱신
      rewardPoint += gained;
      let levelUp = false;
      while (level < MAX_LEVEL && rewardPoint >= needPoint(level)) {
        rewardPoint -= needPoint(level);
        level += 1;
        levelUp = true;
      }

      tx.set(userRef, { level, rewardPoint }, { merge: true });

      const nextRemain = (level < MAX_LEVEL) ? Math.max(0, needPoint(level) - rewardPoint) : 0;
      return { gained, level, rewardPoint, levelUp, nextRemain };
    });

    res.status(200).json({
      assigned: evaluated,
      reward: { gainedPoint: result.gained, levelUp: result.levelUp, newLevel: result.level },
      profile: {
        level: result.level,
        rewardPoint: result.rewardPoint,
        nextLevelPoint: result.nextRemain
      },
      meta: { today: todayStr, joinedStudy }
    });
  } catch (err) {
    console.error("[updateUserQuest] error:", err);
    res.status(500).json({ error: String(err?.message || err) });
  }
});
