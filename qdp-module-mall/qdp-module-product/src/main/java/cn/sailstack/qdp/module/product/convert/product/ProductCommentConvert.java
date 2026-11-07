package cn.sailstack.qdp.module.product.convert.product;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.product.controller.admin.comment.vo.ProductCommentRespVO;
import cn.sailstack.qdp.module.product.controller.app.comment.vo.AppProductCommentRespVO;
import cn.sailstack.qdp.module.product.dal.dataobject.comment.ProductCommentDO;

@Mapper
public interface ProductCommentConvert {

    ProductCommentConvert INSTANCE = Mappers.getMapper(ProductCommentConvert.class);

    PageResult<AppProductCommentRespVO> convertPage(PageResult<ProductCommentDO> page);
    ProductCommentRespVO convert(ProductCommentDO source);
}
