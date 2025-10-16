# Crypto Market Server

가상화폐 거래소 백엔드 서버 프로젝트

## 목차
- [프로젝트 소개](#프로젝트-소개)
- [주요 기능](#주요-기능)
- [기술 스택](#기술-스택)
- [시스템 요구사항](#시스템-요구사항)
- [프로젝트 구조](#프로젝트-구조)
- [설치 및 실행](#설치-및-실행)
- [데이터베이스 설정](#데이터베이스-설정)
- [API 문서](#api-문서)
- [테스트](#테스트)
- [주요 비즈니스 로직](#주요-비즈니스-로직)

## 프로젝트 소개

Crypto Market Server는 가상화폐 거래를 위한 백엔드 API 서버입니다. 사용자는 다양한 가상화폐를 검색하고, 매수/매도 주문을 생성하고, 주문 내역을 조회할 수 있습니다.

## 주요 기능

### 1. 코인 검색 (Coin Search)
- 키워드 기반 코인 검색
- 한글 초성 검색 지원 (예: "ㅂ" 검색 시 "비트코인" 검색)
- 카테고리별 필터링 (KRW, USDT, BTC)

### 2. 주문 관리 (Order Management)
- **주문 생성**: 시장가/지정가 매수/매도 주문
- **주문 조회**: 날짜 범위 및 상태별 필터링, 페이지네이션 지원
- **주문 수정**: 체결 대기 중인 주문의 가격/수량 수정
- **주문 취소**: 체결 대기 중인 주문 취소

### 3. 주문 체결 스케줄러
- 주문 상태 자동 업데이트 (PENDING → PARTIAL_FILLED → FILLED)

## 기술 스택

### Backend
- **Java 21**
- **Spring Boot 3.3.4**
  - Spring Web
  - Spring Data JPA
  - Spring Validation
- **Lombok**: 보일러플레이트 코드 감소
- **Springdoc OpenAPI 2.5.0**: API 문서 자동 생성 (Swagger UI)

### Database
- **MySQL 8.x**
- **MySQL Connector/J 8.1.0**

### Testing
- **JUnit 5.10.3**: 단위 테스트 프레임워크
- **Mockito 5.12.0**: Mocking 프레임워크
- **Spring Boot Test**: 통합 테스트 지원

### Build Tool
- **Gradle 8.x**

## 시스템 요구사항

- Java 21 이상
- MySQL 8.x
- Gradle 8.x (wrapper 포함)

## 프로젝트 구조

```
src/
├── main/
│   ├── java/com/cyptomarket/server/
│   │   ├── config/              # 설정 파일 (Swagger 등)
│   │   ├── controller/          # REST API 컨트롤러
│   │   │   ├── CoinSearchController.java
│   │   │   └── OrderController.java
│   │   ├── dto/                 # 데이터 전송 객체
│   │   │   ├── CoinSearchRequestV1.java
│   │   │   ├── CoinSearchResponseV1.java
│   │   │   ├── OrderRequestV1.java
│   │   │   ├── OrderResponseV1.java
│   │   │   ├── OrderHistoryV1.java
│   │   │   ├── OrderQueryRequestV1.java
│   │   │   └── ErrorResponse.java
│   │   ├── entity/              # JPA 엔티티
│   │   │   ├── User.java
│   │   │   ├── Symbol.java
│   │   │   ├── Order.java
│   │   │   ├── enums/           # Enum 타입
│   │   │   └── commons/         # 공통 엔티티
│   │   ├── repository/          # JPA 리포지토리
│   │   │   ├── UserRepository.java
│   │   │   ├── SymbolRepository.java
│   │   │   └── OrderRepository.java
│   │   ├── service/             # 비즈니스 로직
│   │   │   ├── CoinSearchService.java
│   │   │   └── OrderService.java
│   │   ├── handler/             # 예외 핸들러
│   │   │   └── GlobalExceptionHandler.java
│   │   └── ServerApplication.java
│   └── resources/
│       └── application.yml      # 애플리케이션 설정
└── test/
    └── java/com/cyptomarket/server/
        ├── controller/          # 컨트롤러 단위 테스트
        └── service/             # 서비스 단위 테스트
```

## 설치 및 실행

### 1. 프로젝트 클론

```bash
git clone <repository-url>
cd crypto-market-server
```


### 2. 애플리케이션 설정

`src/main/resources/application.yml` 파일에서 데이터베이스 연결 정보를 확인/수정합니다.


### 3. 빌드 및 실행

```bash
# Gradle 빌드
./gradlew build

# 애플리케이션 실행
./gradlew bootRun
```

또는

```bash
# JAR 파일로 실행
java -jar build/libs/server-0.0.1-SNAPSHOT.jar
```

서버는 기본적으로 `http://localhost:8080`에서 실행됩니다.

## 데이터베이스 설정

### ERD (Entity Relationship Diagram)

```
┌─────────────┐
│    User     │
├─────────────┤
│ id (PK)     │
│ email       │
│ password    │
│ balance     │
│ created_at  │
│ updated_at  │
└──────┬──────┘
       │
       │ 1:N
       │
┌──────▼──────┐      ┌─────────────┐
│    Order    │ N:1  │   Symbol    │
├─────────────┤◄─────┤─────────────┤
│ id (PK)     │      │ id (PK)     │
│ user_id(FK) │      │ symbol      │
│ symbol_id   │      │ base_coin   │
│ order_type  │      │ quote_coin  │
│ order_state │      │ created_at  │
│ price       │      │ updated_at  │
│ quantity    │      └─────────────┘
│ executed_qty│
│ status      │
│ created_at  │
│ updated_at  │
└─────────────┘
       │
       │ N:1
       │
┌──────▼──────┐
│    Trade    │
├─────────────┤
│ id (PK)     │
│ buy_order_id│
│ sell_order  │
│ symbol_id   │
│ price       │
│ quantity    │
│ executed_at │
└─────────────┘
```

### 주요 테이블

- **user**: 사용자 정보 및 잔고 관리
- **symbol**: 거래 가능한 코인 심볼 정보 (BTC/KRW, ETH/USDT 등)
- **order**: 사용자 주문 정보 (매수/매도, 시장가/지정가)
- **trade**: 체결된 거래 내역

## API 문서

애플리케이션 실행 후 Swagger UI를 통해 API 문서를 확인할 수 있습니다.

**Swagger UI**: `http://localhost:8080/swagger-ui.html`

### 주요 API 엔드포인트

#### 코인 검색 API

```
POST /v1/api/coins/search
```

**Request Body:**
```json
{
  "keyword": "Bitcoin",
  "category": "KRW"
}
```

**Response:**
```json
[
  {
    "name": "Bitcoin",
    "symbol": "BTC/KRW",
    "price": 50000000.0,
    "category": "KRW"
  }
]
```

#### 주문 내역 조회 API

```
GET /v1/api/orders
```

**Request Body:**
```json
{
  "startDate": "2024-01-01",
  "endDate": "2024-12-31",
  "orderState": "BUY",
  "page": 0,
  "size": 50
}
```

## 테스트

### 단위 테스트 실행

```bash
# 모든 테스트 실행
./gradlew test

# 특정 테스트 클래스 실행
./gradlew test --tests CoinSearchServiceUnitTest

# 테스트 리포트 확인
open build/reports/tests/test/index.html
```

### 테스트 커버리지

프로젝트는 다음과 같은 단위 테스트를 포함합니다:

- **Controller Layer**
  - `CoinSearchControllerUnitTest`: 코인 검색 API 테스트
  - `OrderControllerUnitTest`: 주문 관리 API 테스트

- **Service Layer**
  - `CoinSearchServiceUnitTest`: 코인 검색 비즈니스 로직 테스트
    - 키워드 검색
    - 초성 검색
    - 경계값 테스트 (null, 빈 문자열, 공백)
    - 예외 상황 테스트 (Repository 예외, 대소문자 검색)
  - `OrderServiceUnitTest`: 주문 관리 비즈니스 로직 테스트

## 주요 비즈니스 로직

### 1. 코인 검색

- **초성 검색**: 한글 초성 패턴(`^[ㄱ-ㅎ]+$`)을 감지하여 자동으로 초성 검색 수행
- **카테고리 필터링**: KRW, USDT, BTC 등 거래 쌍 기준 필터링

### 2. 주문 생성

- **잔고 검증**: 주문 생성 시 사용자 잔고 확인
- **최소/최대 금액 검증**:
  - KRW: 5,000 ~ 1,000,000,000
  - USDT: 5 ~ 1,000,000
  - BTC: 0.00005 ~ 10
- **주문 타입**: LIMIT (지정가), MARKET (시장가)
- **주문 상태**: BUY (매수), SELL (매도)

### 3. 주문 상태 관리

주문은 다음과 같은 상태를 가집니다:

- **PENDING**: 체결 대기 중
- **PARTIAL_FILLED**: 부분 체결
- **FILLED**: 전체 체결 완료
- **CANCELED**: 주문 취소됨

### 4. 주문 체결 스케줄러

- 주기적으로 체결 대기 중인 주문을 확인하고 상태 업데이트
- `OrderService.updateOrderStatuses()` 메서드를 통해 실행

### 5. 보안 및 권한

- 주문 수정/취소 시 주문 소유자 확인
- 체결 대기 중인 주문만 수정/취소 가능
- 사용자 인증 (현재는 Mock userId 사용, 추후 Spring Security 연동 예정)

## 개발 참고사항

### 1. 향후 개발 계획

- [ ] Spring Security 통합 (JWT 인증)
- [ ] 실시간 시세 연동
- [ ] WebSocket을 통한 실시간 주문 체결 알림
- [ ] Redis를 활용한 캐싱
- [ ] 거래 수수료 계산
- [ ] 사용자 자산 관리 (보유 코인, 입출금)
- [ ] Admin API (주문 관리, 사용자 관리)

## 라이선스

이 프로젝트는 개인 학습 및 포트폴리오 목적으로 개발되었습니다.

## 문의

프로젝트에 대한 문의사항이나 버그 리포트는 GitHub Issues를 통해 남겨주세요.
