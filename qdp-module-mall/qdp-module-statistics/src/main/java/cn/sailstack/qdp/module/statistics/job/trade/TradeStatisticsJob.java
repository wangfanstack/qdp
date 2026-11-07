package cn.sailstack.qdp.module.statistics.job.trade;

import cn.sailstack.qdp.framework.common.util.string.StrUtils;
import cn.sailstack.qdp.framework.common.util.object.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import cn.sailstack.qdp.framework.quartz.core.handler.JobHandler;
import cn.sailstack.qdp.framework.tenant.core.job.TenantJob;
import cn.sailstack.qdp.module.statistics.service.trade.TradeStatisticsService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 交易统计 Job
 *
 * @author owen
 */
@Component
public class TradeStatisticsJob implements JobHandler {

    @Resource
    private TradeStatisticsService tradeStatisticsService;

    /**
     * 执行交易统计任务
     *
     * @param param 要统计的天数，只能是正整数，1 代表昨日数据
     * @return 统计结果
     */
    @Override
    @TenantJob
    public String execute(String param) {
        // 默认昨日
        param = ObjectUtils.defaultIfBlank(param, "1");
        // 校验参数的合理性
        if (!cn.sailstack.qdp.framework.common.util.number.NumberUtils.isInteger(param)) {
            throw new RuntimeException("交易统计任务的参数只能为是正整数");
        }
        Integer days = cn.sailstack.qdp.framework.common.util.number.NumberUtils.toInt(param, 0);
        if (days < 1) {
            throw new RuntimeException("交易统计任务的参数只能为是正整数");
        }
        String result = tradeStatisticsService.statisticsTrade(days);
        return StrUtils.format("交易统计:\n{}", result);
    }

}
