const functions = require("firebase-functions/v1");
const admin = require("firebase-admin");

exports.monthStatBatch = functions.pubsub
    .schedule("0 18 1 * *")
    .timeZone("Asia/Seoul")
    .onRun(async (context) => {
      const db = admin.firestore();
      const usersSnap = await db.collection("users").get();

      for (const userDoc of usersSnap.docs) {
        const uid = userDoc.id;
        const now = new Date();
        const year = now.getFullYear();
        const month = now.getMonth();

        const monthKey = `${year}-${String(month + 1).padStart(2, "0")}`;

        const categoriesSnap = await db.collection("users").doc(uid).collection("todoCategories").get();
        const todosSnap = await db.collection("users").doc(uid).collection("todos").get();

        const categoryStats = [];
        categoriesSnap.forEach((cat) => {
          const catId = cat.id;
          const catTodos = todosSnap.docs
              .map((d) => d.data())
              .filter((t) => t.categoryId === catId &&
              t.date.startsWith(`${year}-${String(month + 1).padStart(2, "0")}`));

          const total = catTodos.length;
          const completed = catTodos.filter((t) => t.completed).length;
          const completionRate = total > 0 ? Math.round((completed / total) * 100) : 0;

          categoryStats.push({
            categoryId: catId,
            name: cat.data().name,
            completionRate,
          });
        });

        const goalsSnap = await db.collection("users").doc(uid).collection("goals").get();
        const goalTodosSnap = await db.collection("users").doc(uid).collection("goalTodos").get();

        const goalStats = [];
        goalsSnap.forEach((goalDoc) => {
          const goal = goalDoc.data();
          // 이번 달에 포함된 Goal만 집계
          if (goal.startDate <= `${year}-${String(month + 1).padStart(2, "0")}-31` &&
            goal.endDate >= `${year}-${String(month + 1).padStart(2, "0")}-01`) {
            const goalTodos = goalTodosSnap.docs
                .map((d) => d.data())
                .filter((gt) => gt.goalId === goal.goalId);

            const total = goalTodos.length;
            const completed = goalTodos.filter((gt) => gt.completed).length;
            const completionRate = total > 0 ? Math.round((completed / total) * 100) : 0;

            goalStats.push({
              goalId: goal.goalId,
              title: goal.title,
              completionRate,
            });
          }
        });

        const recordsSnap = await db.collection("users").doc(uid).collection("dailyRecord").get();
        let bestDay = null;
        let bestScore = -1;

        recordsSnap.forEach((recDoc) => {
          const rec = recDoc.data();
          if (!rec.date.startsWith(`${year}-${String(month + 1).padStart(2, "0")}`)) return;

          const studyMinutes = Math.floor((rec.studyTimeMillis || 0) / (1000 * 60));
          const todosOnDay = todosSnap.docs
              .map((d) => d.data())
              .filter((t) => t.date === rec.date);
          const completedCount = todosOnDay.filter((t) => t.completed).length;
          const rate = todosOnDay.length > 0 ? (completedCount / todosOnDay.length) * 100 : 0;

          const score = rate + studyMinutes / 10;
          if (score > bestScore) {
            bestScore = score;
            bestDay = {
              date: rec.date,
              completionRate: Math.round(rate),
              studyTime: `${Math.floor(studyMinutes / 60)}시간 ${studyMinutes % 60}분`,
              score: Math.round(score),
            };
          }
        });

        await db.collection("users").doc(uid).collection("monthStats").doc(monthKey).set({
          year,
          month: month + 1,
          categoryStats,
          goalStats,
          bestDay,
          updatedAt: admin.firestore.FieldValue.serverTimestamp(),
        }, {merge: true});
      }
    });
