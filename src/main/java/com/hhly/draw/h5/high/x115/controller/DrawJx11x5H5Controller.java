package com.hhly.draw.h5.high.x115.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

@Controller
@RequestMapping("/h5/jx11x5")
public class DrawJx11x5H5Controller extends Draw11x5H5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.JX11X5.getName();
	}

}
