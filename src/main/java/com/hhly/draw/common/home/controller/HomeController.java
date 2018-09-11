
package com.hhly.draw.common.home.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.base.controller.BaseController;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.exception.ResultJsonException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.draw.home.bo.DrawHomeHighLotteryBO;
import com.hhly.skeleton.draw.home.bo.DrawHomeLocalLotteryBO;
import com.hhly.skeleton.draw.issue.bo.DrawLotteryBO;

/**
 * @desc
 * @author cheng chen
 * @date 2017年10月13日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class HomeController extends BaseController {

	@Autowired
	private RestTemplate restTemplate;
	@Value("${draw_core_url}")
	private String drawCoreUrl;

	@RequestMapping
	public ModelAndView index() {
		String resultStr = restTemplate.getForObject(drawCoreUrl + "draw/lottery/country", String.class);
		ResultBO<List<DrawLotteryBO>> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<List<DrawLotteryBO>>>() {
		});
		if (result.isError()) {
			throw new ResultJsonException(result);
		}
		List<DrawLotteryBO> drawLotteryBOList = result.getData();
		if (drawLotteryBOList == null) {
			drawLotteryBOList = Collections.emptyList();
		}
		ModelAndView mav = new ModelAndView();
		mav.addObject("drawLotteryBOList", drawLotteryBOList);
		return mav;
	}

	@RequestMapping(value = "high", method = RequestMethod.GET)
	public ModelAndView high() {
		String resultStr = restTemplate.getForObject(drawCoreUrl + "draw/lottery/high", String.class);
		ResultBO<List<DrawHomeHighLotteryBO>> result = JsonUtil.jsonToJackObject(resultStr,
				new TypeReference<ResultBO<List<DrawHomeHighLotteryBO>>>() {
				});
		if (result.isError()) {
			throw new ResultJsonException(result);
		}
		List<DrawHomeHighLotteryBO> homeHighLotteryBOList = result.getData();
		if (homeHighLotteryBOList == null) {
			homeHighLotteryBOList = Collections.emptyList();
		}
		ModelAndView mav = new ModelAndView();
		mav.addObject("homeHighLotteryBOList", homeHighLotteryBOList);
		return mav;
	}

	@RequestMapping(value = "local", method = RequestMethod.GET)
	public ModelAndView local() {
		String resultStr = restTemplate.getForObject(drawCoreUrl + "draw/lottery/local", String.class);
		ResultBO<List<DrawHomeLocalLotteryBO>> result = JsonUtil.jsonToJackObject(resultStr,
				new TypeReference<ResultBO<List<DrawHomeLocalLotteryBO>>>() {
				});
		if (result.isError()) {
			throw new ResultJsonException(result);
		}
		List<DrawHomeLocalLotteryBO> drawLotteryBOList = result.getData();
		if (drawLotteryBOList == null) {
			drawLotteryBOList = Collections.emptyList();
		}
		ModelAndView mav = new ModelAndView();
		mav.addObject("drawLocalLotteryBOList", drawLotteryBOList);
		return mav;
	}
}
