package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.SysUser;
import com.baomidou.mybatisplus.extension.service.IService;
public interface SysUserService extends IService<SysUser>{


    Integer saveSysUser(SysUser sysUser);

    SysUser querySysUserInfoByUserId(Long id);
}
