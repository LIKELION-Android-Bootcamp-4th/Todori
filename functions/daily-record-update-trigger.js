const admin = require("firebase-admin");
const firestore = require("firebase-functions/v1/firestore");

exports.onDailyRecordUpdate = firestore
    .document("users/{uid}/dailyRecord/{date}")
    .onWrite(async (change, context) => {
      const db = admin.firestore();
      const {uid, date} = context.params;
      const [year, month] = date.split("-");
      const monthKey = `${year}-${month}`;

      const after = change.after.exists ? change.after.data() : null;
      if (!after) return;

      // === 기존 Day 통계 처리 (streak 등) ===
      const studyMinutes = Math.floor((after.studyTimeMillis || 0) / (1000 * 60));
      const qualified = studyMinutes >= 30;
      const dayStatRef = db.collection("users").doc(uid).collection("dayStats").doc(date);
      await dayStatRef.set({qualified}, {merge: true});

      // === 월간 통계 처리 ===
      // 1) DailyRecords 집계
      const recordsSnap = await db.collection("users").doc(uid).collection("dailyRecord")
          .where("date", ">=", `${year}-${month}-01`)
          .where("date", "<=", `${year}-${month}-31`)
          .get();

      let totalStudyTime = 0;
      recordsSnap.forEach((doc) => totalStudyTime += doc.data().studyTimeMillis || 0);

      // 2) Todos 집계
      const todosSnap = await db.collection("users").doc(uid).collection("todos")
          .where("date", ">=", `${year}-${month}-01`)
          .where("date", "<=", `${year}-${month}-31`)
          .get();

      const totalTodos = todosSnap.size;
      const completedTodos = todosSnap.docs.filter((d) => d.data().completed).length;
      const todoCompletionRate = totalTodos > 0 ? Math.round(completedTodos / totalTodos * 100) : 0;

      const categoriesSnap = await db.collection("users").doc(uid).collection("todoCategories").get();

      const categoryStats = [];
      categoriesSnap.forEach((cat) => {
        const catId = cat.id;
        const catTodos = todosSnap.docs
            .map((d) => d.data())
            .filter((t) => t.categoryId === catId);

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
        const g = goalDoc.data();
        if (g.startDate <= `${year}-${month}-31` && g.endDate >= `${year}-${month}-01`) {
          const gTodos = goalTodosSnap.docs.filter((gt) => gt.data().goalId === g.goalId);
          const total = gTodos.length;
          const completed = gTodos.filter((gt) => gt.data().completed).length;
          const rate = total > 0 ? Math.round((completed / total) * 100) : 0;

          goalStats.push({goalId: g.goalId, title: g.title, completionRate: rate});
        }
      });

      // 4) Best Day 계산
      let bestDay = null; let bestScore = -1;
      for (const recDoc of recordsSnap.docs) {
        const rec = recDoc.data();
        const sMinutes = Math.floor((rec.studyTimeMillis || 0) / (1000 * 60));
        const todosOnDay = todosSnap.docs.map((d) => d.data()).filter((t) => t.date === rec.date);
        const completedCount = todosOnDay.filter((t) => t.completed).length;
        const rate = todosOnDay.length > 0 ? (completedCount / todosOnDay.length) * 100 : 0;

        const score = rate + sMinutes / 10;
        if (score > bestScore) {
          bestScore = score;
          bestDay = {
            date: rec.date,
            completionRate: Math.round(rate),
            studyTime: `${Math.floor(sMinutes / 60)}시간 ${sMinutes % 60}분`,
            score: Math.round(score),
          };
        }
      }

      await db.collection("users").doc(uid).collection("monthStats").doc(monthKey).set({
        year: parseInt(year),
        month: parseInt(month),
        totalStudyTime,
        totalTodos,
        completedTodos,
        todoCompletionRate,
        categoryStats,
        goalStats,
        bestDay,
        updatedAt: admin.firestore.FieldValue.serverTimestamp(),
      }, {merge: true});
    });
