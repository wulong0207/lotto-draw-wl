package com.hhly.draw.pc.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 湖北福彩30选5
 * @author huangchengfang1219
 * @date 2018年01月19日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/hub30x5")
@Controller
public class DrawHub30x5PcController extends DrawLocalPcController {


	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.HUB30X5.getName();
	}

}
