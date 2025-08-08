const admin = require("firebase-admin");
const axios = require("axios");
const functions = require("firebase-functions/v1");

module.exports = async (data, context) => {
  const kakaoAccessToken = data.accessToken;
  if (!kakaoAccessToken) {
    throw new functions.https.HttpsError("invalid-argument", "Access token is required.");
  }

  try {
    // 카카오 사용자 정보 요청
    const userRes = await axios.get("https://kapi.kakao.com/v2/user/me", {
      headers: {Authorization: `Bearer ${kakaoAccessToken}`},
    });

    const kakaoUser = userRes.data;
    const uid = `kakao:${kakaoUser.id}`;
    const profile = kakaoUser.kakao_account.profile || {};

    // Firestore 갱신
    await admin.firestore().collection("users").doc(uid).set({
      uid,
      nickname: profile.nickname || "",
      photoUrl: profile.profile_image_url || "",
      authProvider: "kakao",
      createdAt: admin.firestore.FieldValue.serverTimestamp(),
      lastLoginAt: admin.firestore.FieldValue.serverTimestamp(),
      intro: null,
      level: 1,
      rewardPoint: 0,
      isDeleted: false,
      fcmToken: null,
    }, {merge: true});

    // Custom Token 발급
    const customToken = await admin.auth().createCustomToken(uid);
    return {token: customToken};
  } catch (err) {
    console.error(err);
    throw new functions.https.HttpsError("internal", "Kakao login failed");
  }
};
