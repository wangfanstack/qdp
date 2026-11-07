package cn.sailstack.qdp.module.member.service.bonus.calculate;

import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusRecordDO;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusStatusEnum;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusTypeEnum;
import lombok.Builder;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 直推奖计算策略
 *
 * 需求 4：第 1、2 名有效直推 30%；第 3 名及以后 55%；计奖基数为该笔有效礼包订单对应 PV。
 * 纯计算单元，不落库；由权益生效流程编排。
 *
 * @author sail
 */
@Component
public class DirectBonusCalculator {

    /**
     * 直推奖入参
     */
    @Data
    @Builder
    public static class DirectBonusCalculateReqBO {
        /** 获奖会员（推荐人） */
        private Long userId;
        /** 被推荐人（购买人） */
        private Long sourceUserId;
        /** 来源订单编号 */
        private Long sourceOrderId;
        /** 订单 PV（元） */
        private Integer pv;
        /** 该被推荐人在推荐人名下的有效直推顺序（从 1 开始） */
        private Integer validSeq;
        /** 生效时间 */
        private LocalDateTime effectTime;
    }

    /**
     * 计算直推奖；PV 为 0 或顺序为空时不产生奖金
     *
     * @param config 奖金规则配置
     * @param req    入参
     * @return 奖金明细（未落库）；不符合条件返回 null
     */
    public MemberBonusRecordDO calculate(MemberBonusConfigDO config, DirectBonusCalculateReqBO req) {
        if (req.getPv() == null || req.getPv() <= 0 || req.getValidSeq() == null) {
            return null;
        }
        // 第 1、2 名 30%，第 3 名及以后 55%
        int rate = req.getValidSeq() <= 2 ? config.getDirectFirstRate() : config.getDirectAfterRate();
        int baseFen = BonusCalcUtils.pvToFen(req.getPv());
        int amount = BonusCalcUtils.calcAmount(baseFen, rate, config.getRoundingMode());
        return MemberBonusRecordDO.builder()
                .dedupKey(buildDedupKey(req.getSourceOrderId()))
                .userId(req.getUserId())
                .bonusType(MemberBonusTypeEnum.DIRECT.getType())
                .sourceUserId(req.getSourceUserId())
                .sourceOrderId(req.getSourceOrderId())
                .basePv(req.getPv())
                .baseAmount(baseFen)
                .rate(rate)
                .amount(amount)
                .period(formatPeriod(req.getEffectTime()))
                .ruleVersion(config.getVersion())
                .status(MemberBonusStatusEnum.WAIT_SETTLE.getStatus())
                .build();
    }

    /**
     * 防重复键：同一订单的直推奖只能有一条
     */
    public static String buildDedupKey(Long orderId) {
        return "DIRECT:" + orderId;
    }

    /**
     * 冲正防重复键：按退款单维度幂等
     */
    public static String buildReversalDedupKey(Long orderId, String refundBizId) {
        return "DIRECT-REV:" + orderId + ":" + refundBizId;
    }

    static String formatPeriod(LocalDateTime time) {
        return time.format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

}
