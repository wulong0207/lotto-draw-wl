package com.hhly.draw.h5.high.ssc.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 天津时时彩
 * @author huangchengfang1219
 * @date 2018年01月02日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/tjssc")
public class DrawTjSscH5Controller extends DrawSscH5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.TJSSC.getName();
	}

}
