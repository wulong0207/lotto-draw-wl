package com.hhly.draw.pc.home.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONObject;
import com.hhly.draw.base.common.CpLotteryUrlConstants;
import com.hhly.draw.base.util.RedisUtil;
import com.hhly.draw.common.home.controller.HomeController;
import com.hhly.draw.http.commoncore.lotto.ILotteryService;
import com.hhly.draw.http.commoncore.operate.IOperateLotteryService;
import com.hhly.skeleton.base.common.DicEnum.PlatFormEnum;
import com.hhly.skeleton.base.constants.CacheConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.DrawLotteryConstant;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;
import com.hhly.skeleton.lotto.base.operate.bo.OperateLotteryBO;
import com.hhly.skeleton.lotto.base.operate.vo.OperateLotteryLottVO;

/**
 * @desc PC端首页
 * @author huangchengfang1219
 * @date 2017年10月18日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/index")
@Controller
public class HomePcController extends HomeController {

	@SuppressWarnings("unused")
	private static final Logger logger = LoggerFactory.getLogger(HomePcController.class);

	@Autowired
	private IOperateLotteryService operateLotteryService;
	@Autowired
	private RedisUtil redisUtil;
	@Autowired
	private ILotteryService lotteryService;

	@ModelAttribute("cpLotteryMap")
	public Map<String, String> bindCpLotteryUrlMap() {
		return CpLotteryUrlConstants.getWebCpUrlMap();
	}

	@Override
	public ModelAndView index() {
		ModelAndView mav = super.index();
		mav.setViewName("pc/index");
		return mav;
	}

	@Override
	public ModelAndView high() {
		ModelAndView mav = super.high();
		mav.setViewName("pc/index-gp");
		LotteryVO vo = new LotteryVO();
		vo.setDrawType(Constants.NUM_2);
		List<LotteryBO> lotteryList = lotteryService.queryLotterySelectList(vo);
		mav.addObject("lotteryList", lotteryList);
		mav.addObject("lotteryKeyJson", JSONObject.toJSON(DrawLotteryConstant.getAllLotteryKey()));
		return mav;
	}

	@Override
	public ModelAndView local() {
		ModelAndView mav = super.local();
		mav.setViewName("pc/index-local");
		LotteryVO vo = new LotteryVO();
		vo.setDrawType(Constants.NUM_3);
		List<LotteryBO> lotteryList = lotteryService.queryLotterySelectList(vo);
		mav.addObject("lotteryList", lotteryList);
		mav.addObject("lotteryKeyJson", JSONObject.toJSON(DrawLotteryConstant.getAllLotteryKey()));
		return mav;
	}

	/**
	 * 彩种运营菜单
	 * 
	 * @return
	 */
	@RequestMapping(value = "operlottery", method = RequestMethod.GET)
	public ModelAndView operlottery() {
		ModelAndView mav = new ModelAndView();
		OperateLotteryLottVO vo = new OperateLotteryLottVO();
		vo.setPlatform(PlatFormEnum.WEB.getValue());
		String key = CacheConstants.C_COMM_LOTTERY_FIND_HOME_OPER + vo.getPlatform() + vo.getCategoryId();
		OperateLotteryBO operLotteryBO = (OperateLotteryBO) redisUtil.getObj(key);
		if (operLotteryBO != null) {
			// 如果缓存中的时间超时则重新查询数据
			String nowstr = DateUtil.dayForWeek() + DateUtil.getNow("HH:mm:ss");
			if (!GetOnlineWeekCheck(operLotteryBO.getOnlineWeek(), operLotteryBO.getOnlineTime(), operLotteryBO.getOfflineWeek(),
					operLotteryBO.getOfflineTime(), nowstr)) {
				operLotteryBO = null;
			}
		}
		if (operLotteryBO == null) {
			operLotteryBO = operateLotteryService.findHomeOperLottery(vo);
		}
		mav.addObject("operLotteryList", operLotteryBO.getLotteryInfoList());
		mav.setViewName("pc/home/operlottery");
		return mav;
	}

	/**
	 * 判断当前时间是否在时间区间之内
	 * 
	 * @param onlineWeek：上线周期
	 * @param onlineTime：上线时间
	 * @param offlineWeek：下线周期
	 * @param offlineTime：下线时间
	 * @param nowstr
	 * @return
	 */
	private boolean GetOnlineWeekCheck(Short onlineWeek, String onlineTime, Short offlineWeek, String offlineTime, String nowstr) {
		String upstr = onlineWeek + onlineTime;
		String downstr = offlineWeek + offlineTime;
		if (onlineWeek <= offlineWeek) {
			if (nowstr.compareTo(upstr) > 0 && nowstr.compareTo(downstr) <= 0) {
				return true;
			}
		} else {
			// 下线时间小于上线时间
			String endstr = 7 + "23:59:59";// 星期天结束时间
			String startstr = 1 + "00:00:00";// 星期1开始时间
			if ((nowstr.compareTo(upstr) > 0 && nowstr.compareTo(endstr) <= 0)
					|| (nowstr.compareTo(startstr) > 0 && nowstr.compareTo(downstr) <= 0)) {
				return true;
			}
		}
		return false;
	}
}
