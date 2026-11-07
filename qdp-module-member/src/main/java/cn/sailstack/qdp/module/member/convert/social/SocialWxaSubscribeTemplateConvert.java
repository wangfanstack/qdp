package cn.sailstack.qdp.module.member.convert.social;

import cn.sailstack.qdp.module.system.api.social.dto.SocialWxaSubscribeTemplateRespDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.member.controller.app.social.vo.AppSocialWxaSubscribeTemplateRespVO;
import java.util.List;


/**
 * Bean 对象转换接口（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface SocialWxaSubscribeTemplateConvert {

    SocialWxaSubscribeTemplateConvert INSTANCE = Mappers.getMapper(SocialWxaSubscribeTemplateConvert.class);


    java.util.List<AppSocialWxaSubscribeTemplateRespVO> convertList(java.util.List<cn.sailstack.qdp.module.system.api.social.dto.SocialWxaSubscribeTemplateRespDTO> list);
}
