package com.hhly.draw.http.drawcore.lottery.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.http.commoncore.lotto.impl.LotteryIssueServiceImpl;
import com.hhly.draw.http.drawcore.lottery.IDrawLotteryService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;

@Service("drawLotteryService")
public class DrawLotteryServiceImpl implements IDrawLotteryService {

	private static final Logger logger = LoggerFactory.getLogger(LotteryIssueServiceImpl.class);

	@Autowired
	protected RestTemplate restTemplate;
	@Value("${draw_core_url}")
	protected String drawCoreUrl;

	@Override
	public DrawLotteryTypeBO findLotteryType(Integer lotteryCode) {
		String url = drawCoreUrl + "/draw/lottery/{lotteryCode}";
		String resultStr = restTemplate.getForObject(url, String.class, lotteryCode);
		ResultBO<DrawLotteryTypeBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<DrawLotteryTypeBO>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("开奖公告彩种{}查询失败:{}", lotteryCode, result.getMessage());
			throw new ServiceRuntimeException(result);
		}
		return result.getData();
	}

}
