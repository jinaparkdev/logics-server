# Logics Server

## 기술 스택
- JDK 21 (Gradle toolchain)
- Kotlin 2.2.21
- Spring Boot 3.2.0 
- ORM: JetBrains Exposed 
- DB 마이그레이션: Liquibase
- 데이터베이스: PostgreSQL 17 

## 사전 준비물
- `docker compose` 명령을 사용할 수 있는 Docker Desktop 혹은 Docker Engine
- 프로젝트의 Gradle toolchain과 동일한 JDK 21
- 실행 권한이 설정된 Gradle Wrapper(`./gradlew`)

## 로컬 개발 워크플로
1. **PostgreSQL 컨테이너 기동**
   ```bash
   cd docker
   docker compose up -d db
   ```
   컨테이너는 `localhost:5432`를 개방하며, 데이터베이스/계정 정보는 `src/main/resources/application.yml`에 정의된 `logics_db` / `postgres` / `postgres`를 그대로 사용합니다.

2. **Spring Boot 서버 실행**
   ```bash
   cd ..
   ./gradlew bootRun
   ```
   Gradle이 Kotlin 코드를 빌드하고 Liquibase 마이그레이션을 적용한 뒤 기본 포트(`8080`)에서 애플리케이션을 구동합니다.

3. **작업 종료 후 컨테이너 정리**
   ```bash
   cd docker
   docker compose down
   ```

> 완전히 초기화하고 싶다면 `docker volume rm logics_db_data`로 볼륨을 제거한 뒤 다시 1단계를 수행하세요.
