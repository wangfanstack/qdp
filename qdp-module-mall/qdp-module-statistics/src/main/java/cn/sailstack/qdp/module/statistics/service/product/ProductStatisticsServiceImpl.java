package cn.sailstack.qdp.module.statistics.service.product;

import cn.sailstack.qdp.framework.common.util.collection.CollectionUtils;
import cn.sailstack.qdp.framework.common.util.date.LocalDateTimeUtils;
import cn.sailstack.qdp.framework.common.util.collection.ArrayUtils;
import cn.sailstack.qdp.framework.common.util.object.ObjectUtils;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.common.pojo.SortablePageParam;
import cn.sailstack.qdp.framework.mybatis.core.util.MyBatisUtils;
import cn.sailstack.qdp.module.statistics.controller.admin.common.vo.DataComparisonRespVO;
import cn.sailstack.qdp.module.statistics.controller.admin.product.vo.ProductStatisticsReqVO;
import cn.sailstack.qdp.module.statistics.controller.admin.product.vo.ProductStatisticsRespVO;
import cn.sailstack.qdp.module.statistics.dal.dataobject.product.ProductStatisticsDO;
import cn.sailstack.qdp.module.statistics.dal.mysql.product.ProductStatisticsMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.validation.annotation.Validated;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


/**
 * 商品统计 Service 实现类
 *
 * @author owen
 */
@Service
@Validated
public class ProductStatisticsServiceImpl implements ProductStatisticsService {

    @Resource
    private ProductStatisticsMapper productStatisticsMapper;


    @Override
    public PageResult<ProductStatisticsDO> getProductStatisticsRankPage(ProductStatisticsReqVO reqVO, SortablePageParam pageParam) {
        MyBatisUtils.buildDefaultSortingField(pageParam, ProductStatisticsDO::getBrowseCount); // 默认浏览量倒序
        return productStatisticsMapper.selectPageGroupBySpuId(reqVO, pageParam);
    }

    @Override
    public DataComparisonRespVO<ProductStatisticsRespVO> getProductStatisticsAnalyse(ProductStatisticsReqVO reqVO) {
        LocalDateTime beginTime = ArrayUtils.get(reqVO.getTimes(), 0);
        LocalDateTime endTime = ArrayUtils.get(reqVO.getTimes(), 1);

        // 统计数据
        ProductStatisticsRespVO value = productStatisticsMapper.selectVoByTimeBetween(reqVO);
        // 对照数据
        LocalDateTime referenceBeginTime = beginTime.minus(Duration.between(beginTime, endTime));
        ProductStatisticsReqVO referenceReqVO = new ProductStatisticsReqVO(new LocalDateTime[]{referenceBeginTime, beginTime});
        ProductStatisticsRespVO reference = productStatisticsMapper.selectVoByTimeBetween(referenceReqVO);
        return new DataComparisonRespVO<>(value, reference);
    }

    @Override
    public List<ProductStatisticsDO> getProductStatisticsList(ProductStatisticsReqVO reqVO) {
        return productStatisticsMapper.selectListByTimeBetween(reqVO);
    }

    @Override
    public String statisticsProduct(Integer days) {
        LocalDateTime today = LocalDateTime.now();
        return IntStream.rangeClosed(1, days)
                .mapToObj(day -> statisticsProduct(today.minusDays(day)))
                .sorted()
                .collect(Collectors.joining("\n"));
    }

    /**
     * 统计商品数据
     *
     * @param date 需要统计的日期
     * @return 统计结果
     */
    private String statisticsProduct(LocalDateTime date) {
        // 1. 处理统计时间范围
        LocalDateTime beginTime = LocalDateTimeUtils.beginOfDay(date);
        LocalDateTime endTime = LocalDateTimeUtils.endOfDay(date);
        String dateStr = date.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        // 2. 检查该日是否已经统计过
        Long count = productStatisticsMapper.selectCountByTimeBetween(beginTime, endTime);
        if (count != null && count > 0) {
            return dateStr + " 数据已存在，如果需要重新统计，请先删除对应的数据";
        }

        StopWatch stopWatch = new StopWatch(dateStr);
        stopWatch.start();
        // 4. 分页统计，避免商品表数据较多时，出现超时问题
        final int pageSize = 100;
        for (int pageNo = 1; ; pageNo++) {
            IPage<ProductStatisticsDO> page = productStatisticsMapper.selectStatisticsResultPageByTimeBetween(
                    Page.of(pageNo, pageSize, false), beginTime, endTime);
            if (CollectionUtils.isEmpty(page.getRecords())) {
                break;
            }
            // 4.1 计算访客支付转化率（百分比）
            for (ProductStatisticsDO record : page.getRecords()) {
                record.setTime(date.toLocalDate());
                if (record.getBrowseUserCount() != null && ObjectUtils.notEqual(record.getBrowseUserCount(), 0)) {
                    record.setBrowseConvertPercent(100 * record.getOrderPayCount() / record.getBrowseUserCount());
                }
            }
            // 4.2 插入数据
            productStatisticsMapper.insertBatch(page.getRecords());
        }
        return stopWatch.prettyPrint();
    }

}