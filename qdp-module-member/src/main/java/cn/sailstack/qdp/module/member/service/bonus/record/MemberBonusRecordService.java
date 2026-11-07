package cn.sailstack.qdp.module.member.service.bonus.record;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.record.MemberBonusRecordPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusRecordDO;

import java.util.List;

/**
 * 礼包奖金明细查询 Service 接口
 *
 * @author sail
 */
public interface MemberBonusRecordService {

    /**
     * 获得奖金明细分页
     *
     * @param pageReqVO 分页查询
     * @return 奖金明细分页
     */
    PageResult<MemberBonusRecordDO> getBonusRecordPage(MemberBonusRecordPageReqVO pageReqVO);

    /**
     * 获得会员的奖金明细分页（App 端）
     *
     * @param userId    会员编号
     * @param pageParam 分页参数
     * @return 奖金明细分页
     */
    PageResult<MemberBonusRecordDO> getBonusRecordPage(Long userId, PageParam pageParam);

}
