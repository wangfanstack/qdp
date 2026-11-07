package cn.sailstack.qdp.module.product.convert.product;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.product.controller.admin.category.vo.ProductCategoryRespVO;
import cn.sailstack.qdp.module.product.controller.admin.category.vo.ProductCategorySaveReqVO;
import cn.sailstack.qdp.module.product.dal.dataobject.category.ProductCategoryDO;
import java.util.List;

@Mapper
public interface ProductCategoryConvert {

    ProductCategoryConvert INSTANCE = Mappers.getMapper(ProductCategoryConvert.class);

    ProductCategoryDO convert(ProductCategorySaveReqVO source);
    ProductCategoryRespVO convert(ProductCategoryDO source);
    List<ProductCategoryRespVO> convertList(List<ProductCategoryDO> list);
}
