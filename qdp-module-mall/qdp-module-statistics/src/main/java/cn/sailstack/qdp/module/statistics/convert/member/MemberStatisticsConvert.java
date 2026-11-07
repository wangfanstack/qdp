package cn.sailstack.qdp.module.statistics.convert.member;

import cn.sailstack.qdp.framework.common.util.collection.MapUtils;
import cn.sailstack.qdp.framework.common.util.object.ObjectUtils;
import cn.sailstack.qdp.framework.common.util.collection.CollectionUtils;
import cn.sailstack.qdp.framework.ip.core.Area;
import cn.sailstack.qdp.module.statistics.controller.admin.common.vo.DataComparisonRespVO;
import cn.sailstack.qdp.module.statistics.controller.admin.member.vo.MemberAnalyseDataRespVO;
import cn.sailstack.qdp.module.statistics.controller.admin.member.vo.MemberAnalyseRespVO;
import cn.sailstack.qdp.module.statistics.controller.admin.member.vo.MemberAreaStatisticsRespVO;
import cn.sailstack.qdp.module.statistics.controller.admin.member.vo.MemberSummaryRespVO;
import cn.sailstack.qdp.module.statistics.service.member.bo.MemberAreaStatisticsRespBO;
import cn.sailstack.qdp.module.statistics.service.pay.bo.RechargeSummaryRespBO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 会员统计 Convert
 *
 * @author owen
 */
@Mapper
public interface MemberStatisticsConvert {

    MemberStatisticsConvert INSTANCE = Mappers.getMapper(MemberStatisticsConvert.class);

    default List<MemberAreaStatisticsRespVO> convertList(List<Area> areaList,
                                                         Map<Integer, Integer> userCountMap,
                                                         Map<Integer, MemberAreaStatisticsRespBO> orderMap) {
        return CollectionUtils.convertList(areaList, area -> {
            MemberAreaStatisticsRespBO orderVo = Optional.ofNullable(orderMap.get(area.getId()))
                    .orElseGet(MemberAreaStatisticsRespBO::new);
            return new MemberAreaStatisticsRespVO()
                    .setAreaId(area.getId()).setAreaName(area.getName())
                    .setUserCount(MapUtils.getInt(userCountMap, area.getId(), 0))
                    .setOrderCreateUserCount(ObjectUtils.defaultIfNull(orderVo.getOrderCreateUserCount(), 0))
                    .setOrderPayUserCount(ObjectUtils.defaultIfNull(orderVo.getOrderPayUserCount(), 0))
                    .setOrderPayPrice(ObjectUtils.defaultIfNull(orderVo.getOrderPayPrice(), 0));
        });
    }

    MemberSummaryRespVO convert(RechargeSummaryRespBO rechargeSummary, Integer expensePrice, Integer userCount);

    MemberAnalyseRespVO convert(Integer visitUserCount, Integer orderUserCount, Integer payUserCount, int atv,
                                DataComparisonRespVO<MemberAnalyseDataRespVO> comparison);

}
