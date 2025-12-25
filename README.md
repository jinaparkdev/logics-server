# Logics Server

Spring Boot와 Kotlin 기반의 백엔드 API 서버입니다.

## 📚 기술 스택

### 코어
- **Language**: Kotlin 2.2.21
- **Runtime**: JDK 21 (Gradle Toolchain)
- **Framework**: Spring Boot 3.2.0
  - Spring Web
  - Spring Security
  - Spring Validation

### 데이터베이스
- **Database**: PostgreSQL 17 (Alpine)
- **ORM**: JetBrains Exposed 0.47.0
- **Migration**: Liquibase
- **Connection Pool**: HikariCP

### 빌드 & 개발
- **Build Tool**: Gradle 8.x (Kotlin DSL)
- **Dev Tools**: Spring Boot DevTools
- **Containerization**: Docker Compose

## 🚀 빠른 시작

### 사전 요구사항
- Docker Desktop 또는 Docker Engine (docker compose 지원)
- JDK 21 (프로젝트는 Gradle Toolchain을 통해 자동 다운로드)

### 1. 데이터베이스 시작
PostgreSQL 컨테이너를 백그라운드로 실행합니다:

```bash
docker compose -f docker/docker-compose.yml up -d
```

> **데이터베이스 접속 정보**
> - Host: `localhost:5432`
> - Database: `logics_db`
> - User: `postgres`
> - Password: `postgres`

### 2. 애플리케이션 실행
프로젝트 루트 디렉토리에서 다음 명령을 실행합니다:

```bash
./gradlew bootRun
```

애플리케이션은 `http://localhost:8080`에서 실행됩니다.

> Liquibase가 자동으로 데이터베이스 스키마를 마이그레이션합니다.

## 🛠️ 개발 가이드

### 프로젝트 구조
```
src/main/kotlin/com/dw/logics/
├── config/          # Spring 설정 클래스
├── controller/      # REST API 컨트롤러
├── domain/          # 도메인 모델
├── entity/          # Exposed 엔티티
├── service/         # 비즈니스 로직
└── utils/           # 유틸리티 함수
```

### 빌드 명령어
```bash
# 빌드
./gradlew build

# 테스트
./gradlew test

# JAR 파일 생성
./gradlew bootJar

# 의존성 확인
./gradlew dependencies
```

### 환경 설정
주요 설정은 `src/main/resources/application.yml`에서 관리되며, 환경변수로 오버라이드할 수 있습니다.

## 🧹 정리

### 컨테이너 종료
```bash
docker compose -f docker/docker-compose.yml down
```

### 데이터베이스 초기화
볼륨을 포함한 완전한 초기화가 필요한 경우:

```bash
docker compose -f docker/docker-compose.yml down -v
# 또는
docker volume rm logics_db_data
```

