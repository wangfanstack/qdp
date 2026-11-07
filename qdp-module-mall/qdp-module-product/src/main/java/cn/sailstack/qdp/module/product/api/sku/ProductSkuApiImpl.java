package cn.sailstack.qdp.module.product.api.sku;


import cn.sailstack.qdp.framework.common.util.object.BeanUtils;
import cn.sailstack.qdp.module.product.api.sku.dto.ProductSkuRespDTO;
import cn.sailstack.qdp.module.product.api.sku.dto.ProductSkuUpdateStockReqDTO;
import cn.sailstack.qdp.module.product.dal.dataobject.sku.ProductSkuDO;
import cn.sailstack.qdp.module.product.service.sku.ProductSkuService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.List;

/**
 * 商品 SKU API 实现类
 *
 * @author LeeYan9
 * @since 2022-09-06
 */
@Service
@Validated
public class ProductSkuApiImpl implements ProductSkuApi {

    @Resource
    private ProductSkuService productSkuService;

    @Override
    public ProductSkuRespDTO getSku(Long id) {
        ProductSkuDO sku = productSkuService.getSku(id);
        return cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(sku, ProductSkuRespDTO.class);
    }

    @Override
    public List<ProductSkuRespDTO> getSkuList(Collection<Long> ids) {
        List<ProductSkuDO> skus = productSkuService.getSkuList(ids);
        return cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(skus, ProductSkuRespDTO.class);
    }

    @Override
    public List<ProductSkuRespDTO> getSkuListBySpuId(Collection<Long> spuIds) {
        List<ProductSkuDO> skus = productSkuService.getSkuListBySpuId(spuIds);
        return cn.sailstack.qdp.framework.common.util.object.BeanUtils.toBean(skus, ProductSkuRespDTO.class);
    }

    @Override
    public void updateSkuStock(ProductSkuUpdateStockReqDTO updateStockReqDTO) {
        productSkuService.updateSkuStock(updateStockReqDTO);
    }

}
