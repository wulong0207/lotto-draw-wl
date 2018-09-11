package com.hhly.draw.pc.home.controller;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.hhly.draw.service.DrawSitemapService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.Md5Util;
import com.hhly.skeleton.base.util.ObjectUtil;

@RequestMapping("/pc/sitemap")
@Controller
public class SitemapController {

	private static final Logger logger = LoggerFactory.getLogger(SitemapController.class);
	private static final String DEFAULT_PASSWORD = Md5Util.md5_32("hhly.2ncai");

	@Autowired
	private DrawSitemapService drawSitemapService;

	@Value("${spring.profiles.active}")
	private String profile;

	/**
	 * 刷新网站站点
	 * 
	 * @throws Exception
	 */
	@RequestMapping(value = "refresh", method = RequestMethod.GET)
	@ResponseBody
	public Object refresh(@RequestParam String password) throws Exception {
		if (!DEFAULT_PASSWORD.equalsIgnoreCase(password)) {
			return ResultBO.err(MessageCodeConstants.PASSWORD_FORMAT_ERROR_FIELD);
		}
		try {
			String path = drawSitemapService.refresh();
			return ObjectUtil.isBlank(path) ? ResultBO.err(MessageCodeConstants.DATA_NOT_EXIST) : ResultBO.ok(path);
		} catch (Exception e) {
			logger.error("site刷新失败", e);
			return ResultBO.err(MessageCodeConstants.SYS_ERROR_SYS);
		}
	}

	/**
	 * 下载
	 * 
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "download", method = RequestMethod.GET)
	@ResponseBody
	public Object download(@RequestParam String password, HttpServletResponse response) throws Exception {
		if (!DEFAULT_PASSWORD.equalsIgnoreCase(password)) {
			return ResultBO.err(MessageCodeConstants.PASSWORD_FORMAT_ERROR_FIELD);
		}
		try {
			response.setContentType("application/octet-stream");
			String fileName = String.format("sitemap_kj_%s_%s.zip", profile, DateUtil.getNow("yyMMdd"));
			response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
			drawSitemapService.download(response.getOutputStream());
			return null;
		} catch (Exception e) {
			logger.error("site下载失败", e);
			return ResultBO.err(MessageCodeConstants.SYS_ERROR_SYS);
		}
	}
}
