package com.hhly.draw.http.usercore.member.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.http.usercore.member.IMemberWinningService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.user.bo.UserWinInfoBO;
import com.hhly.skeleton.user.vo.UserWinInfoVO;

@Service("memberWinningService")
public class MemberWinningServiceImpl implements IMemberWinningService {

	private static final Logger logger = LoggerFactory.getLogger(MemberWinningServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;
	@Value("${user_core_url}")
	private String userCoreUrl;

	@Override
	public List<UserWinInfoBO> queryUserWinByLottery(UserWinInfoVO vo) {
		String url = userCoreUrl + "member/winning/lottery/list";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<List<UserWinInfoBO>> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<List<UserWinInfoBO>>>() {
		});
		if (result == null || result.isError()) {
			logger.error("根据彩种查询中奖信息失败：{}", result.getMessage());
			return null;
		}
		return result.getData();
	}

	@Override
	public List<com.hhly.skeleton.lotto.base.order.bo.UserWinInfoBO> queryWinInfoList(UserWinInfoVO vo) {
		String url = userCoreUrl + "member/winning/new/list";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<List<com.hhly.skeleton.lotto.base.order.bo.UserWinInfoBO>> result = JsonUtil.jsonToJackObject(resultStr,
				new TypeReference<ResultBO<List<com.hhly.skeleton.lotto.base.order.bo.UserWinInfoBO>>>() {
				});
		if (result == null || result.isError()) {
			logger.error("查询最新中奖信息失败：{}", result.getMessage());
			return null;
		}
		return result.getData();
	}

}
