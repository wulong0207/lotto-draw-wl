package com.hhly.draw.pc.num.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

@Controller
@RequestMapping("/pc/ssq")
public class DrawSsqPcController extends DrawNumPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.SSQ.getName();
	}

}
