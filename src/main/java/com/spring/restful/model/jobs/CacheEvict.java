package com.spring.restful.model.jobs;

import org.apache.log4j.Logger;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Scheduled;


public class CacheEvict {

    private final CacheManager cacheManager;
    private static final Logger logger = Logger.getLogger(CacheEvict.class);


    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("currencies");
    }

    public CacheEvict(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(cron = "0 0 0 ? * 1-7")
    public void evictAllcachesAtIntervals() {
        logger.debug("Cache evict starts");
        evictAllCacheValues("currencies");
        logger.debug("Cache evict finished");
    }

    public void evictAllCacheValues(String cacheName) {
        cacheManager.getCache(cacheName).clear();
    }
}



