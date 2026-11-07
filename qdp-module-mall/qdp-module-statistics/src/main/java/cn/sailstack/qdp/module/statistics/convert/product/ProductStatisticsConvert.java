package cn.sailstack.qdp.module.statistics.convert.product;

import cn.sailstack.qdp.module.statistics.controller.admin.product.vo.ProductStatisticsRespVO;
import cn.sailstack.qdp.module.statistics.dal.dataobject.product.ProductStatisticsDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

/**
 * 商品统计 Convert（MapStruct）
 *
 * @author sailstack源码
 */
@Mapper
public interface ProductStatisticsConvert {

    ProductStatisticsConvert INSTANCE = Mappers.getMapper(ProductStatisticsConvert.class);

    List<ProductStatisticsRespVO> convertList(List<ProductStatisticsDO> list);

    ProductStatisticsRespVO convert(ProductStatisticsDO source);



    cn.sailstack.qdp.framework.common.pojo.PageResult<ProductStatisticsRespVO> convertPage(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.statistics.dal.dataobject.product.ProductStatisticsDO> page);

    default cn.sailstack.qdp.framework.common.pojo.PageResult<ProductStatisticsRespVO> convertPage(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.statistics.dal.dataobject.product.ProductStatisticsDO> page, java.util.function.Consumer<ProductStatisticsRespVO> peek) {
        cn.sailstack.qdp.framework.common.pojo.PageResult<ProductStatisticsRespVO> result = convertPage(page);
        if (result != null && result.getList() != null) {
            result.getList().forEach(peek);
        }
        return result;
    }
}
