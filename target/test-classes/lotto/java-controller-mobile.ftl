package com.hhly.lotto.api.${platform}.controller.draw.${package}.v1_0;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hhly.lotto.api.common.controller.draw.${package}.v1_0.Draw${className}CommonV10Controller;

/**
 * @desc ${name}
 * @author huangchengfang1219
 * @date ${(.now)?string('yyyy年MM月dd日')}
 * @company 益彩网络科技公司
 * @version 1.0
 */
@RestController
@RequestMapping("/${platform}/v1.0/draw/${key}")
public class Draw${className}${platform?cap_first}V10Controller extends Draw${className}CommonV10Controller {

}
