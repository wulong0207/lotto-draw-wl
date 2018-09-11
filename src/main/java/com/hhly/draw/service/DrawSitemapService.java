package com.hhly.draw.service;

import java.io.OutputStream;

/**
 * 开奖公告网站地图
 *
 * @author huangchengfang1219
 * @date 2018年1月4日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public interface DrawSitemapService {

	String refresh() throws Exception;
	
	void download(OutputStream out) throws Exception;
}
