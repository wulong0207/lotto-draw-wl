package com.hhly.draw.pc.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @author lidecheng
 * @version 1.0
 * @desc 上海天天彩选4
 * @date 2017年12月02日
 * @company 益彩网络科技公司
 */
@Controller
@RequestMapping("/pc/shttcx4")
public class DrawShttcx4PcController extends DrawLocalPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.SHTTCX4.getName();
	}
}
