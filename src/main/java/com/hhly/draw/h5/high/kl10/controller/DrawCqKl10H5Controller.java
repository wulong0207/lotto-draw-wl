package com.hhly.draw.h5.high.kl10.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

@Controller
@RequestMapping("/h5/cqkl10")
public class DrawCqKl10H5Controller extends DrawKl10H5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.CQKL10.getName();
	}

}
