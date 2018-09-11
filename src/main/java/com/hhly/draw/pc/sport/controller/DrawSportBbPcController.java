package com.hhly.draw.pc.sport.controller;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.base.controller.BasePcController;
import com.hhly.draw.http.commoncore.sport.ISportMatchService;
import com.hhly.draw.http.drawcore.lottery.IDrawLotteryService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.base.util.RegularValidateUtil;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;
import com.hhly.skeleton.draw.sport.bo.SportBbBO;
import com.hhly.skeleton.draw.sport.vo.SportBbVO;

/**
 * 竞彩篮球
 *
 * @author huangchengfang1219
 * @date 2017年11月9日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/pc/bb")
public class DrawSportBbPcController extends BasePcController {

	private static final Logger logger = LoggerFactory.getLogger(DrawSportBbPcController.class);

	@Autowired
	private IDrawLotteryService drawLotteryService;
	@Autowired
	private ISportMatchService sportMatchService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(SportBbVO vo) {
		return query(null, vo);
	}

	@RequestMapping(value = "/{condition}", method = RequestMethod.GET)
	public ModelAndView query(@PathVariable String condition, SportBbVO vo) {
		if (vo == null) {
			vo = new SportBbVO();
		}
		Integer lotteryCode = LotteryEnum.Lottery.BB.getName();
		// 处理查询条件
		buildQuery(condition, vo);
		ModelAndView mav = new ModelAndView();
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(lotteryCode);
		// 开奖列表
		if (vo.getPageIndex() == null) {
			mav.addObject("noPageParam", true);
			vo.setPageIndex(Constants.NUM_0);
		}
		vo.setPageSize(Constants.NUM_20);
		String url = drawCoreUrl + "draw/bb/list";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<PagingBO<SportBbBO>> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<PagingBO<SportBbBO>>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("竞彩篮球开奖公告查询失败", result.getMessage());
			throw new ServiceRuntimeException(result);
		}
		PagingBO<SportBbBO> pageData = result.getData();
		// 查询日期处理
		if (vo.getStartTime() == null && vo.getEndTime() == null && pageData.getOther() != null) {
			Date queryTime = DateUtil.convertStrToDate((String) pageData.getOther(), DateUtil.DATE_FORMAT);
			mav.addObject("queryStartTime", queryTime);
			mav.addObject("queryEndTime", queryTime);
		} else {
			mav.addObject("queryStartTime", vo.getStartTime());
			mav.addObject("queryEndTime", vo.getEndTime());
		}
		// 赛事列表
		try {
			List<?> raceList = sportMatchService.findRaceList(lotteryCode);
			mav.addObject("raceList", raceList);
		} catch (Exception e) {
			logger.error("查询赛事列表失败", e);
		}
		// 构建返回对象
		mav.addObject("lotteryType", lotteryType);
		mav.addObject("pageData", pageData);
		mav.addObject("queryVO", vo);
		mav.setViewName("pc/sport/bb");
		return mav;
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public ModelAndView detail(@RequestParam(required = true) Integer matchId) {
		ModelAndView mav = new ModelAndView();
		String url = drawCoreUrl + "draw/bb/{matchId}/alldata";
		String resultStr = restTemplate.getForObject(url, String.class, matchId);
		ResultBO<SportBbBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<SportBbBO>>() {
		});
		mav.addObject("match", result.getData());
		mav.setViewName("pc/sport/bb-match-det");
		return mav;
	}

	/**
	 * 处理查询条件
	 * 
	 * @param condition
	 * @param vo
	 */
	private void buildQuery(String conditionStr, SportBbVO vo) {
		if (ObjectUtil.isBlank(conditionStr)) {
			return;
		}
		// 格式: {startTime}_{endTime}_{raceId}
		String[] conditions = conditionStr.split(SymbolConstants.UNDERLINE);
		String condition;
		int index = 0;
		// 开始时间
		if (conditions.length > index && (condition = conditions[index]) != null && condition.startsWith("s")
				&& condition.substring(1).matches(RegularValidateUtil.REGULAR_DATE_NO_LINE)) {
			vo.setStartTime(DateUtil.convertStrToDate(condition.substring(1), DateUtil.DATE_FORMAT_NO_LINE));
			index++;
		}
		// 结束时间
		if (conditions.length > index && (condition = conditions[index]) != null && condition.startsWith("e")
				&& condition.substring(1).matches(RegularValidateUtil.REGULAR_DATE_NO_LINE)) {
			vo.setEndTime(DateUtil.convertStrToDate(condition.substring(1), DateUtil.DATE_FORMAT_NO_LINE));
			index++;
		}
		// 赛事ID
		if (conditions.length > index && (condition = conditions[index]) != null && condition.startsWith("r")
				&& condition.substring(1).matches(RegularValidateUtil.REGULAR_ACCOUNT2)) {
			vo.setRaceId(Integer.parseInt(condition.substring(1)));
			index++;
		}
	}

}
