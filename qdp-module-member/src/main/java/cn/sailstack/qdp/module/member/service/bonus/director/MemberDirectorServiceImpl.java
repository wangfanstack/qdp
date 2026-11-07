package cn.sailstack.qdp.module.member.service.bonus.director;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.director.MemberDirectorPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberBonusConfigDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberDirectorDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberGiftBenefitDO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberRelationDO;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberDirectorMapper;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberGiftBenefitMapper;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberRelationMapper;
import cn.sailstack.qdp.module.member.enums.bonus.MemberDirectorLevelEnum;
import cn.sailstack.qdp.module.member.enums.bonus.MemberGiftBenefitStatusEnum;
import cn.sailstack.qdp.module.member.service.bonus.config.MemberBonusConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.module.member.enums.ErrorCodeConstants.BONUS_DIRECTOR_LEVEL_INVALID;

/**
 * 会员董事资格 Service 实现类
 *
 * 资格规则（需求 7.1）：
 * 1. 前提：购买董事资格礼包（默认最高档 39800，配置化）；
 * 2. 一星：推荐 N 个董事资格礼包（关系范围 DIRECT/TEAM，【待业务确认】）；
 * 3. 二星~五星：培养 N 个一星董事（关系范围同上，【待业务确认】）；
 * 4. 同一会员可满足多个等级；第一版不做自动降级。
 *
 * @author sail
 */
@Service
@Validated
@Slf4j
public class MemberDirectorServiceImpl implements MemberDirectorService {

    /**
     * 团队关系遍历最大深度（防御异常数据）
     */
    private static final int MAX_TEAM_DEPTH = 100;

    @Resource
    private MemberDirectorMapper directorMapper;
    @Resource
    private MemberRelationMapper relationMapper;
    @Resource
    private MemberGiftBenefitMapper giftBenefitMapper;
    @Resource
    private MemberBonusConfigService bonusConfigService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<MemberDirectorDO> verifyUserDirector(Long userId) {
        MemberBonusConfigDO config = bonusConfigService.getRequiredConfig();
        List<MemberDirectorDO> newDirectors = new ArrayList<>();
        // 0. 前提：购买过董事资格礼包（存在生效权益记录）
        if (!hasBoughtGift(userId, config.getDirectorQualifyGiftId())) {
            return newDirectors;
        }
        Set<Integer> ownedLevels = directorMapper.selectListByUserId(userId).stream()
                .map(MemberDirectorDO::getLevel).collect(Collectors.toSet());
        // 1. 一星：推荐 N 个董事资格礼包
        if (!ownedLevels.contains(MemberDirectorLevelEnum.ONE.getLevel())) {
            int giftBuyers = countGiftBuyers(userId, config.getDirectorQualifyGiftId(),
                    Objects.equals(config.getDirectorQualifyScope(), "TEAM"), config);
            if (giftBuyers >= config.getDirectorOneStarCount()) {
                newDirectors.add(insertDirector(userId, MemberDirectorLevelEnum.ONE.getLevel(),
                        String.format("推荐%d个董事资格礼包(%s口径)", giftBuyers, config.getDirectorQualifyScope())));
            }
        }
        // 2. 二星~五星：培养 N 个一星董事
        for (MemberDirectorLevelEnum level : new MemberDirectorLevelEnum[]{
                MemberDirectorLevelEnum.TWO, MemberDirectorLevelEnum.THREE,
                MemberDirectorLevelEnum.FOUR, MemberDirectorLevelEnum.FIVE}) {
            if (ownedLevels.contains(level.getLevel())) {
                continue;
            }
            boolean team = Objects.equals(config.getDirectorTraineeScope(), "TEAM");
            int trainees = countOneStarDirectors(userId, team);
            if (trainees >= config.getDirectorTrainees(level.getLevel())) {
                newDirectors.add(insertDirector(userId, level.getLevel(),
                        String.format("培养%d个一星董事(%s口径)", trainees, config.getDirectorTraineeScope())));
            }
        }
        return newDirectors;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int verifyAllDirectors() {
        MemberBonusConfigDO config = bonusConfigService.getRequiredConfig();
        // 候选集：购买过董事资格礼包且权益生效的会员（集合规模可控）
        List<MemberGiftBenefitDO> benefits = giftBenefitMapper.selectList(new LambdaQueryWrapperX<MemberGiftBenefitDO>()
                .eq(MemberGiftBenefitDO::getGiftId, config.getDirectorQualifyGiftId())
                .eq(MemberGiftBenefitDO::getStatus, MemberGiftBenefitStatusEnum.EFFECT.getStatus()));
        Set<Long> candidateUserIds = benefits.stream()
                .map(MemberGiftBenefitDO::getUserId).collect(Collectors.toSet());
        int added = 0;
        for (Long userId : candidateUserIds) {
            added += verifyUserDirector(userId).size();
        }
        log.info("[verifyAllDirectors][候选({})人，本次新增资格({})个]", candidateUserIds.size(), added);
        return added;
    }

    @Override
    public List<MemberDirectorDO> getDirectorListByLevel(Integer level) {
        return directorMapper.selectListByLevel(level);
    }

    @Override
    public List<MemberDirectorDO> getDirectorListByUserId(Long userId) {
        return directorMapper.selectListByUserId(userId);
    }

    @Override
    public PageResult<MemberDirectorDO> getDirectorPage(MemberDirectorPageReqVO pageReqVO) {
        return directorMapper.selectPage(pageReqVO);
    }

    // ========== 内部方法 ==========

    private MemberDirectorDO insertDirector(Long userId, Integer level, String source) {
        // uk(user_id, level) 幂等：已存在则跳过
        if (directorMapper.selectByUserIdAndLevel(userId, level) != null) {
            return null;
        }
        MemberDirectorDO director = MemberDirectorDO.builder()
                .userId(userId).level(level).qualifyTime(LocalDateTime.now())
                .status(1).source(source).build();
        directorMapper.insert(director);
        log.info("[insertDirector][会员({}) 取得董事等级({})]", userId, level);
        return director;
    }

    private boolean hasBoughtGift(Long userId, Long giftId) {
        if (giftId == null) {
            throw exception(BONUS_DIRECTOR_LEVEL_INVALID, "董事资格礼包未配置");
        }
        List<MemberGiftBenefitDO> benefits = giftBenefitMapper.selectListByUserId(userId);
        return benefits.stream().anyMatch(b -> giftId.equals(b.getGiftId())
                && Objects.equals(b.getStatus(), MemberGiftBenefitStatusEnum.EFFECT.getStatus())
                && b.getPayAmount() - b.getReversalAmount() > 0);
    }

    /**
     * 统计"推荐"的董事资格礼包购买人数（DIRECT 仅有效直推；TEAM 团队）
     */
    private int countGiftBuyers(Long userId, Long giftId, boolean team, MemberBonusConfigDO config) {
        Set<Long> scopeUserIds = team ? getTeamUserIds(userId) : getDirectUserIds(userId);
        scopeUserIds = scopeUserIds.stream()
                .filter(id -> !id.equals(userId) && hasBoughtGift(id, giftId))
                .collect(Collectors.toSet());
        return scopeUserIds.size();
    }

    /**
     * 统计"培养"的一星董事人数（DIRECT 仅直推；TEAM 团队）
     */
    private int countOneStarDirectors(Long userId, boolean team) {
        Set<Long> scopeUserIds = team ? getTeamUserIds(userId) : getDirectUserIds(userId);
        Set<Long> oneStarUserIds = directorMapper.selectListByLevel(MemberDirectorLevelEnum.ONE.getLevel())
                .stream().map(MemberDirectorDO::getUserId).collect(Collectors.toSet());
        scopeUserIds.retainAll(oneStarUserIds);
        return scopeUserIds.size();
    }

    private Set<Long> getDirectUserIds(Long userId) {
        return relationMapper.selectListByReferrerId(userId).stream()
                .map(MemberRelationDO::getUserId).collect(Collectors.toSet());
    }

    /**
     * 团队 = 沿推荐关系向下的所有后代（逐层扩展，限制深度防御环）
     */
    private Set<Long> getTeamUserIds(Long userId) {
        Set<Long> result = new HashSet<>();
        Set<Long> frontier = new HashSet<>(Collections.singleton(userId));
        for (int depth = 0; !frontier.isEmpty() && depth < MAX_TEAM_DEPTH; depth++) {
            Set<Long> next = new HashSet<>();
            for (Long currentId : frontier) {
                for (MemberRelationDO child : relationMapper.selectListByReferrerId(currentId)) {
                    if (result.add(child.getUserId())) {
                        next.add(child.getUserId());
                    }
                }
            }
            frontier = next;
        }
        result.remove(userId);
        return result;
    }

}
