package top.nanguomm.mewMarket.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
    public Result<List<SysRole>> loadsysRoleList() {
        List<SysRole> roleList= sysRoleService.querySysRoleList();
        return Result.success(roleList);
    }
}
