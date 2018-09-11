package com.hhly.draw.base.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.hhly.skeleton.base.util.ObjectUtil;

/**
 * @desc 获取项目contextPath路径
 * @author huangchengfang1219
 * @date 2017年10月20日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public class ContextPathInterceptor extends HandlerInterceptorAdapter {

	public static final String ATTRIBUTE_CONTEXT_PATH = "Header-Context-Path";
	public static final String ATTRIBUTE_REQUEST_HTTP_PATH = "Request-HTTP-Path";
	public static final String ATTRIBUTE_REQUEST_HTTP_SCHEME = "Request-HTTP-Scheme";
	private static final String SCHEME = "://";
	private static final int PORT_80 = 80;
	private static final int PORT_443 = 443;
	private static final String HEADER_CONTEXT_PATH = "Context-Path";

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		String contextPath = request.getHeader(HEADER_CONTEXT_PATH);
		if (ObjectUtil.isBlank(contextPath)) {
			contextPath = null;
		} else if ("/".equals(contextPath)) {
			contextPath = "";
		} else if (!contextPath.matches("^[a-zA-Z0-9-_/]+$")) {
			throw new IllegalArgumentException(new StringBuilder(ATTRIBUTE_CONTEXT_PATH).append(": ").append(contextPath).toString());
		}
		StringBuilder httpPathBuilder = new StringBuilder();
		httpPathBuilder.append(request.getScheme()).append(SCHEME);
		httpPathBuilder.append(request.getServerName());
		if (request.getServerPort() != PORT_80 && request.getServerPort() != PORT_443) {
			httpPathBuilder.append(":").append(request.getServerPort());
		}
		request.setAttribute(ATTRIBUTE_REQUEST_HTTP_SCHEME, request.getScheme());
		if(contextPath != null){
			request.setAttribute(ATTRIBUTE_CONTEXT_PATH, contextPath);
		}
		request.setAttribute(ATTRIBUTE_REQUEST_HTTP_PATH, httpPathBuilder.toString());
		return super.preHandle(request, response, handler);
	}

}
