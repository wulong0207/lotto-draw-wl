package com.hhly.draw.http.commoncore.operate.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.http.commoncore.operate.IOperateLotteryService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.lotto.base.operate.bo.OperateLotteryBO;
import com.hhly.skeleton.lotto.base.operate.vo.OperateLotteryLottVO;

@Service("operateLotteryService")
public class OperateLotteryServiceImpl implements IOperateLotteryService {

	private static final Logger logger = LoggerFactory.getLogger(OperateLotteryServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;
	@Value("${lotto_common_core_url}")
	private String lottoCommonCoreUrl;

	@Override
	public OperateLotteryBO findHomeOperLottery(OperateLotteryLottVO vo) {
		String url = lottoCommonCoreUrl + "operate/lottery/home";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<OperateLotteryBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<OperateLotteryBO>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("彩种运营信息查询失败：{}", result.getMessage());
			return null;
		}
		return result.getData();
	}

}
