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
import org.springframework.util.StringUtils;
import top.nanguomm.mewMarket.domain.SysRole;
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

    /**
     * 修改管理员信息
     * 1.删除原有的管理员与角色关系记录
     * 2.添加新的管理员与角色关系记录
     * 3.修改管理员信息
     * @param sysUser
     * @return
     */
    @Override
    public Integer modifySysUserInfo(SysUser sysUser) {
        // 获取管理员标识
        Long userId = sysUser.getUserId();
        // 删除原有的管理员与角色关系记录
        sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>()
                .eq(SysUserRole::getUserId,userId));
        // 添加管理员与角色关系
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
        // 修改管理员信息
        // 获取新密码(如果有值，说明管理员修改了密码，如果没有值，说明密码不变)
        String newPassword = sysUser.getPassword();
        if (StringUtils.hasText(newPassword)) {
            // 有值：说明原密码需要被修改
            sysUser.setPassword(passwordEncoder.encode(newPassword));
        }
        return baseMapper.updateById(sysUser);
    }

    /**
     * 批量/单个删除管理员
     * 1.删除管理员与角色的关系记录
     * 2.删除管理员
     * @param userIds
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean removeSysUserListByUserIds(List<Long> userIds) {
        // 批量/单个删除管理员与角色的关系记录
        sysUserRoleService.remove(new LambdaQueryWrapper<SysUserRole>()
                .in(SysUserRole::getUserId, userIds));
        // 批量/单个删除管理员
        return baseMapper.deleteBatchIds(userIds)==userIds.size();
    }
}

