package com.hhly.draw.http.commoncore.operate;

import java.util.List;
import java.util.Map;

import com.hhly.skeleton.lotto.base.operate.bo.OperateArticleLottBO;
import com.hhly.skeleton.lotto.base.operate.vo.OperateArticleLottVO;

/**
 * @desc 资讯文章管理服务
 * @author lidecheng
 * @date 2017年3月8日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface IOperateArticleService {	

	/**
	 * 根据多个栏目查询资讯信息
	 * @param voList
	 * @return
	 */
	Map<String,List<OperateArticleLottBO>> findNewsByTypeList(List<OperateArticleLottVO> voList);

}
