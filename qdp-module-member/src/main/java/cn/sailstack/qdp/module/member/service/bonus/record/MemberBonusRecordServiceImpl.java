package cn.sailstack.qdp.module.member.service.bonus.record;

import cn.sailstack.qdp.framework.common.pojo.PageParam;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.record.MemberBonusRecordPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusRecordDO;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberBonusRecordMapper;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;

/**
 * 礼包奖金明细查询 Service 实现类
 *
 * @author sail
 */
@Service
@Validated
public class MemberBonusRecordServiceImpl implements MemberBonusRecordService {

    @Resource
    private MemberBonusRecordMapper bonusRecordMapper;

    @Override
    public PageResult<MemberBonusRecordDO> getBonusRecordPage(MemberBonusRecordPageReqVO pageReqVO) {
        return bonusRecordMapper.selectPage(pageReqVO);
    }

    @Override
    public PageResult<MemberBonusRecordDO> getBonusRecordPage(Long userId, PageParam pageParam) {
        MemberBonusRecordPageReqVO pageReqVO = new MemberBonusRecordPageReqVO();
        pageReqVO.setUserId(userId);
        pageReqVO.setPageNo(pageParam.getPageNo());
        pageReqVO.setPageSize(pageParam.getPageSize());
        return bonusRecordMapper.selectPage(pageReqVO);
    }

}
