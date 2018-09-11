package com.hhly.draw.config;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.view.freemarker.FreeMarkerViewResolver;

import com.hhly.draw.base.freemarker.NumberTemplateMethod;
import com.hhly.draw.base.freemarker.URLEncoderTemplateMethod;
import com.hhly.draw.base.freemarker.WeekTemplateMethod;

import freemarker.template.TemplateModelException;

@Configuration
public class FreeMarkerConfig {

	@Autowired
	private freemarker.template.Configuration configuration;
	@SuppressWarnings("unused")
	@Autowired
	private FreeMarkerViewResolver resolver;
	@Autowired
	private NumberTemplateMethod numberTemplateMethod;
	@Autowired
	private WeekTemplateMethod weekTemplateMethod;
	@Autowired
	private URLEncoderTemplateMethod urlEncoderTemplateMethod;
	@Value("${web_cp_url}")
	private String webCpUrl;
	@Value("${wap_cp_url}")
	private String wapCpUrl;
	@Value("${web_kj_url}")
	private String webKjUrl;
	@Value("${wap_kj_url}")
	private String wapKjUrl;
	@Value("${ybf_live_url}")
	private String ybfLiveUrl;
	@Value("${ybf_odds_url}")
	private String ybfOddsUrl;
	@Value("${ybf_data_url}")
	private String ybfDataUrl;

	@PostConstruct
	public void setSharedVariable() throws TemplateModelException {
		// 自定义变量
		configuration.setSharedVariable("webCpUrl", webCpUrl);
		configuration.setSharedVariable("wapCpUrl", wapCpUrl);
		configuration.setSharedVariable("webKjUrl", webKjUrl);
		configuration.setSharedVariable("wapKjUrl", wapKjUrl);
		configuration.setSharedVariable("ybfLiveUrl", ybfLiveUrl);
		configuration.setSharedVariable("ybfOddsUrl", ybfOddsUrl);
		configuration.setSharedVariable("ybfDataUrl", ybfDataUrl);
		// 自定义方法
		configuration.setSharedVariable("numberformat", numberTemplateMethod);
		configuration.setSharedVariable("week", weekTemplateMethod);
		configuration.setSharedVariable("urlEncoder", urlEncoderTemplateMethod);

	}

}
