package com.hhly.draw.h5.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 深圳风采
 * @author huangchengfang1219
 * @date 2018年01月19日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/szfc")
public class DrawSzfcH5Controller extends DrawLocalH5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.SZFC.getName();
	}

}
