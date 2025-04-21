package top.nanguomm.mewMarket.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.nanguomm.mewMarket.constant.ProductConstants;
import top.nanguomm.mewMarket.ex.handler.BusinessException;
import top.nanguomm.mewMarket.mapper.CategoryMapper;
import top.nanguomm.mewMarket.domain.Category;
import top.nanguomm.mewMarket.service.CategoryService;
@Service
@CacheConfig(cacheNames = "top.nanguomm.mewMarket.service.impl.CategoryServiceImpl")
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService{

    /**
     * 查询所有商品类目
     * @return
     */
    @Override
    @Cacheable(key = ProductConstants.ALL_CATEGORY_LIST_KEY)
    public List<Category> queryALlCategoryList() {
        return baseMapper.selectList(new LambdaQueryWrapper<Category>()
                        .orderByDesc(Category::getSeq)
                        .orderByDesc(Category::getCreateTime));
    }

    /**
     * 查询一级类目
     * @return
     */
    @Override
    @Cacheable(key = ProductConstants.FIRST_CATEGORY_LIST_KEY)
    public List<Category> queryFirstCategoryList() {
        return baseMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId,0)
                .eq(Category::getStatus,1)
                .orderByDesc(Category::getSeq)
                .orderByDesc(Category::getCreateTime));
    }

    /**
     * 新增商品类目
      * @param category
     * @return
     */
    @Override
    @Caching(evict = {
            @CacheEvict(key = ProductConstants.FIRST_CATEGORY_LIST_KEY),
            @CacheEvict(key = ProductConstants.ALL_CATEGORY_LIST_KEY)
    })
    public boolean saveCategory(Category category) {
        category.setCreateTime(new Date());
        category.setUpdateTime(new Date());
        return baseMapper.insert(category)>0;
    }

    /**
     * 修改分类
     * @param category
     * @return
     */
    @Override
    @Caching(evict = {
            @CacheEvict(key = ProductConstants.FIRST_CATEGORY_LIST_KEY),
            @CacheEvict(key = ProductConstants.ALL_CATEGORY_LIST_KEY)
    })
    public boolean modifyCategory(Category category) {
        // 修改后的pid
        Long parentId = category.getParentId();
        // 根据标识查询类目详情
        Category beforeCategory = baseMapper.selectById(category.getCategoryId());
        // 判断商品类目修改的详情,如果parentId为0则为一级类目，不为0则为二级类目
        Long beforeParentId = beforeCategory.getParentId();
        // 判断商品类目修改的详情
        // 1 -> 2
        if (beforeParentId == 0 && parentId != null && parentId != 0) {
            // 查询当前类目是否包含子类目，如果有，则不允许修改
            // 根据当前类目标识查询子类目
            List<Category> childList = baseMapper.selectList(new LambdaQueryWrapper<Category>()
                    .eq(Category::getParentId, category.getCategoryId()));
            // 判断是否有值
            if (CollectionUtil.isNotEmpty(childList)) {
                // 说明：当前类目包含子类目，不允许修改
                throw new BusinessException("当前类目包含子类目，不允许修改");
            }
        }
        // 2 -> 1
        if (beforeParentId != 0 && parentId == null) {
            category.setParentId(0L);
        }
        return baseMapper.updateById(category)>0;
    }

    /**
     * 删除商品类目
     *  注意：如果一级类目包含子类目，则不可删除
     * @param categoryId
     * @return
     */
    @Override
    @Caching(evict = {
            @CacheEvict(key = ProductConstants.FIRST_CATEGORY_LIST_KEY),
            @CacheEvict(key = ProductConstants.ALL_CATEGORY_LIST_KEY)
    })
    public Boolean removeCategoryById(Long categoryId) {
        // 根据类目标识查询子类目集合
        List<Category> childList = baseMapper.selectList(new LambdaQueryWrapper<Category>()
                .eq(Category::getParentId, categoryId));
        // 判断是否包含子类目
        if (CollectionUtil.isNotEmpty(childList)) {
            // 说明：当前类目包含子类目，不可删除
            throw new BusinessException("当前类目包含子类目，不可删除");
        }
        // 当前类目不包含子类目，直接删除
        return baseMapper.deleteById(categoryId)>0;
    }
}
