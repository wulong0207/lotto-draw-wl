package com.hhly.draw.pc.num.controller;

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
import com.hhly.draw.base.controller.BasePcController;
import com.hhly.draw.http.commoncore.lotto.ILotteryIssueService;
import com.hhly.draw.http.drawcore.lottery.IDrawLotteryService;
import com.hhly.draw.service.DrawMemberWinningService;
import com.hhly.draw.service.DrawOperateArticleService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.constants.DrawLotteryConstant;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.draw.issue.bo.DrawLotteryIssueBO;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;

public abstract class DrawNumPcController extends BasePcController {

	private static final Logger logger = LoggerFactory.getLogger(DrawNumPcController.class);

	@Autowired
	protected IDrawLotteryService drawLotteryService;
	@Autowired
	protected ILotteryIssueService lotteryIssueService;
	@Autowired
	protected DrawMemberWinningService drawMemberWinningService;
	@Autowired
	protected DrawOperateArticleService drawOperateArticleService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return this.issue(null);
	}

	@RequestMapping(value = "{issueCode}", method = RequestMethod.GET)
	public ModelAndView issue(@PathVariable String issueCode) {
		ModelAndView mav = new ModelAndView();
		Integer lotteryCode = getLotteryCode();
		// 最近彩期
		List<String> issueCodeList = lotteryIssueService.queryIssueCodeList(getLotteryCode(), LotteryEnum.ConIssue.LAST_CURRENT.getValue());
		mav.addObject("issueCodeList", issueCodeList);
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(lotteryCode);
		mav.addObject("lotteryType", lotteryType);
		// 开奖详情
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
		mav.addObject("drawLotteryIssue", result.getData());
		// 资讯信息
		mav.addAllObjects(drawOperateArticleService.findArticles(lotteryCode));
		// 中奖信息
		mav.addObject("userWinInfoList", drawMemberWinningService.findUserWinInfoList(lotteryCode));
		mav.addObject("queryIssueCode", issueCode);
		mav.setViewName("/pc/num/" + DrawLotteryConstant.getLotteryKey(getLotteryCode()));
		return mav;
	}

	protected abstract Integer getLotteryCode();

}
