package top.nanguomm.mewMarket.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import top.nanguomm.mewMarket.domain.Prod;
import top.nanguomm.mewMarket.model.Result;
import top.nanguomm.mewMarket.service.ProdService;

@Api(tags = "商品接口管理")
@RequestMapping("prod/prod")
@RestController
@RequiredArgsConstructor
public class ProdController {

    private final ProdService prodService;

    @ApiOperation("多条件分页查询商品")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('prod:prod:page')")
    public Result<Page<Prod>> loadProdPage(@RequestParam Long current,
                                           @RequestParam Long size,
                                           @RequestParam(required = false) String prodName,
                                           @RequestParam(required = false) Long status) {
        // 创建商品分页对象
        Page<Prod> page = new Page<>(current, size);
        // 多条件分页查询商品
        prodService.page(page, new LambdaQueryWrapper<Prod>()
                .eq(ObjectUtil.isNotNull(status), Prod::getStatus, status)
                .like(StringUtils.hasText(prodName), Prod::getProdName, prodName)
                .orderByDesc(Prod::getCreateTime));
        return Result.success(page);
    }

    @ApiOperation("新增商品")
    @PostMapping
    @PreAuthorize("hasAuthority('prod:prod:save')")
    public Result<String> saveProd(@RequestBody Prod prod) {
        boolean saved = prodService.saveProd(prod);
        return Result.handle(saved);
    }

    @ApiOperation("根据标识查询商品详情")
    @GetMapping("info/{prodId}")
    @PreAuthorize("hasAuthority('prod:prod:info')")
    public Result<Prod> loadProd(@PathVariable Long prodId) {
        Prod prod = prodService.queryProdInfoById(prodId);
        return Result.success(prod);
    }

    @ApiOperation("修改商品信息")
    @PutMapping
    @PreAuthorize("hasAuthority('prod:prod:update')")
    public Result<String> modifyProd(@RequestBody Prod prod) {
        Boolean modified = prodService.modifyProdInfo(prod);
        return Result.handle(modified);
    }

    @ApiOperation("删除商品")
    @DeleteMapping("{prodId}")
    @PreAuthorize("hasAuthority('prod:prod:delete')")
    public Result<String> removeProd(@PathVariable Long prodId) {
        Boolean removed = prodService.removeProdById(prodId);
        return Result.handle(removed);
    }
}
