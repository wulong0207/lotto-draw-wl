package com.hhly.draw.pc.high.other.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 山西泳坛夺金
 * @author huangchengfang1219
 * @date 2018年01月19日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/sxytdj")
@Controller
public class DrawSxytdjPcController extends DrawOtherPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.SXYTDJ.getName();
	}

}
