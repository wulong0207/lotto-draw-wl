package com.hhly.draw.pc.${package}.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.hhly.drawcore.remote.${type}.service.IDraw${type?cap_first}Service;
import com.hhly.drawcore.remote.${package}.service.IDraw${className}Service;
import com.hhly.skeleton.base.common.LotteryEnum;

/**
 * @desc ${name}
 * @author huangchengfang1219
 * @date ${(.now)?string('yyyy年MM月dd日')}
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RequestMapping("/pc/${key}")
@Controller
public class Draw${className}PcController extends Draw${(childType!type)?cap_first}PcController {

	@Autowired
	private IDraw${className}Service draw${className}Service;

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.${enumName}.getName();
	}

	@Override
	public IDraw${type?cap_first}Service getService() {
		return draw${className}Service;
	}
}
