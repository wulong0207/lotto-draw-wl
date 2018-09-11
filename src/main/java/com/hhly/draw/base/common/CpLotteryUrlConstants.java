package com.hhly.draw.base.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hhly.skeleton.base.common.LotteryEnum;

public class CpLotteryUrlConstants {

	/**
	 * PC网站投注链接
	 */
	private static final Map<String, String> webCpUrlMap = new HashMap<>();

	/**
	 * H5网站投注链接
	 */
	private static final Map<String, String> wapCpUrlMap = new HashMap<>();

	/**
	 * H5域名，判断是否给第三方公司
	 */
	private static final List<String> wapDomains = new ArrayList<String>();

	static {
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.SSQ.getName()), "ssq");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.QLC.getName()), "qlc");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.DLT.getName()), "dlt");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.PL5.getName()), "pl5");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.PL3.getName()), "pl3");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.F3D.getName()), "fc3d");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.QXC.getName()), "qxc");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.JX11X5.getName()), "jx11x5");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.SD11X5.getName()), "sd11x5");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.XJ11X5.getName()), "xj11x5");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.D11X5.getName()), "gd11x5");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.GX11X5.getName()), "gx11x5");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.CQSSC.getName()), "cqssc");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.CQKL10.getName()), "cqkl10");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.JSK3.getName()), "jsk3");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.JXK3.getName()), "jxk3");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.SDPOKER.getName()), "sdpk");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.FB.getName()), "jczq");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.BB.getName()), "jclq");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.SFC.getName()), "sfc");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.ZC_NINE.getName()), "rx9c");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.BJDC.getName()), "bjdc/spf");
		webCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.SFGG.getName()), "bjdc/sfgg");
	}
	static {
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.SSQ.getName()), "ssq");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.QLC.getName()), "qlc");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.DLT.getName()), "dlt");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.PL5.getName()), "pl5");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.PL3.getName()), "pl3");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.F3D.getName()), "f3d");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.QXC.getName()), "qxc");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.JX11X5.getName()), "jx11x5");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.SD11X5.getName()), "sd11x5");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.XJ11X5.getName()), "xj11x5");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.D11X5.getName()), "gd11x5");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.GX11X5.getName()), "gx11x5");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.CQSSC.getName()), "cqssc");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.JSK3.getName()), "k3");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.JXK3.getName()), "jxk3");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.SDPOKER.getName()), "klpk3");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.FB.getName()), "jczq");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.BB.getName()), "jclq");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.SFC.getName()), "lzc");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.ZC_NINE.getName()), "lzc/#/9");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.BJDC.getName()), "bjdc");
		wapCpUrlMap.put(String.valueOf(LotteryEnum.Lottery.SFGG.getName()), "bjdc/?page=sfgg");
	}

	static {
		wapDomains.add("localm.2ncai.com");//开发使用
		wapDomains.add("devm.2ncai.com");
		wapDomains.add("sitm.2ncai.com");
		wapDomains.add("uatm.2ncai.com");
		wapDomains.add("m.2ncai.com");
		wapDomains.add("localhost");
		wapDomains.add("127.0.0.1");
	}

	public static Map<String, String> getWebCpUrlMap() {
		return webCpUrlMap;
	}

	public static Map<String, String> getWapCpUrlMap() {
		return wapCpUrlMap;
	}

	/**
	 * 判断是否开售
	 * 
	 * @param lotteryCode
	 * @return
	 */
	public static boolean isOnSale(Integer lotteryCode) {
		return webCpUrlMap.containsKey(String.valueOf(lotteryCode));
	}

	/**
	 * 判断是不是本站H5域名
	 * 
	 * @param serverName
	 * @return
	 */
	public static boolean isYicaiWapDomain(String serverName) {
		return wapDomains.contains(serverName);
	}

}
