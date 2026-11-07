package cn.sailstack.qdp.module.promotion.dal.mysql.bargain;

import cn.sailstack.qdp.framework.common.util.collection.CollectionUtils;
import cn.sailstack.qdp.framework.common.util.collection.MapUtils;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.mapper.BaseMapperX;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.promotion.controller.admin.bargain.vo.help.BargainHelpPageReqVO;
import cn.sailstack.qdp.module.promotion.dal.dataobject.bargain.BargainHelpDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Mapper
public interface BargainHelpMapper extends BaseMapperX<BargainHelpDO> {

    default Long selectCountByUserIdAndActivityId(Long userId, Long activityId) {
        return selectCount(new LambdaQueryWrapper<>(BargainHelpDO.class)
                .eq(BargainHelpDO::getUserId, userId)
                .eq(BargainHelpDO::getActivityId, activityId));
    }

    default Long selectUserCountMapByRecordId(Long recordId) {
        return selectCount(BargainHelpDO::getRecordId, recordId);
    }

    default BargainHelpDO selectByUserIdAndRecordId(Long userId, Long recordId) {
        return selectOne(new LambdaQueryWrapper<>(BargainHelpDO.class)
                .eq(BargainHelpDO::getUserId, userId)
                .eq(BargainHelpDO::getRecordId, recordId));
    }

    default Map<Long, Integer> selectUserCountMapByActivityId(Collection<Long> activityIds) {
        // SQL count 查询
        List<Map<String, Object>> result = selectMaps(new QueryWrapper<BargainHelpDO>()
                .select("COUNT(DISTINCT(user_id)) AS userCount, activity_id AS activityId")
                .in("activity_id", activityIds)
                .groupBy("activity_id"));
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyMap();
        }
        // 转换数据
        return CollectionUtils.convertMap(result,
                record -> MapUtils.getLong(record, "activityId"),
                record -> MapUtils.getInt(record, "userCount" ));
    }

    default Map<Long, Integer> selectUserCountMapByRecordId(Collection<Long> recordIds) {
        // SQL count 查询
        List<Map<String, Object>> result = selectMaps(new QueryWrapper<BargainHelpDO>()
                .select("COUNT(1) AS userCount, record_id AS recordId")
                .in("record_id", recordIds)
                .groupBy("record_id"));
        if (CollectionUtils.isEmpty(result)) {
            return Collections.emptyMap();
        }
        // 转换数据
        return CollectionUtils.convertMap(result,
                record -> MapUtils.getLong(record, "recordId"),
                record -> MapUtils.getInt(record, "userCount" ));
    }

    default PageResult<BargainHelpDO> selectPage(BargainHelpPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<BargainHelpDO>()
                .eqIfPresent(BargainHelpDO::getRecordId, reqVO.getRecordId())
                .orderByDesc(BargainHelpDO::getId));
    }

    default List<BargainHelpDO> selectListByRecordId(Long recordId) {
        return selectList(new LambdaQueryWrapperX<BargainHelpDO>()
                .eq(BargainHelpDO::getRecordId, recordId));
    }

}
