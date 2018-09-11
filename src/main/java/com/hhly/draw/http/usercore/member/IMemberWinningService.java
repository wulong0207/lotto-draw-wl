package com.hhly.draw.http.usercore.member;

import java.util.List;

import com.hhly.skeleton.user.bo.UserWinInfoBO;
import com.hhly.skeleton.user.vo.UserWinInfoVO;

/**
 * 中奖统计
 *
 * @author huangchengfang1219
 * @date 2017年12月14日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface IMemberWinningService {

	/**
	 * 根据彩种查询用户彩种中奖统计
	 * 
	 * @return
	 * @date 2017年9月23日下午4:24:08
	 * @author cheng.chen
	 */
	List<UserWinInfoBO> queryUserWinByLottery(UserWinInfoVO vo);

	/**
	 * 查询中奖列表
	 * 
	 * @param vo
	 * @return
	 */
	List<com.hhly.skeleton.lotto.base.order.bo.UserWinInfoBO> queryWinInfoList(UserWinInfoVO vo);
}
