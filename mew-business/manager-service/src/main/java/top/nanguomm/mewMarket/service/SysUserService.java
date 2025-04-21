package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.SysRole;
import top.nanguomm.mewMarket.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysUserService extends IService<SysUser>{


    Integer saveSysUser(SysUser sysUser);

    SysUser querySysUserInfoByUserId(Long id);

    /**
     * 修改管理员信息
     * 1.删除原有的管理员与角色关系记录
     * 2.添加新的管理员与角色关系记录
     * 3.修改管理员信息
     * @param sysUser
     * @return
     */
    Integer modifySysUserInfo(SysUser sysUser);

    /**
     * 批量/单个删除管理员
     * 1.删除管理员与角色的关系记录
     * 2.删除管理员
     * @param userIds
     * @return
     */
    boolean removeSysUserListByUserIds(List<Long> userIds);
}
