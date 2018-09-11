package com.hhly.draw.pc.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 河北福彩好运3
 * @author huangchengfang1219
 * @date 2018年01月04日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/hebhy3")
@Controller
public class DrawHebhy3PcController extends DrawLocalPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.HEBHY3.getName();
	}

}
