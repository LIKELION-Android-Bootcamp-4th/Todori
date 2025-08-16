const admin = require("firebase-admin");
const firestore = require("firebase-functions/v1/firestore");

exports.onDailyRecordUpdate = firestore
    .document("users/{uid}/dailyRecord/{date}")
    .onWrite(async (change, context) => {
      const db = admin.firestore();
      const {uid, date} = context.params;
      const after = change.after.exists ? change.after.data() : null;

      if (!after) return;

      const studyMinutes = Math.floor((after.studyTimeMillis || 0) / (1000 * 60));
      const qualified = studyMinutes >= 30;

      const dayStatRef = db.collection("users").doc(uid).collection("dayStats").doc(date);
      const statsRef = db.collection("users").doc(uid).collection("stats").doc("streak");

      // streak, bestStreak 가져오기
      const statsSnap = await statsRef.get();
      let currentStreak = 0;
      let bestStreak = 0;
      if (statsSnap.exists) {
        currentStreak = statsSnap.data().currentStreak || 0;
        bestStreak = statsSnap.data().bestStreak || 0;
      }

      if (qualified) {
        currentStreak += 1;
        if (currentStreak > bestStreak) bestStreak = currentStreak;
      } else {
        currentStreak = 0;
      }

      // dayStats 업데이트
      await dayStatRef.set(
          {
            qualified,
            streakCount: currentStreak,
            updatedAt: admin.firestore.FieldValue.serverTimestamp(),
          },
          {merge: true},
      );

      // stats 업데이트
      await statsRef.set(
          {
            currentStreak,
            bestStreak,
            updatedAt: admin.firestore.FieldValue.serverTimestamp(),
          },
          {merge: true},
      );
    });
