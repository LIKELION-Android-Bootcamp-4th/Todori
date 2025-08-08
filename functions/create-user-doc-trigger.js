const admin = require("firebase-admin");

module.exports = async (u) => {
  const db = admin.firestore();
  const ref = db.collection("users").doc(u.uid);

  await ref.set({
    uid: u.uid,
    nickname: u.displayName || "",
    email: u.email || "",
    intro: null,
    level: 1,
    rewardPoint: 0,
    authProvider: u.providerData?.[0]?.providerId || "unknown",
    createdAt: admin.firestore.FieldValue.serverTimestamp(),
    lastLoginAt: admin.firestore.FieldValue.serverTimestamp(),
    isDeleted: false,
    fcmToken: null,
  }, {merge: true});
};
