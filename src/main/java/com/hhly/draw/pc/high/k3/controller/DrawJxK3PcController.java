package com.hhly.draw.pc.high.k3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 江西快3
 * @author huangchengfang1219
 * @date 2017年12月02日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/jxk3")
@Controller
public class DrawJxK3PcController extends DrawK3PcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.JXK3.getName();
	}

}
