package cn.sailstack.qdp.module.member.dal.dataobject.bonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

/**
 * 会员礼包配置 DO
 *
 * 对应需求 2.1 礼包设置：四档礼包（体验官/初级代表/高级课代表/城市合伙人）
 *
 * @author sail
 */
@TableName("member_gift_config")
@KeySequence("member_gift_config_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberGiftConfigDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 礼包名称（例如：会员礼包、课程礼包、加盟店礼包）
     */
    private String name;
    /**
     * 会员等级名称（体验官/初级代表/高级课代表/城市合伙人）
     */
    private String levelName;
    /**
     * 销售价格（分）
     */
    private Integer price;
    /**
     * PV 比例（百分比，需求为 60）
     */
    private Integer pvPercent;
    /**
     * 关联商城商品 SPU 编号（支付载体）
     */
    private Long spuId;
    /**
     * 礼包图标
     */
    private String icon;
    /**
     * 排序（档位从低到高）
     */
    private Integer sort;
    /**
     * 状态
     * <p>
     * 枚举 {@link cn.sailstack.qdp.framework.common.enums.CommonStatusEnum}
     */
    private Integer status;
    /**
     * 备注
     */
    private String remark;

}
