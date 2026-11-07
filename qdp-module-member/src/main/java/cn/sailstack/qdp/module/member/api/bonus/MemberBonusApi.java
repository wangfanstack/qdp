package cn.sailstack.qdp.module.member.api.bonus;

import cn.sailstack.qdp.module.member.api.bonus.dto.MemberBonusConfigRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftConfigRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftEffectReqDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftPrepareRespDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberGiftRefundReqDTO;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberRelationRespDTO;

import jakarta.validation.Valid;
import java.util.List;

/**
 * 会员权益与礼包奖金 API 接口
 *
 * 供 trade（礼包订单/商品奖金）等模块调用；调用方向 mall → member。
 *
 * @author sail
 */
public interface MemberBonusApi {

    /**
     * 下单前准备：校验档位并计算应付金额（全价/补差价）
     *
     * @param userId 会员编号
     * @param giftId 礼包编号
     * @return 准备结果
     */
    MemberGiftPrepareRespDTO prepareGiftBuy(Long userId, Long giftId);

    /**
     * 礼包订单支付成功 → 权益生效并计奖（幂等）
     *
     * @param reqDTO 生效请求
     */
    void effectGiftOrder(@Valid MemberGiftEffectReqDTO reqDTO);

    /**
     * 礼包订单退款成功 → 冲减 PV 与对应奖金（幂等）
     *
     * @param reqDTO 退款请求
     */
    void refundGiftOrder(@Valid MemberGiftRefundReqDTO reqDTO);

    /**
     * 获得会员推荐关系（含市场归属）
     *
     * @param userId 会员编号
     * @return 推荐关系；未绑定返回 null
     */
    MemberRelationRespDTO getRelation(Long userId);

    /**
     * 获得会员的直接上级（推荐人）编号；商品折扣差额奖归属人
     *
     * @param userId 会员编号
     * @return 直接上级编号；无返回 null
     */
    Long getDirectSuperiorId(Long userId);

    /**
     * 绑定推荐关系（幂等；已绑定抛业务异常）
     *
     * @param userId     会员编号
     * @param referrerId 推荐人编号
     */
    void bindRelation(Long userId, Long referrerId);

    /**
     * 获得指定董事等级的会员编号列表（商品董事加权分红用）
     *
     * @param level 董事等级（1-5）
     * @return 会员编号列表
     */
    List<Long> getDirectorUserIds(Integer level);

    /**
     * 校验会员是否存在（下单前校验推荐人有效性）
     *
     * @param userId 会员编号
     * @return 是否存在
     */
    boolean validateUserExists(Long userId);

    /**
     * 按关联 SPU 获得开启中的礼包配置（trade 下单链路识别礼包订单用）
     *
     * @param spuId SPU 编号
     * @return 礼包配置；未绑定或未开启返回 null
     */
    MemberGiftConfigRespDTO getEnabledGiftConfigBySpuId(Long spuId);

    /**
     * 获得开启中的礼包配置列表（按档位从低到高；App 礼包档位展示用）
     *
     * @return 礼包配置列表
     */
    List<MemberGiftConfigRespDTO> getEnabledGiftConfigList();

    /**
     * 获得指定礼包配置（开启中）
     *
     * @param giftId 礼包编号
     * @return 礼包配置；不存在或未开启返回 null
     */
    MemberGiftConfigRespDTO getEnabledGiftConfig(Long giftId);

    /**
     * 获得奖金规则配置（商品奖金结算读取奖金池比例/董事比例等）
     *
     * @return 配置
     */
    MemberBonusConfigRespDTO getBonusConfig();

}
