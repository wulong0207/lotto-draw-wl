package com.hhly.draw.pc.high.other.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 山东群英会
 * @author huangchengfang1219
 * @date 2018年01月18日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/sdqyh")
@Controller
public class DrawSdqyhPcController extends DrawOtherPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.SDQYH.getName();
	}

}
