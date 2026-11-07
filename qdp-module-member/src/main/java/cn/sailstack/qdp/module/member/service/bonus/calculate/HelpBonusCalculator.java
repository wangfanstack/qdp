package cn.sailstack.qdp.module.member.service.bonus.calculate;

import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusRecordDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberRelationDO;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberBonusRecordMapper;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberRelationMapper;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusStatusEnum;
import cn.sailstack.qdp.module.member.enums.bonus.MemberBonusTypeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 第一代帮扶奖计算策略（月度）
 *
 * 需求 6：帮扶奖 = 每个第一代（直接推荐）会员当月有效直推奖与增量奖合计 × 20%；
 * 基数仅含直推奖与增量奖，不含帮扶奖、董事分红与任何商品奖金；先完成直推/增量月度汇总再计算，禁止递归。
 *
 * @author sail
 */
@Component
@Slf4j
public class HelpBonusCalculator {

    @Resource
    private MemberBonusRecordMapper bonusRecordMapper;
    @Resource
    private MemberRelationMapper relationMapper;

    /**
     * 计算指定账期全部帮扶奖
     *
     * 基数口径：该账期直推奖+增量奖明细求和（含待结算/已结算/已发放；负数冲正记录参与抵扣，
     * 已全额冲正的原记录与仅记录追偿的冲正记录不进入基数）。
     *
     * @param config 奖金规则配置
     * @param period 账期（yyyy-MM）
     * @return 帮扶奖明细列表（未落库）
     */
    public List<MemberBonusRecordDO> calculate(MemberBonusConfigDO config, String period) {
        // 1. 汇总当月直推奖 + 增量奖，按来源会员（第一代下级）分组
        List<MemberBonusRecordDO> records = bonusRecordMapper.selectListByPeriodAndTypes(period,
                List.of(MemberBonusTypeEnum.DIRECT.getType(), MemberBonusTypeEnum.INCREMENT.getType()));
        Map<Long, Integer> baseSumByUser = records.stream()
                .filter(r -> !MemberBonusStatusEnum.REVERSED.getStatus().equals(r.getStatus()))
                .filter(r -> r.getSourceUserId() != null)
                .collect(Collectors.groupingBy(MemberBonusRecordDO::getSourceUserId,
                        Collectors.summingInt(MemberBonusRecordDO::getAmount)));
        // 2. 每个第一代下级 → 其推荐人获得 20% 帮扶奖
        List<MemberBonusRecordDO> result = new ArrayList<>();
        for (Map.Entry<Long, Integer> entry : baseSumByUser.entrySet()) {
            Long sourceUserId = entry.getKey();
            int baseAmount = entry.getValue();
            if (baseAmount <= 0) {
                continue; // 当月净额非正不产生帮扶奖
            }
            MemberRelationDO relation = relationMapper.selectByUserId(sourceUserId);
            if (relation == null || relation.getReferrerId() == null) {
                continue;
            }
            int amount = BonusCalcUtils.calcAmount(baseAmount, config.getHelpRate(), config.getRoundingMode());
            result.add(MemberBonusRecordDO.builder()
                    .dedupKey(buildDedupKey(period, sourceUserId))
                    .userId(relation.getReferrerId())
                    .bonusType(MemberBonusTypeEnum.HELP.getType())
                    .sourceUserId(sourceUserId)
                    .basePv(0)
                    .baseAmount(baseAmount)
                    .rate(config.getHelpRate())
                    .amount(amount)
                    .period(period)
                    .ruleVersion(config.getVersion())
                    .status(MemberBonusStatusEnum.WAIT_SETTLE.getStatus())
                    .build());
        }
        return result;
    }

    /**
     * 防重复键：同一账期同一第一代下级的帮扶奖只能有一条
     */
    public static String buildDedupKey(String period, Long sourceUserId) {
        return "HELP:" + period + ":" + sourceUserId;
    }

}
