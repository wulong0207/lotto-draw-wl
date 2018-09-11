package com.hhly.draw.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * 静态资源配置
 *
 * @author huangchengfang1219
 * @date 2018年4月19日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Configuration
public class StaticResourceConfig extends WebMvcConfigurerAdapter {

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/favicon.ico").addResourceLocations("classpath:/statics/common/images/favicon.ico");
		registry.addResourceHandler("/pc/common/resources/**").addResourceLocations("classpath:/statics/common/");
		registry.addResourceHandler("/h5/common/resources/**").addResourceLocations("classpath:/statics/common/");
		registry.addResourceHandler("/pc/resources/**").addResourceLocations("classpath:/statics/pc/");
		registry.addResourceHandler("/h5/resources/**").addResourceLocations("classpath:/statics/h5/");
		super.addResourceHandlers(registry);
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/pc/").setViewName("forward:/pc/index/");
		registry.addViewController("/h5/").setViewName("forward:/h5/index/");
		registry.addViewController("/pc/favicon.ico").setViewName("forward:/favicon.ico");
		registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
		super.addViewControllers(registry);
	}
	
	

}
