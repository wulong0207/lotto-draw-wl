package com.hhly.draw.h5.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 福彩排列7
 * @author huangchengfang1219
 * @date 2018年01月04日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/hebpl7")
public class DrawHebpl7H5Controller extends DrawLocalH5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.HEBPL7.getName();
	}

}
