package com.hhly.draw.pc.high.kl10.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 天津快乐10分
 * @author huangchengfang1219
 * @date 2018年01月02日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/tjkl10")
@Controller
public class DrawTjKl10PcController extends DrawKl10PcController {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.TJKL10.getName();
	}

}
