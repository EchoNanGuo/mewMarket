package top.nanguomm.mewMarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.nanguomm.mewMarket.constant.ProductConstants;
import top.nanguomm.mewMarket.domain.ProdTag;
import top.nanguomm.mewMarket.mapper.ProdTagMapper;
import top.nanguomm.mewMarket.service.ProdTagService;
@Service
@CacheConfig(cacheNames = "top.nanguomm.mewMarket.service.impl.ProdTagServiceImpl")
@RequiredArgsConstructor
public class ProdTagServiceImpl extends ServiceImpl<ProdTagMapper, ProdTag> implements ProdTagService{


    @Override
    @CacheEvict(key = ProductConstants.PROD_TAG_NORMAL_KEY, condition = "#prodTag.status == 1")
    public Boolean saveProdTag(ProdTag prodTag) {
        prodTag.setCreateTime(new Date());
        prodTag.setUpdateTime(new Date());
        return baseMapper.insert(prodTag) > 0;
    }

    @Override
    @CacheEvict(key = ProductConstants.PROD_TAG_NORMAL_KEY, condition = "#prodTag.status == 1")
    public Boolean modifyProdTag(ProdTag prodTag) {
        prodTag.setUpdateTime(new Date());
        return baseMapper.updateById(prodTag) > 0;
    }

    @Override
    @CacheEvict(key = ProductConstants.PROD_TAG_NORMAL_KEY)
    public boolean removeProdTagById(Long tagId) {
        return baseMapper.deleteById(tagId)>0;
    }
    @Override
    @Cacheable(key = ProductConstants.PROD_TAG_NORMAL_KEY)
    public List<ProdTag> queryProdTagList() {
        return baseMapper.selectList(new LambdaQueryWrapper<ProdTag>()
                .eq(ProdTag::getStatus,1)
                .orderByDesc(ProdTag::getSeq));
    }

}
