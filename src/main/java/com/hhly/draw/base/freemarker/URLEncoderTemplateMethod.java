package com.hhly.draw.base.freemarker;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hhly.skeleton.base.util.ObjectUtil;

import freemarker.template.SimpleScalar;
import freemarker.template.TemplateMethodModelEx;
import freemarker.template.TemplateModelException;

@Component("urlEncoderTemplateMethod")
public class URLEncoderTemplateMethod implements TemplateMethodModelEx {

	private static final String DEFAULT_CHARSET = "UTF-8";

	@SuppressWarnings("rawtypes")
	@Override
	public Object exec(List arguments) throws TemplateModelException {
		if (ObjectUtil.isBlank(arguments) || arguments.get(0) == null) {
			throw new TemplateModelException("arguments is missing");
		}
		String url = ((SimpleScalar) arguments.get(0)).getAsString();
		String charset;
		if (arguments.size() > 1 && arguments.get(1) != null) {
			charset = ((SimpleScalar) arguments.get(1)).getAsString();
		} else {
			charset = DEFAULT_CHARSET;
		}
		try {
			String encodeUrl = URLEncoder.encode(url, charset);
			return encodeUrl;
		} catch (UnsupportedEncodingException e) {
			throw new TemplateModelException("charset unsupported:" + charset, e);
		}
	}

}
