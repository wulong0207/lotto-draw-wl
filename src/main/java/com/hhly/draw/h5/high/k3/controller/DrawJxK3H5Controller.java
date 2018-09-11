package com.hhly.draw.h5.high.k3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

@Controller
@RequestMapping("/h5/jxk3")
public class DrawJxK3H5Controller extends DrawK3H5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.JXK3.getName();
	}

}
