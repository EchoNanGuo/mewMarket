package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.SysMenu;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Set;

public interface SysMenuService extends IService<SysMenu>{


    Set<SysMenu> queryUserMenuListByUserId(Long loginUserId);

}
