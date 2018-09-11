package code;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hhly.skeleton.base.common.LotteryEnum;

import freemarker.template.Configuration;
import freemarker.template.DefaultObjectWrapper;
import freemarker.template.Template;
import freemarker.template.TemplateException;

public class CodeGenerate {

	private static final String PATH_WORKSPACE = "D:\\Development\\Workspace\\eclipse\\";
	private static final String PATH_DRAW_CORE = PATH_WORKSPACE + "draw-core\\";
	private static final String PATH_LOTTO = PATH_WORKSPACE + "lotto\\";
	private static final String PATH_LOTTO_DRAW = PATH_WORKSPACE + "lotto-draw\\";
	private static final String PATH_LOTTO_SKELETON = PATH_WORKSPACE + "lotto-skeleton\\";
	private static final String SRC_JAVA_PATH = "src\\main\\java\\";
	private static final String SRC_RESOURCES_PATH = "src\\main\\resources\\";
	private static final String SRC_HTML_PATH = "src\\main\\webapp\\WEB-INF\\html\\";

	private static final int POSITION_LOTTO_SKELETON_DRAW_LOTTERY_CONSTANT = -53;// lotto-skeleton.DrawLotteryConstant

	private static final int POSITION_DRAW_CORE_HESSIAN_XML = -2;// draw-code-remote.servlet.xml
	private static final int POSITION_DRAW_CORE_HESSIAN_XML_DRAW_CODE_PARSE_UTIL = -198;// draw-code.DrawCodeParseUtil
	private static final int POSITION_DRAW_CORE_HESSIAN_XML_LOCAL_DRAW_DETAIL_PARSE = -5;// LocalDrawDetailParseExecutor
	private static final int POSITION_DRAW_CORE_HESSIAN_XML_HIGH_DRAW_DETAIL_PARSE_EXECUTOR = -18;// draw-code.HighDrawDetailParseExecutor

	private static final int POSITION_LOTTO_HESSIAN_XML = -2;// lotto.hessian.xml
	private static final int POSITION_LOTTO_HESSIAN_PROPERTIES = -16;// lotto.hessian.properties

	private static final int POSITION_LOTTO_DRAW_HESSIAN_XML = -36;// lotto-draw.hessian.xml
	private static final int POSITION_LOTTO_DRAW_HESSIAN_PROPERTIES = -14;// lotto-draw.hessian.properties

	private static final String CHARSET = "UTF-8";

	private List<LotteryEnum.Lottery> lotteryList = new ArrayList<>();
	private Configuration configuration;
	private String projectPath;
	private boolean isGenerateDrawCode;
	private boolean isGenerateLotto;
	private boolean isGenerateLottoDraw;

	public void addLottery(LotteryEnum.Lottery lottery) {
		lotteryList.add(lottery);
	}

	public void execute() throws IOException, TemplateException {
		init();
		for (LotteryEnum.Lottery lottery : lotteryList) {
			executeOne(lottery);
		}
	}

	@SuppressWarnings("deprecation")
	public void init() throws IOException {
		projectPath = new File("").getAbsolutePath();
		configuration = new Configuration();
		String path = projectPath + "\\src\\test\\resources";
		configuration.setDirectoryForTemplateLoading(new File(path));
		configuration.setObjectWrapper(new DefaultObjectWrapper());
		configuration.setDefaultEncoding("UTF-8");
	}

	private void executeOne(LotteryEnum.Lottery lottery) throws IOException, TemplateException {
		Map<String, Object> data = getData(lottery);
		// draw-core
		if (isGenerateDrawCode) {
			generateDrawCode(data);
		}
		// lotto
		if (isGenerateLotto) {
			generateLotto(data);
		}
		// lotto-draw
		if (isGenerateLottoDraw) {
			generateLottoDraw(data);
		}
	}

	private Map<String, Object> getData(LotteryEnum.Lottery lottery) {
		System.out.println(lottery.getName());
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("name", lottery.getDesc());
		data.put("enumName", lottery.toString());
		String enumNameLower = lottery.toString().toLowerCase();
		if ("d11x5".equals(enumNameLower) || "dkl10".equals(enumNameLower)) {
			enumNameLower = "g" + enumNameLower;
		} else if ("hl11x5".equals(enumNameLower)) {
			enumNameLower = "hn11x5";
		} else if ("xzgk3".equals(enumNameLower)) {
			enumNameLower = "xzk3";
		}
		data.put("key", enumNameLower);
		char flag = String.valueOf(lottery.getName()).charAt(0);
		data.put("flag", String.valueOf(flag));// 1、2、3
		// 彩种分类
		if (flag == '1') {// 数字彩
			data.put("type", "num");
			data.put("className", upperFirst(enumNameLower));
			data.put("package", "num");
		} else if (flag == '2') {// 高频彩
			data.put("type", "high");
			String childType = null;
			if (enumNameLower.contains("11x5")) {
				childType = "11x5";
			} else if (enumNameLower.contains("k3")) {
				childType = "k3";
			} else if (enumNameLower.contains("kl10")) {
				childType = "kl10";
			} else if (enumNameLower.contains("poker")) {
				childType = "poker";
			} else if (enumNameLower.contains("ssc")) {
				childType = "ssc";
			} else if (enumNameLower.contains("kl12")) {
				childType = "kl12";
			} else {
				childType = "other";
			}
			data.put("childType", childType);
			if ("11x5".equals(childType)) {
				data.put("package", "high." + "x115");
			} else {
				data.put("package", "high." + childType);
			}
			if ("other".equals(childType)) {
				data.put("className", upperFirst(enumNameLower));
			} else {
				data.put("className", upperFirst(enumNameLower.replace(childType, ""), childType));
			}
		} else if (flag == '3') {// 竞技彩
			data.put("type", "sport");
			data.put("className", upperFirst(enumNameLower));
			data.put("package", "sport");
		} else if (flag == '7') {
			data.put("type", "local");
			data.put("className", upperFirst(enumNameLower));
			data.put("package", "local");
		}
		return data;
	}

	/**
	 * 生成draw-core代码
	 * 
	 * @throws IOException
	 * @throws TemplateException
	 */
	private void generateDrawCode(Map<String, Object> data) throws IOException, TemplateException {
		String servicePath = toPath(data, PATH_DRAW_CORE, SRC_JAVA_PATH, "com.hhly.drawcore.remote.${package}.service.");
		Template template = null;
		String path = null;
		ByteArrayOutputStream out = null;
		String str = null;
		// 接口
		template = configuration.getTemplate("draw-core/java-interface.ftl");
		path = toPath(data, servicePath, "IDraw${className}Service.java");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path), CHARSET));
		// 实现类
		template = configuration.getTemplate("draw-core/java-impl.ftl");
		path = toPath(data, servicePath, "impl.Draw${className}ServiceImpl.java");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path), CHARSET));
		// hessian配置文件
		template = configuration.getTemplate("draw-core/xml-remote.ftl");
		path = toPath(data, PATH_DRAW_CORE, SRC_RESOURCES_PATH, "remote-servlet.xml");
		template.process(data, new OutputStreamWriter((out = new ByteArrayOutputStream()), CHARSET));
		insertIntoFile(path, POSITION_DRAW_CORE_HESSIAN_XML, out.toByteArray());
		// DrawLotteryConstant类
		path = toPath(data, PATH_LOTTO_SKELETON, SRC_JAVA_PATH, "com.hhly.skeleton.base.constants.DrawLotteryConstant.java");
		str = String.format("\t\tLOTTERY_KEY_MAP.put(LotteryEnum.Lottery.%s.getName(), \"%s\");", data.get("enumName"), data.get("key"));
		insertIntoFile(path, POSITION_LOTTO_SKELETON_DRAW_LOTTERY_CONSTANT, str);
		if ("high".equals(data.get("type"))) {
			// DrawCodeParseUtil类
			path = toPath(data, PATH_DRAW_CORE, SRC_JAVA_PATH, "com.hhly.drawcore.base.util.DrawCodeParseUtil.java");
			str = String.format("\t\tcase %s:", data.get("enumName"));
			insertIntoFile(path, POSITION_DRAW_CORE_HESSIAN_XML_DRAW_CODE_PARSE_UTIL, str);
			// HighDrawDetailParseExecutor
			path = toPath(data, PATH_DRAW_CORE, SRC_JAVA_PATH, "com.hhly.drawcore.base.parser.high.HighDrawDetailParseExecutor.java");
			str = String.format("\t\t\t\tLotteryEnum.Lottery.%s.getName(),", data.get("enumName"));
			insertIntoFile(path, POSITION_DRAW_CORE_HESSIAN_XML_HIGH_DRAW_DETAIL_PARSE_EXECUTOR, str);
		}
		if ("local".equals(data.get("type"))) {
			// LocalDrawDetailParseExecutor类
			path = toPath(data, PATH_DRAW_CORE, SRC_JAVA_PATH, "com.hhly.drawcore.base.parser.local.LocalDrawDetailParseExecutor.java");
			str = String.format("\t\t\t\tLotteryEnum.Lottery.%s.getName(), //", data.get("enumName"));
			insertIntoFile(path, POSITION_DRAW_CORE_HESSIAN_XML_LOCAL_DRAW_DETAIL_PARSE, str);
		}
	}

	/**
	 * 生成lotto项目代码
	 * 
	 * @param data
	 * @throws IOException
	 * @throws TemplateException
	 */
	private void generateLotto(Map<String, Object> data) throws IOException, TemplateException {
		String path = null;
		Template template = null;
		ByteArrayOutputStream out = null;
		String str = null;
		// Controller-PC
		data.put("platform", "pc");
		path = toPath(data, PATH_LOTTO, SRC_JAVA_PATH,
				"com.hhly.lotto.api.${platform}.controller.draw.${package}.v1_0.Draw${className}PcV10Controller.java");
		template = configuration.getTemplate("lotto/java-controller-mobile.ftl");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// 接口
		path = toPath(data, PATH_LOTTO, SRC_JAVA_PATH, "com.hhly.drawcore.remote.${package}.service.IDraw${className}Service.java");
		template = configuration.getTemplate("lotto/java-interface.ftl");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// Controller-Common
		path = toPath(data, PATH_LOTTO, SRC_JAVA_PATH,
				"com.hhly.lotto.api.common.controller.draw.${package}.v1_0.Draw${className}CommonV10Controller.java");
		template = configuration.getTemplate("lotto/java-controller-common.ftl");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// Controller-Android
		data.put("platform", "android");
		path = toPath(data, PATH_LOTTO, SRC_JAVA_PATH,
				"com.hhly.lotto.api.${platform}.controller.draw.${package}.v1_0.Draw${className}AndroidV10Controller.java");
		template = configuration.getTemplate("lotto/java-controller-mobile.ftl");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// Controller-IOS
		data.put("platform", "ios");
		path = toPath(data, PATH_LOTTO, SRC_JAVA_PATH,
				"com.hhly.lotto.api.${platform}.controller.draw.${package}.v1_0.Draw${className}IosV10Controller.java");
		template = configuration.getTemplate("lotto/java-controller-mobile.ftl");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// Controller-PC
		data.put("platform", "pc");
		path = toPath(data, PATH_LOTTO, SRC_JAVA_PATH,
				"com.hhly.lotto.api.${platform}.controller.draw.${package}.v1_0.Draw${className}PcV10Controller.java");
		template = configuration.getTemplate("lotto/java-controller-mobile.ftl");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// hessian-config.xml
		template = configuration.getTemplate("lotto/xml-hessian.ftl");
		path = toPath(data, PATH_LOTTO, SRC_RESOURCES_PATH, "hessian-config.xml");
		template.process(data, new OutputStreamWriter((out = new ByteArrayOutputStream()), CHARSET));
		insertIntoFile(path.toString(), POSITION_LOTTO_HESSIAN_XML, out.toByteArray());
		// hessian.properties
		path = toPath(data, PATH_LOTTO, SRC_RESOURCES_PATH, "hessian.properties");
		str = String.format("remote_draw_%s_service = remote/draw%sService", data.get("key"), data.get("className"));
		insertIntoFile(path.toString(), POSITION_LOTTO_HESSIAN_PROPERTIES, str);
	}

	/**
	 * 生成lotto-draw项目代码
	 * 
	 * @param data
	 * @throws IOException
	 * @throws TemplateException
	 */
	private void generateLottoDraw(Map<String, Object> data) throws IOException, TemplateException {
		String path = null;
		Template template = null;
		ByteArrayOutputStream out = null;
		String str = null;
		// PC页面
		path = toPath(data, PATH_LOTTO_DRAW, SRC_HTML_PATH, "pc.${package}.${key}.html");
		template = configuration.getTemplate(toPath(data, "lotto-draw/html-pc-${type}.ftl"));
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// 接口
		path = toPath(data, PATH_LOTTO_DRAW, SRC_JAVA_PATH, "com.hhly.drawcore.remote.${package}.service.IDraw${className}Service.java");
		template = configuration.getTemplate("lotto-draw/java-interface.ftl");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// PCController
		path = toPath(data, PATH_LOTTO_DRAW, SRC_JAVA_PATH, "com.hhly.draw.pc.${package}.controller.Draw${className}PcController.java");
		template = configuration.getTemplate("lotto-draw/java-controller-pc.ftl");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// H5Controller
		path = toPath(data, PATH_LOTTO_DRAW, SRC_JAVA_PATH, "com.hhly.draw.h5.${package}.controller.Draw${className}H5Controller.java");
		template = configuration.getTemplate("lotto-draw/java-controller-h5.ftl");
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// hessian-config.xml
		template = configuration.getTemplate("lotto-draw/xml-hessian.ftl");
		path = toPath(data, PATH_LOTTO_DRAW, SRC_RESOURCES_PATH, "hessian.xml");
		template.process(data, new OutputStreamWriter((out = new ByteArrayOutputStream()), CHARSET));
		insertIntoFile(path.toString(), POSITION_LOTTO_DRAW_HESSIAN_XML, out.toByteArray());
		// hessian.properties
		path = toPath(data, PATH_LOTTO_DRAW, SRC_RESOURCES_PATH, "hessian.properties");
		str = String.format("remote_draw_%s_service = remote/draw%sService", data.get("key"), data.get("className"));
		insertIntoFile(path.toString(), POSITION_LOTTO_DRAW_HESSIAN_PROPERTIES, str);
		// H5列表页面
		path = toPath(data, PATH_LOTTO_DRAW, SRC_HTML_PATH, "h5.${package}.${key}.html");
		template = configuration.getTemplate(toPath(data, "lotto-draw/html-h5-${type}-list.ftl"));
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// H5详情页面
		path = toPath(data, PATH_LOTTO_DRAW, SRC_HTML_PATH, "h5.${package}.${key}-detail.html");
		template = configuration.getTemplate(toPath(data, "lotto-draw/html-h5-${type}-detail.ftl"));
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
		// PC页面
		path = toPath(data, PATH_LOTTO_DRAW, SRC_HTML_PATH, "pc.${package}.${key}.html");
		template = configuration.getTemplate(toPath(data, "lotto-draw/html-pc-${type}.ftl"));
		template.process(data, new OutputStreamWriter(new FileOutputStream(path.toString()), CHARSET));
	}

	/**
	 * 首字母大写
	 * 
	 * @param strs
	 * @return
	 */
	private static String upperFirst(String... strs) {
		StringBuilder sb = new StringBuilder();
		for (String str : strs) {
			sb.append(str.substring(0, 1).toUpperCase()).append(str.substring(1));
		}
		return sb.toString();
	}

	/**
	 * 连接路径
	 * 
	 * @param paths
	 * @return
	 */
	private static String toPath(Map<String, Object> data, String... paths) {
		StringBuffer path = new StringBuffer();
		for (String str : paths) {
			path.append(str);
		}
		Matcher m = Pattern.compile("\\$\\{(\\w+)\\}").matcher(path.toString());
		path = new StringBuffer();
		while (m.find()) {
			m.appendReplacement(path, String.valueOf(data.get(m.group(1))));
		}
		m.appendTail(path);
		//
		m = Pattern.compile("(\\.)([^\\.]+)").matcher(path.toString());
		path = new StringBuffer();
		while (m.find()) {
			String value = m.group(2);
			if ("java".equalsIgnoreCase(value) || "xml".equalsIgnoreCase(value) || "html".equalsIgnoreCase(value)
					|| "properties".equalsIgnoreCase(value) || "ftl".equalsIgnoreCase(value)) {
				m.appendReplacement(path, m.group(0));
			} else {
				m.appendReplacement(path, "\\\\" + m.group(2));
			}

		}
		m.appendTail(path);
		return path.toString();
	}

	/**
	 * 文件中插入字符串
	 * 
	 * @param path
	 * @param lineNum
	 * @param str
	 * @throws UnsupportedEncodingException
	 * @throws IOException
	 */
	private static void insertIntoFile(String path, int lineNum, String str) throws UnsupportedEncodingException, IOException {
		insertIntoFile(path, lineNum, str.getBytes("UTF-8"));
	}

	/**
	 * 文件插入
	 * 
	 * @param path
	 * @param lineNum
	 * @param dataBytes
	 * @throws IOException
	 */
	private static void insertIntoFile(String path, int lineNum, byte[] dataBytes) throws IOException {
		RandomAccessFile raf = null;
		try {
			raf = new RandomAccessFile(path.toString(), "rw");
			List<Long> linePosList = new ArrayList<Long>();
			linePosList.add(0L);
			while (raf.readLine() != null) {
				linePosList.add(raf.getFilePointer());
			}
			int lineLen = linePosList.size();
			if (lineNum >= lineLen) {
				lineNum = lineLen - 1;
			} else if (lineNum < 0) {
				lineNum = lineLen + lineNum;
				if (lineNum < 0) {
					lineNum = 0;
				}
			}
			long pos = 0;
			if (lineNum == lineLen) {
				pos = raf.length();
			} else {
				pos = linePosList.get(lineNum);
			}
			raf.seek(pos);
			int endLen = (int) (raf.length() - pos);
			byte[] endBytes = new byte[endLen < 0 ? 0 : endLen];
			raf.readFully(endBytes);
			raf.setLength(dataBytes.length + raf.length());
			raf.seek(pos);
			raf.write(dataBytes);
			raf.writeBytes("\n");
			raf.write(endBytes);
		} finally {
			if (raf != null) {
				raf.close();
			}
		}
	}

	public void setGenerateDrawCode(boolean isGenerateDrawCode) {
		this.isGenerateDrawCode = isGenerateDrawCode;
	}

	public void setGenerateLotto(boolean isGenerateLotto) {
		this.isGenerateLotto = isGenerateLotto;
	}

	public void setGenerateLottoDraw(boolean isGenerateLottoDraw) {
		this.isGenerateLottoDraw = isGenerateLottoDraw;
	}

	public static void main(String[] args) throws IOException, TemplateException {
		CodeGenerate g = new CodeGenerate();
		// 11选5 类
		// g.addLottery(LotteryEnum.Lottery.SD11X5);
		// g.addLottery(LotteryEnum.Lottery.JX11X5);
		// g.addLottery(LotteryEnum.Lottery.D11X5);
		// g.addLottery(LotteryEnum.Lottery.XJ11X5);
		// g.addLottery(LotteryEnum.Lottery.HB11X5);
		// g.addLottery(LotteryEnum.Lottery.JS11X5);
		// g.addLottery(LotteryEnum.Lottery.LN11X5);
		// g.addLottery(LotteryEnum.Lottery.HL11X5);
		// g.addLottery(LotteryEnum.Lottery.TJ11X5);
		// g.addLottery(LotteryEnum.Lottery.SX11X5);
		// g.addLottery(LotteryEnum.Lottery.JL11X5);
		// g.addLottery(LotteryEnum.Lottery.CQ11X5);
		// g.addLottery(LotteryEnum.Lottery.SC11X5);
		// g.addLottery(LotteryEnum.Lottery.AH11X5);
		// g.addLottery(LotteryEnum.Lottery.FJ11X5);
		// g.addLottery(LotteryEnum.Lottery.HLJ11X5);
		// g.addLottery(LotteryEnum.Lottery.SHX11X5);
		// g.addLottery(LotteryEnum.Lottery.GS11X5);
		// g.addLottery(LotteryEnum.Lottery.YN11X5);
		// g.addLottery(LotteryEnum.Lottery.ZJ11X5);
		// g.addLottery(LotteryEnum.Lottery.BJ11X5);
		// g.addLottery(LotteryEnum.Lottery.SH11X5);
		// g.addLottery(LotteryEnum.Lottery.GX11X5);
		// g.addLottery(LotteryEnum.Lottery.GZ11X5);
		// g.addLottery(LotteryEnum.Lottery.HEB11X5);
		// g.addLottery(LotteryEnum.Lottery.NX11X5);
		// g.addLottery(LotteryEnum.Lottery.QH11X5);
		// g.addLottery(LotteryEnum.Lottery.NMG11X5);
		// g.addLottery(LotteryEnum.Lottery.XZ11X5);
		// // 时时彩类
		// g.addLottery(LotteryEnum.Lottery.CQSSC);
		// g.addLottery(LotteryEnum.Lottery.JXSSC);
		// g.addLottery(LotteryEnum.Lottery.XJSSC);
		// g.addLottery(LotteryEnum.Lottery.YNSSC);
		// g.addLottery(LotteryEnum.Lottery.TJSSC);
		// // 快乐10分
		// g.addLottery(LotteryEnum.Lottery.CQKL10);
		// g.addLottery(LotteryEnum.Lottery.HNKL10);
		// g.addLottery(LotteryEnum.Lottery.DKL10);
		// g.addLottery(LotteryEnum.Lottery.GXKL10);
		// g.addLottery(LotteryEnum.Lottery.TJKL10);
		// g.addLottery(LotteryEnum.Lottery.YNKL10);
		// g.addLottery(LotteryEnum.Lottery.HLJKL10);
		// g.addLottery(LotteryEnum.Lottery.SHXKL10);
		// g.addLottery(LotteryEnum.Lottery.SZKL10);
		// g.addLottery(LotteryEnum.Lottery.SXKL10);
		// // 快3
		// g.addLottery(LotteryEnum.Lottery.JXK3);
		// g.addLottery(LotteryEnum.Lottery.JSK3);
		// g.addLottery(LotteryEnum.Lottery.GXK3);
		// g.addLottery(LotteryEnum.Lottery.AHK3);
		// g.addLottery(LotteryEnum.Lottery.JLK3);
		// g.addLottery(LotteryEnum.Lottery.HNK3);
		// g.addLottery(LotteryEnum.Lottery.QHK3);
		// g.addLottery(LotteryEnum.Lottery.GSK3);
		// g.addLottery(LotteryEnum.Lottery.HEBK3);
		// g.addLottery(LotteryEnum.Lottery.SHK3);
		// g.addLottery(LotteryEnum.Lottery.BJK3);
		// g.addLottery(LotteryEnum.Lottery.FJK3);
		// g.addLottery(LotteryEnum.Lottery.GZK3);
		// g.addLottery(LotteryEnum.Lottery.NMGK3);
		// g.addLottery(LotteryEnum.Lottery.HBK3);
		// g.addLottery(LotteryEnum.Lottery.XZGK3);
		// // 快乐12
		// g.addLottery(LotteryEnum.Lottery.SCHKL12);
		// g.addLottery(LotteryEnum.Lottery.ZHJKL12);
		// g.addLottery(LotteryEnum.Lottery.LNKL12);
		// // 其它高频彩
		// g.addLottery(LotteryEnum.Lottery.CQBBWP);
		// g.addLottery(LotteryEnum.Lottery.HENKY481);
		// g.addLottery(LotteryEnum.Lottery.BJKZC);
		// g.addLottery(LotteryEnum.Lottery.SHSSL);
		// g.addLottery(LotteryEnum.Lottery.SDQYH);
		// g.addLottery(LotteryEnum.Lottery.SXYTDJ);
		// g.addLottery(LotteryEnum.Lottery.BJKL8);
		// // 地方彩
		g.addLottery(LotteryEnum.Lottery.HDDF6J1);
		g.addLottery(LotteryEnum.Lottery.HD15X5);
		g.addLottery(LotteryEnum.Lottery.SHTTCX4);
		g.addLottery(LotteryEnum.Lottery.JS7N);
		g.addLottery(LotteryEnum.Lottery.ZJ20X5);
		g.addLottery(LotteryEnum.Lottery.ZJ6J1);
		g.addLottery(LotteryEnum.Lottery.HLJ36X7);
		g.addLottery(LotteryEnum.Lottery.HLJP62);
		g.addLottery(LotteryEnum.Lottery.HLJ6J1);
		g.addLottery(LotteryEnum.Lottery.HLJ22X5);
		g.addLottery(LotteryEnum.Lottery.LN35X7);
		g.addLottery(LotteryEnum.Lottery.HEBHY2);
		g.addLottery(LotteryEnum.Lottery.HEBHY3);
		g.addLottery(LotteryEnum.Lottery.HEBPL7);
		g.addLottery(LotteryEnum.Lottery.HEBPL5);
		g.addLottery(LotteryEnum.Lottery.HEB20X5);
		g.addLottery(LotteryEnum.Lottery.HEN22X5);
		g.addLottery(LotteryEnum.Lottery.XJ25X7);
		g.addLottery(LotteryEnum.Lottery.XJ18X7);
		g.addLottery(LotteryEnum.Lottery.XJ35X7);
		g.addLottery(LotteryEnum.Lottery.GXKLSC);
		g.addLottery(LotteryEnum.Lottery.AH25X5);
		g.addLottery(LotteryEnum.Lottery.HAIN4J1);
		g.addLottery(LotteryEnum.Lottery.FJ22X5);
		g.addLottery(LotteryEnum.Lottery.FJ31X7);
		g.addLottery(LotteryEnum.Lottery.FJ36X7);
		g.addLottery(LotteryEnum.Lottery.GD36X7);
		g.addLottery(LotteryEnum.Lottery.GDHC1);
		g.addLottery(LotteryEnum.Lottery.GD26X5);
		g.addLottery(LotteryEnum.Lottery.SZFC);
		g.addLottery(LotteryEnum.Lottery.HUB22X5);
		g.addLottery(LotteryEnum.Lottery.HUB30X5);

		g.setGenerateDrawCode(false);
		g.setGenerateLotto(true);
		g.setGenerateLottoDraw(false);
		g.execute();
	}

}
