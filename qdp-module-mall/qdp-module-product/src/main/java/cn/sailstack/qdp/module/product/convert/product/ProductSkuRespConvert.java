package cn.sailstack.qdp.module.product.convert.product;

import cn.sailstack.qdp.module.product.api.sku.dto.ProductSkuRespDTO;
import cn.sailstack.qdp.module.product.dal.dataobject.sku.ProductSkuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Convert（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface ProductSkuRespConvert {

    ProductSkuRespConvert INSTANCE = Mappers.getMapper(ProductSkuRespConvert.class);

    java.util.List<ProductSkuRespDTO> convertList(java.util.List<ProductSkuDO> list);

    ProductSkuRespDTO convert(ProductSkuDO source);

}
