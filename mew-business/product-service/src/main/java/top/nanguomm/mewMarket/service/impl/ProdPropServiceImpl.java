package top.nanguomm.mewMarket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import top.nanguomm.mewMarket.constant.ProductConstants;
import top.nanguomm.mewMarket.domain.ProdPropValue;
import top.nanguomm.mewMarket.mapper.ProdPropMapper;
import top.nanguomm.mewMarket.domain.ProdProp;
import top.nanguomm.mewMarket.service.ProdPropService;
import top.nanguomm.mewMarket.service.ProdPropValueService;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "top.nanguomm.mewMarket.service.impl.ProdPropServiceImpl")
public class ProdPropServiceImpl extends ServiceImpl<ProdPropMapper, ProdProp> implements ProdPropService{

    private final ProdPropValueService prodPropValueService;

    @Override
    public Page<ProdProp> queryProdSpecPage(Long current, Long size, String propName) {
        // 创建分页对象
        Page<ProdProp> page = new Page<>(current, size);
       // 多条件分页查询商品属性
        baseMapper.selectPage(page,new LambdaQueryWrapper<ProdProp>()
                .like(StringUtils.hasText(propName), ProdProp::getPropName, propName));
        // 从分页对象中获取属性记录
        List<ProdProp> prodPropList = page.getRecords();
        // 判断是否有值
        if (CollectionUtil.isEmpty(prodPropList)) {
            // 如果对象集合没有值，说明属性值也为空
            return page;
        }
        // 从属性对象集合中获取属性id集合
        List<Long> prodIdList = prodPropList.stream().map(ProdProp::getPropId).collect(Collectors.toList());

        // 属性id集合查询属性值对象集合
        List<ProdPropValue> propPropValues = prodPropValueService.getBaseMapper().selectList(new LambdaQueryWrapper<ProdPropValue>()
                .in(ProdPropValue::getPropId, prodIdList));

        // 循环遍历属性对象集合
        prodPropList.forEach(prodProp -> {
            // 从属性值对象集合中过滤出与当前属性对象的属性id一致的属性对象集合
            List<ProdPropValue> prodPropValueList = propPropValues.stream()
                    .filter(prodPropValue -> prodPropValue.getPropId().equals(prodProp.getPropId()))
                    .collect(Collectors.toList());
            // 设置属性值
            prodProp.setProdPropValues(prodPropValueList);
        });
        return page;
    }

    /**
     * 1. 新增商品属性对象
     * 2. 批量添加商品属性值对象
     * @param prodProp
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(key = ProductConstants.PROD_PROP_KEY)
    public boolean saveProdSpec(ProdProp prodProp) {
        // 新增商品属性对象
        prodProp.setShopId(1L);
        prodProp.setRule(2);
        int i = baseMapper.insert(prodProp);
        if (i > 0) {
            // 获取属性id
            Long propId = prodProp.getPropId();
            // 添加商品属性对象与属性值的记录
            // 获取商品属性值集合
            List<ProdPropValue> prodPropValues  = prodProp.getProdPropValues();
            // 判断是否有值
            if (CollectionUtil.isNotEmpty(prodPropValues)) {
                // 循环遍历属性值对象集合
                prodPropValues.forEach(prodPropValue -> prodPropValue.setPropId(propId));
                // 批量添加属性值对象集合
                prodPropValueService.saveBatch(prodPropValues);
            }
        }
        return i>0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(key = ProductConstants.PROD_PROP_KEY)
    public boolean modifyProdSpec(ProdProp prodProp) {
        // 获取行动属性值对象
        List<ProdPropValue> prodPropValues = prodProp.getProdPropValues();
        // 批量修改属性值对象
        boolean flag = prodPropValueService.updateBatchById(prodPropValues);
        if (flag) {
            // 修改属性对象
            baseMapper.updateById(prodProp);
        }

        // 修改商品
        return flag;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(key = ProductConstants.PROD_PROP_KEY)
    public boolean removeProdSpecByPropId(Long propId) {
        // 根据属性标识删除属性值
        prodPropValueService.remove(new LambdaQueryWrapper<ProdPropValue>()
                .eq(ProdPropValue::getPropId, propId));

        // 删除属性对象
        return baseMapper.deleteById(propId)>0;
    }

    @Override
    @Cacheable(key = ProductConstants.PROD_PROP_KEY)
    public List<ProdProp> queryProdPropList() {
        return baseMapper.selectList(null);
    }
}
