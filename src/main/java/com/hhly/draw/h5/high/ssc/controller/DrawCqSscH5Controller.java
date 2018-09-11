package com.hhly.draw.h5.high.ssc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

@Controller
@RequestMapping("/h5/cqssc")
public class DrawCqSscH5Controller extends DrawSscH5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.CQSSC.getName();
	}

}
