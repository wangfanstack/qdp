package cn.sailstack.qdp.module.trade.service.giftorder;

import cn.sailstack.qdp.module.trade.controller.app.giftorder.vo.AppGiftOrderCreateReqVO;
import cn.sailstack.qdp.module.trade.controller.app.giftorder.vo.AppGiftOrderCreateRespVO;
import cn.sailstack.qdp.module.trade.controller.app.giftorder.vo.AppGiftRespVO;

import java.util.List;

/**
 * 会员礼包订单（开通/升级）Service 接口
 *
 * @author sail
 */
public interface TradeGiftOrderService {

    /**
     * 获得会员可购买的礼包档位列表（含当前应付金额/是否升级）
     *
     * @param userId 会员编号
     * @return 礼包档位列表
     */
    List<AppGiftRespVO> getGiftList(Long userId);

    /**
     * 创建礼包开通/升级订单（复用标准交易订单链路，由 TradeGiftOrderHandler 标记类型与补差价）
     *
     * @param userId  会员编号
     * @param reqVO   下单请求
     * @return 订单编号 + 支付单编号
     */
    AppGiftOrderCreateRespVO createGiftOrder(Long userId, AppGiftOrderCreateReqVO reqVO);

}
