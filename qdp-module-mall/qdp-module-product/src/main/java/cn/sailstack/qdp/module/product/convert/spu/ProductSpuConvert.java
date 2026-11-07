package cn.sailstack.qdp.module.product.convert.spu;

import cn.sailstack.qdp.framework.common.util.object.BeanUtils;
import cn.sailstack.qdp.module.product.convert.spu.ProductSpuConvert;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.product.controller.admin.spu.vo.ProductSpuSaveReqVO;
import cn.sailstack.qdp.module.product.convert.sku.ProductSkuConvert;
import cn.sailstack.qdp.framework.common.util.collection.CollectionUtils;

import cn.sailstack.qdp.module.product.controller.admin.spu.vo.ProductSkuRespVO;
import cn.sailstack.qdp.module.product.controller.admin.spu.vo.ProductSpuPageReqVO;
import cn.sailstack.qdp.module.product.controller.admin.spu.vo.ProductSpuRespVO;
import cn.sailstack.qdp.module.product.controller.app.spu.vo.AppProductSpuPageReqVO;
import cn.sailstack.qdp.module.product.dal.dataobject.sku.ProductSkuDO;
import cn.sailstack.qdp.module.product.dal.dataobject.spu.ProductSpuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;

import static cn.sailstack.qdp.framework.common.util.collection.CollectionUtils.convertMultiMap;

/**
 * 商品 SPU Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ProductSpuConvert {

    ProductSpuConvert INSTANCE = Mappers.getMapper(ProductSpuConvert.class);

    ProductSpuPageReqVO convert(AppProductSpuPageReqVO bean);

    ProductSpuRespVO convert(cn.sailstack.qdp.module.product.dal.dataobject.spu.ProductSpuDO source);

    default ProductSpuRespVO convert(ProductSpuDO spu, List<ProductSkuDO> skus) {
        ProductSpuRespVO spuVO = INSTANCE.convert(spu);
        spuVO.setSkus(cn.sailstack.qdp.framework.common.util.collection.CollectionUtils.convertList(skus, sku -> cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(sku, cn.sailstack.qdp.module.product.controller.admin.spu.vo.ProductSkuRespVO.class)));
        return spuVO;
    }

    default List<ProductSpuRespVO> convertForSpuDetailRespListVO(List<ProductSpuDO> spus, List<ProductSkuDO> skus) {
        Map<Long, List<ProductSkuDO>> skuMultiMap = convertMultiMap(skus, ProductSkuDO::getSpuId);
        return CollectionUtils.convertList(spus, spu -> convert(spu, skuMultiMap.get(spu.getId())));
    }







    java.util.List<cn.sailstack.qdp.module.product.controller.app.spu.vo.AppProductSpuRespVO> convertAppList(java.util.List<cn.sailstack.qdp.module.product.dal.dataobject.spu.ProductSpuDO> list);


}
