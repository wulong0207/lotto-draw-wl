package com.hhly.draw.service;

import java.util.List;

import com.hhly.skeleton.user.bo.UserWinInfoBO;

/**
 * 查询用户中奖统计信息
 *
 * @author huangchengfang1219
 * @date 2017年12月14日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface DrawMemberWinningService {

	/**
	 * 根据彩种查询中奖统计
	 * 
	 * @param lotteryCode
	 * @return
	 */
	List<UserWinInfoBO> findUserWinInfoList(Integer lotteryCode);

	/**
	 * 查询最新中奖信息
	 * 
	 * @return
	 */
	List<com.hhly.skeleton.lotto.base.order.bo.UserWinInfoBO> findNewestWinInfoList();
}
