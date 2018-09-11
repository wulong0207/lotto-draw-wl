package com.hhly.draw.pc.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 河北福彩排列5
 * @author huangchengfang1219
 * @date 2018年01月04日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/hebpl5")
@Controller
public class DrawHebpl5PcController extends DrawLocalPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.HEBPL5.getName();
	}

}
