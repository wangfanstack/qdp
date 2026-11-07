package cn.sailstack.qdp.module.member.convert.social;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.member.controller.app.social.vo.AppSocialUserRespVO;
import cn.sailstack.qdp.module.system.api.social.dto.SocialUserRespDTO;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface SocialUserConvert {

    SocialUserConvert INSTANCE = Mappers.getMapper(SocialUserConvert.class);

    AppSocialUserRespVO convert(cn.sailstack.qdp.module.system.api.social.dto.SocialUserRespDTO source);
}
