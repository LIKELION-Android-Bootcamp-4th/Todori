# 🌰 TODORI (토도리) - 공부 중심 스터디 플래너

> **TO-DO + 도토리**<br>
> **공부 중심 스터디 플래너 앱**
> 작은 습관이 만드는 큰 변화
---

## 팀 소개

| 이름  | 역할  | GitHub 프로필                                        |
| --- | --- | ------------------------------------------------- |
| 허준서 | 팀장  | [Junseo0324](https://github.com/Junseo0324)       |
| 이창한 | 부팀장 | [chlee9610](https://github.com/chlee9610)         |
| 안현진 | 팀원  | [h1jn2](https://github.com/h1jn2)                 |
| 이다은 | 팀원  | [mninm](https://github.com/mninm)                 |
| 이지형 | 팀원  | [groundinsider](https://github.com/groundinsider) |

---

## 기획 의도

공부 계획을 세워도 꾸준히 기록하지 못해 흐지부지…
혼자 공부하다 쉽게 포기…
자기 점검이 어려워 **작심삼일의 함정**에 빠지기 쉽습니다.

👉 **TODORI**는 이를 해결하기 위해:

* ✅ 카테고리 별 할 일 생성 및 달성률 관리
* ✅ 공부 시간 측정 & 하루 회고
* ✅ 스터디와 집중 타이머
* ✅ 통계를 통한 성취 시각화

를 제공하여 꾸준한 공부 습관을 만들어 줍니다.

---

## 📁 폴더 구조

**Feature-based (MVVM + Clean Architecture 기반)**

```bash
📦 com.mukmuk.todori
├── 📁 data/                   # 데이터 계층
│   ├── 📁 local.datastore/    # DataStore (로컬 저장소)
│   ├── 📁 remote/             # 원격 데이터 (Firestore 등)
│   ├── 📁 repository/         # Repository
│   └── 📁 service/            # Firebase / API Service
│
├── 📁 di/                     # Hilt 의존성 주입 모듈
├── 📁 navigation/             # 네비게이션 그래프
│
├── 📁 ui/                     # UI 계층
│   ├── 📁 component/          # 공통 UI 컴포넌트
│   ├── 📁 screen/             # 화면 (로그인, 타이머, TODO, 통계 등)
│   └── 📁 theme/              # 테마 (색상, 폰트, 스타일)
│
├── 📁 util/                   # 유틸리티 클래스 & 헬퍼
│
├── 📁 widget/                 # App Widget 관련
│
├── MainActivity.kt
└── TodoriApplication.kt
```

---

## 주요 기능

* ✅ 개인/목표/스터디 기반 TODO 관리
* ⏱ 뽀모도로 & 스톱워치 타이머
* 📊 공부 시간 및 집중 패턴 통계
* 👥 스터디 모집 및 공유 기능
* 🔔 알림 기능 (D-Day, 회고 작성, 미완료 TODO 등)

---

## 🏗 아키텍처

> 본 프로젝트는 **MVVM 패턴 기반의 클라이언트 구조**와
> Firebase 및 외부 API를 활용한 **서버리스 구조**로 설계되었습니다.
<img width="1133" height="494" alt="image" src="https://github.com/user-attachments/assets/209efe52-cf01-4056-aa12-1cff3ee16f09" />


### 🔹 구조 설명

* **UI Layer (Screen, ViewModel)**: 화면 로직과 상태 분리
* **Data Layer (Repository, Service)**: 데이터 흐름 계층화, API 호출 관리
* **Server**:

  * Firebase Firestore – 앱 데이터 저장
  * Firebase Auth – 사용자 인증
  * Firebase Cloud Functions – 통계/레벨/알림 처리

---

## 🛠 기술 스택

| 분류        | 사용 기술 / 도구                                   |
| --------- | -------------------------------------------- |
| **개발 언어** | Kotlin, JavaScript                           |
| **프레임워크** | Android (Jetpack Compose)                    |
| **상태 관리** | Coroutine, Flow, ViewModel                   |
| **DI**    | Hilt                                         |
| **스토리지**  | Firebase Firestore, DataStore                |
| **인증**    | Firebase Auth, Kakao/Naver 로그인 (CustomToken) |
| **시각화**   | MPAndroidChart, Kizitonwose Calendar         |
| **알림/위젯** | FCM, Glance (Jetpack Compose App Widget)     |
| **협업 도구** | GitHub, Figma, Notion                        |

---

## 📸 TODORI 화면

| 타이머 | TODO 관리 |
| ------ | ---------- |
| <img width="200" alt="1" src="https://github.com/user-attachments/assets/eb4f5135-4f71-401a-9d05-9fa2b6e44117" /> <img width="200" alt="1 (2)" src="https://github.com/user-attachments/assets/ef3db5a4-81e3-4378-94a0-0c11a044905c" /> | <img width="200" alt="1 (5)" src="https://github.com/user-attachments/assets/c77b6949-0cb2-44a0-a47f-6ec5fe5cb5ea" /> <img width="200" alt="1 (6)" src="https://github.com/user-attachments/assets/c3ca994c-a5e1-4242-929b-1dec12f9c493" /> |

| 통계 화면 | 스터디 |
| ---------- | ------ |
| <img width="200" alt="1 (3)" src="https://github.com/user-attachments/assets/5b02d08c-97dd-47b9-937e-cd4e0da29d13" /> <img width="200" alt="1 (4)" src="https://github.com/user-attachments/assets/15cea113-df7c-4af1-8ae0-b4b44b5f9c61" /> | <img width="200" alt="1 (7)" src="https://github.com/user-attachments/assets/3ac8a71f-5edc-4034-9bce-dbd51fecec97" /> <img width="200" alt="1 (8)" src="https://github.com/user-attachments/assets/3bc175b7-f6cb-4571-a692-08018c4e47e7" /> |

| 추가 화면 |  
| --- |
| <img width="200" alt="1 (9)" src="https://github.com/user-attachments/assets/33ac2aae-931b-4d16-9d35-4b9f7a7632fa" /> <img width="200" alt="1 (11)" src="https://github.com/user-attachments/assets/64c69c1e-7670-4cd2-9fd4-2656c53bd302" /> |  
