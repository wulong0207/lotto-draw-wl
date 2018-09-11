package com.hhly.draw.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.draw.base.util.ArticleUtil;
import com.hhly.draw.base.util.RedisUtil;
import com.hhly.draw.http.commoncore.operate.IOperateArticleService;
import com.hhly.draw.service.DrawOperateArticleService;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.lotto.base.operate.bo.OperateArticleLottBO;
import com.hhly.skeleton.lotto.base.operate.vo.OperateArticleLottVO;

@Service("drawOperateArticleService")
public class DrawOperateArticleServiceImpl implements DrawOperateArticleService {

	private static final Logger logger = LoggerFactory.getLogger(DrawOperateArticleServiceImpl.class);
	@Autowired
	protected IOperateArticleService operateArticleService;
	@Autowired
	protected RedisUtil redisUtil;

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, List<OperateArticleLottBO>> findArticles(Integer lotteryCode) {
		try {
			List<OperateArticleLottVO> articleLottVOList = new ArrayList<OperateArticleLottVO>();
			OperateArticleLottVO articleLottVO1 = ArticleUtil.buildAnalyzeQuery(lotteryCode);
			articleLottVOList.add(articleLottVO1);
			OperateArticleLottVO articleLottVO2 = ArticleUtil.buildPrizeQuery(lotteryCode);
			articleLottVOList.add(articleLottVO2);
			StringBuilder keyBuilder = new StringBuilder(CacheConstants.C_PC_ARTICLE_FIND_BY_TYPE_LIST);
			for (OperateArticleLottVO vo : articleLottVOList) {
				keyBuilder.append(vo.getTypeCode()).append(vo.getRownum()).append(vo.getPlatform());
			}
			String key = keyBuilder.toString();
			Map<String, List<OperateArticleLottBO>> ariMap = (Map<String, List<OperateArticleLottBO>>) redisUtil.getObj(key);
			if (ariMap != null) {
				Map<String, List<OperateArticleLottBO>> newMap = new HashMap<String, List<OperateArticleLottBO>>();
				newMap.put("articelAnalyzeList", ariMap.get(ArticleUtil.getKey(articleLottVO1)));
				newMap.put("articelPrizeList", ariMap.get(ArticleUtil.getKey(articleLottVO2)));
				return newMap;
			}
			ariMap = operateArticleService.findNewsByTypeList(articleLottVOList);
			if (ariMap != null) {
				redisUtil.addObj(key, ariMap, CacheConstants.TEN_MINUTES);
				Map<String, List<OperateArticleLottBO>> newMap = new HashMap<String, List<OperateArticleLottBO>>();
				newMap.put("articelAnalyzeList", ariMap.get(ArticleUtil.getKey(articleLottVO1)));
				newMap.put("articelPrizeList", ariMap.get(ArticleUtil.getKey(articleLottVO2)));
				return newMap;
			}
		} catch (ResultJsonException e) {
			// 处理没有资讯时报错问题
			if (!MessageCodeConstants.DATA_NOT_FOUND_SYS.equals(e.getCode())) {
				logger.error("资讯信息查询失败", e);
			}
		} catch (Exception e) {
			logger.error("资讯信息查询失败", e);
		}
		return null;
	}

}
