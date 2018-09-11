package com.hhly.draw.h5.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @author lidecheng
 * @version 1.0
 * @desc 体彩7位数
 * @date 2017年12月02日
 * @company 益彩网络科技公司
 */
@Controller
@RequestMapping("/h5/js7n")
public class DrawJs7nH5Controller extends DrawLocalH5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.JS7N.getName();
	}
}
