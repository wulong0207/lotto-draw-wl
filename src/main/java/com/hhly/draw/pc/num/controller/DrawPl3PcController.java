package com.hhly.draw.pc.num.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

@Controller
@RequestMapping("/pc/pl3")
public class DrawPl3PcController extends DrawNumPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.PL3.getName();
	}

}
