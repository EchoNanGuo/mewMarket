package top.nanguomm.mewMarket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.nanguomm.mewMarket.constant.ManagerConstants;
import top.nanguomm.mewMarket.domain.SysRole;
import top.nanguomm.mewMarket.domain.SysRoleMenu;
import top.nanguomm.mewMarket.mapper.SysRoleMapper;
import top.nanguomm.mewMarket.service.SysRoleMenuService;
import top.nanguomm.mewMarket.service.SysRoleService;
import top.nanguomm.mewMarket.util.AuthUtils;

import static org.apache.commons.collections4.CollectionUtils.collect;

@Service
@CacheConfig(cacheNames = "top.nanguomm.mewMarket.service.impl.SysRoleServiceImpl")
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService{

    private final SysRoleMenuService sysRoleMenuService;

    /**
     * 查询的是系统中所有角色数据（全量查询）
     * 全量查询是需要将数据存放到缓存中
     * @return
     */
    @Override
    @Cacheable(key = ManagerConstants.SYS_ALL_ROLE_KEY)
    public List<SysRole> querySysRoleList() {
        return baseMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .orderByDesc(SysRole::getCreateTime));
    }

    /**
     * 1。新增角色
     * 2.新增角色与权限的集合
     * @param sysRole
     * @return
     */
    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_ROLE_KEY)
    @Transactional(rollbackFor = Exception.class)
    public boolean saveSysRole(SysRole sysRole) {
        // 初始化
        sysRole.setCreateTime(new Date());
        sysRole.setCreateUserId(AuthUtils.getLoginUserId());

        int i = baseMapper.insert(sysRole);
        if (i > 0) {
            // 获取角色id
            Long roleId = sysRole.getRoleId();
            // 新增角色和权限的关系记录
            // 获取角色对于的权限id集合
            List<Long> menuIdList = sysRole.getMenuIdList();
            // 创建角色与权限关系集合对象
            List<SysRoleMenu> sysRoleMenusList = new ArrayList<>();
            // 判断是否有值
            if (CollectionUtil.isNotEmpty(menuIdList)) {
                // 循环遍历权限id集合
                menuIdList.forEach(menuId -> {
                    // 创建角色与权限关系记录
                    SysRoleMenu sysRoleMenu = new SysRoleMenu();
                    sysRoleMenu.setRoleId(roleId);
                    sysRoleMenu.setMenuId(menuId);
                    // 收集角色权限关系记录
                    sysRoleMenusList.add(sysRoleMenu);
                });
            }
            sysRoleMenuService.saveBatch(sysRoleMenusList);
        }

        return i>0;
    }

    /**
     * 根据角色标识查询角色和角色权限记录
     * 1.查询角色
     * 2.查询角色和权限对应关系
     * @param roleId
     * @return
     */
    @Override
    public SysRole querySysRoleInfoByRoleId(Long roleId) {
        // 查询角色
        SysRole sysRole = baseMapper.selectById(roleId);
        // 查询角色与权限集合
        List<SysRoleMenu> sysRoleMenus = sysRoleMenuService.getBaseMapper().selectList(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));
        // 判断是否有值
        if (CollectionUtil.isNotEmpty(sysRoleMenus)) {
            // 记录权限id集合
            // 构造权限id集合
            List<Long> sysRoleMenusId = sysRoleMenus.stream().map(SysRoleMenu::getMenuId).collect(Collectors.toList());

            // 收集权限id集合
            sysRole.setMenuIdList(sysRoleMenusId);
        }
        return sysRole;
    }

    /**
     * 修改用户
     * 1.删除原有的权限id集合
     * 2.添加新的权限id集合
     * 3.修改用户
     * @param sysRole
     * @return
     */
    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_ROLE_KEY)
    @Transactional(rollbackFor = Exception.class)
    public boolean modifySysRole(SysRole sysRole) {
        // 获取角色标识
        Long roleId = sysRole.getRoleId();
        // 删除原有的权限id集合
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>()
                .eq(SysRoleMenu::getRoleId, roleId));
        // 新增角色和权限的关系记录
        // 获取角色对于的权限id集合
        List<Long> menuIdList = sysRole.getMenuIdList();
        // 创建角色与权限关系集合对象
        List<SysRoleMenu> sysRoleMenusList = new ArrayList<>();
        // 判断是否有值
        if (CollectionUtil.isNotEmpty(menuIdList)) {
            // 循环遍历权限id集合
            menuIdList.forEach(menuId -> {
                // 创建角色与权限关系记录
                SysRoleMenu sysRoleMenu = new SysRoleMenu();
                sysRoleMenu.setRoleId(roleId);
                sysRoleMenu.setMenuId(menuId);
                // 收集角色权限关系记录
                sysRoleMenusList.add(sysRoleMenu);
            });
        }
        sysRoleMenuService.saveBatch(sysRoleMenusList);
        // 更新角色信息
        return baseMapper.updateById(sysRole)>0;
    }

    /**
     * 批量或单个删除角色
     * 1.删除角色和权限的关系记录
     * 2.删除角色
     * @param roleIdList
     * @return
     */
    @Override
    @CacheEvict(key = ManagerConstants.SYS_ALL_ROLE_KEY)
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeSysRoleListByIds(List<Long> roleIdList) {
        // 删除角色和权限关系集合
        sysRoleMenuService.remove(new LambdaQueryWrapper<SysRoleMenu>()
                .in(SysRoleMenu::getRoleId, roleIdList));
        // 删除角色
        return baseMapper.deleteBatchIds(roleIdList)==roleIdList.size();
    }
}
