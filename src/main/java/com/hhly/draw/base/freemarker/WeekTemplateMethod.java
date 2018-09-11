package com.hhly.draw.base.freemarker;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.ObjectUtil;

import freemarker.template.SimpleDate;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

@Component("weekTemplateMethod")
public class WeekTemplateMethod implements TemplateMethodModelEx {

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		Date date = null;
		Object dateObj = null;
		if (ObjectUtil.isBlank(arguments)) {
			dateObj = new Date();
		} else {
			dateObj = arguments.get(0);
		}
		if (dateObj == null) {
			return null;
		}
		if (dateObj instanceof SimpleDate) {
			date = ((SimpleDate) dateObj).getAsDate();
		} else if (dateObj instanceof Date) {
			date = (Date) dateObj;
		} else if (dateObj instanceof Long) {
			date = new Date((Long) dateObj);
		} else {
			throw new TemplateModelException("Wrong Date arguments");
		}
		return DateUtil.dayForWeekStr(date);
	}

}
