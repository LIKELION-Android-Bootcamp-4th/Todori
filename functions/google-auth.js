const admin = require("firebase-admin");
const functions = require("firebase-functions/v1");

module.exports = async (data, context) => {
  const {uid, email, displayName, photoUrl} = data;

  if (!uid) {
    throw new functions.https.HttpsError("invalid-argument", "UID is required.");
  }

  await admin.firestore().collection("users").doc(uid).set({
    uid,
    nickname: displayName || "",
    email: email || "",
    photoUrl: photoUrl || "",
    authProvider: "google",
    lastLoginAt: admin.firestore.FieldValue.serverTimestamp(),
  }, {merge: true});

  // Custom Token 발급
  const customToken = await admin.auth().createCustomToken(uid);
  return {token: customToken};
};
