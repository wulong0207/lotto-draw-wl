package com.hhly.draw.base.controller;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.client.RestTemplate;

import com.hhly.draw.base.common.CpLotteryUrlConstants;

public class BaseH5Controller {

	@Autowired
	protected RestTemplate restTemplate;
	@Value("${draw_core_url}")
	protected String drawCoreUrl;

	@ModelAttribute("cpLotteryMap")
	public Map<String, String> bindCpLotteryUrlMap() {
		return CpLotteryUrlConstants.getWapCpUrlMap();
	}

	@ModelAttribute("isYicaiWapDomain")
	public boolean isYicaiWapDomain(HttpServletRequest request) {
		return CpLotteryUrlConstants.isYicaiWapDomain(request.getServerName());
	}
}
