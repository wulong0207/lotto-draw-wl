package com.hhly.draw.http.commoncore.operate.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.http.commoncore.operate.IOperateArticleService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.lotto.base.operate.bo.OperateArticleLottBO;
import com.hhly.skeleton.lotto.base.operate.vo.OperateArticleLottVO;

@Service("operateArticleService")
public class OperateArticleServiceImpl implements IOperateArticleService {

	private static final Logger logger = LoggerFactory.getLogger(OperateArticleServiceImpl.class);

	@Autowired
	private RestTemplate restTemplate;
	@Value("${lotto_common_core_url}")
	private String lottoCommonCoreUrl;

	@Override
	public Map<String, List<OperateArticleLottBO>> findNewsByTypeList(List<OperateArticleLottVO> voList) {
		String url = lottoCommonCoreUrl + "operate/article/news/bytypes";
		String resultStr = restTemplate.postForEntity(url, voList, String.class).getBody();
		ResultBO<HashMap<String, List<OperateArticleLottBO>>> result = JsonUtil.jsonToJackObject(resultStr,
				new TypeReference<ResultBO<HashMap<String, List<OperateArticleLottBO>>>>() {
				});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("资讯信息查询失败：{}", result.getMessage());
			return null;
		}
		return result.getData();
	}

}
