package cn.sailstack.qdp.module.infra.convert.logger;

import cn.sailstack.qdp.framework.common.biz.infra.logger.dto.ApiErrorLogCreateReqDTO;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.infra.controller.admin.logger.vo.apierrorlog.ApiErrorLogRespVO;
import cn.sailstack.qdp.module.infra.dal.dataobject.logger.ApiErrorLogDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * API 错误日志 Convert
 *
 * @author sailstack源码
 */
@Mapper
public interface ApiErrorLogConvert {

    ApiErrorLogConvert INSTANCE = Mappers.getMapper(ApiErrorLogConvert.class);

    ApiErrorLogDO convert(ApiErrorLogCreateReqDTO bean);

    ApiErrorLogRespVO convert(ApiErrorLogDO bean);

    List<ApiErrorLogRespVO> convertList(List<ApiErrorLogDO> list);

    default PageResult<ApiErrorLogRespVO> convertPage(PageResult<ApiErrorLogDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

}
