package com.hhly.draw.service;

import java.util.List;
import java.util.Map;

import com.hhly.skeleton.lotto.base.operate.bo.OperateArticleLottBO;

/**
 * 资讯
 *
 * @author huangchengfang1219
 * @date 2017年12月14日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface DrawOperateArticleService {

	/**
	 * 根据彩种信息查询资讯内容
	 * 
	 * @param lotteryCode
	 * @return
	 */
	public Map<String, List<OperateArticleLottBO>> findArticles(Integer lotteryCode);
}
