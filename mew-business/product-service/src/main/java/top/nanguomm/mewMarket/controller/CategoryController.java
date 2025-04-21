package top.nanguomm.mewMarket.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import top.nanguomm.mewMarket.domain.Category;
import top.nanguomm.mewMarket.model.Result;
import top.nanguomm.mewMarket.service.CategoryService;

import java.util.List;

@Api(tags = "商品类目接口管理")
@RequestMapping("prod/category")
@RestController
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;


    @ApiOperation("查询系统所有商品类目")
    @GetMapping("table")
    @PreAuthorize("hasAuthority('prod:category:page')")
    public Result<List<Category>> loadAllCategoryList() {
        List<Category> list = categoryService.queryALlCategoryList();
        return Result.success(list);
    }

    @ApiOperation("查询系统商品一级类目")
    @GetMapping("listCategory")
    @PreAuthorize("hasAuthority('prod:category:page')")
    public Result<List<Category>> loadFirstCategoryList() {
        List<Category> list = categoryService.queryFirstCategoryList();
        return Result.success(list);
    }

    @ApiOperation("新增商品类目")
    @PostMapping
    @PreAuthorize("hasAuthority('prod:category:save')")
    public Result<String> saveCategory(@RequestBody Category category) {
        boolean saved = categoryService.saveCategory(category);
        return Result.handle(saved);
    }

    @ApiOperation("根据标识查询商品类目详情")
    @GetMapping("info/{categoryId}")
    @PreAuthorize("hasAuthority('prod:category:info')")
    public Result<Category> loadCategoryById(@PathVariable Long categoryId) {
        Category category = categoryService.getById(categoryId);
        return Result.success(category);
    }

    @ApiOperation("修改商品类目信息")
    @PutMapping
    @PreAuthorize("hasAuthority('prod:category:update')")
    public Result<String> modifyCategory(@RequestBody Category category) {
        boolean modified = categoryService.modifyCategory(category);
        return Result.handle(modified);
    }

    @ApiOperation("删除商品类目")
    @DeleteMapping("{categoryId}")
    @PreAuthorize("hasAuthority('prod:category:delete')")
    public Result<String> removeCategory(@PathVariable Long categoryId) {
        Boolean removed = categoryService.removeCategoryById(categoryId);
        return Result.handle(removed);
    }
}
