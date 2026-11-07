package cn.sailstack.qdp.module.trade.dal.dataobject.productbonus;

import cn.sailstack.qdp.framework.tenant.core.db.TenantBaseDO;
import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 会员商品 GMV 记录 DO（按订单一条）
 *
 * uk(order_id)：订单完成时写入，售后退款累减 refund_amount；
 * 礼包订单/未完成/已取消/未支付订单不写入。
 *
 * @author sail
 */
@TableName("trade_product_gmv_record")
@KeySequence("trade_product_gmv_record_seq")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TradeProductGmvRecordDO extends TenantBaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 订单编号（唯一）
     */
    private Long orderId;
    /**
     * 订单流水号
     */
    private String orderNo;
    /**
     * 购买会员编号
     */
    private Long userId;
    /**
     * 订单实付金额（分）
     */
    private Integer payAmount;
    /**
     * 累计退款金额（分）
     */
    private Integer refundAmount;
    /**
     * 有效 GMV（分，= pay_amount - refund_amount）
     */
    private Integer gmvAmount;
    /**
     * 订单完成所在账期（yyyy-MM）
     */
    private String period;
    /**
     * 订单完成时间
     */
    private LocalDateTime finishTime;

}
