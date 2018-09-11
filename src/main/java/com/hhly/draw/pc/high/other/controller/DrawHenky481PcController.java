package com.hhly.draw.pc.high.other.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 河南快赢481
 * @author huangchengfang1219
 * @date 2018年01月05日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/henky481")
@Controller
public class DrawHenky481PcController extends DrawOtherPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.HENKY481.getName();
	}

}
