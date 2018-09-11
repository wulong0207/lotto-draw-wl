package com.hhly.draw.h5.num.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

@Controller
@RequestMapping("/h5/qlc")
public class DrawQlcH5Controller extends DrawNumH5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.QLC.getName();
	}

}
