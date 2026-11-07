package cn.sailstack.qdp.module.system.api.logger;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.common.biz.system.logger.dto.OperateLogCreateReqDTO;
import cn.sailstack.qdp.module.system.api.logger.dto.OperateLogPageReqDTO;
import cn.sailstack.qdp.module.system.api.logger.dto.OperateLogRespDTO;
import cn.sailstack.qdp.module.system.convert.logger.OperateLogConvert;
import cn.sailstack.qdp.module.system.dal.dataobject.logger.OperateLogDO;
import cn.sailstack.qdp.module.system.service.logger.OperateLogService;
import org.dromara.core.trans.anno.TransMethodResult;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * 操作日志 API 实现类
 *
 * @author sailstack源码
 */
@Service
@Validated
public class OperateLogApiImpl implements OperateLogApi {

    @Resource
    private OperateLogService operateLogService;

    @Override
    public void createOperateLog(OperateLogCreateReqDTO createReqDTO) {
        operateLogService.createOperateLog(createReqDTO);
    }

    @Override
    @TransMethodResult
    public PageResult<OperateLogRespDTO> getOperateLogPage(OperateLogPageReqDTO pageReqDTO) {
        PageResult<OperateLogDO> operateLogPage = operateLogService.getOperateLogPage(pageReqDTO);
        return OperateLogConvert.INSTANCE.convertRespDTOPage(operateLogPage);
    }

}
