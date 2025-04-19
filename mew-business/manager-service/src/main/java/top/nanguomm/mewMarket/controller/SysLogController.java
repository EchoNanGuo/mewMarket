package top.nanguomm.mewMarket.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import top.nanguomm.mewMarket.domain.SysLog;
import top.nanguomm.mewMarket.model.Result;
import top.nanguomm.mewMarket.service.SysLogService;

/**
 * 系统操作日志
 */
@Api(tags = "系统操作日志接口管理")
@RequestMapping("sys/log")
@RestController
@RequiredArgsConstructor
public class SysLogController {

   private final SysLogService sysLogService;

   @ApiOperation("多条件分页擦好像系统操作日志")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('sys:log:page')")
    public Result<Page<SysLog>> loadSysLogPage(@RequestParam Long current,
                                               @RequestParam Long size,
                                               @RequestParam(required = false) Long userId,
                                               @RequestParam(required = false) String operation) {
       // 创建分页对象
       Page<SysLog> page = new Page<>(current, size);
       // 多条件查询分页系统操作日志
       sysLogService.page(page,new LambdaQueryWrapper<SysLog>()
               .eq(ObjectUtil.isNotNull(userId), SysLog::getUserId, userId)
               .like(StringUtils.hasText(operation), SysLog::getOperation, operation)
               .orderByDesc(SysLog::getCreateDate));

       return Result.success(page);
   }
}
