package com.hhly.draw.http.commoncore.lotto;

import java.util.List;

import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryIssueBaseBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

public interface ILotteryIssueService {

	/**
	 * 根据彩种编码查询所有彩期
	 * 
	 * @param lotteryCode
	 * @return
	 * @date 2017年9月23日上午11:24:02
	 * @author cheng.chen
	 */
	List<String> queryIssueByLottery(LotteryVO lotteryVO);

	/**
	 * 查询所有彩期
	 * @param lotteryCode
	 * @param currentIssue
	 * @return
	 */
	List<String> queryIssueCodeList(Integer lotteryCode, Short currentIssue);
	
	/**
	 * 查询指定数量彩期
	 * @param lotteryCode
	 * @param currentIssue
	 * @param qryCount
	 * @return
	 */
	List<String> queryIssueCodeList(Integer lotteryCode, Short currentIssue, Integer qryCount);

	/**
	 * 查询彩种当前期
	 * 
	 * @param lotteryCode
	 * @return
	 */
	IssueBO queryCurrentIssue(Integer lotteryCode);
	
	/**
	 * 查询彩种基础信息
	 * @param lotteryCode
	 * @return
	 */
	LotteryIssueBaseBO findLotteryIssueBase(Integer lotteryCode);
}
