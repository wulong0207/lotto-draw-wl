package com.hhly.draw.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.hhly.draw.base.util.RedisUtil;
import com.hhly.draw.http.usercore.member.IMemberWinningService;
import com.hhly.draw.service.DrawMemberWinningService;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.user.bo.UserWinInfoBO;
import com.hhly.skeleton.user.vo.UserWinInfoVO;

/**
 * 用户中奖统计
 *
 * @author huangchengfang1219
 * @date 2017年12月14日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Service("drawMemberWinningService")
public class DrawMemberWinningServiceImpl implements DrawMemberWinningService {

	private static final Logger logger = LoggerFactory.getLogger(DrawMemberWinningServiceImpl.class);

	@Autowired
	protected IMemberWinningService memberWinningService;
	@Autowired
	protected RedisUtil redisUtil;

	@Override
	public List<UserWinInfoBO> findUserWinInfoList(Integer lotteryCode) {
		try {
			String key = new StringBuilder(CacheConstants.C_COMM_USER_WINNING).append(SymbolConstants.UNDERLINE).append("list")
					.append(SymbolConstants.UNDERLINE).append(lotteryCode).toString();
			@SuppressWarnings("unchecked")
			List<UserWinInfoBO> userWinInfoList = (List<UserWinInfoBO>) redisUtil.getObj(key);
			if (userWinInfoList == null) {
				UserWinInfoVO winInfoVO = new UserWinInfoVO();
				winInfoVO.setPageIndex(Constants.NUM_0);
				winInfoVO.setPageSize(Constants.NUM_10);
				winInfoVO.setLotteryCode(lotteryCode);
				userWinInfoList = memberWinningService.queryUserWinByLottery(winInfoVO);
				if (userWinInfoList != null) {
					redisUtil.addObj(key, userWinInfoList, CacheConstants.ONE_DAY);
				}
			}
			return userWinInfoList;
		} catch (Exception e) {
			logger.error("中奖排行信息查询失败", e);
			return null;
		}
	}

	@Override
	public List<com.hhly.skeleton.lotto.base.order.bo.UserWinInfoBO> findNewestWinInfoList() {
		UserWinInfoVO winInfoVO = new UserWinInfoVO();
		winInfoVO.setPageIndex(Constants.NUM_0);
		winInfoVO.setPageSize(Constants.NUM_10);
		return memberWinningService.queryWinInfoList(winInfoVO);
	}

}
