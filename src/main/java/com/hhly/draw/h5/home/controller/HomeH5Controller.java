
package com.hhly.draw.h5.home.controller;

import com.hhly.draw.base.common.CpLotteryUrlConstants;
import com.hhly.draw.common.home.controller.HomeController;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * @desc
 * @author cheng chen
 * @date 2017年10月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */

@RequestMapping("/h5/index")
@Controller
public class HomeH5Controller extends HomeController {

	@ModelAttribute("cpLotteryMap")
	public Map<String, String> bindCpLotteryUrlMap() {
		return CpLotteryUrlConstants.getWapCpUrlMap();
	}

	@ModelAttribute("isYicaiWapDomain")
	public boolean isYicaiWapDomain(HttpServletRequest request) {
		return CpLotteryUrlConstants.isYicaiWapDomain(request.getServerName());
	}

	@Override
	public ModelAndView index() {
		ModelAndView mav = super.index();
		mav.setViewName("h5/index");
		return mav;
	}

	@Override
	public ModelAndView high() {
		ModelAndView mav = super.high();
		mav.setViewName("h5/index-gp");
		return mav;
	}

	@Override
	public ModelAndView local() {
		ModelAndView mav = super.local();
		mav.setViewName("h5/index-df");
		return mav;
	}
}
