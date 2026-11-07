package cn.sailstack.qdp.module.infra.convert.file;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.infra.controller.admin.file.vo.file.FileCreateReqVO;
import cn.sailstack.qdp.module.infra.controller.admin.file.vo.file.FileRespVO;
import cn.sailstack.qdp.module.infra.dal.dataobject.file.FileDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 文件 Convert
 *
 * @author sailstack源码
 */
@Mapper
public interface FileConvert {

    FileConvert INSTANCE = Mappers.getMapper(FileConvert.class);

    FileDO convert(FileCreateReqVO bean);

    FileRespVO convert(FileDO bean);

    List<FileRespVO> convertList(List<FileDO> list);

    default PageResult<FileRespVO> convertPage(PageResult<FileDO> page) {
        return new PageResult<>(convertList(page.getList()), page.getTotal());
    }

}
