package com.hhly.draw.h5.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 河南福彩22选5
 * @author huangchengfang1219
 * @date 2018年01月18日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/hen22x5")
public class DrawHen22x5H5Controller extends DrawLocalH5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.HEN22X5.getName();
	}

}
