package com.hhly.draw.http.commoncore.lotto;

import java.util.List;

import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

/**
 * 使用HTTP远程调用接口实现彩种信息查询
 *
 * @author huangchengfang1219
 * @date 2018年4月18日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface ILotteryService {

	/**
	 * 通过开奖彩种类型查询彩种集合
	 * 
	 * @param lotteryVO
	 * @return
	 * @date 2017年9月23日上午11:27:42
	 * @author cheng.chen
	 */
	List<LotteryBO> queryLotterySelectList(LotteryVO vo);
}
