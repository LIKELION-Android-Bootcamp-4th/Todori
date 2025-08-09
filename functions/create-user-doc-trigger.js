const {upsertUserDoc} = require("./upsert-user");

module.exports = async (userRecord) => {
  await upsertUserDoc({
    uid: userRecord.uid,
    nickname: userRecord.displayName || "",
    authProvider: userRecord.providerData?.[0]?.providerId || "google",
  });
};
