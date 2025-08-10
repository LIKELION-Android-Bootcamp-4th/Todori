const {onCall} = require("firebase-functions/v1/https");
const auth = require("firebase-functions/v1/auth");
const admin = require("firebase-admin");

const handleUserCreate = require("./create-user-doc-trigger");
const handleGoogle = require("./google-auth");
const handleKakao = require("./kakao-auth");
const handleNaver = require("./n-auth");


admin.initializeApp();

// 신규 유저 Firestore 문서 생성
exports.createUserDoc = auth.user().onCreate(handleUserCreate);

// 로그인 관련 Cloud Functions
exports.googleCustomAuth = onCall(handleGoogle);
exports.kakaoCustomAuth = onCall(handleKakao);
exports.nCustomAuth = onCall(handleNaver);

