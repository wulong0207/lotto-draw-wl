package com.hhly.draw.pc.high.other.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 北京赛车
 * @author lidecheng
 * @date 2018年01月05日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/pc/pk10")
public class DrawBjScPcController extends DrawOtherPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.PK10.getName();
	}

}
