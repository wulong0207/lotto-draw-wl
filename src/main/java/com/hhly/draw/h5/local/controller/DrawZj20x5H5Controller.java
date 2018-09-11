package com.hhly.draw.h5.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @author lidecheng
 * @version 1.0
 * @desc 浙江体彩20选5
 * @date 2017年12月02日
 * @company 益彩网络科技公司
 */
@Controller
@RequestMapping("/h5/zj20x5")
public class DrawZj20x5H5Controller extends DrawLocalH5Controller {

	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.ZJ20X5.getName();
	}
}
