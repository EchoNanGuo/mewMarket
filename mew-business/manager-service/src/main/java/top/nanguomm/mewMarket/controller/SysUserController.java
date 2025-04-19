package top.nanguomm.mewMarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.nanguomm.mewMarket.domain.SysUser;
import top.nanguomm.mewMarket.model.Result;
import top.nanguomm.mewMarket.service.SysUserService;
import top.nanguomm.mewMarket.util.AuthUtils;

import java.util.List;

/**
 * 系统管理员控制层
 */
@Api(tags = "系统管理员用户接口管理")
@RestController
@RequestMapping("sys/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @ApiOperation("查询登录的用户信息")
    @GetMapping("info")
    public Result<SysUser> loadSysUserInfo() {
        // 获取登录用户标识
        Long loginUserId = AuthUtils.getLoginUserId();
        // 根据用户标识查询登录用户信息
        SysUser sysUser = sysUserService.getById(loginUserId);
        return Result.success(sysUser);
    }

    @ApiOperation("多条件分页查询系统管理员")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('sys:user:page')")
    public Result<Page<SysUser>> loadSysUserPage(@RequestParam Long current ,
                                                 @RequestParam Long size,
                                                 @RequestParam(required = false) String username) {
        // 创建分页对象
        Page<SysUser> page = new Page<>(current, size);
        // 多条件分页查询系统管理员
        sysUserService.page(page, new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(username),SysUser::getUsername,username)
                .orderByDesc(SysUser::getCreateTime));

        return Result.success(page);
    }

    @ApiOperation("新增管理员")
    @PostMapping
    @PreAuthorize("hasAuthority('sys:user:save')")
    public Result<String> savaSysUser(@RequestBody SysUser sysUser) {
        Integer count = sysUserService.saveSysUser(sysUser);
        return Result.handle(count > 0);
    }

    @ApiOperation("根据标识查询系统管理员信息")
    @GetMapping("info/{id}")
    @PreAuthorize("hasAuthority('sys:user:info')")
    public Result<SysUser> loadSysUserInfo(@PathVariable Long id) {
        SysUser sysUser = sysUserService.querySysUserInfoByUserId(id);
        return Result.success(sysUser);
    }

    @ApiOperation("修改管理员信息")
    @PutMapping
    @PreAuthorize("hasAuthority('sys:user:update')")
    public Result<String> modifySysUserInfo(@RequestBody SysUser sysUser) {
        Integer count = sysUserService.modifySysUserInfo(sysUser);
        return Result.handle(count > 0);
    }

    @ApiOperation("批量/单个删除管理员")
    @DeleteMapping("{userIds}")
    @PreAuthorize("hasAuthority('sys:user:delete')")
    public Result<String> deleteSysUser(@PathVariable List<Long> userIds) {
        boolean removed = sysUserService.removeSysUserListByUserIds(userIds);
        return Result.handle(removed);
    }

}
