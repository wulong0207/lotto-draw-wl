package com.hhly.draw.http.commoncore.lotto.impl;

import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.http.commoncore.lotto.ILotteryService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.DrawLotteryConstant;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

@Service("lotteryService")
public class LotteryServiceImpl implements ILotteryService {

	@Autowired
	private RestTemplate restTemplate;
	@Value("${lotto_common_core_url}")
	private String lottoCommonCoreUrl;

	@Override
	public List<LotteryBO> queryLotterySelectList(LotteryVO vo) {
		String url = lottoCommonCoreUrl + "lottery/list";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<List<LotteryBO>> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<List<LotteryBO>>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			throw new ResultJsonException(result);
		}
		List<LotteryBO> lotteryList = result.getData();
		if (!ObjectUtil.isBlank(lotteryList)) {
			Iterator<LotteryBO> i = lotteryList.iterator();
			// 去除没有开发的彩种
			while (i.hasNext()) {
				if (DrawLotteryConstant.getLotteryKey(i.next().getLotteryCode()) == null) {
					i.remove();
				}
			}
		}
		return lotteryList;
	}

}
