package com.hhly.draw.h5.high.x115.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 福建11选5
 * @author huangchengfang1219
 * @date 2018年01月02日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/fj11x5")
public class DrawFj11x5H5Controller extends Draw11x5H5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.FJ11X5.getName();
	}

}
