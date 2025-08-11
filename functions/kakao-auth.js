const admin = require("firebase-admin");
const axios = require("axios");
const functions = require("firebase-functions/v1");
const {upsertUserDoc} = require("./upsert-user");

module.exports = async (data, context) => {
  const accessToken = data?.accessToken;
  if (!accessToken) throw new functions.https.HttpsError("invalid-argument", "Access token is required.");

  try {
    const res = await axios.get("https://kapi.kakao.com/v2/user/me", {
      headers: {Authorization: `Bearer ${accessToken}`},
    });

    const kakaoUser = res.data;
    const account = kakaoUser.kakao_account || {};
    const profile = account.profile || {};
    const uid = `kakao:${kakaoUser.id}`;

    await upsertUserDoc({
      uid,
      nickname: profile.nickname || "",
      authProvider: "kakao",
    });

    const token = await admin.auth().createCustomToken(uid);
    return {token};
  } catch (err) {
    console.error("kakao-auth error:", err.response?.data || err.message);
    throw new functions.https.HttpsError("internal", "Kakao login failed");
  }
};
