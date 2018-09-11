package com.hhly.draw.pc.sport.controller;

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
import com.hhly.draw.base.controller.BasePcController;
import com.hhly.draw.http.commoncore.lotto.ILotteryIssueService;
import com.hhly.draw.http.drawcore.lottery.IDrawLotteryService;
import com.hhly.draw.service.DrawMemberWinningService;
import com.hhly.draw.service.DrawOperateArticleService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;
import com.hhly.skeleton.draw.sport.bo.DrawSportOldBO;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryIssueBaseBO;

@Controller
@RequestMapping("/pc/sfc")
public class DrawSportSfcPcController extends BasePcController {

	private static final Logger logger = LoggerFactory.getLogger(DrawSportJq4PcController.class);

	@Autowired
	private IDrawLotteryService drawLotteryService;
	@Autowired
	private ILotteryIssueService lotteryIssueService;
	@Autowired
	protected DrawMemberWinningService drawMemberWinningService;
	@Autowired
	protected DrawOperateArticleService drawOperateArticleService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return this.detail(null);
	}

	@RequestMapping(value = "{issueCode}", method = RequestMethod.GET)
	public ModelAndView detail(@PathVariable String issueCode) {
		Integer lotteryCode = LotteryEnum.Lottery.SFC.getName();
		ModelAndView mav = new ModelAndView();
		// 开奖详情
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("lotteryCode", lotteryCode);
		params.put("issueCode", issueCode);
		String url = drawCoreUrl + "draw/sfc/detail";
		String resultStr = restTemplate.postForEntity(url, params, String.class).getBody();
		ResultBO<DrawSportOldBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<DrawSportOldBO>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询胜负彩开奖详情{}失败: {}", issueCode, result.getMessage());
			throw new ServiceRuntimeException(result);
		}
		DrawSportOldBO drawLotteryIssue = result.getData();
		// 最近彩期
		List<String> issueCodeList = lotteryIssueService.queryIssueCodeList(lotteryCode, LotteryEnum.ConIssue.LAST_CURRENT.getValue());
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(lotteryCode);
		// 当前期
		LotteryIssueBaseBO lotteryBase = lotteryIssueService.findLotteryIssueBase(lotteryCode);
		mav.addObject("curIssue", lotteryBase.getCurIssue());
		mav.addObject("lastIssue", lotteryBase.getLatestIssue());
		// 资讯信息
		mav.addAllObjects(drawOperateArticleService.findArticles(lotteryCode));
		// 中奖信息
		mav.addObject("userWinInfoList", drawMemberWinningService.findUserWinInfoList(lotteryCode));
		mav.addObject("issueCodeList", issueCodeList);
		mav.addObject("drawLotteryIssue", drawLotteryIssue);
		mav.addObject("lotteryType", lotteryType);
		mav.addObject("queryIssueCode", issueCode);
		mav.setViewName("pc/sport/sfc");
		return mav;
	}

}
