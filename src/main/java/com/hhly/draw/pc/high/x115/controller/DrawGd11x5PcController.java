package com.hhly.draw.pc.high.x115.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 广东11选5
 * @author huangchengfang1219
 * @date 2017年11月16日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/gd11x5")
@Controller
public class DrawGd11x5PcController extends Draw11x5PcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.D11X5.getName();
	}

}
