package com.hhly.draw.base.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.hhly.skeleton.base.common.ArticleEnum;
import com.hhly.skeleton.base.common.DicEnum;
import com.hhly.skeleton.base.common.LotteryEnum;
import com.hhly.skeleton.base.constants.ArticleConstants;
import com.hhly.skeleton.base.constants.Constants;
import com.hhly.skeleton.lotto.base.operate.vo.OperateArticleLottVO;

/**
 * 
 * @desc 资讯
 * @author huangchengfang1219
 * @date 2017年10月25日
 * @company 益彩网络科技公司
 * @version 1.0
 */
public class ArticleUtil {

	/**
	 * 分析预测资讯类码
	 */
	private final static Map<Integer, String> ANALYZE_TYPE_CODE_MAP = new HashMap<Integer, String>();
	/**
	 * 大奖名称资讯类码
	 */
	private final static Map<Integer, String> PRIZE_TYPE_CODE_MAP = new HashMap<Integer, String>();

	static {
		// 双色球
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.SSQ.getName(), ArticleEnum.AriticleEnum.SSQ_ANALYZE.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.SSQ.getName(), ArticleEnum.AriticleEnum.SSQ_PRIZE.getValue());
		// 大乐透
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.DLT.getName(), ArticleEnum.AriticleEnum.DLT_ANALYZE.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.DLT.getName(), ArticleEnum.AriticleEnum.DLT_PRIZE.getValue());
		// 福彩3D
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.F3D.getName(), ArticleEnum.AriticleEnum.F3D_ANALYZE.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.F3D.getName(), ArticleEnum.AriticleEnum.F3D_PRIZE.getValue());
		// 七星彩
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.QXC.getName(), ArticleEnum.AriticleEnum.QXC_ANALYZE.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.QXC.getName(), ArticleEnum.AriticleEnum.QXC_PRIZE.getValue());
		// 七乐彩
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.QLC.getName(), ArticleEnum.AriticleEnum.QLC_ANALYZE.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.QLC.getName(), ArticleEnum.AriticleEnum.QLC_PRIZE.getValue());
		// 排列三
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.PL3.getName(), ArticleEnum.AriticleEnum.PL_ANALYZE.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.PL3.getName(), ArticleEnum.AriticleEnum.PL_PRIZE.getValue());
		// 排列五
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.PL5.getName(), ArticleEnum.AriticleEnum.PL_ANALYZE.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.PL5.getName(), ArticleEnum.AriticleEnum.PL_PRIZE.getValue());
		//四场进球
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.JQ4.getName(), ArticleEnum.AriticleEnum.FOOTBALLANALY.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.JQ4.getName(), ArticleEnum.AriticleEnum.FOOTBALLLAST.getValue());
		//六场半全场
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.ZC6.getName(), ArticleEnum.AriticleEnum.FOOTBALLANALY.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.ZC6.getName(), ArticleEnum.AriticleEnum.FOOTBALLLAST.getValue());
		//胜负彩
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.SFC.getName(), ArticleEnum.AriticleEnum.FOOTBALLANALY.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.SFC.getName(), ArticleEnum.AriticleEnum.FOOTBALLLAST.getValue());
		//任九
		ANALYZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.ZC_NINE.getName(), ArticleEnum.AriticleEnum.FOOTBALLANALY.getValue());
		PRIZE_TYPE_CODE_MAP.put(LotteryEnum.Lottery.ZC_NINE.getName(), ArticleEnum.AriticleEnum.FOOTBALLLAST.getValue());
	}

	/**
	 * 根据彩种编号获取分析预测类码
	 * 
	 * @param lotteryCode
	 * @return
	 */
	public static String getAnalyzeTypeCode(Integer lotteryCode) {
		return ANALYZE_TYPE_CODE_MAP.get(lotteryCode);
	}

	/**
	 * 根据彩种编号获取大奖名单类码
	 * 
	 * @param lotteryCode
	 * @return
	 */
	public static String getPrizeTypeCode(Integer lotteryCode) {
		return PRIZE_TYPE_CODE_MAP.get(lotteryCode);
	}

	/**
	 * 专家预测
	 * 
	 * @param lotteryCode
	 * @return
	 */
	public static OperateArticleLottVO buildAnalyzeQuery(Integer lotteryCode) {
		return buildQueryVO(ArticleUtil.getAnalyzeTypeCode(lotteryCode));
	}

	/**
	 * 大奖名单
	 * 
	 * @param lotteryCode
	 * @return
	 */
	public static OperateArticleLottVO buildPrizeQuery(Integer lotteryCode) {
		return buildQueryVO(ArticleUtil.getPrizeTypeCode(lotteryCode));
	}

	/**
	 * 资讯查询
	 * 
	 * @param lotteryCode
	 * @return
	 */
	public static List<OperateArticleLottVO> buildQueryList(Integer lotteryCode) {
		List<OperateArticleLottVO> voList = new ArrayList<OperateArticleLottVO>();
		if (ANALYZE_TYPE_CODE_MAP.containsKey(lotteryCode)) {
			voList.add(buildQueryVO(ANALYZE_TYPE_CODE_MAP.get(lotteryCode)));
		}
		if (PRIZE_TYPE_CODE_MAP.containsKey(lotteryCode)) {
			voList.add(buildQueryVO(PRIZE_TYPE_CODE_MAP.get(lotteryCode)));
		}
		return voList;
	}

	public static OperateArticleLottVO buildQueryVO(String typeCode) {
		OperateArticleLottVO articleLottVO = new OperateArticleLottVO();
		articleLottVO.setPlatform(DicEnum.PlatFormEnum.WEB.getValue());
		articleLottVO.setRownum(Constants.NUM_4);
		articleLottVO.setTypeCode(typeCode);
		return articleLottVO;
	}

	public static String getKey(String typeCode) {
		return ArticleConstants.NEWS_NOW_ROW + typeCode;
	}

	public static String getKey(OperateArticleLottVO vo) {
		return ArticleConstants.NEWS_NOW_ROW + vo.getTypeCode();
	}
}
