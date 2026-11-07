package cn.sailstack.qdp.module.system.api.permission;

import cn.sailstack.qdp.module.system.api.permission.dto.RoleRespDTO;
import cn.sailstack.qdp.module.system.convert.permission.RoleConvert;
import cn.sailstack.qdp.module.system.dal.dataobject.permission.RoleDO;
import cn.sailstack.qdp.module.system.service.permission.RoleService;
import org.springframework.stereotype.Service;

import jakarta.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 角色 API 实现类
 *
 * @author sailstack源码
 */
@Service
public class RoleApiImpl implements RoleApi {

    @Resource
    private RoleService roleService;

    @Override
    public void validRoleList(Collection<Long> ids) {
        roleService.validateRoleList(ids);
    }

    @Override
    public RoleRespDTO getRole(Long id) {
        RoleDO role = roleService.getRole(id);
        return RoleConvert.INSTANCE.convertToRespDTO(role);
    }

    @Override
    public List<RoleRespDTO> getRoleList(Collection<Long> ids) {
        List<RoleDO> list = roleService.getRoleList(ids);
        return RoleConvert.INSTANCE.convertRespDTOList(list);
    }
}
