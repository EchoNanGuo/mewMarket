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
import top.nanguomm.mewMarket.domain.ProdComm;
import top.nanguomm.mewMarket.model.Result;
import top.nanguomm.mewMarket.service.ProdCommService;

@Api(tags = "商品评论接口管理")
@RestController
@RequestMapping("prod/prodComm")
@RequiredArgsConstructor
public class ProdCommController {
    private final ProdCommService prodCommService;

    @ApiOperation("多条件分页查询商品评论")
    @GetMapping("page")
    @PreAuthorize("hasAuthority('prod:prodComm:page')")
    public Result<Page<ProdComm>> loadProdCommPage(@RequestParam Long current,
                                                   @RequestParam Long size,
                                                   @RequestParam(required = false) String prodName,
                                                   @RequestParam(required = false) Integer status) {
        // 创建分页对象
        Page<ProdComm> prodCommPage = new Page<>(current, size);
        // 多条件分页查询商品评论
        prodCommService.page(prodCommPage,new LambdaQueryWrapper<ProdComm>()
                .eq(ObjectUtil.isNotNull(status), ProdComm::getStatus, status)
                .like(StringUtils.hasText(prodName), ProdComm::getProdName, prodName)
                .orderByDesc(ProdComm::getCreateTime));

        return Result.success(prodCommPage);
    }

    @ApiOperation("根据标识查询商品评论详情")
    @GetMapping("{id}")
    @PreAuthorize("hasAuthority('prod:prodComm:info')")
    public Result<ProdComm> loadProdCommInfo(@PathVariable Long id) {
        ProdComm prodComm = prodCommService.getById(id);
        return Result.success(prodComm);
    }


    @ApiOperation("回复和审核商品评论")
    @PutMapping
    @PreAuthorize("hasAuthority('prod:prodComm:update')")
    public Result<String> replyAndExamineProdComm(@RequestBody ProdComm prodComm) {
        Boolean flag = prodCommService.replyAndExamineProdComm(prodComm);
        return Result.handle(flag);
    }
}
