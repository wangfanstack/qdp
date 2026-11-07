package cn.sailstack.qdp.module.product.convert.sku;

import cn.sailstack.qdp.module.product.controller.admin.spu.vo.ProductSkuRespVO;
import cn.sailstack.qdp.module.product.controller.admin.spu.vo.ProductSkuSaveReqVO;
import cn.sailstack.qdp.module.product.dal.dataobject.sku.ProductSkuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 商品 SKU Convert
 *
 * @author 芋道源码
 */
@Mapper
public interface ProductSkuConvert {

    ProductSkuConvert INSTANCE = Mappers.getMapper(ProductSkuConvert.class);

    ProductSkuDO convert(ProductSkuSaveReqVO bean);

    default List<ProductSkuDO> convertList(List<ProductSkuSaveReqVO> list, Consumer<ProductSkuDO> peek) {
        List<ProductSkuDO> result = convertListInternal(list);
        if (result != null) {
            result.forEach(peek);
        }
        return result;
    }

    default List<ProductSkuDO> convertListWithPeek(List<ProductSkuDO> list, Consumer<ProductSkuDO> peek) {
        List<ProductSkuDO> result = convertListInternalFromDO(list);
        if (result != null) {
        }
        return result;
    }


    List<ProductSkuRespVO> convertList(List<ProductSkuDO> list);

    default List<ProductSkuDO> convertListInternal(List<ProductSkuSaveReqVO> list) {
        return list == null ? null : list.stream().map(this::convert).collect(Collectors.toList());
    }

    default List<ProductSkuDO> convertListInternalFromDO(List<ProductSkuDO> list) {
        return list;
    }

    /**
     * 获得 SPU 的库存变化 Map
     *
     * @param items SKU 库存变化
     * @param skus SKU 列表
     * @return SPU 的库存变化 Map
     */
    default java.util.Map<Long, Integer> convertSpuStockMap(java.util.List<cn.sailstack.qdp.module.product.api.sku.dto.ProductSkuUpdateStockReqDTO.Item> items,
                                                          List<ProductSkuDO> skus) {
        java.util.Map<Long, Long> skuIdAndSpuIdMap = new java.util.HashMap<>();
        skus.forEach(sku -> skuIdAndSpuIdMap.put(sku.getId(), sku.getSpuId()));
        java.util.Map<Long, Integer> spuIdAndStockMap = new java.util.HashMap<>();
        items.forEach(item -> {
            Long spuId = skuIdAndSpuIdMap.get(item.getId());
            if (spuId == null) {
                return;
            }
            Integer stock = spuIdAndStockMap.getOrDefault(spuId, 0) + item.getIncrCount();
            spuIdAndStockMap.put(spuId, stock);
        });
        return spuIdAndStockMap;
    }

    default String buildPropertyKey(ProductSkuDO bean) {
        if (bean.getProperties() == null || bean.getProperties().isEmpty()) {
            return "";
        }
        java.util.List<ProductSkuDO.Property> properties = new java.util.ArrayList<>(bean.getProperties());
        properties.sort(java.util.Comparator.comparing(ProductSkuDO.Property::getValueId));
        return properties.stream().map(m -> String.valueOf(m.getValueId())).collect(java.util.stream.Collectors.joining());
    }

}
