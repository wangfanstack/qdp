package cn.sailstack.qdp.module.infra.convert.demo;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.infra.controller.admin.demo.demo02.vo.Demo02CategoryRespVO;
import cn.sailstack.qdp.module.infra.controller.admin.demo.demo02.vo.Demo02CategorySaveReqVO;
import cn.sailstack.qdp.module.infra.dal.dataobject.demo.demo02.Demo02CategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 示例分类 Convert
 *
 * @author sailstack源码
 */
@Mapper
public interface Demo02CategoryConvert {

    Demo02CategoryConvert INSTANCE = Mappers.getMapper(Demo02CategoryConvert.class);

    Demo02CategoryDO convert(Demo02CategorySaveReqVO bean);

    Demo02CategoryRespVO convert(Demo02CategoryDO bean);

    List<Demo02CategoryRespVO> convertList(List<Demo02CategoryDO> list);

    default PageResult<Demo02CategoryRespVO> convertPage(PageResult<Demo02CategoryDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

}
