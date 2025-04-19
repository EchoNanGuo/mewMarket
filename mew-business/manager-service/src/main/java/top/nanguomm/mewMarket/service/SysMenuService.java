package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Set;

public interface SysMenuService extends IService<SysMenu>{


    Set<SysMenu> queryUserMenuListByUserId(Long loginUserId);

    List<SysMenu> queryAllSysMenuList();

    Boolean saveSysMenu(SysMenu sysMenu);


    Boolean modifySysMenu(SysMenu sysMenu);

    Boolean removeSysMenuById(Long menuId);
}
