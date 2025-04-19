package top.nanguomm.mewMarket.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.nanguomm.mewMarket.domain.SysMenu;
import top.nanguomm.mewMarket.model.Result;
import top.nanguomm.mewMarket.service.SysMenuService;
import top.nanguomm.mewMarket.util.AuthUtils;
import top.nanguomm.mewMarket.vo.MenuAndAuth;

import java.util.List;
import java.util.Set;

@Api(tags = "系统权限接口管理")
@RestController
@RequestMapping("sys/menu")
@RequiredArgsConstructor
public class SysMenuController {
   private final SysMenuService sysMenuService;

    @ApiOperation("查询用户菜单权限和操作权限")
    @GetMapping("nav")
    public Result<MenuAndAuth> loadUserMenuAndAuth() {
       // 获取当前用户标识
       Long loginUserId = AuthUtils.getLoginUserId();

       // 查询操作权限集合
       Set<String> perms = AuthUtils.getLoginUserPerms();

       // 根据用户标识查询菜单权限集合
       Set<SysMenu> menus = sysMenuService.queryUserMenuListByUserId(loginUserId);
       // 返回结果
       return Result.success(new MenuAndAuth(menus,perms));
   }

   @ApiOperation("查询系统所有权限集合")
   @GetMapping("table")
   @PreAuthorize("hasAuthority('sys:menu:list')")
    public Result<List<SysMenu>> loadUserMenuList() {
        List<SysMenu> list = sysMenuService.queryAllSysMenuList();
        return Result.success(list);
   }

   @ApiOperation("新增权限")
   @PostMapping
   @PreAuthorize("hasAuthority('sys:menu:save')")
    public Result<String> saveSysMenu(@RequestBody SysMenu sysMenu) {
        Boolean saved = sysMenuService.saveSysMenu(sysMenu);
        return Result.handle(saved);
   }

   @ApiOperation("根据标识查询菜单权限信息")
    @GetMapping("/info/{menuId}")
    @PreAuthorize("hasAuthority('sys:menu:info')")
    public Result<SysMenu> loadSysMenu(@PathVariable Long menuId) {
       SysMenu sysMenu = sysMenuService.getById(menuId);
       return Result.success(sysMenu);
   }

   @ApiOperation("修改菜单权限信息")
    @PutMapping
    @PreAuthorize("hasAuthority('sys:menu:update')")
    public Result<String> modifySysMenu(@RequestBody SysMenu sysMenu) {
        Boolean modified = sysMenuService.modifySysMenu(sysMenu);
        return Result.handle(modified);
   }

   @ApiOperation("删除菜单权限")
    @DeleteMapping("{menuId}")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    public Result<String> deleteSysMenu(@PathVariable Long menuId) {
        Boolean removed = sysMenuService.removeSysMenuById(menuId);
        return Result.handle(removed);
   }
}
