# Spring 기반 로컬/분산 캐싱 전략 예제 프로젝트

이 프로젝트는 Spring Boot 환경에서 **로컬 캐시(Caffeine)**와 **분산 캐시(Redis)**를 활용하여
다양한 캐싱 전략을 직접 구현하고, 테스트하며 학습할 수 있도록 구성된 예제입니다.

---

## 1. 프로젝트 구조 및 주요 파일

- **config/CacheConfig.java**
  - Caffeine(로컬) 캐시와 Redis(분산) 캐시 설정을 담당
  - (주석 해제 시) 두 캐시 매니저를 동시에 등록 가능

- **controller/CategoryController.java**
  - `/categories` 엔드포인트 제공
  - `mode` 파라미터(`db`, `local`, `redis`)로 캐싱 전략 선택

- **service/CategoryService.java**
  - `getAllCategories()`: DB 직접 조회
  - `getAllCategoriesWithLocalCache()`: Caffeine 로컬 캐시 사용
  - `getAllCategoriesWithRedis()`: Redis 분산 캐시 사용

- **repository/CategoryRepository.java**
  - JPA 기반 데이터 접근 계층

- **domain/Category.java**
  - 카테고리 엔티티 정의

- **test/CategoryServiceCacheTest.java**
  - 각 캐싱 전략별 동작 및 효과를 검증하는 테스트 코드 포함
  - Mockito를 활용한 Repository 호출 횟수 검증 예제 포함

---

## 2. 캐싱 전략 및 동작 방식

- **로컬 캐시(Caffeine):**
  - JVM 내 메모리 캐시, 빠른 응답
  - `@Cacheable(cacheNames = "localCache")` 사용

- **분산 캐시(Redis):**
  - 여러 인스턴스 간 데이터 공유 가능
  - `@Cacheable(cacheNames = "redisCache")` 사용

- **전략 선택 방법:**
  - `/categories?mode=db` : DB 직접 조회
  - `/categories?mode=local` : 로컬 캐시 사용
  - `/categories?mode=redis` : Redis 캐시 사용

---

## 3. 실행 방법

### (1) 의존성
- 주요 의존성: `spring-boot-starter-data-jpa`, `spring-boot-starter-data-redis`, `spring-boot-starter-cache`, `caffeine`, `lombok`
- Redis 서버 필요 (로컬 또는 Docker)

### (2) 애플리케이션 빌드 및 실행
```bash
# 1. 애플리케이션 빌드
./gradlew clean build

# 2. Docker Compose로 전체 환경 실행 (PostgreSQL, Redis, Spring Boot 앱)
docker compose up
```

### (3) 개별 서비스 실행 (선택사항)
```bash
# Redis만 실행
docker run --name redis -p 6379:6379 -d redis

# PostgreSQL만 실행
docker run --name postgres -e POSTGRES_DB=dvdrental -e POSTGRES_USER=rlawnsdud05 -e POSTGRES_PASSWORD=Rlawnsdud1@ -p 5432:5432 -d postgres:15
```

---

## 4. 테스트 및 학습 포인트

- **테스트 코드:**  
  `CategoryServiceCacheTest`에서 각 캐싱 전략별로 2회 호출 시 DB 접근이 1회만 발생하는지 검증
- **Mockito 활용:**  
  Repository의 실제 호출 횟수를 검증하여 캐시 효과를 명확히 확인
- **로그/응답속도:**  
  첫 번째 호출만 DB 접근 로그가 출력되고, 두 번째 호출은 캐시에서 반환됨을 확인

---

## 5. 참고 및 확장

- Spring Boot, Spring Data JPA, Caffeine, Redis 등 실무에서 널리 쓰이는 기술 스택 기반
- 다양한 TTL, 캐시 키 전략, 동시성 실험 등으로 확장 가능
- Spring Actuator, Redis CLI 등으로 캐시 상태 실시간 모니터링 가능

---

**이 프로젝트는 Spring 기반 캐싱 전략을 실습하고 싶은 모든 분께 추천합니다!**
궁금한 점이나 추가 실습 아이디어가 있다면 언제든 문의해 주세요.