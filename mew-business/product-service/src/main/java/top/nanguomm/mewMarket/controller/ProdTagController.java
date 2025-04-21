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
import top.nanguomm.mewMarket.domain.ProdTag;
import top.nanguomm.mewMarket.model.Result;
import top.nanguomm.mewMarket.service.ProdTagService;

import java.util.List;

@Api(tags = "分组标签接口管理")
@RequiredArgsConstructor
@RestController
@RequestMapping("prod/prodTag")
public class ProdTagController {
    private final ProdTagService prodTagService;

    @ApiOperation("多条件分页查询分组标签")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('prod:prodTag:page')")
    public Result<Page<ProdTag>> loadProdTagPage(@RequestParam Long current,
                                                 @RequestParam Long size,
                                                 @RequestParam(required = false) String title,
                                                 @RequestParam(required = false) String status) {
        // 创建分页对象
        Page<ProdTag> page = new Page<>(current,size);
        // 多条件分页查询分组列表
        prodTagService.page(page,new LambdaQueryWrapper<ProdTag>()
                .eq(ObjectUtil.isNotNull(status),ProdTag::getStatus,status)
                .like(StringUtils.hasText(title),ProdTag::getTitle,title)
                .orderByDesc(ProdTag::getSeq)
                .orderByDesc(ProdTag::getCreateTime));
        return Result.success(page);
    }

    @ApiOperation("新增商品分组标签")
    @PostMapping
    @PreAuthorize("hasAuthority('prod:prodTag:save')")
    public Result<String> saveProdTag(@RequestBody ProdTag prodTag) {
        Boolean saved = prodTagService.saveProdTag(prodTag);
        return Result.handle(saved);
    }

    @ApiOperation("根据标识查询分组标签详情")
    @GetMapping("info/{tagId}")
    @PreAuthorize("hasAuthority('prod:prodTag:info')")
    public Result<ProdTag> loadProdTag(@PathVariable Long tagId) {
        ProdTag prodTag = prodTagService.getById(tagId);
        return Result.success(prodTag);
    }

    @ApiOperation("修改商品分组标签信息")
    @PutMapping
    @PreAuthorize("hasAuthority('prod:prodTag:update')")
    public Result<String> modifyProdTag(@RequestBody ProdTag prodTag) {
        Boolean modified = prodTagService.modifyProdTag(prodTag);
        return Result.handle(modified);
    }

    @ApiOperation("根据标识删除商品分组标签")
    @DeleteMapping("{tagId}")
    @PreAuthorize("hasAuthority('prod:prodTag:delete')")
    public Result<String> removeProdTag(@PathVariable Long tagId) {
        boolean removed = prodTagService.removeProdTagById(tagId);
        return Result.handle(removed);
    }

    @ApiOperation("查询状态正常的商品分类标签集合")
    @GetMapping("listTagList")
    @PreAuthorize("hasAuthority('prod:prodTag:page')")
    public Result<List<ProdTag>> loadProdTagList() {
        List<ProdTag> prodTagList = prodTagService.queryProdTagList();
        return Result.success(prodTagList);
    }
}
