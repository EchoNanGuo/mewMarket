package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.SysRole;
import top.nanguomm.mewMarket.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysUserService extends IService<SysUser>{


    Integer saveSysUser(SysUser sysUser);

    SysUser querySysUserInfoByUserId(Long id);

    Integer modifySysUserInfo(SysUser sysUser);

    boolean removeSysUserListByUserIds(List<Long> userIds);
}
