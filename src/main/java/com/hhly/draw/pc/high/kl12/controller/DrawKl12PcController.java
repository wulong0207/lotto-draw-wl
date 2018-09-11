package com.hhly.draw.pc.high.kl12.controller;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.ModelAndView;

import com.hhly.draw.pc.high.controller.DrawHighPcController;
import com.hhly.skeleton.base.constants.DrawLotteryConstant;

/**
 * PC端快乐12类
 *
 * @author huangchengfang1219
 * @date 2018年1月2日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class DrawKl12PcController extends DrawHighPcController {

	@Override
	public ModelAndView date(@PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date date) {
		ModelAndView mav = super.date(date);
		mav.setViewName("pc/high/kl12/" + DrawLotteryConstant.getLotteryKey(getLotteryCode()));
		return mav;
	}

}
