package com.hhly.drawcore.remote.${package}.service.impl;

import org.springframework.stereotype.Service;

import com.hhly.drawcore.remote.${type}.service.impl.Draw${type?cap_first}ServiceImpl;
import com.hhly.drawcore.remote.${package}.service.IDraw${className}Service;
import com.hhly.skeleton.base.common.LotteryEnum;

@Service("draw${className}Service")
public class Draw${className}ServiceImpl extends Draw${type?cap_first}ServiceImpl implements IDraw${className}Service {

	@Override
	public Integer getLotteryCode() {
		return LotteryEnum.Lottery.${enumName}.getName();
	}

}
