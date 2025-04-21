package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysRoleService extends IService<SysRole>{


    /**
     * 查询的是系统中所有角色数据（全量查询）
     * 全量查询是需要将数据存放到缓存中
     * @return
     */
    List<SysRole> querySysRoleList();

    /**
     * 1。新增角色
     * 2.新增角色与权限的集合
     * @param sysRole
     * @return
     */
    boolean saveSysRole(SysRole sysRole);

    /**
     * 根据角色标识查询角色和角色权限记录
     * 1.查询角色
     * 2.查询角色和权限对应关系
     * @param roleId
     * @return
     */
    SysRole querySysRoleInfoByRoleId(Long roleId);

    /**
     * 修改用户
     * 1.删除原有的权限id集合
     * 2.添加新的权限id集合
     * 3.修改用户
     * @param sysRole
     * @return
     */
    boolean modifySysRole(SysRole sysRole);

    /**
     * 批量或单个删除角色
     * 1.删除角色和权限的关系记录
     * 2.删除角色
     * @param roleIdList
     * @return
     */
    Boolean removeSysRoleListByIds(List<Long> roleIdList);

}
