package com.hhly.draw.h5.num.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

@Controller
@RequestMapping("/h5/pl5")
public class DrawPl5H5Controller extends DrawNumH5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.PL5.getName();
	}

}
