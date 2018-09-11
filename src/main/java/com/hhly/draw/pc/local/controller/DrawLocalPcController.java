package com.hhly.draw.pc.local.controller;

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
import com.hhly.draw.base.controller.BasePcController;
import com.hhly.draw.http.commoncore.lotto.ILotteryIssueService;
import com.hhly.draw.http.drawcore.lottery.IDrawLotteryService;
import com.hhly.draw.service.DrawMemberWinningService;
import com.hhly.draw.service.DrawOperateArticleService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.DrawLotteryConstant;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.draw.issue.bo.DrawLotteryIssueBO;
import com.hhly.skeleton.draw.issue.vo.DrawLotteryIssueVO;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;

public abstract class DrawLocalPcController extends BasePcController {

	private static final Logger logger = LoggerFactory.getLogger(DrawLocalPcController.class);

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
		String url = drawCoreUrl + "draw/local/detail";
		String resultStr = restTemplate.postForEntity(url, params, String.class).getBody();
		ResultBO<DrawLotteryIssueBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<DrawLotteryIssueBO>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询地方彩开奖详情失败: {}", lotteryCode);
			throw new ServiceRuntimeException(ResultBO.err(MessageCodeConstants.DATA_NOT_FOUND_SYS));
		}
		mav.addObject("drawLotteryIssue", result.getData());
		mav.addObject("queryIssueCode", issueCode);
		// 往期开奖
		mav.addObject("pastDrawLotteryList", queryPastDrawList());
		mav.setViewName("/pc/local/" + DrawLotteryConstant.getLotteryKey(getLotteryCode()));
		return mav;
	}

	private List<?> queryPastDrawList() {
		try {
			DrawLotteryIssueVO vo = new DrawLotteryIssueVO();
			vo.setPageIndex(Constants.NUM_0);
			vo.setPageSize(Constants.NUM_5);
			vo.setLotteryCode(getLotteryCode());
			String url = drawCoreUrl + "draw/local/list";
			String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
			ResultBO<PagingBO<DrawLotteryIssueBO>> result = JsonUtil.jsonToJackObject(resultStr,
					new TypeReference<ResultBO<PagingBO<DrawLotteryIssueBO>>>() {
					});
			if (result.isError() || result.getData() == null) {
				logger.error("往期开奖查询失败:{}", result.getMessage());
			}
			return result.getData().getData();
		} catch (Exception e) {
			logger.error("往期开奖查询失败", e);
		}
		return Collections.emptyList();
	}

	protected abstract Integer getLotteryCode();

}
