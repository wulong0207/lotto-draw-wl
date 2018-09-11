package com.hhly.draw.h5.${package}.controller;

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
@Controller
@RequestMapping("/h5/${key}")
public class Draw${className}H5Controller extends Draw${(childType!type)?cap_first}H5Controller {

	@Autowired
	private IDraw${className}Service draw${className}Service;

	@Override
	public IDraw${type?cap_first}Service getService() {
		return draw${className}Service;
	}

	@Override
	protected Integer getLotteryCode() {
		return LotteryEnum.Lottery.${enumName}.getName();
	}

}
