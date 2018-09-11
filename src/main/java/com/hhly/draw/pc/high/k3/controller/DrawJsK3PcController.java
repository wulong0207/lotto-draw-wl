package com.hhly.draw.pc.high.k3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 江苏快3
 * @author huangchengfang1219
 * @date 2017年11月16日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/jsk3")
@Controller
public class DrawJsK3PcController extends DrawK3PcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.JSK3.getName();
	}

}
