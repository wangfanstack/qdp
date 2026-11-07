package cn.sailstack.qdp.module.product.controller.app.comment;

import cn.sailstack.qdp.framework.common.util.object.BeanUtils;
import cn.sailstack.qdp.framework.common.util.collection.CollectionUtils;
import cn.sailstack.qdp.framework.common.pojo.CommonResult;
import cn.sailstack.qdp.framework.common.pojo.PageResult;

import cn.sailstack.qdp.module.product.controller.app.comment.vo.AppCommentPageReqVO;
import cn.sailstack.qdp.module.product.controller.app.comment.vo.AppProductCommentRespVO;
import cn.sailstack.qdp.module.product.dal.dataobject.comment.ProductCommentDO;
import cn.sailstack.qdp.module.product.service.comment.ProductCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.sailstack.qdp.framework.common.pojo.CommonResult.success;

@Tag(name = "用户 APP - 商品评价")
@RestController
@RequestMapping("/product/comment")
@Validated
public class AppProductCommentController {

    @Resource
    private ProductCommentService productCommentService;

    @GetMapping("/page")
    @Operation(summary = "获得商品评价分页")
    @PermitAll
    public CommonResult<PageResult<AppProductCommentRespVO>> getCommentPage(@Valid AppCommentPageReqVO pageVO) {
        // 查询评论分页
        PageResult<ProductCommentDO> pageResult = productCommentService.getCommentPage(pageVO, Boolean.TRUE);
        if (CollectionUtils.isEmpty(pageResult.getList())) {
            return success(PageResult.empty(pageResult.getTotal()));
        }

        // 拼接返回
        pageResult.getList().forEach(item -> {
            if (Boolean.TRUE.equals(item.getAnonymous())) {
                item.setUserNickname(ProductCommentDO.NICKNAME_ANONYMOUS);
            }
        });
        return success(cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(pageResult, AppProductCommentRespVO.class));
    }

}
