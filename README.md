# 🗺️ U-Hyu : 지도 기반 LG U+ 멤버십 제휴처 안내 및 추천 서비스

## 📍 프로젝트 소개

> U-HYU(유휴)는 사용자 주변의 LG U+ 멤버십 제휴처를 **지도 기반**으로 쉽고 직관적으로 탐색할 수 있게 해주는 **위치 기반 혜택 제공 플랫폼** 입니다.
> 개인화 추천 및 즐겨찾기, 소셜 공유를 통해 **유휴 혜택 자원의 재발견**을 유도합니다.

### 🧑‍💻 개발 기간
- 2025.07.05 ~ 2025.08.08

### 🚀 **서비스 오픈!**  
> 지금 바로 👉 **[https://www.u-hyu.site](https://www.u-hyu.site)** 접속해서  
> 내 주변 혜택을 확인해보세요 🎁  

<br>

---

### 💡 문제 인식 및 기대효과

1. **낮은 멤버십 혜택 활용률**
   - 국내 통신사 멤버십 이용률은 36% 수준 (2024, 옴디아)
   - "사용처를 몰라서", "매력 없음"이 가장 큰 이유
   - LG U+ 기준 최근 5년간 소멸된 포인트만 233억 원 규모
     
   ✨ 제휴처 정보 제공과 탐색 UX 개선을 통해 멤버십 사용율 상승 기대

2. **복잡한 구조와 정보 탐색의 어려움**
   - 제휴 혜택은 있지만 ‘어디서, 어떻게’ 쓸 수 있는지 알기 어려움
   - 기존 앱/웹은 정적 정보 중심이며, UX가 복잡하여 탐색이 불편함
   - 사용자 위치 기반의 탐색이나 실시간성 부재
     
   ✨ 지도 중심 UI + 위치 기반 필터링을 통해 정보 접근성과 탐색 편의성 제공

3. **지도 중심 서비스 기회**
   - 모바일 쿠폰 시장, 연간 6조 원 이상 규모로 성장 중
   - 실시간 위치 기반으로 혜택을 연결하는 O2O 서비스 수요 증가

   ✨ 지도 기반 실시간 혜택 탐색으로 생활밀착형 서비스 경험 제공

---

### 🎯 프로젝트 목표

1. **지도에 제휴처 시각화**  
   - 사용자 위치 기반 제휴 매장을 직관적으로 탐색 기능 제공

2. **개인화 맞춤 제휴처 추천**
   - **비로그인 사용자**: 전체 사용자 데이터를 기반으로 인기 제휴처 Top 3 안내
   - **신규 사용자**: 온보딩 설문을 통해 방문/관심 브랜드 수집 → 초기 맞춤 추천 제공
   - **기존 사용자**: 즐겨찾기, 행동 로그 등 누적 사용자 데이터 기반으로 개인화된 제휴처 추천 제공

3. **실시간 추천에 대한 사용자 피드백 반영**
   - 사용자가 남긴 추천 결과에 대한 '싫어요'에 대한 반응을 남기면,
   - 해당 피드백을 기반으로 즉시 추천 모델에 반영하여 재추천 제공

4. **퍼포먼스 최적화**
   - PostGIS + 공간 인덱스 기반 쿼리 최적화로 빠른 지도 응답 속도 확보
   - 프론트에서는 React Query + 마커 클러스터링 적용으로 렌더링 효율 개선

5. **관리자 통계 시스템 구축**
   - 즐겨찾기, 접속 현황, 추천 이력, 멤버십 이용 내역 등에 대해
   - 카테고리별 / 브랜드별로 정량 분석할 수 있는 기능 제공
  
---

## 🚀 U-HYU 만의 차별화된 기능

> 💬 U-Hyu의 핵심 기능 설명 및 설계 과정에서의 고민은 [U-HYU 백엔드 Wiki 문서](https://github.com/U-Final/U-Hyu-be/wiki)에 상세히 정리되어 있습니다!

### 1. 개인화 추천 및 실시간 재추천 기능

**1-1. 추천 시스템 도입 배경**

- 사용자 데이터를 활용하여 개인화된 추천을 제공하고, 더 나은 사용자 경험을 실현하기 위해 도입

**1-2. U-HYU 추천에 LightFM 알고리즘을 도입한 이유**

- U-Hyu에서 수집하는 사용자 및 브랜드 정보 :
	- 유저 온보딩 시 수집되는 관심 브랜드 / 최근 방문 브랜드
	- 서비스 이용 중 발생하는 클릭 로그, 즐겨찾기, 제휴처 이용 이력
	- 각 브랜드의 카테고리, 등급, 온라인/오프라인 유형 등의 속성 정보
	- 사용자의 성별, 연령대 등 선택적으로 수집되는 개인 정보

이처럼 다양한 정형 데이터를 하나의 추천 모델에 결합해 학습하기 위해서는, 단순한 협업 필터링을 넘어선 LightFM과 같은 하이브리드 추천 알고리즘이 필요!

**1-3. U-HYU 만의 추천 : 사용자 의견 반영 및 실시간 재추천 제공**

- 사용자가 추천 결과에 대해 불만족(싫어요)을 표현하면, 해당 브랜드는 이후 추천 알고리즘의 학습/예측 대상에서 제거 후 재추천 결과 제공

<br>

✅ U-HYU 화면

(1) 사용자가 추천 받은 제휴처에 대해 불만족(싫어요)를 표현

<img width="355" height="650" alt="Image" src="https://github.com/user-attachments/assets/7505123f-2707-4132-85b6-8a2e9177cc29" />

<img width="355" height="650" alt="Image" src="https://github.com/user-attachments/assets/02ac448d-67e3-4b9d-a742-ba4b38126633" />

<br>

(2) 즉시 추천 알고리즘 재실행

<img width="505" height="650" alt="Image" src="https://github.com/user-attachments/assets/5deb1d87-42fb-471e-864c-72ce4322221e" />

<br>

(3) 사용자에게 즉시 "싫어요"한 브랜드를 제외한 또 다른 제휴처를 추천

<img width="355" height="650" alt="Image" src="https://github.com/user-attachments/assets/36e48809-f4de-4db3-b421-6ce816ec08fb" />

<br>
<br>

✅ 결과 : 실시간 개인화 추천 반영

- 기존: 추천이 배치로 동작하여 사용자의 행동 로그 등에 대한 재추천 및 반영이 배치마다 시간이 걸림
- 현재: 유저 행동에 대한 즉각적인 추천 재실행 및 결과 제공 **→ 즉각적인 피드백 기반 개인화 추천 경험 제공**

📙 추천 시스템에 대한 저희의 고민과 설계 과정은 [개인화 추천 시스템 설계 관련 Wiki](https://github.com/U-Final/U-Hyu-be/wiki/Recommendation-System)에서 더 볼 수 있습니다.

### 2. PostGIS

📙 PostGIS 쿼리 개선에 대한 저희의 고민과 개선 과정은 [지도 기능 관련 Wiki](https://github.com/U-Final/U-Hyu-be/wiki/PostGIS-%EC%A7%80%EB%8F%84-%EC%84%9C%EB%B9%84%EC%8A%A4-%EC%A1%B0%ED%9A%8C-%EC%84%B1%EB%8A%A5-%EC%B5%9C%EC%A0%81%ED%99%94-(1))에서 더 볼 수 있습니다.

### 3. 통계

📙 통계 테이블 설계에 대한 저희의 고민과 설계 과정은 [통계 기능 관련 Wiki](https://github.com/U-Final/U-Hyu-be/wiki/%ED%86%B5%EA%B3%84-%ED%85%8C%EC%9D%B4%EB%B8%94-%EC%B6%94%EA%B0%80-%EB%B0%8F-%EC%BF%BC%EB%A6%AC-%EA%B0%9C%EC%84%A0)에서 더 볼 수 있습니다.

---
## ERD

### 1. 사용자 정보 - user

<img width="781" height="522" alt="Image" src="https://github.com/user-attachments/assets/8a52bd8c-489b-4179-af1a-f25f5682ce60" />

### 2. 제휴처 정보 - brand, store, categories

<img width="1188" height="360" alt="Image" src="https://github.com/user-attachments/assets/b89bcd54-dadf-4e0d-9e18-4ef514f8a825" />

### 3. 사용자가 서비스를 이용하면서 개인화하는 정보 - bookmark, mymap

<img width="994" height="526" alt="Image" src="https://github.com/user-attachments/assets/5aa92bb1-80a0-4b84-808f-dc04cdfb4a88" />

### 4. 추천 관련 - recommendation

<img width="1354" height="745" alt="Image" src="https://github.com/user-attachments/assets/063b6427-a645-4014-941f-96ce64f31ca6" />

### 5. 어드민 통계 - statistics

<img width="656" height="280" alt="Image" src="https://github.com/user-attachments/assets/7657e1b1-cec0-40b4-ba9b-d1666c5a397d" />

### 전체 erd 구성

<img width="1311" height="553" alt="Image" src="https://github.com/user-attachments/assets/d06cbaab-dc23-4cc2-9544-7b30ff0a040d" />

📙 ERD 및 Database에 대한 저희의 고민과 설계 과정은 [데이터베이스 관련 Wiki](https://github.com/U-Final/U-Hyu-be/wiki/%EB%8D%B0%EC%9D%B4%ED%84%B0-%EB%AA%A8%EB%8D%B8%EB%A7%81-%EB%B0%8F-%EC%B6%94%EC%B2%9C-%EC%8B%9C%EC%8A%A4%ED%85%9C-%EC%84%A4%EA%B3%84-%EA%B3%A0%EB%AF%BC)에서 더 볼 수 있습니다.

---

## 🔧 기술스택

<img width="680" height="715" alt="image" src="https://github.com/user-attachments/assets/1bdf5490-8f6d-4105-9e2e-95e136a80636" />

<br>

### 기술 스택 정리

| 항목         | 기술                                                                 |
|--------------|----------------------------------------------------------------------|
| **클라우드**   | AWS EC2 (Spring, Prometheus, Grafana, LightFM), RDS (PostgreSQL + PostGIS), S3 |
| **배포**       | Docker, GitHub Actions (CI/CD), Vercel (Frontend)                  |
| **인증**       | ACM + Route 53 (도메인 인증 및 HTTPS), Kakao OAuth2 + Spring Security + JWT |
| **모니터링**   | Prometheus + Grafana                                                |
| **추천 엔진**  | Python + LightFM                                                    |
| **프론트엔드** | Vercel (GitHub 연동 자동 배포)                                       |

---

## 아키텍쳐

<img width="891" height="551" alt="image" src="https://github.com/user-attachments/assets/d8e93731-92a6-4de9-b986-a113497ea794" />

📙 아키텍쳐에 대한 저희의 고민과 설계 과정은 [인프라 관련 Wiki](https://github.com/U-Final/U-Hyu-be/wiki/Recommendation-System)에서 더 볼 수 있습니다.

---

## 🙋‍♂️ Developer

| 사진 | 이름 | GitHub |
|------|------|--------|
| <img src="https://avatars.githubusercontent.com/u/127932430?v=4" width="80"> | 👑임동준 | [djlim00](https://github.com/djlim00) |
| <img src="https://avatars.githubusercontent.com/Leesowon" width="80"> | 이소원 | [Leesowon](https://github.com/Leesowon) |
| <img src="https://avatars.githubusercontent.com/etoile0626" width="80"> | 최윤제 | [etoile0626](https://github.com/etoile0626) |

