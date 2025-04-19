package top.nanguomm.mewMarket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.nanguomm.mewMarket.constant.ManagerConstants;
import top.nanguomm.mewMarket.ex.handler.BusinessException;
import top.nanguomm.mewMarket.mapper.SysMenuMapper;
import top.nanguomm.mewMarket.domain.SysMenu;
import top.nanguomm.mewMarket.service.SysMenuService;

@Service
@CacheConfig(cacheNames = "top.nanguomm.mewMarket.service.impl.SysMenuServiceImpl")
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService{

    @Override
    @Cacheable(key = "#loginUserId")
    public Set<SysMenu> queryUserMenuListByUserId(Long loginUserId) {
        Set<SysMenu> menus = baseMapper.selectUserMenuListByUserId(loginUserId);
        return transformTree(menus,0L);
    }

    @Override
    @Cacheable(key = ManagerConstants.SYS_ALL_MENU_KEY)
    public List<SysMenu> queryAllSysMenuList() {
        return baseMapper.selectList(null);
    }

    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_MENU_KEY)
    public Boolean saveSysMenu(SysMenu sysMenu) {
        return baseMapper.insert(sysMenu)>0;
    }

    /**
     * 修改菜单权限信息
     * @param sysMenu
     * @return
     */
    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_MENU_KEY)
    public Boolean modifySysMenu(SysMenu sysMenu) {
        // TODO 这里前端有问题，当要将某节点更改为根目录/菜单/按钮时，无法选择
        return baseMapper.updateById(sysMenu)>0;
    }

    /**
     * 删除菜单权限
     * @param menuId
     * @return
     */
    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_MENU_KEY)
    public Boolean removeSysMenuById(Long menuId) {
        // 根据菜单标识查询子菜单集合
        List<SysMenu> sysMenus = baseMapper.selectList(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, menuId));
        // 判断子菜单是否有值
        if (CollectionUtil.isNotEmpty(sysMenus)) {
            // 说明：当前菜单节点包含字节点集合
            throw new BusinessException("当前子节点包含子节点集合，不可删除");
        }
        // 说明：当前惨淡节点不包含子节点集合，可以删除
        return baseMapper.deleteById(menuId)>0;
    }

    /**
     * 集合转换为树结构
     * @param menus
     * @param pid
     * @return
     */
    private Set<SysMenu> transformTree(Set<SysMenu> menus, long pid) {
        // 获取根节点
        Set<SysMenu> roots = menus.stream().
                filter(m -> m.getParentId().equals(pid)).collect(Collectors.toSet());
        // 递归调用
        roots.forEach( r -> r.setList(transformTree(menus,r.getMenuId())));
        return roots;
    }
}
