package com.hhly.lotto.api.common.controller.draw.${package}.v1_0;

import org.springframework.beans.factory.annotation.Autowired;

import com.hhly.drawcore.remote.${package}.service.IDraw${className}Service;
import com.hhly.drawcore.remote.${type}.service.IDraw${type?cap_first}Service;

/**
 * @desc ${name}
 * @author huangchengfang1219
 * @date ${(.now)?string('yyyy年MM月dd日')}
 * @company 益彩网络科技公司
 * @version 1.0
 */
public class Draw${className}CommonV10Controller extends Draw${(childType!type)?cap_first}CommonV10Controller {

	@Autowired
	private IDraw${className}Service draw${className}Service;

	@Override
	public IDraw${type?cap_first}Service getService() {
		return draw${className}Service;
	}

}
