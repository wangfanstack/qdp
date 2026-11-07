package cn.sailstack.qdp.module.member.job.bonus;

import cn.sailstack.qdp.framework.quartz.core.handler.JobHandler;
import cn.sailstack.qdp.framework.tenant.core.job.TenantJob;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO;
import cn.sailstack.qdp.module.member.service.bonus.config.MemberBonusConfigService;
import cn.sailstack.qdp.module.member.service.bonus.settle.MemberBonusSettleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 会员礼包奖金月度结算 Job
 *
 * 参数：账期 yyyy-MM，为空时默认结算上一个自然月。
 * 注意：结算日（settle_day）未配置【待业务确认】时跳过，不得自动正式结算；
 * 分红池收入口径为 MANUAL 时需通过管理端手动结算并录入分红池。
 *
 * @author sail
 */
@Component("memberBonusSettleJob")
@Slf4j
public class MemberBonusSettleJob implements JobHandler {

    @Resource
    private MemberBonusSettleService settleService;
    @Resource
    private MemberBonusConfigService configService;

    @Override
    @TenantJob
    public String execute(String param) {
        MemberBonusConfigDO config = configService.getRequiredConfig();
        // 结算日未配置：不自动结算（待业务确认项）
        if (config.getSettleDay() == null) {
            log.info("[execute][结算日未配置，跳过本租户礼包奖金月结]");
            return "settled_day 未配置，跳过";
        }
        LocalDate today = LocalDate.now();
        if (today.getDayOfMonth() < config.getSettleDay()) {
            log.info("[execute][今日({})未到结算日({})，跳过]", today, config.getSettleDay());
            return "未到结算日，跳过";
        }
        // 默认结算上一个自然月
        String period = (param == null || param.isBlank())
                ? today.minusMonths(1).format(DateTimeFormatter.ofPattern("yyyy-MM"))
                : param.trim();
        try {
            settleService.settleMonth(period, null);
            return "账期(" + period + ")结算完成";
        } catch (Exception ex) {
            // 同账期已结算（幂等补跑）直接返回，其余异常抛出由任务日志记录
            log.warn("[execute][账期({})结算异常：{}]", period, ex.getMessage());
            return "账期(" + period + ")结算异常：" + ex.getMessage();
        }
    }

}
