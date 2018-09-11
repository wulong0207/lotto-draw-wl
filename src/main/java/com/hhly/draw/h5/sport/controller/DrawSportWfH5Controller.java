package com.hhly.draw.h5.sport.controller;

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
import com.hhly.draw.base.controller.BaseH5Controller;
import com.hhly.draw.http.commoncore.lotto.ILotteryIssueService;
import com.hhly.draw.http.drawcore.lottery.IDrawLotteryService;
import com.hhly.skeleton.base.bo.PagingBO;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.SportConstants;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.util.JsonUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.draw.lottery.bo.DrawLotteryTypeBO;
import com.hhly.skeleton.draw.sport.bo.SportWfBO;
import com.hhly.skeleton.draw.sport.vo.SportWfVO;

/**
 * @desc 胜负过关
 * @author huangchengfang1219
 * @date 2017年10月24日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/wf")
public class DrawSportWfH5Controller extends BaseH5Controller {

	private static final Logger logger = LoggerFactory.getLogger(DrawSportWfH5Controller.class);

	@Autowired
	private ILotteryIssueService lotteryIssueService;
	@Autowired
	protected IDrawLotteryService drawLotteryService;

	@RequestMapping(method = RequestMethod.GET)
	public ModelAndView index(SportWfVO vo) {
		return this.issue(null, vo);
	}

	@RequestMapping(value = "{issueCode}", method = RequestMethod.GET)
	public ModelAndView issue(@PathVariable String issueCode, SportWfVO vo) {
		if (vo == null) {
			vo = new SportWfVO();
		}
		vo.setIssueCode(issueCode);
		vo.setMatchStatusArr(SportConstants.SPORT_DRAW_CANCEL_STATUS);
		String url = drawCoreUrl + "draw/wf/listall";
		String resultStr = restTemplate.postForEntity(url, vo, String.class).getBody();
		ResultBO<PagingBO<SportWfBO>> result = JsonUtil.jsonToJackObject(resultStr, new TypeReference<ResultBO<PagingBO<SportWfBO>>>() {
		});
		if (result == null || result.isError() || result.getData() == null) {
			logger.error("查询胜负过关开奖列表失败:{}", result.getMessage());
			throw new ServiceRuntimeException(result);
		}
		PagingBO<SportWfBO> pageData = result.getData();

		ModelAndView mav = new ModelAndView();
		List<SportWfBO> matchList = pageData.getData();
		mav.addObject("matchList", matchList);
		if (ObjectUtil.isBlank(issueCode)) {
			mav.addObject("queryIssueCode", pageData.getOther());
		} else {
			mav.addObject("queryIssueCode", issueCode);
		}
		// 彩种信息
		DrawLotteryTypeBO lotteryType = drawLotteryService.findLotteryType(LotteryEnum.Lottery.SFGG.getName());
		mav.addObject("lotteryType", lotteryType);
		// 查找最近5期
		List<String> issueCodeList = lotteryIssueService.queryIssueCodeList(LotteryEnum.Lottery.SFGG.getName(),
				LotteryEnum.ConIssue.CURRENT.getValue(), Constants.NUM_5);
		mav.addObject("issueCodeList", issueCodeList);
		mav.addObject("queryVO", vo);
		mav.setViewName("h5/sport/wf");
		return mav;
	}

}
