package top.nanguomm.mewMarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.nanguomm.mewMarket.domain.SysRole;
import top.nanguomm.mewMarket.model.Result;
import top.nanguomm.mewMarket.service.SysRoleService;

import java.util.List;

@Api(tags = "系统角色接口管理")
@RequestMapping("sys/role")
@RestController
@RequiredArgsConstructor
public class SysRoleController {

    private final SysRoleService sysRoleService;

    @ApiOperation("查询系统所有角色")
    @GetMapping("list")
    @PreAuthorize("hasAuthority('sys:role:list')")
    public Result<List<SysRole>> loadSysRoleList() {
        List<SysRole> roleList= sysRoleService.querySysRoleList();
        return Result.success(roleList);
    }

    @ApiOperation("角色分页查询")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('sys:role:page')")
    public Result<Page<SysRole>> loadSysRolePage(@RequestParam Long current ,
                                                 @RequestParam Long size,
                                                 @RequestParam(required = false) String roleName) {
        // 创建分页对象
        Page<SysRole> sysRolePage = new Page<>(current, size);
        // 按条件进行分页查询
        sysRoleService.page(sysRolePage,new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(roleName),SysRole::getRoleName,roleName)
                .orderByDesc(SysRole::getCreateTime));
        return Result.success(sysRolePage);
    }

    @ApiOperation("新增角色")
    @PostMapping
    @PreAuthorize("hasAuthority('sys:role:save')")
    public Result<String> saveSysRole(@RequestBody SysRole sysRole) {
        boolean saved = sysRoleService.saveSysRole(sysRole);
        return Result.handle(saved);
    }

    @ApiOperation("根据标识查询角色详情")
    @GetMapping("info/{roleId}")
    @PreAuthorize("hasAuthority('sys:role:info')")
    public Result<SysRole> loadSysRoleInfo(@PathVariable Long roleId) {
        SysRole sysRole =sysRoleService.querySysRoleInfoByRoleId(roleId);
        return Result.success(sysRole);
    }

    @ApiOperation("修改角色信息")
    @PutMapping
    @PreAuthorize("hasAuthority('sys:role:update')")
    public Result<String> modifySysRole(@RequestBody SysRole sysRole) {
        boolean updated = sysRoleService.modifySysRole(sysRole);
        return Result.handle(updated);
    }

    @ApiOperation("批量/单个删除角色")
    @DeleteMapping
    @PreAuthorize("hasAuthority('sys:role:delete')")
    public Result<String> deleteSysRole(@RequestBody List<Long> roleIdList) {
        Boolean removed = sysRoleService.removeSysRoleListByIds(roleIdList);
        return Result.handle(removed);
    }

}
