package com.hhly.draw.h5.high.other.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 重庆百变王牌
 * @author huangchengfang1219
 * @date 2018年01月05日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/cqbbwp")
public class DrawCqbbwpH5Controller extends DrawOtherH5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.CQBBWP.getName();
	}

}
