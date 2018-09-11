package com.hhly.draw.h5.sport.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.draw.issue.bo.DrawLotteryIssueBO;
import com.hhly.skeleton.draw.issue.vo.DrawLotteryIssueVO;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;
import com.hhly.skeleton.draw.sport.bo.DrawSportOldBO;

/**
 * @desc 六场半全场
 * @author zhouyang
 * @date 2017年10月24日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/zc6")
public class DrawSportZc6H5Controller extends BaseH5Controller {

	private static final Logger logger = LoggerFactory.getLogger(DrawSportZc6H5Controller.class);

	@Autowired
	protected IDrawLotteryService drawLotteryService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		DrawLotteryIssueVO vo = new DrawLotteryIssueVO();
		vo.setPageIndex(Constants.NUM_0);
		vo.setPageSize(Constants.NUM_100);
		String url = drawCoreUrl + "draw/zc6/list";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<PagingBO<DrawLotteryIssueBO>> result = JsonUtil.jsonToJackObject(resultStr,
				new TypeReference<ResultBO<PagingBO<DrawLotteryIssueBO>>>() {
				});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询六场半全场开奖列表失败:{}", result.getMessage());
			throw new ServiceRuntimeException(result);
		}
		PagingBO<DrawLotteryIssueBO> pageData = result.getData();

		List<DrawLotteryIssueBO> issueList = pageData.getData();
		DrawLotteryIssueBO lastIssue = null;
		if (ObjectUtil.isBlank(issueList)) {
			lastIssue = new DrawLotteryIssueBO();
			issueList = Collections.emptyList();
		} else if (issueList.size() == Constants.NUM_1) {
			lastIssue = issueList.get(0);
			issueList = Collections.emptyList();
		} else if (issueList.size() > Constants.NUM_1) {
			lastIssue = issueList.get(0);
			issueList = issueList.subList(1, issueList.size());
		}
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(LotteryEnum.Lottery.ZC6.getName());
		ModelAndView mav = new ModelAndView();
		mav.addObject("lotteryType", lotteryType);
		mav.addObject("issueList", issueList);
		mav.addObject("lastIssue", lastIssue);
		mav.setViewName("h5/sport/zc6");
		return mav;
	}

	@RequestMapping(value = "/{issueCode}", method = RequestMethod.GET)
	public ModelAndView detail(@PathVariable("issueCode") String issueCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("issueCode", issueCode);
		String url = drawCoreUrl + "draw/zc6/detail";
		String resultStr = restTemplate.postForEntity(url, params, String.class).getBody();
		ResultBO<DrawSportOldBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<DrawSportOldBO>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询六场半全场开奖详情{}失败: {}", issueCode, result.getMessage());
			throw new ServiceRuntimeException(result);
		}

		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(LotteryEnum.Lottery.ZC6.getName());
		ModelAndView mav = new ModelAndView();
		mav.addObject("lotteryType", lotteryType);
		mav.addObject("issue", result.getData());
		mav.setViewName("h5/sport/zc6-detail");
		return mav;
	}

}
