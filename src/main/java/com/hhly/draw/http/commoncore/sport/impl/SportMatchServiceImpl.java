package com.hhly.draw.http.commoncore.sport.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.http.commoncore.lotto.impl.LotteryIssueServiceImpl;
import com.hhly.draw.http.commoncore.sport.ISportMatchService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.lotto.base.sport.bo.MatchDataBO;

@Service("sportMatchService")
public class SportMatchServiceImpl implements ISportMatchService {

	private static final Logger logger = LoggerFactory.getLogger(LotteryIssueServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;
	@Value("${lotto_common_core_url}")
	private String lottoCommonCoreUrl;

	@Override
	public List<MatchDataBO> findRaceList(Integer lotteryCode) {
		String url = lottoCommonCoreUrl + "sport/match/races/{lotteryCode}";
		String resultStr = restTemplate.getForObject(url, String.class, lotteryCode);
		ResultBO<List<MatchDataBO>> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<List<MatchDataBO>>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询赛事列表{}失败:{}", lotteryCode, result.getMessage());
			return null;
		}
		return result.getData();
	}

}
