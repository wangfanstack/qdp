package cn.sailstack.qdp.module.product.convert.product;

import cn.sailstack.qdp.module.product.api.spu.dto.ProductSpuRespDTO;
import cn.sailstack.qdp.module.product.dal.dataobject.spu.ProductSpuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * Convert（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface ProductSpuRespConvert {

    ProductSpuRespConvert INSTANCE = Mappers.getMapper(ProductSpuRespConvert.class);

    java.util.List<ProductSpuRespDTO> convertList(java.util.List<ProductSpuDO> list);

    ProductSpuRespDTO convert(ProductSpuDO source);

}
