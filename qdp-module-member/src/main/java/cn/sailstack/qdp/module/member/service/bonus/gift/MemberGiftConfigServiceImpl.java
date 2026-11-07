package cn.sailstack.qdp.module.member.service.bonus.gift;

import cn.sailstack.qdp.framework.common.enums.CommonStatusEnum;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.gift.MemberGiftConfigPageReqVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.gift.MemberGiftConfigSaveReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberGiftBenefitDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberGiftConfigDO;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberGiftBenefitMapper;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberGiftConfigMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.List;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.module.member.enums.ErrorCodeConstants.*;

/**
 * 会员礼包配置 Service 实现类
 *
 * @author sail
 */
@Service
@Validated
@Slf4j
public class MemberGiftConfigServiceImpl implements MemberGiftConfigService {

    @Resource
    private MemberGiftConfigMapper giftConfigMapper;
    @Resource
    private MemberGiftBenefitMapper giftBenefitMapper;

    @Override
    public Long createGiftConfig(MemberGiftConfigSaveReqVO createReqVO) {
        // 校验名称、SPU 唯一
        validateNameUnique(null, createReqVO.getName());
        validateSpuUnique(null, createReqVO.getSpuId());
        MemberGiftConfigDO config = MemberGiftConfigDO.builder()
                .name(createReqVO.getName())
                .levelName(createReqVO.getLevelName())
                .price(createReqVO.getPrice())
                .pvPercent(createReqVO.getPvPercent())
                .spuId(createReqVO.getSpuId())
                .icon(createReqVO.getIcon())
                .sort(createReqVO.getSort())
                .status(createReqVO.getStatus())
                .remark(createReqVO.getRemark())
                .build();
        giftConfigMapper.insert(config);
        return config.getId();
    }

    @Override
    public void updateGiftConfig(MemberGiftConfigSaveReqVO updateReqVO) {
        MemberGiftConfigDO config = validateGiftConfigExists(updateReqVO.getId());
        validateNameUnique(config.getId(), updateReqVO.getName());
        validateSpuUnique(config.getId(), updateReqVO.getSpuId());
        MemberGiftConfigDO updateObj = new MemberGiftConfigDO()
                .setId(config.getId())
                .setName(updateReqVO.getName())
                .setLevelName(updateReqVO.getLevelName())
                .setPrice(updateReqVO.getPrice())
                .setPvPercent(updateReqVO.getPvPercent())
                .setSpuId(updateReqVO.getSpuId())
                .setIcon(updateReqVO.getIcon())
                .setSort(updateReqVO.getSort())
                .setStatus(updateReqVO.getStatus())
                .setRemark(updateReqVO.getRemark());
        giftConfigMapper.updateById(updateObj);
    }

    @Override
    public void deleteGiftConfig(Long id) {
        MemberGiftConfigDO config = validateGiftConfigExists(id);
        // 存在权益记录的礼包不可删除，保证权益与 PV 可追溯
        Long benefitCount = giftBenefitMapper.selectCount(MemberGiftBenefitDO::getGiftId, id);
        if (benefitCount != null && benefitCount > 0) {
            throw exception(GIFT_CONFIG_HAS_BENEFIT);
        }
        giftConfigMapper.deleteById(config.getId());
    }

    @Override
    public MemberGiftConfigDO getGiftConfig(Long id) {
        return giftConfigMapper.selectById(id);
    }

    @Override
    public PageResult<MemberGiftConfigDO> getGiftConfigPage(MemberGiftConfigPageReqVO pageReqVO) {
        return giftConfigMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MemberGiftConfigDO> getGiftConfigListEnable() {
        return giftConfigMapper.selectListByStatusEnable();
    }

    @Override
    public MemberGiftConfigDO getGiftConfigBySpuId(Long spuId) {
        if (spuId == null) {
            return null;
        }
        return giftConfigMapper.selectBySpuId(spuId);
    }

    @Override
    public MemberGiftConfigDO validateGiftConfigEnable(Long id) {
        MemberGiftConfigDO config = validateGiftConfigExists(id);
        if (CommonStatusEnum.isDisable(config.getStatus())) {
            throw exception(GIFT_CONFIG_DISABLED, config.getName());
        }
        return config;
    }

    private MemberGiftConfigDO validateGiftConfigExists(Long id) {
        MemberGiftConfigDO config = giftConfigMapper.selectById(id);
        if (config == null) {
            throw exception(GIFT_CONFIG_NOT_EXISTS);
        }
        return config;
    }

    private void validateNameUnique(Long id, String name) {
        MemberGiftConfigDO config = giftConfigMapper.selectByName(name);
        if (config == null || (id != null && config.getId().equals(id))) {
            return;
        }
        throw exception(GIFT_CONFIG_NAME_EXISTS, name);
    }

    private void validateSpuUnique(Long id, Long spuId) {
        if (spuId == null) {
            return;
        }
        MemberGiftConfigDO config = giftConfigMapper.selectBySpuId(spuId);
        if (config == null || (id != null && config.getId().equals(id))) {
            return;
        }
        throw exception(GIFT_CONFIG_SPU_EXISTS, spuId);
    }

}
