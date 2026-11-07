package cn.sailstack.qdp.module.member.service.bonus.config;

import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.config.MemberBonusConfigSaveReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberBonusConfigMapper;
import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import jakarta.validation.Valid;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.module.member.enums.ErrorCodeConstants.BONUS_CONFIG_NOT_EXISTS;

/**
 * 奖金规则配置 Service 实现类
 *
 * @author sail
 */
@Service
@Validated
@Slf4j
public class MemberBonusConfigServiceImpl implements MemberBonusConfigService {

    @Resource
    private MemberBonusConfigMapper bonusConfigMapper;

    @Override
    public MemberBonusConfigDO getRequiredConfig() {
        MemberBonusConfigDO config = bonusConfigMapper.selectConfig();
        if (config == null) {
            throw exception(BONUS_CONFIG_NOT_EXISTS);
        }
        return config;
    }

    @Override
    public void updateConfig(@Valid MemberBonusConfigSaveReqVO updateReqVO) {
        MemberBonusConfigDO config = getRequiredConfig();
        // 待业务确认项：分红池收入口径切换为 DISABLED 时，自动结算将跳过董事分红
        Preconditions.checkArgument(updateReqVO.getPvPercent() > 0 && updateReqVO.getPvPercent() <= 100, "PV 比例必须在 (0, 100]");
        MemberBonusConfigDO updateObj = new MemberBonusConfigDO()
                .setId(config.getId())
                .setPvPercent(updateReqVO.getPvPercent())
                .setDirectFirstRate(updateReqVO.getDirectFirstRate())
                .setDirectAfterRate(updateReqVO.getDirectAfterRate())
                .setIncrementRate(updateReqVO.getIncrementRate())
                .setHelpRate(updateReqVO.getHelpRate())
                .setDirectorOneRate(updateReqVO.getDirectorOneRate())
                .setDirectorTwoRate(updateReqVO.getDirectorTwoRate())
                .setDirectorThreeRate(updateReqVO.getDirectorThreeRate())
                .setDirectorFourRate(updateReqVO.getDirectorFourRate())
                .setDirectorFiveRate(updateReqVO.getDirectorFiveRate())
                .setGiftPoolPercent(updateReqVO.getGiftPoolPercent())
                .setGiftIncomeSource(updateReqVO.getGiftIncomeSource())
                .setDirectorQualifyGiftId(updateReqVO.getDirectorQualifyGiftId())
                .setDirectorOneStarCount(updateReqVO.getDirectorOneStarCount())
                .setDirectorQualifyScope(updateReqVO.getDirectorQualifyScope())
                .setDirectorTraineeScope(updateReqVO.getDirectorTraineeScope())
                .setDirectorTwoStarTrainees(updateReqVO.getDirectorTwoStarTrainees())
                .setDirectorThreeStarTrainees(updateReqVO.getDirectorThreeStarTrainees())
                .setDirectorFourStarTrainees(updateReqVO.getDirectorFourStarTrainees())
                .setDirectorFiveStarTrainees(updateReqVO.getDirectorFiveStarTrainees())
                .setEmptyLevelPolicy(updateReqVO.getEmptyLevelPolicy())
                .setObserveDays(updateReqVO.getObserveDays())
                .setSettleDay(updateReqVO.getSettleDay())
                .setPayDay(updateReqVO.getPayDay())
                .setRoundingMode(updateReqVO.getRoundingMode())
                .setReversalPolicy(updateReqVO.getReversalPolicy())
                .setProductPoolPercent(updateReqVO.getProductPoolPercent())
                .setGmvIncludeDelivery(updateReqVO.getGmvIncludeDelivery())
                // 规则版本自增：历史奖金按产生时的规则版本追溯
                .setVersion(config.getVersion() + 1)
                .setRemark(updateReqVO.getRemark());
        bonusConfigMapper.updateById(updateObj);
        log.info("[updateConfig][配置({}) 已更新为版本 {}]", config.getId(), config.getVersion() + 1);
    }

}
