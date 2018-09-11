package com.hhly.draw.pc.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 海南4+1
 * @author huangchengfang1219
 * @date 2018年01月18日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/hain4j1")
@Controller
public class DrawHain4j1PcController extends DrawLocalPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.HAIN4J1.getName();
	}

}
