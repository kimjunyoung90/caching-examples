package com.example.cachingexamples.level2;

import com.example.cachingexamples.common.dto.ProductResponse;
import com.example.cachingexamples.common.dto.ProductUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/level2/write-strategy")
@RequiredArgsConstructor
@Slf4j
public class WriteStrategyController {
    private final WriteStrategyService writeStrategyService;

    /**
     * Scenario 1: Write-Through 테스트
     */
    @PostMapping("/scenario1-write-through")
    public Map<String, Object> scenario1WriteThrough(@RequestParam Long productId) {
        Map<String, Object> result = new HashMap<>();

        log.info("=== [Level 2] Scenario 1: Write-Through 시작 ===");

        // 1. 초기 조회 (캐시 저장)
        long start1 = System.currentTimeMillis();
        ProductResponse product1 = writeStrategyService.getProduct(productId);
        long time1 = System.currentTimeMillis() - start1;
        result.put("1. 초기 조회 시간(ms)", time1);
        result.put("1. 초기 데이터", Map.of("name", product1.name(), "price", product1.price()));

        // 2. 재조회 (캐시 히트)
        long start2 = System.currentTimeMillis();
        writeStrategyService.getProduct(productId);
        long time2 = System.currentTimeMillis() - start2;
        result.put("2. 재조회 시간(ms)", time2);
        result.put("2. 결과", "캐시 히트 (빠름)");

        // 3. Write-Through 업데이트
        ProductUpdateRequest updateReq = new ProductUpdateRequest("Write-Through 상품", 99999);
        long start3 = System.currentTimeMillis();
        ProductResponse updated = writeStrategyService.updateWithWriteThrough(productId, updateReq);
        long time3 = System.currentTimeMillis() - start3;
        result.put("3. Write-Through 업데이트 시간(ms)", time3);
        result.put("3. 업데이트 데이터", Map.of("name", updated.name(), "price", updated.price()));

        // 4. 업데이트 후 조회 (캐시 히트, 최신 데이터)
        long start4 = System.currentTimeMillis();
        ProductResponse product4 = writeStrategyService.getProduct(productId);
        long time4 = System.currentTimeMillis() - start4;
        result.put("4. 업데이트 후 조회 시간(ms)", time4);
        result.put("4. 조회 데이터", Map.of("name", product4.name(), "price", product4.price()));
        result.put("4. 결과", "✅ 캐시 히트 (빠름) + 최신 데이터 (캐시 갱신됨)");

        log.info("=== [Level 2] Scenario 1 완료 ===");
        return result;
    }

    /**
     * Scenario 2: Write-Around + Evict 테스트
     */
    @PostMapping("/scenario2-evict")
    public Map<String, Object> scenario2Evict(@RequestParam Long productId) {
        Map<String, Object> result = new HashMap<>();

        log.info("=== [Level 2] Scenario 2: Write-Around + Evict 시작 ===");

        // 1. 초기 조회
        ProductResponse product1 = writeStrategyService.getProduct(productId);
        result.put("1. 초기 데이터", Map.of("name", product1.name(), "price", product1.price()));

        // 2. Write-Around + Evict 업데이트
        ProductUpdateRequest updateReq = new ProductUpdateRequest("Evict 상품", 88888);
        long start2 = System.currentTimeMillis();
        ProductResponse updated = writeStrategyService.updateWithEvict(productId, updateReq);
        long time2 = System.currentTimeMillis() - start2;
        result.put("2. 업데이트 시간(ms)", time2);
        result.put("2. 업데이트 데이터", Map.of("name", updated.name(), "price", updated.price()));

        // 3. 업데이트 후 조회 (캐시 미스, DB 조회)
        long start3 = System.currentTimeMillis();
        ProductResponse product3 = writeStrategyService.getProduct(productId);
        long time3 = System.currentTimeMillis() - start3;
        result.put("3. 업데이트 후 조회 시간(ms)", time3);
        result.put("3. 조회 데이터", Map.of("name", product3.name(), "price", product3.price()));
        result.put("3. 결과", "✅ 캐시 미스 (느림, 캐시 삭제됨) → DB 조회 → 최신 데이터 + 재캐싱");

        // 4. 재조회 (캐시 히트)
        long start4 = System.currentTimeMillis();
        writeStrategyService.getProduct(productId);
        long time4 = System.currentTimeMillis() - start4;
        result.put("4. 재조회 시간(ms)", time4);
        result.put("4. 결과", "✅ 캐시 히트 (빠름)");

        log.info("=== [Level 2] Scenario 2 완료 ===");
        return result;
    }

    /**
     * Scenario 3: Write-Around (순수) - 문제 발생!
     */
    @PostMapping("/scenario3-problem")
    public Map<String, Object> scenario3Problem(@RequestParam Long productId) {
        Map<String, Object> result = new HashMap<>();

        log.info("=== [Level 2] Scenario 3: Write-Around (순수) - 문제 상황! ===");

        // 1. 초기 조회 (캐시 저장)
        ProductResponse product1 = writeStrategyService.getProduct(productId);
        result.put("1. 초기 캐시 데이터", Map.of("name", product1.name(), "price", product1.price()));

        // 2. Write-Around (순수) - 캐시 무시!
        ProductUpdateRequest updateReq = new ProductUpdateRequest("문제 상품", 77777);
        ProductResponse updated = writeStrategyService.updateWithoutCache(productId, updateReq);
        result.put("2. DB 업데이트 데이터", Map.of("name", updated.name(), "price", updated.price()));

        // 3. 조회 (캐시 히트 - 오래된 데이터!)
        ProductResponse product3 = writeStrategyService.getProduct(productId);
        result.put("3. 조회된 캐시 데이터", Map.of("name", product3.name(), "price", product3.price()));

        // 4. 결과 분석
        boolean isDataMatch = product3.price().equals(updated.price());
        result.put("4. 데이터 일치 여부", isDataMatch);
        result.put("4. 문제", "❌ 캐시는 오래된 데이터 (가격: " + product1.price() +
                "), DB는 새 데이터 (가격: " + updated.price() + ")");
        result.put("⚠️ 결론", "Write-Around 순수 방식은 캐시-DB 불일치 발생! 반드시 Evict 또는 Put 필요!");

        log.warn("=== [Level 2] Scenario 3 완료 - 문제 확인됨! ===");
        return result;
    }
}
