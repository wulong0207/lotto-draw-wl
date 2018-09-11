package com.hhly.draw.pc.sport.controller;

import java.util.List;

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
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;
import com.hhly.skeleton.draw.sport.bo.SportWfBO;
import com.hhly.skeleton.draw.sport.vo.SportWfVO;

/**
 * @desc 胜负过关
 * @author huangchengfang1219
 * @date 2017年10月27日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/pc/wf")
public class DrawSportWfPcController extends BasePcController {

	private static final Logger logger = LoggerFactory.getLogger(DrawSportWfPcController.class);

	@Autowired
	private IDrawLotteryService drawLotteryService;
	@Autowired
	private ILotteryIssueService lotteryIssueService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(SportWfVO vo) {
		return this.query(null, vo);
	}

	@RequestMapping(value = "/{issueCode}", method = RequestMethod.GET)
	public ModelAndView query(@PathVariable String issueCode, SportWfVO vo) {
		if (vo == null) {
			vo = new SportWfVO();
		}
		Integer lotteryCode = LotteryEnum.Lottery.SFGG.getName();
		vo.setIssueCode(issueCode);
		ModelAndView mav = new ModelAndView();
		// 最近彩期
		List<String> issueCodeList = lotteryIssueService.queryIssueCodeList(lotteryCode, LotteryEnum.ConIssue.CURRENT.getValue());
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(lotteryCode);
		// 赛事列表
		if (vo.getPageIndex() == null) {
			vo.setPageIndex(Constants.NUM_0);
		}
		vo.setPageSize(Constants.NUM_20);
		String url = drawCoreUrl + "draw/wf/list";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<PagingBO<SportWfBO>> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<PagingBO<SportWfBO>>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("胜负过关开奖公告查询失败", result.getMessage());
			throw new ServiceRuntimeException(result);
		}
		PagingBO<SportWfBO> pageData = result.getData();

		if (ObjectUtil.isBlank(issueCode)) {
			mav.addObject("queryIssueCode", pageData.getOther());
		} else {
			mav.addObject("queryIssueCode", issueCode);
		}

		// 构建返回对象
		mav.addObject("issueCodeList", issueCodeList);
		mav.addObject("lotteryType", lotteryType);
		mav.addObject("pageData", pageData);
		mav.addObject("queryVO", vo);
		mav.setViewName("pc/sport/wf");
		return mav;
	}
}
