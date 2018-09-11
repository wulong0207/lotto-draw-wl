package com.hhly.draw.h5.high.other.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;

/**
 * @desc 北京快乐8
 * @author huangchengfang1219
 * @date 2018年01月19日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/bjkl8")
public class DrawBjkl8H5Controller extends DrawOtherH5Controller {
	
	@Override
	public ModelAndView detail() {
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(getLotteryCode());
		ModelAndView mav = new ModelAndView();
		mav.addObject("lotteryType", lotteryType);
		mav.setViewName("h5/high/other/bjkl8-detail");
		return mav;
	}

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.BJKL8.getName();
	}

}
