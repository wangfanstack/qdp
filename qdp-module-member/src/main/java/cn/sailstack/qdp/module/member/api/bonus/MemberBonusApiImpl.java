package cn.sailstack.qdp.module.member.api.bonus;

import cn.sailstack.qdp.framework.common.enums.CommonStatusEnum;
import cn.sailstack.qdp.framework.common.util.object.BeanUtils;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberBonusConfigRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftConfigRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftEffectReqDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftPrepareRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftRefundReqDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberRelationRespDTO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberDirectorDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberGiftConfigDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberRelationDO;
import cn.sailstack.qdp.module.member.service.bonus.director.MemberDirectorService;
import cn.sailstack.qdp.module.member.service.bonus.gift.MemberGiftBenefitService;
import cn.sailstack.qdp.module.member.service.bonus.gift.MemberGiftConfigService;
import cn.sailstack.qdp.module.member.service.bonus.relation.MemberRelationService;
import cn.sailstack.qdp.module.member.service.user.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员权益与礼包奖金 API 实现类
 *
 * @author sail
 */
@Service
@Validated
public class MemberBonusApiImpl implements MemberBonusApi {

    @Resource
    private MemberGiftBenefitService giftBenefitService;
    @Resource
    private MemberGiftConfigService giftConfigService;
    @Resource
    private cn.sailstack.qdp.module.member.service.bonus.config.MemberBonusConfigService bonusConfigService;
    @Resource
    private MemberRelationService relationService;
    @Resource
    private MemberDirectorService directorService;
    @Resource
    private MemberUserService memberUserService;

    @Override
    public MemberGiftPrepareRespDTO prepareGiftBuy(Long userId, Long giftId) {
        return giftBenefitService.prepareGiftBuy(userId, giftId);
    }

    @Override
    public void effectGiftOrder(MemberGiftEffectReqDTO reqDTO) {
        giftBenefitService.effectGiftOrder(reqDTO);
    }

    @Override
    public void refundGiftOrder(MemberGiftRefundReqDTO reqDTO) {
        giftBenefitService.refundGiftOrder(reqDTO);
    }

    @Override
    public MemberRelationRespDTO getRelation(Long userId) {
        MemberRelationDO relation = relationService.getRelation(userId);
        return BeanUtils.toBean(relation, MemberRelationRespDTO.class);
    }

    @Override
    public Long getDirectSuperiorId(Long userId) {
        MemberRelationDO relation = relationService.getRelation(userId);
        return relation != null ? relation.getReferrerId() : null;
    }

    @Override
    public void bindRelation(Long userId, Long referrerId) {
        relationService.bindRelation(userId, referrerId);
    }

    @Override
    public List<Long> getDirectorUserIds(Integer level) {
        return directorService.getDirectorListByLevel(level).stream()
                .map(MemberDirectorDO::getUserId).collect(Collectors.toList());
    }

    @Override
    public boolean validateUserExists(Long userId) {
        return memberUserService.getUser(userId) != null;
    }

    @Override
    public MemberGiftConfigRespDTO getEnabledGiftConfigBySpuId(Long spuId) {
        MemberGiftConfigDO giftConfig = giftConfigService.getGiftConfigBySpuId(spuId);
        return toEnabledGiftConfig(giftConfig);
    }

    @Override
    public List<MemberGiftConfigRespDTO> getEnabledGiftConfigList() {
        return giftConfigService.getGiftConfigListEnable().stream()
                .map(this::toEnabledGiftConfig).collect(Collectors.toList());
    }

    @Override
    public MemberGiftConfigRespDTO getEnabledGiftConfig(Long giftId) {
        return toEnabledGiftConfig(giftConfigService.getGiftConfig(giftId));
    }

    @Override
    public MemberBonusConfigRespDTO getBonusConfig() {
        return BeanUtils.toBean(bonusConfigService.getRequiredConfig(), MemberBonusConfigRespDTO.class);
    }

    private MemberGiftConfigRespDTO toEnabledGiftConfig(MemberGiftConfigDO giftConfig) {
        if (giftConfig == null || !CommonStatusEnum.ENABLE.getStatus().equals(giftConfig.getStatus())) {
            return null;
        }
        return BeanUtils.toBean(giftConfig, MemberGiftConfigRespDTO.class);
    }

}
