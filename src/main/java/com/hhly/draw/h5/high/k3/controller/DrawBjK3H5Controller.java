package com.hhly.draw.h5.high.k3.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 北京快3
 * @author huangchengfang1219
 * @date 2018年01月02日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/bjk3")
public class DrawBjK3H5Controller extends DrawK3H5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.BJK3.getName();
	}

}
