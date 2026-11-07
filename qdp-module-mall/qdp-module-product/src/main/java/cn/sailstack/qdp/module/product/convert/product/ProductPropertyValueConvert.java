package cn.sailstack.qdp.module.product.convert.product;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.product.controller.admin.property.vo.value.ProductPropertyValueRespVO;
import cn.sailstack.qdp.module.product.controller.admin.property.vo.value.ProductPropertyValueSaveReqVO;
import cn.sailstack.qdp.module.product.dal.dataobject.property.ProductPropertyValueDO;

@Mapper
public interface ProductPropertyValueConvert {

    ProductPropertyValueConvert INSTANCE = Mappers.getMapper(ProductPropertyValueConvert.class);

    ProductPropertyValueDO convert(ProductPropertyValueSaveReqVO source);
    ProductPropertyValueRespVO convert(ProductPropertyValueDO source);
}
