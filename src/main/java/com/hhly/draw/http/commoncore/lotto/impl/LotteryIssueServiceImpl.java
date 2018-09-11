package com.hhly.draw.http.commoncore.lotto.impl;

import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.http.commoncore.lotto.ILotteryIssueService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryIssueBaseBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

@Service("lotteryIssueService")
public class LotteryIssueServiceImpl implements ILotteryIssueService {

	private static final Logger logger = LoggerFactory.getLogger(LotteryIssueServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;
	@Value("${lotto_common_core_url}")
	private String lottoCommonCoreUrl;

	@Override
	public List<String> queryIssueByLottery(LotteryVO vo) {
		String url = lottoCommonCoreUrl + "lottery/issue/list";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<List<String>> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<List<String>>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			throw new ServiceRuntimeException(result);
		}
		return result.getData();
	}

	@Override
	public List<String> queryIssueCodeList(Integer lotteryCode, Short currentIssue) {
		return queryIssueCodeList(lotteryCode, currentIssue, null);
	}

	@Override
	public List<String> queryIssueCodeList(Integer lotteryCode, Short currentIssue, Integer qryCount) {
		try {
			LotteryVO lotteryVO = new LotteryVO();
			lotteryVO.setLotteryCode(lotteryCode);
			lotteryVO.setCurrentIssue(currentIssue);
			lotteryVO.setQryCount(qryCount);
			List<String> issueCodeList = queryIssueByLottery(lotteryVO);
			return issueCodeList;
		} catch (Exception e) {
			logger.error("最新彩期查询失败", e);
		}
		return Collections.emptyList();
	}

	@Override
	public IssueBO queryCurrentIssue(Integer lotteryCode) {
		LotteryIssueBaseBO lotteryIssueBase = findLotteryIssueBase(lotteryCode);
		if (lotteryIssueBase == null) {
			return null;
		}
		return lotteryIssueBase.getCurIssue();
	}

	@Override
	public LotteryIssueBaseBO findLotteryIssueBase(Integer lotteryCode) {
		String url = lottoCommonCoreUrl + "lottery/issue/base/{lotteryCode}";
		String resultStr = restTemplate.getForObject(url, String.class, lotteryCode);
		ResultBO<LotteryIssueBaseBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<LotteryIssueBaseBO>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询彩种{}当前期信息失败:{}", lotteryCode, result.getMessage());
			return null;
		}
		return result.getData();
	}

}
