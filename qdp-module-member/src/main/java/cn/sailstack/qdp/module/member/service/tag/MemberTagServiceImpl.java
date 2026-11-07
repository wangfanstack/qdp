package cn.sailstack.qdp.module.member.service.tag;

import cn.sailstack.qdp.framework.common.util.collection.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import cn.sailstack.qdp.framework.common.pojo.PageResult;
import cn.sailstack.qdp.module.member.controller.admin.tag.vo.MemberTagCreateReqVO;
import cn.sailstack.qdp.module.member.controller.admin.tag.vo.MemberTagPageReqVO;
import cn.sailstack.qdp.module.member.controller.admin.tag.vo.MemberTagUpdateReqVO;
import cn.sailstack.qdp.module.member.convert.tag.MemberTagConvert;
import cn.sailstack.qdp.module.member.dal.dataobject.tag.MemberTagDO;
import cn.sailstack.qdp.module.member.dal.mysql.tag.MemberTagMapper;
import cn.sailstack.qdp.module.member.service.user.MemberUserService;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import jakarta.annotation.Resource;
import java.util.Collections;
import java.util.Collection;
import java.util.List;

import static cn.sailstack.qdp.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.sailstack.qdp.module.member.enums.ErrorCodeConstants.*;

/**
 * 会员标签 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class MemberTagServiceImpl implements MemberTagService {

    @Resource
    private MemberTagMapper memberTagMapper;

    @Resource
    private MemberUserService memberUserService;

    @Override
    public Long createTag(MemberTagCreateReqVO createReqVO) {
        // 校验名称唯一
        validateTagNameUnique(null, createReqVO.getName());
        // 插入
        MemberTagDO tag = MemberTagConvert.INSTANCE.convert(createReqVO);
        memberTagMapper.insert(tag);
        // 返回
        return tag.getId();
    }

    @Override
    public void updateTag(MemberTagUpdateReqVO updateReqVO) {
        // 校验存在
        validateTagExists(updateReqVO.getId());
        // 校验名称唯一
        validateTagNameUnique(updateReqVO.getId(), updateReqVO.getName());
        // 更新
        MemberTagDO updateObj = MemberTagConvert.INSTANCE.convert(updateReqVO);
        memberTagMapper.updateById(updateObj);
    }

    @Override
    public void deleteTag(Long id) {
        // 校验存在
        validateTagExists(id);
        // 校验标签下是否有用户
        validateTagHasUser(id);
        // 删除
        memberTagMapper.deleteById(id);
    }

    private void validateTagExists(Long id) {
        if (memberTagMapper.selectById(id) == null) {
            throw exception(TAG_NOT_EXISTS);
        }
    }

    private void validateTagNameUnique(Long id, String name) {
        if (StringUtils.isBlank(name)) {
            return;
        }
        MemberTagDO tag = memberTagMapper.selelctByName(name);
        if (tag == null) {
            return;
        }

        // 如果 id 为空，说明不用比较是否为相同 id 的标签
        if (id == null) {
            throw exception(TAG_NAME_EXISTS);
        }
        if (!tag.getId().equals(id)) {
            throw exception(TAG_NAME_EXISTS);
        }
    }

    void validateTagHasUser(Long id) {
        Long count = memberUserService.getUserCountByTagId(id);
        if (count > 0) {
            throw exception(TAG_HAS_USER);
        }
    }

    @Override
    public MemberTagDO getTag(Long id) {
        return memberTagMapper.selectById(id);
    }

    @Override
    public List<MemberTagDO> getTagList(Collection<Long> ids) {
        if (CollectionUtils.isEmpty(ids)) {
            return Collections.emptyList();
        }
        return memberTagMapper.selectByIds(ids);
    }

    @Override
    public PageResult<MemberTagDO> getTagPage(MemberTagPageReqVO pageReqVO) {
        return memberTagMapper.selectPage(pageReqVO);
    }

    @Override
    public List<MemberTagDO> getTagList() {
        return memberTagMapper.selectList();
    }

}
