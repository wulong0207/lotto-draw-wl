package com.hhly.draw.h5.num.controller;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.hhly.skeleton.base.constants.DrawLotteryConstant;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.draw.issue.bo.DrawLotteryIssueBO;
import com.hhly.skeleton.draw.issue.vo.DrawLotteryIssueVO;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;

/**
 * @desc H5数字彩公共接口
 * @author huangchengfang1219
 * @date 2017年10月17日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class DrawNumH5Controller extends BaseH5Controller {

	private static final Logger logger = LoggerFactory.getLogger(DrawNumH5Controller.class);
	@Autowired
	protected IDrawLotteryService drawLotteryService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		DrawLotteryIssueVO vo = new DrawLotteryIssueVO();
		vo.setPageIndex(Constants.NUM_0);
		vo.setPageSize(Constants.NUM_100);
		vo.setLotteryCode(getLotteryCode());
		String url = drawCoreUrl + "draw/num/list";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<PagingBO<DrawLotteryIssueBO>> result = JsonUtil.jsonToJackObject(resultStr,
				new TypeReference<ResultBO<PagingBO<DrawLotteryIssueBO>>>() {
				});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询数字彩{}开奖列表失败:{}", getLotteryCode(), result.getMessage());
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
		mav.addObject("issueList", issueList);
		mav.addObject("lastIssue", lastIssue);
		mav.setViewName("h5/num/" + DrawLotteryConstant.getLotteryKey(getLotteryCode()));
		return mav;
	}

	@RequestMapping(value = "/{issueCode}", method = RequestMethod.GET)
	public ModelAndView detail(@PathVariable("issueCode") String issueCode) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lotteryCode", getLotteryCode());
		params.put("issueCode", issueCode);
		String url = drawCoreUrl + "draw/num/detail";
		String resultStr = restTemplate.postForEntity(url, params, String.class).getBody();
		ResultBO<DrawLotteryIssueBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<DrawLotteryIssueBO>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询数字彩开奖详情{}-{}失败: {}", getLotteryCode(), issueCode, result.getMessage());
			throw new ServiceRuntimeException(result);
		}
		DrawLotteryIssueBO issue = result.getData();
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(getLotteryCode());
		ModelAndView mav = new ModelAndView();
		mav.addObject("lotteryType", lotteryType);
		mav.addObject("issue", issue);
		mav.setViewName("h5/num/" + DrawLotteryConstant.getLotteryKey(getLotteryCode()) + "-detail");
		return mav;
	}

	protected abstract Integer getLotteryCode();
}
