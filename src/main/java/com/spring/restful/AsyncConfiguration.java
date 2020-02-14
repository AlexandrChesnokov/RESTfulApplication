package com.spring.restful;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableAsync
@EnableWebMvc
@EnableScheduling
@EnableCaching

public class AsyncConfiguration implements WebMvcConfigurer {


	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
		configurer.favorPathExtension(true).
				favorParameter(false).
				parameterName("mediaType").
				ignoreAcceptHeader(true).
				defaultContentType(MediaType.APPLICATION_JSON).
				mediaType("xml", MediaType.APPLICATION_XML).
				mediaType("json", MediaType.APPLICATION_JSON);
	}
}

