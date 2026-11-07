package cn.sailstack.qdp.module.member.service.bonus.relation;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.relation.MemberRelationPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberMarketHistoryDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberRelationDO;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberMarketHistoryMapper;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberRelationMapper;
import cn.sailstack.qdp.module.member.service.user.MemberUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.module.member.enums.ErrorCodeConstants.*;

/**
 * 会员推荐关系 Service 实现类
 *
 * 并发一致性：有效直推顺序的分配在 {@code MemberGiftBenefitServiceImpl} 中
 * 通过锁定推荐人关系行（SELECT FOR UPDATE）保证；本类只负责绑定与查询。
 *
 * @author sail
 */
@Service
@Validated
@Slf4j
public class MemberRelationServiceImpl implements MemberRelationService {

    /**
     * 防环检查沿链向上的最大深度（防御数据异常导致死循环）
     */
    private static final int MAX_CHAIN_DEPTH = 1000;

    @Resource
    private MemberRelationMapper relationMapper;
    @Resource
    private MemberMarketHistoryMapper marketHistoryMapper;
    @Resource
    private MemberUserService memberUserService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void bindRelation(Long userId, Long referrerId) {
        // 1. 防自绑
        if (userId.equals(referrerId)) {
            throw exception(RELATION_BIND_SELF);
        }
        // 2. 同一会员只能存在一个有效推荐关系（uk + 先查后插）
        MemberRelationDO exists = getRelation(userId);
        if (exists != null && exists.getReferrerId() != null) {
            throw exception(RELATION_EXISTS);
        }
        // 3. 推荐人必须是平台会员
        if (memberUserService.getUser(referrerId) == null) {
            throw exception(RELATION_REFERRER_NOT_EXISTS);
        }
        // 4. 确保推荐人关系行存在（保证后续可加锁、可沿链向上）
        getOrCreateRelation(referrerId);
        // 5. 防环：沿推荐人链向上不能出现自己
        validateNoLoop(referrerId, userId);
        // 6. 写入/更新自己的关系行
        if (exists == null) {
            relationMapper.insert(MemberRelationDO.builder()
                    .userId(userId).referrerId(referrerId).bindTime(LocalDateTime.now()).build());
        } else {
            relationMapper.updateById(new MemberRelationDO().setId(exists.getId())
                    .setReferrerId(referrerId).setBindTime(LocalDateTime.now()));
        }
        log.info("[bindRelation][会员({}) 绑定推荐人({})]", userId, referrerId);
    }

    @Override
    public MemberRelationDO getRelation(Long userId) {
        return relationMapper.selectByUserId(userId);
    }

    @Override
    public MemberRelationDO getOrCreateRelation(Long userId) {
        MemberRelationDO relation = relationMapper.selectByUserId(userId);
        if (relation != null) {
            return relation;
        }
        relationMapper.insert(MemberRelationDO.builder().userId(userId).build());
        return relationMapper.selectByUserId(userId);
    }

    @Override
    public PageResult<MemberRelationDO> getRelationPage(MemberRelationPageReqVO pageReqVO) {
        return relationMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MemberRelationDO> getValidDirectList(Long referrerId) {
        return relationMapper.selectListByReferrerId(referrerId);
    }

    @Override
    public MemberRelationDO findMarketOwner(Long fromUserId, LocalDateTime bizTime) {
        Long currentUserId = fromUserId;
        for (int depth = 0; currentUserId != null && depth < MAX_CHAIN_DEPTH; depth++) {
            MemberRelationDO relation = relationMapper.selectByUserId(currentUserId);
            if (relation == null) {
                return null;
            }
            // 老板身份必须早于业务时间；恰好本次成为老板的（bossTime == bizTime）仍归原市场
            if (relation.getBossTime() != null && relation.getBossTime().isBefore(bizTime)
                    && relation.getMarketId() != null) {
                return relation;
            }
            currentUserId = relation.getReferrerId();
        }
        log.warn("[findMarketOwner][会员({}) 推荐链深度超过 {}，疑似数据异常]", fromUserId, MAX_CHAIN_DEPTH);
        return null;
    }

    @Override
    public List<MemberMarketHistoryDO> getMarketHistoryList(Long userId) {
        return marketHistoryMapper.selectListByUserId(userId);
    }

    private void validateNoLoop(Long referrerId, Long userId) {
        Long currentUserId = referrerId;
        for (int depth = 0; currentUserId != null && depth < MAX_CHAIN_DEPTH; depth++) {
            if (currentUserId.equals(userId)) {
                throw exception(RELATION_BIND_LOOP);
            }
            MemberRelationDO relation = relationMapper.selectByUserId(currentUserId);
            currentUserId = relation == null ? null : relation.getReferrerId();
        }
    }

}
