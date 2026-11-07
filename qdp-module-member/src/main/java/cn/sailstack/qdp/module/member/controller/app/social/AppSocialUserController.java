package cn.sailstack.qdp.module.member.controller.app.social;

import cn.sailstack.qdp.framework.common.util.object.BeanUtils;
import cn.sailstack.qdp.module.member.convert.social.SocialWxaSubscribeTemplateConvert;
import cn.sailstack.qdp.module.member.convert.social.SocialWxQrcodeReqConvert;
import cn.sailstack.qdp.module.member.convert.social.SocialUserConvert;
import cn.sailstack.qdp.framework.common.enums.UserTypeEnum;
import cn.sailstack.qdp.framework.common.pojo.CommonResult;

import cn.sailstack.qdp.module.member.controller.app.social.vo.*;
import cn.sailstack.qdp.module.system.api.social.SocialClientApi;
import cn.sailstack.qdp.module.system.api.social.SocialUserApi;
import cn.sailstack.qdp.module.system.api.social.dto.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;
import static cn.sailstack.qdp.framework.security.core.util.SecurityFrameworkUtils.getLoginUserId;

@Tag(name = "用户 App - 社交用户")
@RestController
@RequestMapping("/member/social-user")
@Validated
public class AppSocialUserController {

    @Resource
    private SocialUserApi socialUserApi;
    @Resource
    private SocialClientApi socialClientApi;

    @PostMapping("/bind")
    @Operation(summary = "社交绑定，使用 code 授权码")
    @PermitAll
    public CommonResult<String> socialBind(@RequestBody @Valid AppSocialUserBindReqVO reqVO) {
        SocialUserBindReqDTO reqDTO = new SocialUserBindReqDTO(getLoginUserId(), UserTypeEnum.MEMBER.getValue(),
                reqVO.getType(), reqVO.getCode(), reqVO.getState());
        String openid = socialUserApi.bindSocialUser(reqDTO);
        return success(openid);
    }

    @DeleteMapping("/unbind")
    @Operation(summary = "取消社交绑定")
    public CommonResult<Boolean> socialUnbind(@RequestBody AppSocialUserUnbindReqVO reqVO) {
        SocialUserUnbindReqDTO reqDTO = new SocialUserUnbindReqDTO(getLoginUserId(), UserTypeEnum.MEMBER.getValue(),
                reqVO.getType(), reqVO.getOpenid());
        socialUserApi.unbindSocialUser(reqDTO);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得社交用户")
    @Parameter(name = "type", description = "社交平台的类型，参见 SocialTypeEnum 枚举值", required = true, example = "10")
    public CommonResult<AppSocialUserRespVO> getSocialUser(@RequestParam("type") Integer type) {
        SocialUserRespDTO socialUser = socialUserApi.getSocialUserByUserId(UserTypeEnum.MEMBER.getValue(), getLoginUserId(), type);
        return success(cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(socialUser, AppSocialUserRespVO.class));
    }

    @PostMapping("/wxa-qrcode")
    @Operation(summary = "获得微信小程序码(base64 image)")
    @PermitAll
    public CommonResult<String> getWxaQrcode(@RequestBody @Valid AppSocialWxaQrcodeReqVO reqVO) {
        byte[] wxQrcode = socialClientApi.getWxaQrcode(cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(reqVO, SocialWxQrcodeReqDTO.class));
        return success(java.util.Base64.getEncoder().encodeToString(wxQrcode));
    }

    @GetMapping("/get-subscribe-template-list")
    @Operation(summary = "获得微信小程订阅模板列表")
    @PermitAll
    public CommonResult<List<AppSocialWxaSubscribeTemplateRespVO>> getSubscribeTemplateList() {
        List<SocialWxaSubscribeTemplateRespDTO> template = socialClientApi.getWxaSubscribeTemplateList(UserTypeEnum.MEMBER.getValue());
        return success(cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(template, AppSocialWxaSubscribeTemplateRespVO.class));
    }

}
