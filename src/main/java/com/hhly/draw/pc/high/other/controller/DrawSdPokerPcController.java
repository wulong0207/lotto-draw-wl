package com.hhly.draw.pc.high.other.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 山东快乐扑克3
 * @author huangchengfang1219
 * @date 2017年11月16日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/sdpoker")
@Controller
public class DrawSdPokerPcController extends DrawOtherPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.SDPOKER.getName();
	}

}
