package cn.sailstack.qdp.module.member.service.bonus.relation;

import cn.sailstack.qdp.framework.test.core.ut.BaseDbUnitTest;
import cn.sailstack.qdp.framework.common.exception.ServiceException;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberRelationDO;
import cn.sailstack.qdp.module.member.dal.dataobject.user.MemberUserDO;
import cn.sailstack.qdp.module.member.dal.mysql.bonus.MemberRelationMapper;
import cn.sailstack.qdp.module.member.service.user.MemberUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static cn.sailstack.qdp.module.member.enums.ErrorCodeConstants.*;

/**
 * {@link MemberRelationServiceImpl} 的单元测试
 *
 * 覆盖验收用例 10：同一会员唯一有效推荐关系、防自绑、防环、最近有效老板归属。
 *
 * @author sail
 */
@Import(MemberRelationServiceImpl.class)
public class MemberRelationServiceImplTest extends BaseDbUnitTest {

    private static final LocalDateTime T0 = LocalDateTime.of(2026, 11, 1, 10, 0, 0);

    @Resource
    private MemberRelationService relationService;
    @Resource
    private MemberRelationMapper relationMapper;

    @MockitoBean
    private MemberUserService memberUserService;

    @BeforeEach
    public void setUp() {
        when(memberUserService.getUser(anyLong())).thenReturn(new MemberUserDO());
    }

    @Test
    public void testBindRelation_success() {
        relationService.bindRelation(102L, 101L);
        MemberRelationDO relation = relationService.getRelation(102L);
        assertEquals(101L, relation.getReferrerId());
        assertNotNull(relation.getBindTime());
        // 推荐人占位行被创建（保证后续可加锁/沿链向上）
        assertNotNull(relationMapper.selectByUserId(101L));
    }

    @Test
    public void testBindRelation_duplicate() {
        relationService.bindRelation(102L, 101L);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> relationService.bindRelation(102L, 103L));
        assertEquals(RELATION_EXISTS.getCode(), ex.getCode());
    }

    @Test
    public void testBindRelation_self() {
        ServiceException ex = assertThrows(ServiceException.class,
                () -> relationService.bindRelation(102L, 102L));
        assertEquals(RELATION_BIND_SELF.getCode(), ex.getCode());
    }

    @Test
    public void testBindRelation_loop() {
        // 101 ← 102 ← 103；103 再绑 101 形成环 → 拒绝
        relationService.bindRelation(102L, 101L);
        relationService.bindRelation(103L, 102L);
        ServiceException ex = assertThrows(ServiceException.class,
                () -> relationService.bindRelation(101L, 103L));
        assertEquals(RELATION_BIND_LOOP.getCode(), ex.getCode());
    }

    @Test
    public void testFindMarketOwner_nearestBoss() {
        // 链：D(104) ← C(103) ← B(102, 老板, T0) ← A(101, 老板, T0 前一天)
        relationMapper.insert(MemberRelationDO.builder().userId(103L).referrerId(102L).build());
        relationMapper.insert(MemberRelationDO.builder().userId(102L).referrerId(101L)
                .bossTime(T0).marketId(2L).build());
        relationMapper.insert(MemberRelationDO.builder().userId(101L)
                .bossTime(T0.minusDays(1)).marketId(1L).build());
        // T0 之后：B 是最近老板
        MemberRelationDO owner = relationService.findMarketOwner(103L, T0.plusHours(1));
        assertEquals(102L, owner.getUserId());
        assertEquals(2L, owner.getMarketId());
        // 恰好 T0（B 刚成为老板的那笔）：归 A（严格早于）
        owner = relationService.findMarketOwner(103L, T0);
        assertEquals(101L, owner.getUserId());
    }

    @Test
    public void testFindMarketOwner_noBoss() {
        relationMapper.insert(MemberRelationDO.builder().userId(102L).referrerId(101L).build());
        assertNull(relationService.findMarketOwner(102L, T0));
    }

}
