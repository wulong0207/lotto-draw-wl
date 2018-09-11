package com.hhly.draw.pc.high.kl12.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 辽宁快乐12
 * @author huangchengfang1219
 * @date 2018年01月03日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/lnkl12")
@Controller
public class DrawLnKl12PcController extends DrawKl12PcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.LNKL12.getName();
	}

}
