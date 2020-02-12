package com.spring.restful.model;

import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;


public class CacheEvict {

    private final CacheManager cacheManager;

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("currencies");
    }

    public CacheEvict(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "0 0 0 ? * 1-7")
    public void evictAllcachesAtIntervals() {
        evictAllCacheValues("currencies");
    }

    public void evictAllCacheValues(String cacheName) {
        cacheManager.getCache(cacheName).clear();
    }
}



