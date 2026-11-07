package cn.sailstack.qdp.module.product.convert.product;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.product.controller.admin.history.vo.ProductBrowseHistoryPageReqVO;
import cn.sailstack.qdp.module.product.controller.admin.history.vo.ProductBrowseHistoryRespVO;
import cn.sailstack.qdp.module.product.controller.app.history.vo.AppProductBrowseHistoryPageReqVO;
import cn.sailstack.qdp.module.product.controller.app.history.vo.AppProductBrowseHistoryRespVO;
import cn.sailstack.qdp.module.product.dal.dataobject.history.ProductBrowseHistoryDO;
import java.util.function.Consumer;

@Mapper
public interface ProductBrowseHistoryConvert {

    ProductBrowseHistoryConvert INSTANCE = Mappers.getMapper(ProductBrowseHistoryConvert.class);

    ProductBrowseHistoryPageReqVO convert(AppProductBrowseHistoryPageReqVO source);
    PageResult<ProductBrowseHistoryRespVO> convertPage(PageResult<ProductBrowseHistoryDO> page);
    PageResult<AppProductBrowseHistoryRespVO> convertAppPage(PageResult<ProductBrowseHistoryDO> page);
    default PageResult<AppProductBrowseHistoryRespVO> convertAppPage(PageResult<ProductBrowseHistoryDO> page, Consumer<AppProductBrowseHistoryRespVO> peek) {
        PageResult<AppProductBrowseHistoryRespVO> result = convertAppPage(page);
        if (result != null && result.getList() != null) {
            result.getList().forEach(peek);
        }
        return result;
    }


    default PageResult<ProductBrowseHistoryRespVO> convertPage(cn.sailstack.qdp.framework.common.pojo.PageResult<cn.sailstack.qdp.module.product.dal.dataobject.history.ProductBrowseHistoryDO> page, java.util.function.Consumer<ProductBrowseHistoryRespVO> peek) {
        cn.sailstack.qdp.framework.common.pojo.PageResult<ProductBrowseHistoryRespVO> result = convertPage(page);
        if (result != null && result.getList() != null) {
        }
        return result;
    }
}
