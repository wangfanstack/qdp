package cn.sailstack.qdp.module.member.enums.bonus;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 会员董事 - 资格核验关系范围枚举
 *
 * 需求文档未确认"推荐 6 个礼包"与"培养一星董事"的关系范围（直推还是团队），
 * 该口径作为配置项存储于 member_bonus_config，上线前必须由业务确认。
 *
 * @author sail
 */
@AllArgsConstructor
@Getter
public enum MemberDirectorScopeEnum {

    DIRECT("DIRECT", "仅直推"),
    TEAM("TEAM", "团队"),
    ;

    /**
     * 范围标识
     */
    private final String scope;
    /**
     * 说明
     */
    private final String name;

}
