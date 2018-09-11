package com.hhly.draw.pc.high.ssc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 重庆时时彩
 * @author huangchengfang1219
 * @date 2017年11月16日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/cqssc")
@Controller
public class DrawCqSscPcController extends DrawSscPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.CQSSC.getName();
	}

}
