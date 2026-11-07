package cn.sailstack.qdp.module.product.convert.product;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.product.controller.admin.property.vo.property.ProductPropertyRespVO;
import cn.sailstack.qdp.module.product.controller.admin.property.vo.property.ProductPropertySaveReqVO;
import cn.sailstack.qdp.module.product.dal.dataobject.property.ProductPropertyDO;

@Mapper
public interface ProductPropertyConvert {

    ProductPropertyConvert INSTANCE = Mappers.getMapper(ProductPropertyConvert.class);

    ProductPropertyDO convert(ProductPropertySaveReqVO source);
    ProductPropertyRespVO convert(ProductPropertyDO source);
}
