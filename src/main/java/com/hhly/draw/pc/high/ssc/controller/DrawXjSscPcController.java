package com.hhly.draw.pc.high.ssc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 新疆时时彩
 * @author huangchengfang1219
 * @date 2018年01月02日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/xjssc")
@Controller
public class DrawXjSscPcController extends DrawSscPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.XJSSC.getName();
	}

}
