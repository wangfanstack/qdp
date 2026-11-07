package cn.sailstack.qdp.module.member.service.bonus.calculate;

import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusRecordDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberDirectorDO;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberDirectorMapper;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusStatusEnum;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusTypeEnum;
import cn.sailstack.qdp.module.member.enums.bonus.MemberDirectorLevelEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 礼包董事分红计算策略（月度）
 *
 * 需求 7：分红池 = 公司当月约定口径收入 × 60%；各等级预算 = 分红池 × 等级比例（10/7/5/3/2%）；
 * 同等级符合条件成员平均分配；符合多个等级者可参与多个等级；第一版不自动降级。
 * 收入口径【待业务确认】：默认 DISABLED 不结算，MANUAL 由结算操作人录入分红池金额。
 *
 * @author sail
 */
@Component
@Slf4j
public class DirectorGiftBonusCalculator {

    /**
     * 计算结果
     */
    @Data
    @AllArgsConstructor
    public static class CalculateResultBO {
        /** 分红明细（未落库） */
        private List<MemberBonusRecordDO> records;
        /** 空档等级说明（无人符合条件，预算按配置策略处理） */
        private List<String> emptyLevelRemarks;
    }

    @Resource
    private MemberDirectorMapper directorMapper;

    /**
     * 计算指定账期的礼包董事分红
     *
     * @param config          奖金规则配置
     * @param period          账期（yyyy-MM）
     * @param dividendPoolFen 分红池（分，MANUAL 录入）
     * @return 计算结果
     */
    public CalculateResultBO calculate(MemberBonusConfigDO config, String period, Integer dividendPoolFen) {
        List<MemberBonusRecordDO> records = new ArrayList<>();
        List<String> emptyLevelRemarks = new ArrayList<>();
        if (dividendPoolFen == null || dividendPoolFen <= 0) {
            return new CalculateResultBO(records, emptyLevelRemarks);
        }
        for (MemberDirectorLevelEnum level : MemberDirectorLevelEnum.values()) {
            // 1. 各等级独立预算 = 分红池 × 等级比例
            int rate = config.getDirectorRate(level.getLevel());
            int budget = BonusCalcUtils.calcAmount(dividendPoolFen, rate, config.getRoundingMode());
            // 2. 同等级平均分配
            List<MemberDirectorDO> directors = directorMapper.selectListByLevel(level.getLevel());
            if (directors.isEmpty()) {
                // 空档等级预算处理策略【待业务确认】：默认 KEEP 留存公司不分配
                emptyLevelRemarks.add(String.format("%s：预算(%d分)无人符合条件，按策略(%s)留存",
                        level.getName(), budget, config.getEmptyLevelPolicy()));
                continue;
            }
            int avgAmount = BonusCalcUtils.calcAvgAmount(budget, directors.size());
            for (MemberDirectorDO director : directors) {
                records.add(MemberBonusRecordDO.builder()
                        .dedupKey(buildDedupKey(period, director.getUserId(), level.getLevel()))
                        .userId(director.getUserId())
                        .bonusType(MemberBonusTypeEnum.DIRECTOR.getType())
                        .directorLevel(level.getLevel())
                        .basePv(0)
                        .baseAmount(budget)
                        .rate(rate)
                        .amount(avgAmount)
                        .period(period)
                        .ruleVersion(config.getVersion())
                        .status(MemberBonusStatusEnum.WAIT_SETTLE.getStatus())
                        .build());
            }
        }
        return new CalculateResultBO(records, emptyLevelRemarks);
    }

    /**
     * 防重复键：同一账期同一会员同一等级的礼包董事分红只能有一条
     */
    public static String buildDedupKey(String period, Long userId, Integer level) {
        return "DIRECTOR:" + period + ":" + userId + ":" + level;
    }

}
