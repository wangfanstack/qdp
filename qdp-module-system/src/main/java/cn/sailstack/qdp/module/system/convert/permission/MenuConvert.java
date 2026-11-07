package cn.sailstack.qdp.module.system.convert.permission;

import cn.sailstack.qdp.module.system.controller.admin.permission.vo.menu.MenuRespVO;
import cn.sailstack.qdp.module.system.controller.admin.permission.vo.menu.MenuSaveVO;
import cn.sailstack.qdp.module.system.controller.admin.permission.vo.menu.MenuSimpleRespVO;
import cn.sailstack.qdp.module.system.dal.dataobject.permission.MenuDO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface MenuConvert {

    MenuConvert INSTANCE = Mappers.getMapper(MenuConvert.class);

    MenuDO convertToDO(MenuSaveVO bean);

    MenuRespVO convertToRespVO(MenuDO bean);

    List<MenuRespVO> convertRespVOList(List<MenuDO> list);

    List<MenuSimpleRespVO> convertSimpleRespVOList(List<MenuDO> list);

}
