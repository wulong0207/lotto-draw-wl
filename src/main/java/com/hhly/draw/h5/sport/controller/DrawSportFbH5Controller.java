package com.hhly.draw.h5.sport.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.base.controller.BaseH5Controller;
import com.hhly.draw.http.drawcore.lottery.IDrawLotteryService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.constants.SportConstants;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;
import com.hhly.skeleton.draw.sport.bo.SportFbBO;
import com.hhly.skeleton.draw.sport.vo.SportFbVO;

/**
 * @desc 竞彩足球
 * @author huangchengfang1219
 * @date 2017年10月24日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/fb")
public class DrawSportFbH5Controller extends BaseH5Controller {

	private static final Logger logger = LoggerFactory.getLogger(DrawSportFbH5Controller.class);

	@Autowired
	protected IDrawLotteryService drawLotteryService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return query(null);
	}

	@RequestMapping(value = "/s{date}", method = RequestMethod.GET)
	public ModelAndView query(@PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date date) {
		SportFbVO vo = new SportFbVO();
		vo.setStartTime(date);
		vo.setEndTime(vo.getStartTime());
		vo.setMatchStatusArr(SportConstants.SPORT_DRAW_CANCEL_STATUS);
		String url = drawCoreUrl + "draw/fb/listall";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<PagingBO<SportFbBO>> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<PagingBO<SportFbBO>>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询竞彩足球开奖列表失败:{}", result.getMessage());
			throw new ServiceRuntimeException(result);
		}
		PagingBO<SportFbBO> pageData = result.getData();
		ModelAndView mav = new ModelAndView();
		List<SportFbBO> matchList = pageData.getData();
		mav.addObject("matchList", matchList);
		if (date == null) {
			mav.addObject("queryDate", pageData.getOther());
		} else {
			mav.addObject("queryDate", DateUtil.convertDateToStr(date, DateUtil.DATE_FORMAT));
		}
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(LotteryEnum.Lottery.FB.getName());
		mav.addObject("lotteryType", lotteryType);
		mav.addObject("queryVO", vo);
		mav.setViewName("h5/sport/fb");
		return mav;
	}

	@RequestMapping(value = "{date}_{matchId}", method = RequestMethod.GET)
	public ModelAndView detail(@PathVariable Integer matchId) {
		ModelAndView mav = new ModelAndView();
		String url = drawCoreUrl + "draw/fb/{matchId}/alldata";
		String resultStr = restTemplate.getForObject(url, String.class, matchId);
		ResultBO<SportFbBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<SportFbBO>>() {
		});
		mav.addObject("fbData", result.getData());
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(LotteryEnum.Lottery.FB.getName());
		mav.addObject("lotteryType", lotteryType);
		mav.setViewName("h5/sport/fb-detail");
		return mav;
	}

}
