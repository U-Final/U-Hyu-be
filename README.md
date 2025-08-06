# 🗺️ U-Hyu : 지도 기반 LG U+ 멤버십 제휴처 안내 및 추천 서비스

## 📍 프로젝트 소개

> U-HYU(유휴)는 사용자 주변의 LG U+ 멤버십 제휴처를 **지도 기반 UX**로 직관적으로 탐색하고, 다른 사용자의 이용 내역까지 함께 확인할 수 있는 **위치 기반 혜택 공유 플랫폼**입니다.  
> 개인화 추천 및 즐겨찾기, 소셜 공유를 통해 **유휴 혜택 자원의 재발견**을 유도합니다.

### 개발 기간
- 2025.07.05 ~ 2025.08.08

### 🧑🏻‍💻 Backend 팀원 소개

| <img src="https://avatars.githubusercontent.com/u/127932430?v=4" width="80"><br><a href="https://github.com/djlim00">👑 임동준</a> | <img src="https://avatars.githubusercontent.com/Leesowon" width="80"><br><a href="https://github.com/Leesowon">이소원</a> | <img src="https://avatars.githubusercontent.com/etoile0626" width="80"><br><a href="https://github.com/etoile0626">최윤제</a> |
|-----------------------------------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------|

### 🔗 배포 링크

https://www.u-hyu.site/

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

## 🚀 핵심 / 주요 기능

저희 팀 핵심 기능 및 ~는 위키에서 확인할 수 있습니다!

1. 추천 시스템 : [[위키 링크]]
2. postgis
3. 관리자 통계 


---

## 🔧 기술스택

<img width="1256" height="1408" alt="image" src="https://github.com/user-attachments/assets/1bdf5490-8f6d-4105-9e2e-95e136a80636" />

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

### 🧩 데이터 모델링 및 추천 시스템 설계 고민

#### ✅ 문제 상황

1. **방문 정보의 이중 관리**  
   - 사용자 온보딩 과정에서 **방문한 브랜드** 정보를 수집하고,
   - 이후 앱 이용 중에는 **실제 매장 방문 기록**이 저장됨  
   → 결국 두 데이터 모두 방문 정보지만, `recommendation_base_data`와 `history`라는 **서로 다른 테이블**에 저장되고 있음

2. **데이터 복사 전략의 한계**  
   - 추천 배치마다 `history` 데이터를 `recommendation_base_data`로 **복사**하는 방안도 고려했으나  
     → **데이터 이중화** 및 **정합성 문제** 발생  
     → 유지보수 및 데이터 관리에 불리

#### 🔍 최종 설계 결정

- **관심 브랜드**와 **방문 브랜드/매장 정보**를 **명확히 분리된 테이블**에 저장
- 온보딩에서 입력받은 **방문 브랜드**에 대해서 `store_id = null`로 저장되지만, 초반에 최대 3개의 정보만 받아 수용 가능하다고 판단
- `recommendation_base_data`는 **관심 브랜드 (DataType = INTEREST)** 저장
- 테이블 명은 **추천 기반 데이터의 확장 가능성**을 고려하여 `recommendation_base_data` 그대로 유지

#### 🧐 기타 고려 사항

- **관심 브랜드를 `users` 테이블에 직접 저장하지 않음**  
  → 추천 전용 도메인으로 분리하여 관리하는 것이 유지보수 및 확장성 측면에서 바람직
- 추천 로직은  
  - 초기 추천: `recommendation_base_data` 사용  

---

#
