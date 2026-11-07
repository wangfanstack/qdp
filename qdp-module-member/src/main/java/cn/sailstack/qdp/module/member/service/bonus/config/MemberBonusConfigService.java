package cn.sailstack.qdp.module.member.service.bonus.config;

import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.config.MemberBonusConfigSaveReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO;

/**
 * 奖金规则配置 Service 接口
 *
 * @author sail
 */
public interface MemberBonusConfigService {

    /**
     * 获取奖金规则配置（不存在抛出异常）
     *
     * @return 配置
     */
    MemberBonusConfigDO getRequiredConfig();

    /**
     * 更新奖金规则配置（规则版本自增）
     *
     * @param updateReqVO 更新信息
     */
    void updateConfig(MemberBonusConfigSaveReqVO updateReqVO);

}
