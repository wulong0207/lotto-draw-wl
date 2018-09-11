package com.hhly.draw.h5.high.other.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

@Controller
@RequestMapping("/h5/sdpoker")
public class DrawSdPokerH5Controller extends DrawOtherH5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.SDPOKER.getName();
	}

}
