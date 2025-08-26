const admin = require("firebase-admin");

/**
 * Firestore users/{uid} 문서를 생성 또는 업데이트합니다.
 * @param {Object} params
 * @param {string} params.uid - 사용자 UID
 * @param {string} [params.nickname] - 사용자 닉네임
 * @param {string} [params.authProvider] - 인증 제공자
 * @return {Promise<void>}
 */
function baseDefaults() {
  return {
    intro: null,
    level: 1,
    rewardPoint: 0,
    deleted: false,
    fcmToken: null,
    lastLoginAt: admin.firestore.FieldValue.serverTimestamp(),
  };
}

/**
 * Firestore users/{uid} 문서를 생성 또는 업데이트합니다.
 * @param {Object} params
 * @param {string} params.uid - 사용자 UID
 * @param {string} [params.nickname] - 사용자 닉네임
 * @param {string} [params.authProvider] - 인증 제공자
 * @return {Promise<void>}
 */
async function upsertUserDoc({ uid, nickname = "", authProvider = "unknown" }) {
  const db = admin.firestore();
  const ref = db.collection("users").doc(uid);

  await db.runTransaction(async (tx) => {
    const snap = await tx.get(ref);

    if (!snap.exists) {
      const trimmed = typeof nickname === "string" ? nickname.trim() : "";
      const initialNickname = trimmed ? trimmed.slice(0, 8) : "";

      tx.set(
        ref,
        {
          uid,
          authProvider,
          nickname: initialNickname,
          ...baseDefaults(),
          createdAt: admin.firestore.FieldValue.serverTimestamp(),
          lastLoginAt: admin.firestore.FieldValue.serverTimestamp(),
        },
        { merge: true }
      );
      return;
    }

    const prev = snap.data() || {};
    const update = {
      authProvider,
      lastLoginAt: admin.firestore.FieldValue.serverTimestamp(),
    };

    const hasPrevNickname =
      prev.nickname !== undefined && String(prev.nickname).trim() !== "";
    const trimmed = typeof nickname === "string" ? nickname.trim() : "";
    if (!hasPrevNickname && trimmed) {
      update.nickname = trimmed.slice(0, 8);
    }

    tx.set(ref, update, { merge: true });
  });
}

module.exports = { upsertUserDoc };