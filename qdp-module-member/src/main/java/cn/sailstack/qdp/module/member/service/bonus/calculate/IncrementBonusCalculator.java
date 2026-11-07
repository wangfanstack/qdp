package cn.sailstack.qdp.module.member.service.bonus.calculate;

import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusRecordDO;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusStatusEnum;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusTypeEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 市场增量奖计算策略
 *
 * 需求 5：市场内每位会员本人前两名有效直推产生的合格新增 PV 按 25% 归属市场老板；
 * 第 3 名及以后不计入；按最近有效老板市场归属原则，市场切换按生效时间切分。
 * 纯计算单元，不落库。
 *
 * @author sail
 */
@Component
public class IncrementBonusCalculator {

    /**
     * 增量奖入参
     */
    @Data
    @Builder
    public static class IncrementBonusCalculateReqBO {
        /** 获奖会员（市场归属人/老板） */
        private Long userId;
        /** 归属市场编号 */
        private Long marketId;
        /** 被推荐人（产生业绩的会员） */
        private Long sourceUserId;
        /** 来源订单编号 */
        private Long sourceOrderId;
        /** 订单 PV（元） */
        private Integer pv;
        /** 生效时间 */
        private LocalDateTime effectTime;
    }

    /**
     * 计算增量奖
     *
     * @param config 奖金规则配置
     * @param req    入参
     * @return 奖金明细（未落库）；不符合条件返回 null
     */
    public MemberBonusRecordDO calculate(MemberBonusConfigDO config, IncrementBonusCalculateReqBO req) {
        if (req.getPv() == null || req.getPv() <= 0 || req.getUserId() == null || req.getMarketId() == null) {
            return null;
        }
        int baseFen = BonusCalcUtils.pvToFen(req.getPv());
        int amount = BonusCalcUtils.calcAmount(baseFen, config.getIncrementRate(), config.getRoundingMode());
        return MemberBonusRecordDO.builder()
                .dedupKey(buildDedupKey(req.getSourceOrderId()))
                .userId(req.getUserId())
                .bonusType(MemberBonusTypeEnum.INCREMENT.getType())
                .sourceUserId(req.getSourceUserId())
                .sourceOrderId(req.getSourceOrderId())
                .basePv(req.getPv())
                .baseAmount(baseFen)
                .rate(config.getIncrementRate())
                .amount(amount)
                .marketId(req.getMarketId())
                .period(DirectBonusCalculator.formatPeriod(req.getEffectTime()))
                .ruleVersion(config.getVersion())
                .status(MemberBonusStatusEnum.WAIT_SETTLE.getStatus())
                .build();
    }

    /**
     * 防重复键：同一订单的增量奖只能有一条（一笔业绩只能有一个增量奖归属人）
     */
    public static String buildDedupKey(Long orderId) {
        return "INCREMENT:" + orderId;
    }

    /**
     * 冲正防重复键：按退款单维度幂等
     */
    public static String buildReversalDedupKey(Long orderId, String refundBizId) {
        return "INCREMENT-REV:" + orderId + ":" + refundBizId;
    }

}
