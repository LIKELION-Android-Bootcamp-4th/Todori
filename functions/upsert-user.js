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
async function upsertUserDoc({uid, nickname = "", authProvider = "unknown"}) {
  const ref = admin.firestore().collection("users").doc(uid);
  await admin.firestore().runTransaction(async (tx) => {
    const snap = await tx.get(ref);
    const data = {
      uid,
      nickname,
      authProvider,
      ...baseDefaults(),
    };
    if (!snap.exists || !snap.data()?.createdAt) {
      data.createdAt = admin.firestore.FieldValue.serverTimestamp();
    }
    tx.set(ref, data, {merge: true});
  });
}

module.exports = {upsertUserDoc};
