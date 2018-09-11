package com.hhly.draw.base.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

import com.hhly.draw.base.common.CpLotteryUrlConstants;

public class BasePcController extends BaseController {
	
	@Autowired
	protected RestTemplate restTemplate;
	@Value("${draw_core_url}")
	protected String drawCoreUrl;

	@ModelAttribute("cpLotteryMap")
	public Map<String, String> bindCpLotteryUrlMap() {
		return CpLotteryUrlConstants.getWebCpUrlMap();
	}
}
