const admin = require("firebase-admin");
const pubsub = require("firebase-functions/v1/pubsub");

exports.dailyMidnightBatch = pubsub
    .schedule("0 0 * * *")
    .timeZone("Asia/Seoul")
    .onRun(async () => {
      const db = admin.firestore();
      const usersSnap = await db.collection("users").get();

      const today = new Date();
      const yyyy = today.getFullYear();
      const mm = String(today.getMonth() + 1).padStart(2, "0");
      const dd = String(today.getDate()).padStart(2, "0");
      const todayStr = `${yyyy}-${mm}-${dd}`;

      const batch = db.batch();

      for (const userDoc of usersSnap.docs) {
        const uid = userDoc.id;
        const dayStatRef = db.collection("users").doc(uid).collection("dayStats").doc(todayStr);
        const statsRef = db.collection("users").doc(uid).collection("stats").doc("streak");

        const dayStatSnap = await dayStatRef.get();
        if (!dayStatSnap.exists) {
          batch.set(dayStatRef, {
            qualified: false,
            streakCount: 0,
            updatedAt: admin.firestore.FieldValue.serverTimestamp(),
          });

          batch.set(
              statsRef,
              {
                currentStreak: 0,
                updatedAt: admin.firestore.FieldValue.serverTimestamp(),
              },
              {merge: true},
          );
        }
      }

      await batch.commit();
      console.log("dailyMidnightBatch 완료");
    });
