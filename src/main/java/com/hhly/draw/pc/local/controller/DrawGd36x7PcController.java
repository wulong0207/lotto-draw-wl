package com.hhly.draw.pc.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 广东36选7
 * @author huangchengfang1219
 * @date 2018年01月19日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/gd36x7")
@Controller
public class DrawGd36x7PcController extends DrawLocalPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.GD36X7.getName();
	}

}
