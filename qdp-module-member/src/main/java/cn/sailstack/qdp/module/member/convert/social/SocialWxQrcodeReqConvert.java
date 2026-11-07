package cn.sailstack.qdp.module.member.convert.social;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.member.controller.app.social.vo.AppSocialWxaQrcodeReqVO;
import cn.sailstack.qdp.module.system.api.social.dto.SocialWxQrcodeReqDTO;

/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface SocialWxQrcodeReqConvert {

    SocialWxQrcodeReqConvert INSTANCE = Mappers.getMapper(SocialWxQrcodeReqConvert.class);

    SocialWxQrcodeReqDTO convert(AppSocialWxaQrcodeReqVO source);
}
