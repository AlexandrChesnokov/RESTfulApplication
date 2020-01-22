package com.howtodoinjava.example.async;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
@EnableCaching
public class SpringBootAsyncApplication {

	private static final Logger logger = Logger.getLogger(SpringBootAsyncApplication.class);

	public static void main(String[] args) {
		logger.debug("Запуск приложения");
		SpringApplication.run(SpringBootAsyncApplication.class, args);
	}

	@Bean
	public CacheManager cacheManager() {
		return new ConcurrentMapCacheManager("currencies");
	}
}
