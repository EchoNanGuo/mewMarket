package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.ProdTag;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ProdTagService extends IService<ProdTag>{


    Boolean saveProdTag(ProdTag prodTag);

    Boolean modifyProdTag(ProdTag prodTag);

    /**
     * 查询状态正常的商品分类标签集合
     * @return
     */
    List<ProdTag> queryProdTagList();

    /**
     * 通过id删除商品分类标签
     * @param tagId
     * @return
     */
    boolean removeProdTagById(Long tagId);
}
