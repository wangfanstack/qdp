package cn.sailstack.qdp.module.trade.job.productbonus;

import cn.sailstack.qdp.framework.quartz.core.handler.JobHandler;
import cn.sailstack.qdp.framework.tenant.core.job.TenantJob;
import cn.sailstack.qdp.module.member.api.bonus.MemberBonusApi;
import cn.sailstack.qdp.module.member.api.bonus.dto.MemberBonusConfigRespDTO;
import cn.sailstack.qdp.module.trade.service.productbonus.TradeProductBonusSettleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 商品奖金月度结算 Job
 *
 * 参数：账期 yyyy-MM，为空时默认结算上一个自然月。
 * 注意：结算日（settle_day）未配置【待业务确认】时跳过，不得自动正式结算。
 *
 * @author sail
 */
@Component("tradeProductBonusSettleJob")
@Slf4j
public class TradeProductBonusSettleJob implements JobHandler {

    @Resource
    private TradeProductBonusSettleService settleService;
    @Resource
    private MemberBonusApi memberBonusApi;

    @Override
    @TenantJob
    public String execute(String param) {
        MemberBonusConfigRespDTO config = memberBonusApi.getBonusConfig();
        // 结算日未配置：不自动结算（待业务确认项）
        if (config.getSettleDay() == null) {
            log.info("[execute][结算日未配置，跳过本租户商品奖金月结]");
            return "settled_day 未配置，跳过";
        }
        LocalDate today = LocalDate.now();
        if (today.getDayOfMonth() < config.getSettleDay()) {
            log.info("[execute][今日({})未到结算日({})，跳过]", today, config.getSettleDay());
            return "未到结算日，跳过";
        }
        String period = (param == null || param.isBlank())
                ? today.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"))
                : param.trim();
        try {
            settleService.settleMonth(period);
            return "账期(" + period + ")商品奖金结算完成";
        } catch (Exception ex) {
            log.warn("[execute][账期({})商品奖金结算异常：{}]", period, ex.getMessage());
            return "账期(" + period + ")商品奖金结算异常：" + ex.getMessage();
        }
    }

}
