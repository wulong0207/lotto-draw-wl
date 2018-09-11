package com.hhly.draw.h5.high.controller;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.type.TypeReference;
import com.hhly.draw.base.controller.BaseH5Controller;
import com.hhly.draw.http.drawcore.lottery.IDrawLotteryService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.draw.issue.bo.DrawLotteryIssueBO;
import com.hhly.skeleton.draw.issue.vo.DrawLotteryIssueVO;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;

/**
 * @desc H5高频彩接口
 * @author huangchengfang1219
 * @date 2017年10月21日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class DrawHighH5Controller extends BaseH5Controller {

	private static final Logger logger = LoggerFactory.getLogger(DrawHighH5Controller.class);
	@Autowired
	protected IDrawLotteryService drawLotteryService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return this.date(null);
	}

	@RequestMapping(value = "{date}", method = RequestMethod.GET)
	public ModelAndView date(@PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date date) {
		// 开奖列表
		DrawLotteryIssueVO vo = new DrawLotteryIssueVO();
		vo.setStartTime(date);
		vo.setLotteryCode(getLotteryCode());
		String url = drawCoreUrl + "draw/high/listall";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<PagingBO<DrawLotteryIssueBO>> result = JsonUtil.jsonToJackObject(resultStr,
				new TypeReference<ResultBO<PagingBO<DrawLotteryIssueBO>>>() {
				});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询高频彩{}开奖列表失败:{}", getLotteryCode(), result.getMessage());
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
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(getLotteryCode());
		ModelAndView mav = new ModelAndView();
		mav.addObject("lotteryType", lotteryType);
		if (date == null) {
			mav.addObject("queryDateStr", pageData.getOther());
		} else {
			mav.addObject("queryDateStr", DateUtil.convertDateToStr(date, DateUtil.DATE_FORMAT));
		}

		mav.addObject("queryDate", date);
		mav.addObject("issueList", issueList);
		mav.addObject("lastIssue", lastIssue);
		return mav;
	}

	@RequestMapping(value = "/detail", method = RequestMethod.GET)
	public ModelAndView detail() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lotteryCode", getLotteryCode());
		String url = drawCoreUrl + "draw/high/detail";
		String resultStr = restTemplate.postForEntity(url, params, String.class).getBody();
		ResultBO<DrawLotteryIssueBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<DrawLotteryIssueBO>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询高频彩开奖详情{}失败: {}", getLotteryCode(), result.getMessage());
			throw new ServiceRuntimeException(result);
		}
		DrawLotteryIssueBO issue = result.getData();
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(getLotteryCode());
		ModelAndView mav = new ModelAndView();
		mav.addObject("lotteryType", lotteryType);
		mav.addObject("issue", issue);
		return mav;
	}

	protected abstract Integer getLotteryCode();

}
