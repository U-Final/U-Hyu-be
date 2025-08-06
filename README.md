# 🗺️ U-Hyu : 지도 기반 LG U+ 멤버십 제휴처 안내 및 추천 서비스

## 📍 프로젝트 소개

> U-HYU(유휴)는 사용자 주변의 LG U+ 멤버십 제휴처를 **지도 기반 UX**로 직관적으로 탐색하고, 다른 사용자의 이용 내역까지 함께 확인할 수 있는 **위치 기반 혜택 공유 플랫폼**입니다.  
> 개인화 추천 및 즐겨찾기, 소셜 공유를 통해 **유휴 혜택 자원의 재발견**을 유도합니다.

### 🧑‍💻 개발 기간
- 2025.07.05 ~ 2025.08.08

### 🚀 **서비스 오픈!**  
> 지금 바로 👉 **[https://www.u-hyu.site](https://www.u-hyu.site)** 접속해서  
> 내 주변 혜택을 확인해보세요 🎁  

---

### 💡 기획 배경

1. **낮은 멤버십 혜택 활용률**
   - 국내 통신사 멤버십 이용률은 36% 수준 (2024, 옴디아)
   - "사용처를 몰라서", "매력 없음"이 가장 큰 이유.
   - LG U+ 기준 최근 5년간 소멸된 포인트만 233억 원 규모

2. **복잡한 구조와 정보 탐색의 어려움**
   - 혜택은 있지만 ‘어디서, 어떻게’ 쓸 수 있는지 알기 어려움
   - 기존 앱/웹은 정적 정보 중심이며, UX가 복잡하여 탐색이 불편함
   - 사용자 위치 기반의 탐색이나 실시간성 부재

3. **지도 중심 서비스 기회**
   - 모바일 쿠폰 시장 성장세 (6조 규모)
   - 사용자 위치와 실시간 혜택을 연결하는 O2O 기반 서비스 수요 증가

---

### 🎯 프로젝트 목표 및 기대효과

1. **지도에 제휴처 시각화**  
   → 사용자 위치 기반 4km 반경 내 혜택 매장을 직관적으로 탐색 가능

2. **개인화 추천 시스템 구현**
   - **비로그인 사용자**: 인기 제휴처 안내
   - **신규 사용자**: 설문 기반 방문 및 관심 브랜드 수집으로 Cold Start 완화
   - **기존 사용자**: 즐겨찾기, 액션 로그 기반 LightFM 추천

3. **실사용자 피드백 기반 추천 고도화** 
   → 추천 결과 저장 및 재학습 구조 적용 예정

4. **퍼포먼스 최적화**
   - PostGIS + 공간 인덱스 활용한 위치 쿼리 최적화
   - React Query + 클러스터링 렌더링 등 UX/성능 개선

5. **관리자 통계 시스템 구축**
   - 즐겨찾기/접속/이용 현황 기반의 정량 분석 지원
  
---

## 🚀 핵심 기능 & 기술 설계는 Wiki에서 확인하세요!

U-Hyu의 핵심 기능과 설계 과정에서의 고민은 아래 Wiki 문서에 상세히 정리되어 있습니다:

1.	[🧩 데이터 모델링 & 설계 고민](https://github.com/U-Final/U-Hyu-be/wiki/Data-Modeling)
2.	[🧠 개인화 추천 시스템 설계](https://github.com/U-Final/U-Hyu-be/wiki/Recommendation-System)
3.	[🌍 PostGIS 기반 위치 기반 탐색 로직]
4.	[📊 관리자 통계 시스템]

> Wiki 문서는 계속 업데이트됩니다. 
> 💬 각 문서에 팀의 고민과 선택 배경도 함께 담아두었어요!

---

## 🔧 기술스택

<img width="700" height="715" alt="image" src="https://github.com/user-attachments/assets/1bdf5490-8f6d-4105-9e2e-95e136a80636" />

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

---

## ERD

### 1. 사용자 정보 - user

<img width="781" height="522" alt="Image" src="https://github.com/user-attachments/assets/8a52bd8c-489b-4179-af1a-f25f5682ce60" />

### 2. 제휴처 정보 - brand, store, categories

<img width="1188" height="360" alt="Image" src="https://github.com/user-attachments/assets/b89bcd54-dadf-4e0d-9e18-4ef514f8a825" />

### 3. 사용자가 서비스를 이용하면서 개인화하는 정보 - bookmark, mymap

<img width="994" height="526" alt="Image" src="https://github.com/user-attachments/assets/5aa92bb1-80a0-4b84-808f-dc04cdfb4a88" />

### 4. 추천 관련

<img width="1354" height="745" alt="Image" src="https://github.com/user-attachments/assets/063b6427-a645-4014-941f-96ce64f31ca6" />

### 5. 어드민 통계

<img width="656" height="280" alt="Image" src="https://github.com/user-attachments/assets/7657e1b1-cec0-40b4-ba9b-d1666c5a397d" />

### 전체 erd 구성

<img width="1311" height="553" alt="Image" src="https://github.com/user-attachments/assets/d06cbaab-dc23-4cc2-9544-7b30ff0a040d" />

---

## 🙋‍♂️ Developer

| 사진 | 이름 | GitHub |
|------|------|--------|
| <img src="https://avatars.githubusercontent.com/u/127932430?v=4" width="80"> | 👑임동준 | [djlim00](https://github.com/djlim00) |
| <img src="https://avatars.githubusercontent.com/Leesowon" width="80"> | 이소원 | [Leesowon](https://github.com/Leesowon) |
| <img src="https://avatars.githubusercontent.com/etoile0626" width="80"> | 최윤제 | [etoile0626](https://github.com/etoile0626) |

