package cn.sailstack.qdp.module.product.convert.product;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.module.product.controller.app.spu.vo.AppProductSpuDetailRespVO;
import cn.sailstack.qdp.module.product.dal.dataobject.sku.ProductSkuDO;
import cn.sailstack.qdp.module.product.dal.dataobject.spu.ProductSpuDO;
import java.util.List;

@Mapper
public interface ProductSpuDetailConvert {

    ProductSpuDetailConvert INSTANCE = Mappers.getMapper(ProductSpuDetailConvert.class);

    AppProductSpuDetailRespVO convert(ProductSpuDO source);
    List<AppProductSpuDetailRespVO.Sku> convertList(List<ProductSkuDO> list);
}
