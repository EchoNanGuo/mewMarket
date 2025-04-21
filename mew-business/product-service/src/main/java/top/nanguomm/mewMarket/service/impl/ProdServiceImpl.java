package top.nanguomm.mewMarket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import top.nanguomm.mewMarket.domain.Prod;
import top.nanguomm.mewMarket.domain.ProdTagReference;
import top.nanguomm.mewMarket.domain.Sku;
import top.nanguomm.mewMarket.mapper.ProdMapper;
import top.nanguomm.mewMarket.service.ProdService;
import top.nanguomm.mewMarket.service.ProdTagReferenceService;
import top.nanguomm.mewMarket.service.SkuService;

@Service
@RequiredArgsConstructor
public class ProdServiceImpl extends ServiceImpl<ProdMapper, Prod> implements ProdService {

    private final ProdTagReferenceService prodTagReferenceService;
    private final SkuService skuService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveProd(Prod prod) {
        // 新增商品
        prod.setShopId(1L);
        prod.setSoldNum(0);
        prod.setCreateTime(new Date());
        prod.setUpdateTime(new Date());
        prod.setPutawayTime(new Date());
        prod.setVersion(0);
        Prod.DeliveryModeVo deliveryModeVo = prod.getDeliveryModeVo();
        prod.setDeliveryMode(JSONObject.toJSONString(deliveryModeVo));

        int i = baseMapper.insert(prod);
        if (i > 0) {
            // 处理商品与分组标签的关系
            // 获取商品分组标签
            List<Long> tagList = prod.getTagList();
            // 获取商品标识
            Long prodId = prod.getProdId();
            // 判断是否有值
            if (CollectionUtil.isNotEmpty(tagList)) {
                // 创建商品与分组标签关系集合
                List<ProdTagReference> prodTagReferenceList = new ArrayList<>();
                // 循环遍历分组标签id集合
                tagList.forEach(tagId -> {
                    // 创建商品与分组标签的关系记录
                    ProdTagReference prodTagReference = new ProdTagReference();
                    prodTagReference.setProdId(prodId);
                    prodTagReference.setTagId(tagId);
                    prodTagReference.setCreateTime(new Date());
                    prodTagReference.setShopId(1L);
                    prodTagReference.setStatus(1);
                    prodTagReferenceList.add(prodTagReference);
                });
                // 批量添加商品与分组标签的关系记录
                prodTagReferenceService.saveBatch(prodTagReferenceList);
            }

            // 处理商品与商品sku的关系
            // 获取商品sku对象集合
            List<Sku> skuList = prod.getSkuList();
            // 判断是否有值
            if (CollectionUtil.isNotEmpty(skuList)) {
                // 循环遍历商品sku对象集合
                skuList.forEach(sku -> {
                    sku.setProdId(prodId);
                    sku.setCreateTime(new Date());
                    sku.setUpdateTime(new Date());
                    sku.setVersion(0);
                    sku.setActualStocks(sku.getStocks());
                });
            }
            skuService.saveBatch(skuList);
        }
        return i > 0;
    }

    @Override
    public Prod queryProdInfoById(Long prodId) {
        // 根据标识查询商品详情
        Prod prod = baseMapper.selectById(prodId);
        // 根据商品标识查询商品与分组标签的关系
        List<ProdTagReference> prodTagReferenceList = prodTagReferenceService.getBaseMapper().selectList(new LambdaQueryWrapper<ProdTagReference>()
                .eq(ProdTagReference::getProdId, prodId));
        // 判断是否有值
        if (CollectionUtil.isNotEmpty(prodTagReferenceList)) {
            // 从商品与分组标签的关系集合中获取分组标签id集合
            List<Long> tagIdList = prodTagReferenceList.stream().map(ProdTagReference::getTagId).collect(Collectors.toList());
            prod.setTagList(tagIdList);
        }
        // 根据商品id查询商品sku对象集合
        List<Sku> skus = skuService.getBaseMapper().selectList(new LambdaQueryWrapper<Sku>()
                .eq(Sku::getProdId, prodId));
        prod.setSkuList(skus);
        return prod;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean modifyProdInfo(Prod prod) {
        // 获取商品标识
        Long prodId = prod.getProdId();
        // 删除商品原有的与分组标签的关系
        prodTagReferenceService.remove(new LambdaQueryWrapper<ProdTagReference>()
                .eq(ProdTagReference::getProdId, prodId));
        // 获取商品分组标签
        List<Long> tagList = prod.getTagList();
        // 判断是否有值
        if (CollectionUtil.isNotEmpty(tagList)) {
            // 创建商品与分组标签关系集合
            List<ProdTagReference> prodTagReferenceList = new ArrayList<>();
            // 循环遍历分组标签id集合
            tagList.forEach(tagId -> {
                // 创建商品与分组标签的关系记录
                ProdTagReference prodTagReference = new ProdTagReference();
                prodTagReference.setProdId(prodId);
                prodTagReference.setTagId(tagId);
                prodTagReference.setCreateTime(new Date());
                prodTagReference.setShopId(1L);
                prodTagReference.setStatus(1);
                prodTagReferenceList.add(prodTagReference);
            });
            // 批量添加商品与分组标签的关系记录
            prodTagReferenceService.saveBatch(prodTagReferenceList);
        }
        // 批量修改商品sku对象集合
        // 获取商品sku对象集合
        List<Sku> skuList = prod.getSkuList();
        // 循环遍历商品sku对象集合
        skuList.forEach(sku -> {
            sku.setUpdateTime(new Date());
            sku.setActualStocks(sku.getStocks());
        });
        // 批量修改商品sku对象数量
        skuService.updateBatchById(skuList);

        // 修改商品对象
        prod.setUpdateTime(new Date());

        return baseMapper.updateById(prod) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean removeProdById(Long prodId) {
        // 删除商品与分组的关系
        prodTagReferenceService.remove(new LambdaQueryWrapper<ProdTagReference>()
                .eq(ProdTagReference::getProdId, prodId));

        // 根据商品id删除商品sku对象
        skuService.remove(new LambdaQueryWrapper<Sku>()
                .eq(Sku::getProdId, prodId));

        return baseMapper.deleteById(prodId) > 0;
    }
}

