package top.nanguomm.mewMarket.service.impl;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Set;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
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
