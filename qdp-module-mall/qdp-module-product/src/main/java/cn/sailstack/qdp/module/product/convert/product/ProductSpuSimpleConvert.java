package cn.sailstack.qdp.module.product.convert.product;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.product.controller.admin.spu.vo.ProductSpuSimpleRespVO;
import cn.sailstack.qdp.module.product.dal.dataobject.spu.ProductSpuDO;
import java.util.List;

@Mapper
public interface ProductSpuSimpleConvert {

    ProductSpuSimpleConvert INSTANCE = Mappers.getMapper(ProductSpuSimpleConvert.class);

    List<ProductSpuSimpleRespVO> convertList(List<ProductSpuDO> list);
}
