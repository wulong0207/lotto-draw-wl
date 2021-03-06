package com.hhly.draw.h5.high.kl10.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc 广西快乐10分
 * @author huangchengfang1219
 * @date 2018年01月02日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Controller
@RequestMapping("/h5/gxkl10")
public class DrawGxKl10H5Controller extends DrawKl10H5Controller {

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.GXKL10.getName();
	}

}
