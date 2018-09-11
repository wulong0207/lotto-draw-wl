package com.hhly.draw.pc.high.x115.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 河北11选5
 * @author huangchengfang1219
 * @date 2018年01月02日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/heb11x5")
@Controller
public class DrawHeb11x5PcController extends Draw11x5PcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.HEB11X5.getName();
	}

}
