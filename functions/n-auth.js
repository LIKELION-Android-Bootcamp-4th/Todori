const admin = require("firebase-admin");
const axios = require("axios");
const functions = require("firebase-functions/v1");
const {upsertUserDoc} = require("./upsert-user");

module.exports = async (data, context) => {
  const accessToken = data?.accessToken;
  if (!accessToken) throw new functions.https.HttpsError("invalid-argument", "Access token is required.");

  try {
    const res = await axios.get("https://openapi.naver.com/v1/nid/me", {
      headers: {Authorization: `Bearer ${accessToken}`},
      timeout: 5000,
    });

    const naver = res.data?.response || {};
    const uid = `naver:${naver.id}`;

    await upsertUserDoc({
      uid,
      nickname: naver.nickname || "",
      authProvider: "naver",
    });

    const token = await admin.auth().createCustomToken(uid);
    return {token};
  } catch (err) {
    console.error("naver-auth error:", err.response?.data || err.message);
    throw new functions.https.HttpsError("internal", "Naver login failed");
  }
};
