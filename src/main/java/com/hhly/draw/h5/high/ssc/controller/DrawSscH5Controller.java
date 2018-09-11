package com.hhly.draw.h5.high.ssc.controller;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.hhly.draw.h5.high.controller.DrawHighH5Controller;
import com.hhly.skeleton.base.constants.DrawLotteryConstant;

/**
 * @desc 时时彩类
 * @author huangchengfang1219
 * @date 2017年10月21日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class DrawSscH5Controller extends DrawHighH5Controller {

	@Override
	public ModelAndView date(@PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date date) {
		ModelAndView mav = super.date(date);
		mav.setViewName("h5/high/ssc/" + DrawLotteryConstant.getLotteryKey(getLotteryCode()));
		return mav;
	}

	@Override
	public ModelAndView detail() {
		ModelAndView mav = super.detail();
		mav.setViewName("h5/high/ssc/" + DrawLotteryConstant.getLotteryKey(getLotteryCode()) + "-detail");
		return mav;
	}

}
