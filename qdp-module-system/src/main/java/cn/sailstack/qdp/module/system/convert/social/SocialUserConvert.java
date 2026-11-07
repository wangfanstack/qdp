package cn.sailstack.qdp.module.system.convert.social;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.system.controller.admin.social.vo.user.SocialUserRespVO;
import cn.sailstack.qdp.module.system.dal.dataobject.social.SocialUserDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface SocialUserConvert {

    SocialUserConvert INSTANCE = Mappers.getMapper(SocialUserConvert.class);

    SocialUserRespVO convertToRespVO(SocialUserDO bean);

    List<SocialUserRespVO> convertRespVOList(List<SocialUserDO> list);

    default PageResult<SocialUserRespVO> convertRespVOPage(PageResult<SocialUserDO> page) {
        return new PageResult<>(convertRespVOList(page.getList()), page.getTotal());
    }

}
