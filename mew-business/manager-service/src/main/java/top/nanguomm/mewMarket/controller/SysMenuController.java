package top.nanguomm.mewMarket.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.nanguomm.mewMarket.model.Result;
import top.nanguomm.mewMarket.model.SecurityUser;
import top.nanguomm.mewMarket.service.SysMenuService;
import top.nanguomm.mewMarket.util.AuthUtils;

import java.util.Set;

@Api(tags = "系统权限接口管理")
@RestController
@RequestMapping("sys/menu")
@RequiredArgsConstructor
public class SysMenuController {
   private final SysMenuService sysMenuService;

   @ApiOperation("查询用户菜单权限和菜单权限")
    @GetMapping("nav")
    public Result<String> loadUserMenuAndAuth() {
       // 获取当前用户标识
       Long loginUserId = AuthUtils.getLoginUserId();

       // 查询操作权限集合
       Set<String> perms = AuthUtils.getLoginUserPerms();

       // 根据用户标识查询菜单权限集合

       return Result.success(null);
   }

}
