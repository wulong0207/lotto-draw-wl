package com.hhly.draw.http.drawcore.lottery;

import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;

public interface IDrawLotteryService {

	/**
	 * 查询彩种信息
	 * 
	 * @param lotteryCode
	 * @return
	 */
	DrawLotteryTypeBO findLotteryType(Integer lotteryCode);
}
