package cn.sailstack.qdp.module.system.api.dept;

import cn.sailstack.qdp.module.system.api.dept.dto.DeptRespDTO;
import cn.sailstack.qdp.module.system.convert.dept.DeptConvert;
import cn.sailstack.qdp.module.system.dal.dataobject.dept.DeptDO;
import cn.sailstack.qdp.module.system.service.dept.DeptService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 部门 API 实现类
 *
 * @author sailstack源码
 */
@Service
public class DeptApiImpl implements DeptApi {

    @Resource
    private DeptService deptService;

    @Override
    public DeptRespDTO getDept(Long id) {
        DeptDO dept = deptService.getDept(id);
        return DeptConvert.INSTANCE.convertToRespDTO(dept);
    }

    @Override
    public List<DeptRespDTO> getDeptList(Collection<Long> ids) {
        List<DeptDO> depts = deptService.getDeptList(ids);
        return DeptConvert.INSTANCE.convertRespDTOList(depts);
    }

    @Override
    public void validateDeptList(Collection<Long> ids) {
        deptService.validateDeptList(ids);
    }

    @Override
    public List<DeptRespDTO> getChildDeptList(Collection<Long> ids) {
        List<DeptDO> childDepts = deptService.getChildDeptList(ids);
        return DeptConvert.INSTANCE.convertRespDTOList(childDepts);
    }

    @Override
    public List<DeptRespDTO> getParentDeptList(Long id) {
        List<DeptDO> parentDepts = deptService.getParentDeptList(id);
        return DeptConvert.INSTANCE.convertRespDTOList(parentDepts);
    }

}
