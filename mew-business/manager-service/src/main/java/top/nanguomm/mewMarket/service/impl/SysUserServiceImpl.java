package top.nanguomm.mewMarket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.nanguomm.mewMarket.domain.SysUserRole;
import top.nanguomm.mewMarket.mapper.SysUserMapper;
import top.nanguomm.mewMarket.domain.SysUser;
import top.nanguomm.mewMarket.service.SysUserRoleService;
import top.nanguomm.mewMarket.service.SysUserService;
import top.nanguomm.mewMarket.util.AuthUtils;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService{

    private final SysUserRoleService sysUserRoleService;
    private final BCryptPasswordEncoder passwordEncoder;;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer saveSysUser(SysUser sysUser) {
         // 新增管理员
        sysUser.setCreateUserId(AuthUtils.getLoginUserId());
        sysUser.setCreateTime(new Date());
        sysUser.setPassword(passwordEncoder.encode(sysUser.getPassword()));
        sysUser.setShopId(1L);
        int i = baseMapper.insert(sysUser);
        if (i > 0) {
            // 获取管理员标识
            Long userId = sysUser.getUserId();
            // 新增管理员与角色的关系
            // 获取管理员的角色id集合
            List<Long> roleIdList = sysUser.getRoleIdList();
            // 判断是否有值
            if (CollectionUtil.isNotEmpty(roleIdList)) {
                // 创建管理员与角色关系集合
                List<SysUserRole> sysUserRoleList= new ArrayList<>();
                // 循环遍历管理员和角色的关系
                roleIdList.forEach(roleId -> {
                    // 创建管理员与角色的关系
                    SysUserRole sysUserRole = new SysUserRole();
                    sysUserRole.setUserId(userId);
                    sysUserRole.setRoleId(roleId);
                    sysUserRoleList.add(sysUserRole);
                });
                // 批量添加管理员与角色关系
                sysUserRoleService.saveBatch(sysUserRoleList);
            }
        }
        return i;
    }

    @Override
    public SysUser querySysUserInfoByUserId(Long id) {
        // 根据标识查询管理员信息
        SysUser sysUser = baseMapper.selectById(id);
        // 根据用户标识查询管理员与角色的关系集合
        List<SysUserRole> sysUserRoleList = sysUserRoleService.getBaseMapper().selectList(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId, id));
        // 判断是否有值
        if (CollectionUtil.isNotEmpty(sysUserRoleList)) {
            // 从管理员与角色关系中获取角色id集合
            List<Long> roldIdList = sysUserRoleList.stream().map(SysUserRole::getRoleId).collect(Collectors.toList());
            sysUser.setRoleIdList(roldIdList);
        }
        return sysUser;
    }
}

