# CareFull

<div align="center">
  <img width="200" height="200" alt="app_logo" src="https://github.com/user-attachments/assets/510a5cff-01a1-45f1-839f-02fbd25985c6" />
  <br />
  <strong>"운동·식단 기록부터 건강 정보 검색까지, 하나의 앱으로 케어하세요"</strong>
  <br /><br />
  AI(ML Kit)를 활용한 실시간 운동 자세 분석과 생성형 AI(ChatGPT)기반의 맞춤형 건강 상담이 결합된 통합 헬스케어 앱입니다.
</div>

## 목차
1. [프로젝트 개요](#%EF%B8%8F-프로젝트-개요)
2. [서비스 목표](#-서비스-목표)
3. [기술 스택](#-기술-스택)
4. [프로젝트 아키텍쳐](#%EF%B8%8F-프로젝트-아키텍쳐)
5. [서비스 주요 기능](#-서비스-주요-기능)
6. [데이터 셋](#-데이터-셋)
7. [팀원 소개](#%E2%80%8D-팀원-소개)

<br>

## 🗂️ 프로젝트 개요

- **앱 이름** : CareFull
- **팀원**
    - 양정규
    - 조해민
- **작업 기간** : 2025.06 ~ 마켓 출시 준비 중
- **플랫폼** : Android
- **개발 언어** : Kotlin
- **개발 환경** : Android Studio
- **외부API 및 서비스**
    - 공공데이터 API
    - Firebase Firestore
    - Kakao OAuth
    - On-device AI Google ML Kit API
    - Naver Maps
    - OpenAI API (ChatGPT)

## 🎯 서비스 목표

사용자의 운동·식단 습관화를 돕고, 개인의 상태를 분석하여 최적의 건강 정보를 제공함으로써 사용자 중심의 건강관리 서비스를 실현합니다.

<br>

## 🧰 기술 스택

| 분류             | 기술                                                                              |
|----------------|---------------------------------------------------------------------------------|
| Language       | Kotlin                                                                          |
| Architecture   | Clean Architecture (Hybrid), MVVM                                               |
| Asynchronous   | Coroutine, Flow                                                                 |
| UI             | Coil, Jetpack Compose, Navigation, Paging3                                      |
| Network        | Gson, Interceptor, OkHttp3, Retrofit2, tikxml                                   |
| AI             | On-device AI Google ML Kit(Pose Detection, Face Detection), OpenAI API (생성형 AI) |
| SDK & API      | Fused Location Provider,Kakao SDK, Naver Map SDK                                |
| Authentication | KaKao OAuth                                                                     |
| DataBase       | Firebase (Firestore), Room                                                      |
| DI             | Hilt                                                                            |
| ETC / Tools    | Figma, Github, Notion, Postman, Runtime Permission                              |

<br>

## 🏗️ 프로젝트 아키텍쳐

<img width="1000" height="569" alt="project-flow" src="https://github.com/user-attachments/assets/fedc8a46-eecc-4e58-b61c-8f5ef6a5a0a4" />

<img width="1000" height="678" alt="module" src="https://github.com/user-attachments/assets/febe767e-3b62-4dde-a566-af9ea5468b9a" />

## ✨ 서비스 주요 기능

### 1. 로그인

| <img width="200" height="433" alt="Signin" src="https://github.com/user-attachments/assets/c8f1f3a6-224e-4747-a497-f6d2e30a630b" /> |
|:-----------------------------------------------------------------------------------------------------------------------------------:|
|                                                   Kakao OAuth를 이용하여<br>계정을 관리합니다.                                                   |

### 2. 메인 화면

| <img width="200" height="398" alt="home" src="https://github.com/user-attachments/assets/87967983-c311-4991-a4b4-fcce8d7cec5a" /> |
|:---------------------------------------------------------------------------------------------------------------------------------:|
|                                                     운동과 식단 활동이<br>달력에 표시됩니다.                                                      |

### 3. 운동 기록

|   <img src="https://github.com/user-attachments/assets/d8aafe14-2b04-4072-8532-44c65d5b3551" alt="squat" width="200"/>   |
|:------------------------------------------------------------------------------------------------------------------------:|
| On-device AI Google ML Kit를 활용하여 사용자의 <br> 관절 포인트(Skeleton)를 실시간으로 추적합니다.<br> 스쿼트 등 운동 자세의 정확도를 판별하고 <br>자동으로 횟수를 카운팅합니다. |

### 4. 식단 기록

| <img src="https://github.com/user-attachments/assets/dbb8668c-66da-496b-88ba-e1d9864399f1" alt="diet" width="200"/> |
|:-------------------------------------------------------------------------------------------------------------------:|
|                                       공공데이터 API를 활용하여<br>음식을 검색 후 식단을 등록합니다.                                        |

### 5. 챗봇

| <img src="https://github.com/user-attachments/assets/709605b3-f803-4037-b60f-7c753d398341" alt="chatbot" width="200"/> |
|:----------------------------------------------------------------------------------------------------------------------:|
|                                  OpenAI API를 활용하여 몸 상태를 챗봇에<br>입력 시 증상에 맞는 질환과 진료 과목을 추천해 줍니다.                                  |

### 6. 병원 검색

### 7. 질병, 약 검색

| <img src="https://github.com/user-attachments/assets/8c68fcc1-4d13-4811-ac5e-eb17db5d2da1" alt="medicineSearch" width="200"/> |
|:-----------------------------------------------------------------------------------------------------------------------------:|
|                                            공공데이터 API를 활용하여<br>검색한 약의 상세정보를 조회합니다.                                             |

### 8. 커뮤니티, 랭킹

| <img src="https://github.com/user-attachments/assets/1ddd0bc3-1b44-4d4e-9bb5-79808ba95fc7" alt="social" width="200"/> | <img width="200" height="418" alt="ranking" src="https://github.com/user-attachments/assets/0f80f387-481f-493e-80cf-a874559be6f5" /> |
|:---------------------------------------------------------------------------------------------------------------------:|:------------------------------------------------------------------------------------------------------------------------------------:|
|                                            Firebase를 활용한<br> 커뮤니티 컨텐츠입니다.                                             |                                                    주간 운동 종목별<br>운동 횟수로 순위를 보여줍니다.                                                    |

<br>

## 📊 데이터 셋

### 1. API 데이터

| 데이터 종류    | 활용 API               | 제공 기관     | 주요 활용 목적          |
|-----------|----------------------|-----------|-------------------|
| 의약품 정보    | 의약품 제품 허가정보 API      | 식품의약품안전처  | 약 상세 정보 조회        |
| 병원 위치 정보  | 병원 정보 조회 API         | 건강보험심사평가원 | 위치 기반 병원 검색       |
| 식품 영양 정보  | 식품영양성분 API           | 식품의약품안전처  | 음식 영양 성분 조회       |
| 위치 정보     | Naver Map API        | Naver     | 현재 위치 안내          |
| AI 기반 콘텐츠 | OpenAI API (ChatGPT) | OpenAI    | 사용자 질문 답변, 콘텐츠 추천 |

### 2. 사용자 생성 데이터

- **프로필 데이터** : 나이, 성별, 키, 체중, 활동량
- **운동 기록** : 운동 종목, 횟수, 날짜
- **식단 기록** : 음식명, 음식 영양 성분, 섭취량, 날짜
- **소셜 활동** : 게시글, 댓글, 좋아요

### 3. 가공 및 시스템 생성 데이터

- **통계·분석 데이터** : 운동 빈도, 섭취 칼로리, 영양 비율

<br>

## 🧑‍💻 팀원 소개

<table>
  <tr>
    <td align="center"><a href="https://github.com/PEANUTBUTTER1001"><img src="https://avatars.githubusercontent.com/u/183465637?v=4" width="100px;"></td>
    <td align="center"><a href="https://github.com/dptcldpa"><img src="https://avatars.githubusercontent.com/u/116916268?v=4" width="100px;"></td>
  </tr>
  <tr>
    <td align="center"><b><a href="https://github.com/PEANUTBUTTER1001">양정규</a></b></td>
    <td align="center"><b><a href="https://github.com/dptcldpa">조해민</a></b></td>
  </tr>
</table>
