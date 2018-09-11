package com.hhly.draw.pc.num.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

@Controller
@RequestMapping("/pc/f3d")
public class DrawF3dPcController extends DrawNumPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.F3D.getName();
	}

}
