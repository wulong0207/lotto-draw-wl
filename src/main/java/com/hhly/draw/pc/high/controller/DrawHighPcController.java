package com.hhly.draw.pc.high.controller;

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
import com.hhly.draw.base.common.CpLotteryUrlConstants;
import com.hhly.draw.base.controller.BasePcController;
import com.hhly.draw.http.commoncore.lotto.ILotteryIssueService;
import com.hhly.draw.http.drawcore.lottery.IDrawLotteryService;
import com.hhly.draw.service.DrawMemberWinningService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.draw.issue.bo.DrawLotteryIssueBO;
import com.hhly.skeleton.draw.issue.vo.DrawLotteryIssueVO;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;
import com.hhly.skeleton.lotto.base.issue.bo.IssueBO;

/**
 * @desc PC端高频彩公用接口
 * @author huangchengfang1219
 * @date 2017年10月14日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public abstract class DrawHighPcController extends BasePcController {

	private static final Logger logger = LoggerFactory.getLogger(DrawHighPcController.class);

	@Autowired
	protected IDrawLotteryService drawLotteryService;
	@Autowired
	protected ILotteryIssueService lotteryIssueService;
	@Autowired
	protected DrawMemberWinningService drawMemberWinningService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index() {
		return this.date(null);
	}

	@RequestMapping(value = "{date}", method = RequestMethod.GET)
	public ModelAndView date(@PathVariable @DateTimeFormat(pattern = "yyyyMMdd") Date date) {
		Integer lotteryCode = getLotteryCode();
		ModelAndView mav = new ModelAndView();
		// 开奖列表查询
		PagingBO<DrawLotteryIssueBO> pageData = queryDrawList(date);
		List<DrawLotteryIssueBO> drawIssueList = pageData.getData();
		mav.addObject("drawIssueList", drawIssueList);
		// 查询日期
		String queryDateStr = queryDateStr(date, pageData);
		mav.addObject("queryDateStr", queryDateStr);
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(lotteryCode);
		mav.addObject("lotteryType", lotteryType);
		// 彩期列表转换为Map，方便页面渲染
		mav.addObject("drawIssueMap", convertDrawIssueMap(drawIssueList, lotteryType));
		// 当天最新一期开奖详情
		DrawLotteryIssueBO lastIssue = queryLastIssueDetail(drawIssueList);
		mav.addObject("lastIssue", lastIssue);
		// 当前期
		IssueBO curIssue = lotteryIssueService.queryCurrentIssue(lotteryCode);
		if (curIssue != null && queryDateStr.equals(DateUtil.convertDateToStr(curIssue.getLotteryTime(), DateUtil.DATE_FORMAT))) {
			mav.addObject("curIssue", curIssue);
			if (lastIssue == null) {
				mav.addObject("lastIssue", curIssue);
			}
		}
		if (CpLotteryUrlConstants.isOnSale(lotteryCode)) {
			// 中奖信息
			mav.addObject("userWinInfoList", drawMemberWinningService.findUserWinInfoList(lotteryCode));
		} else {
			// 最新中奖信息
			mav.addObject("newestWinInfoList", drawMemberWinningService.findNewestWinInfoList());
		}
		mav.addObject("queryDate", date);
		return mav;
	}

	// 开奖列表
	private PagingBO<DrawLotteryIssueBO> queryDrawList(Date date) {
		DrawLotteryIssueVO vo = new DrawLotteryIssueVO();
		vo.setStartTime(date);
		vo.setSortOrder("asc");
		vo.setLotteryCode(getLotteryCode());
		String url = drawCoreUrl + "draw/high/listall";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<PagingBO<DrawLotteryIssueBO>> result = JsonUtil.jsonToJackObject(resultStr,
				new TypeReference<ResultBO<PagingBO<DrawLotteryIssueBO>>>() {
				});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询高频彩列表{}出错:{}", getLotteryCode(), result.getMessage());
			throw new ServiceRuntimeException(result);
		}
		return result.getData();
	}

	// 处理查询时间
	private String queryDateStr(Date date, PagingBO<DrawLotteryIssueBO> pageData) {
		String queryDateStr = "";
		if (date == null) {
			queryDateStr = (String) pageData.getOther();
			if (ObjectUtil.isBlank(queryDateStr)) {
				queryDateStr = DateUtil.convertDateToStr(new Date(), DateUtil.DATE_FORMAT);
			}
		} else {
			queryDateStr = DateUtil.convertDateToStr(date, DateUtil.DATE_FORMAT);
		}
		return queryDateStr;
	}

	// 列表转换Map
	private Map<String, DrawLotteryIssueBO> convertDrawIssueMap(List<DrawLotteryIssueBO> drawIssueList, DrawLotteryTypeBO lotteryType) {
		Map<String, DrawLotteryIssueBO> drawIssueMap = new HashMap<String, DrawLotteryIssueBO>();
		if (ObjectUtil.isBlank(drawIssueList)) {
			return drawIssueMap;
		}
		Integer totalIssueNum = lotteryType.getTotalIssueNum();
		Integer issueDisplayNum = String.valueOf(totalIssueNum).length();
		for (DrawLotteryIssueBO drawIssue : drawIssueList) {
			String issueCode = drawIssue.getIssueCode();
			drawIssueMap.put(issueCode.substring(issueCode.length() - issueDisplayNum), drawIssue);
		}
		return drawIssueMap;
	}

	// 查询当天最新一期开奖详情
	private DrawLotteryIssueBO queryLastIssueDetail(List<DrawLotteryIssueBO> drawIssueList) {
		if (ObjectUtil.isBlank(drawIssueList)) {
			return null;
		}
		String issueCode = drawIssueList.get(drawIssueList.size() - 1).getIssueCode();
		DrawLotteryIssueVO vo = new DrawLotteryIssueVO();
		vo.setLotteryCode(getLotteryCode());
		vo.setIssueCode(issueCode);
		String url = drawCoreUrl + "draw/high/detail";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<DrawLotteryIssueBO> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<DrawLotteryIssueBO>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询高频彩最新一期开奖详情出错：{}", result.getMessage());
			throw new ServiceRuntimeException(ResultBO.err(MessageCodeConstants.DATA_NOT_FOUND_SYS));
		}
		return result.getData();
	}

	protected abstract Integer getLotteryCode();

}
