package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface SysRoleService extends IService<SysRole>{


    List<SysRole> querySysRoleList();

    boolean saveSysRole(SysRole sysRole);

    SysRole querySysRoleInfoByRoleId(Long roleId);

    boolean modifySysRole(SysRole sysRole);

    Boolean removeSysRoleListByIds(List<Long> roleIdList);

}
