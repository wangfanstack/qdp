package cn.sailstack.qdp.module.member.service.bonus.gift;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.gift.MemberGiftConfigPageReqVO;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.gift.MemberGiftConfigSaveReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberGiftConfigDO;

import java.util.List;

/**
 * 会员礼包配置 Service 接口
 *
 * @author sail
 */
public interface MemberGiftConfigService {

    /**
     * 创建礼包配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createGiftConfig(MemberGiftConfigSaveReqVO createReqVO);

    /**
     * 更新礼包配置
     *
     * @param updateReqVO 更新信息
     */
    void updateGiftConfig(MemberGiftConfigSaveReqVO updateReqVO);

    /**
     * 删除礼包配置（存在生效权益记录时禁止删除）
     *
     * @param id 编号
     */
    void deleteGiftConfig(Long id);

    /**
     * 获得礼包配置
     *
     * @param id 编号
     * @return 礼包配置
     */
    MemberGiftConfigDO getGiftConfig(Long id);

    /**
     * 获得礼包配置分页
     *
     * @param pageReqVO 分页查询
     * @return 礼包配置分页
     */
    PageResult<MemberGiftConfigDO> getGiftConfigPage(MemberGiftConfigPageReqVO pageReqVO);

    /**
     * 获得开启中的礼包列表（按档位从低到高）
     *
     * @return 礼包配置列表
     */
    List<MemberGiftConfigDO> getGiftConfigListEnable();

    /**
     * 按关联 SPU 获得礼包配置
     *
     * @param spuId SPU 编号
     * @return 礼包配置；未绑定返回 null
     */
    MemberGiftConfigDO getGiftConfigBySpuId(Long spuId);

    /**
     * 校验礼包配置存在并开启
     *
     * @param id 礼包编号
     * @return 礼包配置
     */
    MemberGiftConfigDO validateGiftConfigEnable(Long id);

}
