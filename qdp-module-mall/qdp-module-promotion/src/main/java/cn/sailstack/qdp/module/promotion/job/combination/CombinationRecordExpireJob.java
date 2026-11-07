package cn.sailstack.qdp.module.promotion.job.combination;

import cn.sailstack.qdp.framework.common.util.string.StrUtils;
import org.apache.commons.lang3.StringUtils;
import cn.sailstack.qdp.framework.common.core.KeyValue;
import cn.sailstack.qdp.framework.quartz.core.handler.JobHandler;
import cn.sailstack.qdp.framework.tenant.core.job.TenantJob;
import cn.sailstack.qdp.module.promotion.service.combination.CombinationRecordService;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;

/**
 * 拼团过期 Job
 *
 * @author HUIHUI
 */
@Component
public class CombinationRecordExpireJob implements JobHandler {

    @Resource
    private CombinationRecordService combinationRecordService;

    @Override
    @TenantJob
    public String execute(String param) {
        KeyValue<Integer, Integer> keyValue = combinationRecordService.expireCombinationRecord();
        return StrUtils.format("过期拼团 {} 个, 虚拟成团 {} 个", keyValue.getKey(), keyValue.getValue());
    }

}
