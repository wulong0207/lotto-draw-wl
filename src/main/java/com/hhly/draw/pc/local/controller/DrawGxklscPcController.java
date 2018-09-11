package com.hhly.draw.pc.local.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 广西福彩快乐双彩
 * @author huangchengfang1219
 * @date 2018年01月18日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/gxklsc")
@Controller
public class DrawGxklscPcController extends DrawLocalPcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.GXKLSC.getName();
	}

}
