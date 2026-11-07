package cn.sailstack.qdp.module.product.convert.category;

import cn.sailstack.qdp.module.product.controller.app.category.vo.AppCategoryRespVO;
import cn.sailstack.qdp.module.product.dal.dataobject.category.ProductCategoryDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 商品分类 Convert（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface CategoryConvert {

    CategoryConvert INSTANCE = Mappers.getMapper(CategoryConvert.class);

    java.util.List<AppCategoryRespVO> convertList(java.util.List<ProductCategoryDO> list);

}
