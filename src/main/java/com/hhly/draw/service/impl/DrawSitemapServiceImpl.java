package com.hhly.draw.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.hhly.draw.http.commoncore.lotto.ILotteryIssueService;
import com.hhly.draw.http.commoncore.lotto.ILotteryService;
import com.hhly.draw.service.DrawSitemapService;
import com.hhly.skeleton.base.bo.ResultBO;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.base.constants.DrawLotteryConstant;
import com.hhly.skeleton.base.constants.MessageCodeConstants;
import com.hhly.skeleton.base.constants.SymbolConstants;
import com.hhly.skeleton.base.exception.ServiceRuntimeException;
import com.hhly.skeleton.base.qiniu.QiniuUpload;
import com.hhly.skeleton.base.qiniu.QiniuUploadResultVO;
import com.hhly.skeleton.base.qiniu.QiniuUploadVO;
import com.hhly.skeleton.base.util.DateUtil;
import com.hhly.skeleton.base.util.ObjectUtil;
import com.hhly.skeleton.lotto.base.lottery.bo.LotteryBO;
import com.hhly.skeleton.lotto.base.lottery.vo.LotteryVO;

/**
 * 网站地图
 *
 * @author huangchengfang1219
 * @date 2018年1月4日
 * @company 益彩网络科技公司
 * @version 1.0
 */
@Service("drawSitemapService")
public class DrawSitemapServiceImpl implements DrawSitemapService {

	private static final Logger logger = LoggerFactory.getLogger(DrawSitemapServiceImpl.class);

	private static final String URL_FLAG_START = "s";
	private static final String URL_FLAG_END = "e";
	private static final int MAX_LINE = 50000;

	@Value("${web_kj_url}")
	private String webKjUrl;
	@Value("${wap_kj_url}")
	private String wapKjUrl;
	@Value("${file_seo_path}")
	private String fileSeoPath;
	@Value("${before_file_url}")
	private String beforeFileUrl;
	/** 7牛accessKey **/
	@Value("${accessKey}")
	protected String accessKey;
	/** 7牛secretKey **/
	@Value("${secretKey}")
	protected String secretKey;
	/** bucketName **/
	@Value("${bucketName}")
	protected String bucketName;
	/** 允许批量上传文件数量 **/
	@Value("${uploadLimit}")
	protected Integer uploadLimit;
	/** 允许上传文件类型 **/
	@Value("${fileType}")
	protected String fileType;
	/** 允许批量上传文件大小 */
	@Value("${limitSize}")
	protected String limitSize;

	@Autowired
	private ILotteryIssueService lotteryIssueService;
	@Autowired
	private ILotteryService lotteryService;

	@Override
	public String refresh() throws Exception {
		ByteArrayInputStream in = getInputStream();
		if (in == null) {
			return null;
		}
		return uploadToQiniu(in);
	}

	@Override
	public void download(OutputStream out) throws Exception {
		ByteArrayInputStream in = getInputStream();
		if (in == null) {
			throw new ServiceRuntimeException(ResultBO.err(MessageCodeConstants.DATA_NOT_EXIST));
		}
		try {
			IOUtils.copy(in, out);
		} finally {
			IOUtils.closeQuietly(in);
		}
	}

	private ByteArrayInputStream getInputStream() throws Exception {
		ExecutorService threadPool = Executors.newFixedThreadPool(5);
		try {
			CompletionService<List<String>> cs = new ExecutorCompletionService<>(threadPool);
			// 这里使用futureList，按序获取结果
			List<Future<List<String>>> pcFutureList = new ArrayList<>();
			doPcUrl(Constants.NUM_1, cs, pcFutureList);// 全国
			doPcUrl(Constants.NUM_2, cs, pcFutureList);// 高频
			doPcUrl(Constants.NUM_3, cs, pcFutureList);// 地方
			List<Future<List<String>>> h5FutureList = new ArrayList<>();
			// doH5Url(Constants.NUM_1, cs, h5FutureList);// 全国
			// doH5Url(Constants.NUM_2, cs, h5FutureList);// 高频
			// doH5Url(Constants.NUM_3, cs, h5FutureList);// 地方
			if (pcFutureList.isEmpty() && h5FutureList.isEmpty()) {
				logger.info("没有URL生成");
				return null;
			}
			return createZipFile(pcFutureList, h5FutureList);
		} finally {
			threadPool.shutdown();
		}
	}

	private void doPcUrl(Integer drawType, CompletionService<List<String>> cs, List<Future<List<String>>> futureList) {
		LotteryVO vo = new LotteryVO();
		vo.setDrawType(drawType);
		List<LotteryBO> lotteryList = lotteryService.queryLotterySelectList(vo);
		for (LotteryBO lottery : lotteryList) {
			String lotteryKey = DrawLotteryConstant.getLotteryKey(lottery.getLotteryCode());
			if (!ObjectUtil.isBlank(lotteryKey)) {
				futureList.add(doPcUrl(drawType, lottery.getLotteryCode(), cs));
			}
		}
	}

	private Future<List<String>> doPcUrl(final Integer drawType, final Integer lotteryCode, CompletionService<List<String>> cs) {
		return cs.submit(new Callable<List<String>>() {

			@Override
			public List<String> call() throws Exception {
				logger.info("正在处理彩种:{}", lotteryCode);
				List<String> issueList = findIssueList(drawType, lotteryCode);
				List<String> urlList = new ArrayList<String>(issueList.size() + Constants.NUM_1);
				String lotteryKey = DrawLotteryConstant.getLotteryKey(lotteryCode);
				String indexUrl = new StringBuilder().append(webKjUrl).append(SymbolConstants.OBLIQUE_LINE).append(lotteryKey)
						.append(SymbolConstants.OBLIQUE_LINE).toString();
				urlList.add(indexUrl);
				if (LotteryEnum.Lottery.FB.getName() == lotteryCode || LotteryEnum.Lottery.BB.getName() == lotteryCode) {
					// 竞足竞篮URl处理
					for (String issue : issueList) {
						issue = issue.replace(SymbolConstants.TRAVERSE_SLASH, "");
						urlList.add(new StringBuilder(indexUrl).append(URL_FLAG_START).append(issue).append(SymbolConstants.UNDERLINE)
								.append(URL_FLAG_END).append(issue).append(Constants.HTML_SUFFIX).toString());
					}
				} else if (drawType == Constants.NUM_2) {
					// 高频彩URl处理
					for (String issue : issueList) {
						urlList.add(new StringBuilder(indexUrl).append(issue.replaceAll(SymbolConstants.TRAVERSE_SLASH, ""))
								.append(Constants.HTML_SUFFIX).toString());
					}
				} else {
					// 其它使用彩期的URL处理
					for (String issue : issueList) {
						urlList.add(new StringBuilder(indexUrl).append(issue).append(Constants.HTML_SUFFIX).toString());
					}
				}
				return urlList;
			}

		});
	}

	@SuppressWarnings("unused")
	private void doH5Url(Integer drawType, CompletionService<List<String>> cs, List<Future<List<String>>> futureList) {
		LotteryVO vo = new LotteryVO();
		vo.setDrawType(drawType);
		List<LotteryBO> lotteryList = lotteryService.queryLotterySelectList(vo);
		for (LotteryBO lottery : lotteryList) {
			String lotteryKey = DrawLotteryConstant.getLotteryKey(lottery.getLotteryCode());
			if (!ObjectUtil.isBlank(lotteryKey)) {
				futureList.add(doH5Url(drawType, lottery.getLotteryCode(), cs));
			}
		}
	}

	private Future<List<String>> doH5Url(final Integer drawType, final Integer lotteryCode, CompletionService<List<String>> cs) {
		return cs.submit(new Callable<List<String>>() {

			@Override
			public List<String> call() throws Exception {
				logger.info("正在处理彩种:{}", lotteryCode);
				List<String> issueList = findIssueList(drawType, lotteryCode);
				List<String> urlList = new ArrayList<String>(issueList.size() + Constants.NUM_1);
				String lotteryKey = DrawLotteryConstant.getLotteryKey(lotteryCode);
				String indexUrl = new StringBuilder().append(wapKjUrl).append(SymbolConstants.OBLIQUE_LINE).append(lotteryKey)
						.append(SymbolConstants.OBLIQUE_LINE).toString();
				urlList.add(indexUrl);
				if (LotteryEnum.Lottery.FB.getName() == lotteryCode || LotteryEnum.Lottery.BB.getName() == lotteryCode) {
					// 竞足竞篮URl处理
					for (String issue : issueList) {
						issue = issue.replace(SymbolConstants.TRAVERSE_SLASH, "");
						urlList.add(
								new StringBuilder(indexUrl).append(URL_FLAG_START).append(issue).append(Constants.HTML_SUFFIX).toString());
					}
				} else if (drawType == Constants.NUM_2) {
					// 高频彩URl处理
					for (String issue : issueList) {
						urlList.add(new StringBuilder(indexUrl).append(issue.replaceAll(SymbolConstants.TRAVERSE_SLASH, ""))
								.append(Constants.HTML_SUFFIX).toString());
					}
					urlList.add(new StringBuilder(indexUrl).append("detail").append(Constants.HTML_SUFFIX).toString());
				} else {
					// 其它使用彩期的URL处理
					for (String issue : issueList) {
						urlList.add(new StringBuilder(indexUrl).append(issue).append(Constants.HTML_SUFFIX).toString());
					}
				}
				return urlList;
			}

		});
	}

	private List<String> findIssueList(int drawType, Integer lotteryCode) {
		LotteryVO lotteryVO = new LotteryVO();
		lotteryVO.setLotteryCode(lotteryCode);
		boolean isJc = LotteryEnum.Lottery.FB.getName() == lotteryCode || LotteryEnum.Lottery.BB.getName() == lotteryCode;
		if (drawType == Constants.NUM_2 || isJc) {
			lotteryVO.setQryFlag(Constants.NUM_2);
		}
		lotteryVO.setCurrentIssue(isJc ? (short) Constants.NUM_1 : (short) Constants.NUM_2);
		lotteryVO.setCurrentIssue(LotteryEnum.ConIssue.LAST_CURRENT.getValue());
		return lotteryIssueService.queryIssueByLottery(lotteryVO);
	}

	/**
	 * 写入文件，并上传
	 * 
	 * @param fileName
	 * @param cs
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */
	private ByteArrayInputStream createZipFile(List<Future<List<String>>> webFutureList, List<Future<List<String>>> wapFutureList)
			throws Exception {
		ByteArrayOutputStream byteOut = null;
		ZipOutputStream zipOut = null;
		try {
			byteOut = new ByteArrayOutputStream();
			zipOut = new ZipOutputStream(byteOut);
			writeToZip(zipOut, webFutureList, "pcSitemap", webKjUrl);
			writeToZip(zipOut, wapFutureList, "h5Sitemap", wapKjUrl);
			zipOut.flush();
		} catch (Exception e) {
			logger.error("开奖公告SEO文件生成失败", e);
			throw e;
		} finally {
			IOUtils.closeQuietly(zipOut);
			IOUtils.closeQuietly(byteOut);
		}
		return new ByteArrayInputStream(byteOut.toByteArray());
	}

	private void writeToZip(ZipOutputStream zipOut, List<Future<List<String>>> futureList, String entryName, String indexUrl)
			throws Exception {
		if (ObjectUtil.isBlank(futureList)) {
			return;
		}
		int fileNum = 1;
		int count = 3;
		zipOut.putNextEntry(new ZipEntry(entryName + fileNum + ".txt"));
		writeUrl(zipOut, indexUrl);
		writeUrl(zipOut, new StringBuilder().append(indexUrl).append(SymbolConstants.OBLIQUE_LINE).append("index/high.html").toString());
		writeUrl(zipOut, new StringBuilder().append(indexUrl).append(SymbolConstants.OBLIQUE_LINE).append("index/local.html").toString());
		for (Future<List<String>> future : futureList) {
			List<String> urlList = future.get();
			for (String url : urlList) {
				if (count % MAX_LINE == 0) {
					fileNum++;
					zipOut.putNextEntry(new ZipEntry(entryName + fileNum + ".txt"));
				}
				writeUrl(zipOut, url);
				count++;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private String uploadToQiniu(ByteArrayInputStream byteIn) {
		QiniuUploadVO qiniuUploadVO = new QiniuUploadVO(accessKey, secretKey, bucketName, uploadLimit, fileType, fileSeoPath,
				Long.parseLong(limitSize));
		StringBuilder fileNameBuilder = new StringBuilder(fileSeoPath);
		fileNameBuilder.append("sitemap_kj");
		fileNameBuilder.append(DateUtil.getNow("yyMMdd"));
		fileNameBuilder.append(".zip");
		qiniuUploadVO.setFileRelativePath(fileNameBuilder.toString());
		QiniuUpload qiniu = new QiniuUpload(qiniuUploadVO);
		ResultBO<?> result = qiniu.uploadFileNotRename(byteIn);
		if (result == null || result.isError()) {
			logger.error("开奖公告SEO文件上传失败");
			throw new ServiceRuntimeException("开奖公告SEO文件上传失败");
		}
		List<QiniuUploadResultVO> list = (List<QiniuUploadResultVO>) result.getData();
		return ObjectUtil.isBlank(list) ? null : (beforeFileUrl + list.get(0).getFileName());
	}

	private void writeUrl(ZipOutputStream zipOut, String url) throws IOException {
		zipOut.write(url.getBytes());
		zipOut.write(SymbolConstants.NEW_LINE.getBytes());
	}

}