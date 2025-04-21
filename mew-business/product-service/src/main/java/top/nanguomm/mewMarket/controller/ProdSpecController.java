package top.nanguomm.mewMarket.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.nanguomm.mewMarket.domain.ProdProp;
import top.nanguomm.mewMarket.domain.ProdPropValue;
import top.nanguomm.mewMarket.model.Result;
import top.nanguomm.mewMarket.service.ProdPropService;
import top.nanguomm.mewMarket.service.ProdPropValueService;

import java.util.List;

@Api(tags = "商品规格接口管理")
@RestController
@RequestMapping("prod/spec")
@RequiredArgsConstructor
public class ProdSpecController {

    private final ProdPropService prodPropService;
    private final ProdPropValueService prodPropValueService;

    @ApiOperation("多条件分页查询商品规格")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('prod:spec:page')")
    public Result<Page<ProdProp>> loadProdSpecPage(@RequestParam Long current,
                                                   @RequestParam Long size,
                                                   @RequestParam(required = false) String propName) {
         // 多条件分页查询商品规格
        Page<ProdProp> page = prodPropService.queryProdSpecPage(current,size,propName);
        return Result.success(page);
    }

    @ApiOperation("新增商品规格")
    @PostMapping
    @PreAuthorize("hasAuthority('prod:spec:save')")
    public Result<String> saveProdSpec(@RequestBody ProdProp prodProp) {
        boolean saved = prodPropService.saveProdSpec(prodProp);
        return Result.handle(saved);
    }

    @ApiOperation("修改商品规格")
    @PutMapping
    @PreAuthorize("hasAuthority('prod:spec:update')")
    public Result<String> modifyProdSpec(@RequestBody ProdProp prodProp) {
        boolean modified = prodPropService.modifyProdSpec(prodProp);
        return Result.handle(modified);
    }


    @ApiOperation("删除商品规格")
    @DeleteMapping("{propId}")
    @PreAuthorize("hasAuthority('prod:spec:delete')")
    public Result<String> removeProdSpec(@PathVariable Long propId) {
        boolean removed = prodPropService.removeProdSpecByPropId(propId);
        return Result.handle(removed);
    }

    @ApiOperation("查询系统商品属性集合")
    @GetMapping("list")
    @PreAuthorize("hasAuthority('prod:spec:page')")
    public Result<List<ProdProp>> loadProdPropList() {
        List<ProdProp> prodProps = prodPropService.queryProdPropList();
        return Result.success(prodProps);
    }

    @ApiOperation("根据商品属性id查询属性值集合")
    @GetMapping("listSpecValue/{propId}")
    @PreAuthorize("hasAuthority('prod:spec:page')")
    public Result<List<ProdPropValue>> loadProdPropValues(@PathVariable Long propId) {
        List<ProdPropValue> prodPropValues = prodPropValueService.list(new LambdaQueryWrapper<ProdPropValue>()
                .eq(ProdPropValue::getPropId,propId));
        return Result.success(prodPropValues);
    }
}
