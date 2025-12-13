# [주니어 개발자 교육용] Spring Boot 캐싱 전략 예제 프로젝트

안녕하세요, 주니어 백엔드 개발자 여러분! 👋

이 프로젝트는 Spring Boot 환경에서 **캐싱(Caching)**의 개념을 쉽고 재미있게 학습할 수 있도록 설계된 예제 프로젝트입니다. 직접 코드를 실행하고, 로그를 확인하며 캐시가 어떻게 동작하는지, 왜 필요한지를 몸으로 체득할 수 있습니다.

---

## 🎯 학습 목표

이 프로젝트를 통해 여러분은 다음을 배울 수 있습니다.

1.  **캐시의 필요성 이해**: 캐시가 없을 때와 있을 때의 성능 차이를 직접 확인하고, 캐시가 왜 중요한지 이해합니다.
2.  **다양한 캐시 종류 학습**:
    *   **로컬 캐시 (Local Cache)**: 내장 메모리 기반의 `Caffeine` 캐시를 사용해보고, 장단점을 이해합니다.
    *   **분산 캐시 (Distributed Cache)**: `Redis`를 활용하여 여러 서버 환경에서도 동작하는 분산 캐시를 경험합니다.
3.  **핵심 캐싱 전략 습득**:
    *   **Cache-Aside (Lazy Loading)**: 가장 보편적인 캐시 조회 전략을 이해합니다.
    *   **Write-Through**: 데이터베이스와 캐시의 데이터를 항상 일치시키는 쓰기 전략을 학습합니다.
    *   **Write-Around**: 데이터베이스를 먼저 업데이트하고, 캐시는 다음 조회 시점에 갱신하는 전략을 배웁니다.
    *   **Cache Eviction**: 데이터 변경 시 캐시를 어떻게 지워야 하는지(`@CacheEvict`) 알아봅니다.

---

## 🛠️ 기술 스택

*   **Language**: Java 17
*   **Framework**: Spring Boot
*   **Database**: H2 (메모리 DB), PostgreSQL (Docker)
*   **Cache**: Caffeine (로컬 캐시), Redis (분산 캐시)
*   **Build**: Gradle

---

## 🚀 프로젝트 실행 방법

### 1. 사전 준비

*   **Java 17** 설치
*   **Docker** 설치 (Redis와 PostgreSQL 실행을 위해 필요)

### 2. 프로젝트 빌드

아래 명령어를 실행하여 프로젝트를 빌드합니다.

```bash
./gradlew clean build
```

### 3. Docker Compose로 실행 (권장)

가장 간단하게 프로젝트 환경(Spring Boot 애플리케이션, PostgreSQL, Redis)을 한 번에 실행하는 방법입니다.

```bash
docker-compose up
```

이제 애플리케이션이 실행되었으며, API를 테스트할 준비가 되었습니다!

---

## 📚 단계별 캐싱 전략 학습하기

이 프로젝트는 `level0`부터 `level2`까지, 단계별로 캐싱 전략을 학습할 수 있도록 구성되어 있습니다. 각 단계의 코드를 비교하며 어떤 점이 달라졌는지 확인해보세요!

### 🐌 Level 0: 캐시가 없는 기본 상태

**경로**: `src/main/java/com/example/cachingexamples/level0`

`BasicProductService`는 캐시를 전혀 사용하지 않고, 모든 요청을 데이터베이스에서 직접 처리합니다.

```java
// level0/BasicProductService.java

@Transactional(readOnly = true)
public ProductResponse getProduct(Long id) {
    // DB에서 직접 상품 조회
    Product product = productRepository.findById(id).orElseThrow();
    return ProductResponse.from(product);
}
```

**🤔 생각해보기**: 같은 상품을 여러 번 조회하면 어떤 일이 발생할까요? `BasicProductController`의 조회 API를 여러 번 호출하며 응답 시간과 로그를 확인해보세요.

### 🐣 Level 1: 기본적인 캐시 적용 (Cache-Aside)

**경로**: `src/main/java/com/example/cachingexamples/level1`

`CacheProductService`에서는 Spring의 `@Cacheable` 어노테이션을 사용하여 간단하게 캐시를 적용합니다.

*   `@Cacheable(value = "products", key = "#id")`:
    *   `products`라는 이름의 캐시 저장소에
    *   메서드의 파라미터인 `id`를 `key`로 사용하여
    *   메서드의 반환값(`ProductResponse`)을 **캐싱**합니다.
    *   **동작 방식**:
        1.  메서드 호출 시, 먼저 캐시에 `key`에 해당하는 데이터가 있는지 확인합니다.
        2.  **캐시에 데이터가 있으면 (Cache Hit)**: DB를 조회하지 않고, 즉시 캐시 데이터를 반환합니다.
        3.  **캐시에 데이터가 없으면 (Cache Miss)**:
            1.  기존 로직대로 DB에서 데이터를 조회합니다.
            2.  조회된 데이터를 캐시에 저장합니다.
            3.  결과를 반환합니다.

```java
// level1/CacheProductService.java

@Cacheable(value = "products", key = "#id")
@Transactional(readOnly = true)
public ProductResponse getProduct(Long id) {
    // 첫 호출 시에만 실행됨 (Cache Miss)
    Product product = productRepository.findById(id).orElseThrow();
    return ProductResponse.from(product);
}
```

#### 데이터 변경 시 캐시 삭제 (`@CacheEvict`)

상품 정보가 변경되면 캐시에 저장된 기존 데이터는 더 이상 유효하지 않습니다. `@CacheEvict`를 사용하여 데이터 변경 시 캐시를 삭제해줍니다.

```java
// level1/CacheProductService.java

@CacheEvict(value = "products", key = "#id")
@Transactional
public ProductResponse updateProduct(Long id, ProductUpdateRequest request) {
    // DB 업데이트
    Product product = productRepository.findById(id).orElseThrow();
    product.update(request.name(), request.price());
    Product saved = productRepository.save(product);

    // 'products' 캐시에서 해당 id의 데이터를 삭제
    return ProductResponse.from(saved);
}
```

### 🚀 Level 2: 다양한 쓰기 전략 (Write Strategies)

**경로**: `src/main/java/com/example/cachingexamples/level2`

`WriteStrategyService`에서는 데이터 변경 시 캐시를 어떻게 처리할지에 대한 다양한 '쓰기 전략'을 보여줍니다.

#### 1. Write-Through: DB와 캐시를 함께 업데이트 (`@CachePut`)

`@CachePut`은 `@Cacheable`과 달리 **메서드를 항상 실행**하고, 그 결과를 캐시에 **업데이트(저장)**합니다.

*   **장점**: 캐시와 DB의 데이터 정합성을 높은 수준으로 유지할 수 있습니다.
*   **단점**: 데이터를 업데이트할 때마다 캐시에도 쓰기 작업이 발생하여, 쓰기 성능이 약간 저하될 수 있습니다.

```java
// level2/WriteStrategyService.java

@CachePut(value = "products", key = "#id")
@Transactional
public ProductResponse updateWithWriteThrough(Long id, ProductUpdateRequest request) {
    // 1. DB 업데이트
    Product product = productRepository.findById(id).orElseThrow();
    product.update(request.name(), request.price());
    Product saved = productRepository.save(product);

    // 2. 메서드 반환값을 캐시에 업데이트
    return ProductResponse.from(saved);
}
```

#### 2. Write-Around: DB만 업데이트하고 캐시는 삭제 (가장 일반적인 전략)

`Level 1`에서 사용한 `@CacheEvict`와 동일한 방식입니다. DB만 우선 업데이트하고, 캐시는 지워버립니다. 다음에 해당 데이터 조회가 들어오면 `Cache Miss`가 발생하여 DB에서 최신 데이터를 읽어와 캐시를 갱신하게 됩니다.

*   **장점**: 쓰기 성능이 빠르고, 구현이 간단합니다.
*   **단점**: 캐시가 삭제된 후 다음 조회까지 캐시가 비어있게 됩니다.

```java
// level2/WriteStrategyService.java

@CacheEvict(value = "products", key = "#id")
@Transactional
public ProductResponse updateWithWriteAround(Long id, ProductUpdateRequest request) {
    // DB 업데이트 후 캐시는 삭제
    ...
}
```

---

## ⚙️ 로컬 캐시 vs 분산 캐시切换

`application.yml` 파일에서 `spring.profiles.active` 값을 변경하여 로컬 캐시와 분산 캐시를 손쉽게 전환하며 테스트할 수 있습니다.

*   **로컬 캐시 (Caffeine) 사용**: `local`
*   **분산 캐시 (Redis) 사용**: `redis`

```yaml
# application.yml
spring:
  profiles:
    active: local # 또는 redis
```

`LocalCacheConfig.java`와 `RedisCacheConfig.java` 파일에서 각 캐시의 상세 설정을 확인할 수 있습니다.

---

## 🎓 핵심 정리

*   **캐시는 성능 향상의 핵심 열쇠입니다.** 반복적인 DB 조회를 줄여 애플리케이션의 응답 속도를 크게 개선할 수 있습니다.
*   **상황에 맞는 캐시를 선택해야 합니다.** 단일 서버 환경이라면 `Caffeine` 같은 로컬 캐시로도 충분하지만, 여러 서버를 운영하는 분산 환경이라면 `Redis` 같은 분산 캐시가 필수적입니다.
*   **데이터 정합성이 중요합니다.** 데이터가 변경되었을 때 캐시를 어떻게 처리할지(`@CacheEvict`, `@CachePut`) 항상 고민해야 합니다.

이 프로젝트를 통해 캐싱에 대한 자신감을 얻고, 실제 프로젝트에 자신있게 적용해보시길 바랍니다! 😊
