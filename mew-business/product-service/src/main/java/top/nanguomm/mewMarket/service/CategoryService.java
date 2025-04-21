package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface CategoryService extends IService<Category>{


    List<Category> queryALlCategoryList();

    List<Category> queryFirstCategoryList();


    boolean saveCategory(Category category);

    boolean modifyCategory(Category category);

    Boolean removeCategoryById(Long categoryId);
}
