package cn.sailstack.qdp.module.member.service.bonus.director;

import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.bonus.vo.director.MemberDirectorPageReqVO;
import cn.sailstack.qdp.module.member.dal.dataobject.bonus.MemberDirectorDO;

import java.util.List;

/**
 * 会员董事资格 Service 接口
 *
 * @author sail
 */
public interface MemberDirectorService {

    /**
     * 核验单个会员的董事资格（一星~五星；已取得等级不重复核验，第一版不自动降级）
     *
     * 核验口径（推荐/培养的关系范围）取奖金配置，属【待业务确认】项。
     *
     * @param userId 会员编号
     * @return 本次新取得的资格列表
     */
    List<MemberDirectorDO> verifyUserDirector(Long userId);

    /**
     * 核验全部候选会员（购买过董事资格礼包的会员）的董事资格
     *
     * @return 本次新增资格数量
     */
    int verifyAllDirectors();

    /**
     * 获得指定等级的董事资格列表
     *
     * @param level 董事等级（1-5）
     * @return 董事资格列表
     */
    List<MemberDirectorDO> getDirectorListByLevel(Integer level);

    /**
     * 获得会员的董事资格列表
     *
     * @param userId 会员编号
     * @return 董事资格列表
     */
    List<MemberDirectorDO> getDirectorListByUserId(Long userId);

    /**
     * 获得董事资格分页
     *
     * @param pageReqVO 分页查询
     * @return 董事资格分页
     */
    PageResult<MemberDirectorDO> getDirectorPage(MemberDirectorPageReqVO pageReqVO);

}
