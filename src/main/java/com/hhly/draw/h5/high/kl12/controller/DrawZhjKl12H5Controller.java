package com.hhly.draw.h5.high.kl12.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 浙江快乐12
 * @author huangchengfang1219
 * @date 2018年01月03日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/zhjkl12")
public class DrawZhjKl12H5Controller extends DrawKl12H5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.ZHJKL12.getName();
	}

}
