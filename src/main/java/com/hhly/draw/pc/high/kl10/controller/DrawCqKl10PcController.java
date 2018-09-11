package com.hhly.draw.pc.high.kl10.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 幸运农场(重庆快乐10分)
 * @author huangchengfang1219
 * @date 2017年11月16日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/cqkl10")
@Controller
public class DrawCqKl10PcController extends DrawKl10PcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.CQKL10.getName();
	}

}
