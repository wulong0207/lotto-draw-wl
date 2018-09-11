package com.hhly.draw.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.hhly.draw.base.interceptor.ContextPathInterceptor;

/**
 * 拦截器配置
 *
 * @author huangchengfang1219
 * @date 2018年4月19日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Configuration
public class InterceptorConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		InterceptorRegistration registration = registry.addInterceptor(createContextPathInterceptor());
		registration.addPathPatterns("/**");
		registration.excludePathPatterns("/pc/common/resources/**");
		registration.excludePathPatterns("/h5/common/resources/**");
		registration.excludePathPatterns("/pc/resources/**");
		registration.excludePathPatterns("/h5/resources/**");
		registration.excludePathPatterns("/favicon.ico");
		registration.excludePathPatterns("/pc/favicon.ico");
		registration.excludePathPatterns("/h5/favicon.ico");
		super.addInterceptors(registry);
	}

	@Bean
	public ContextPathInterceptor createContextPathInterceptor() {
		return new ContextPathInterceptor();
	}
}
