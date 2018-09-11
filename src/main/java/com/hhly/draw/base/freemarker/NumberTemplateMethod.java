package com.hhly.draw.base.freemarker;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.util.ObjectUtil;

import freemarker.template.SimpleNumber;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

@Component("numberTemplateMethod")
public class NumberTemplateMethod implements TemplateMethodModelEx {

	private static final BigDecimal Y = new BigDecimal("100000000");
	private static final BigDecimal W = new BigDecimal("10000");
	private static final String NAME_Y = "亿";
	private static final String NAME_W = "万";
	private static final String DEFAULT_FORMAT = "#,###.##";

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (ObjectUtil.isBlank(arguments)) {
			throw new TemplateModelException("arguments is missing");
		}
		Integer index = 0;
		SimpleNumber number = (SimpleNumber) arguments.get(0);
		index++;
		if (number == null) {
			return new String[] { "", "", "" };
		}
		String format = null;
		if (arguments.size() > index && arguments.get(index) != null && (arguments.get(index) instanceof SimpleScalar)) {
			format = ((SimpleScalar) arguments.get(index)).getAsString();
			index++;
		} else {
			format = DEFAULT_FORMAT;
		}
		Integer scale = null;
		if (arguments.size() > index && arguments.get(index) != null && (arguments.get(index) instanceof SimpleNumber)) {
			scale = ((SimpleNumber) arguments.get(index)).getAsNumber().intValue();
			index++;
		}
		String scaleMode = null;
		if (arguments.size() > index && arguments.get(index) != null && (arguments.get(index) instanceof SimpleScalar)) {
			scaleMode = ((SimpleScalar) arguments.get(index)).getAsString();
			index++;
		}
		BigDecimal b = new BigDecimal(number.toString());
		BigDecimal result = null;
		String name = null;
		if (b.compareTo(Y) >= 0) {// 超过一亿
			result = b.divide(Y);
			name = NAME_Y;
		} else if (b.compareTo(W) >= 0) {
			result = b.divide(W);
			name = NAME_W;
		} else {
			result = b;
			name = "";
		}
		if (scale != null && "ROUND_DOWN".equals(scaleMode)) {
			result = result.setScale(scale, BigDecimal.ROUND_DOWN);
		}
		DecimalFormat df = new DecimalFormat(format);
		String value = df.format(result);
		return new String[] { value + name, value, name };
	}

}
