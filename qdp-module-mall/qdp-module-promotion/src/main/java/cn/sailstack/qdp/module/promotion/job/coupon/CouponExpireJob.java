package cn.sailstack.qdp.module.promotion.job.coupon;

import cn.sailstack.qdp.framework.common.util.string.StrUtils;
import org.apache.commons.lang3.StringUtils;
import cn.sailstack.qdp.framework.quartz.core.handler.JobHandler;
import cn.sailstack.qdp.framework.tenant.core.job.TenantJob;
import cn.sailstack.qdp.module.promotion.service.coupon.CouponService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 优惠券过期 Job
 *
 * @author owen
 */
@Component
public class CouponExpireJob implements JobHandler {

    @Resource
    private CouponService couponService;

    @Override
    @TenantJob
    public String execute(String param) {
        int count = couponService.expireCoupon();
        return StrUtils.format("过期优惠券 {} 个", count);
    }

}
