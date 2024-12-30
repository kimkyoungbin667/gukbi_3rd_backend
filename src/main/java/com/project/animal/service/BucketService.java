package com.project.animal.service;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BucketService {

    private final Map<String, Bucket> userBuckets = new ConcurrentHashMap<>();

    public Bucket getBucketForUser(String userKey) {
        return userBuckets.computeIfAbsent(userKey, k -> createBucket());
    }

    private Bucket createBucket() {
        
        // 토큰은 10초당 1개씩 충전
        Refill refill = Refill.intervally(1, Duration.ofSeconds(10));
        
        // 최대 버킷수는 1개로 설정
        Bandwidth limit = Bandwidth.classic(1, refill);
        return Bucket.builder().addLimit(limit).build();
    }
}
