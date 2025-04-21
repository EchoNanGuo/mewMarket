package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.Prod;
import com.baomidou.mybatisplus.extension.service.IService;
public interface ProdService extends IService<Prod>{


    /**
     * 新增商品
     * @param prod
     * @return
     */
    boolean saveProd(Prod prod);

    /**
     * 根据标识查询商品详情
     * @param prodId
     * @return
     */
    Prod queryProdInfoById(Long prodId);

    /**
     *  修改商品信息
     * @param prod
     * @return
     */
    Boolean modifyProdInfo(Prod prod);

    /**
     * 删除商品
     * @param prodId
     * @return
     */
    Boolean removeProdById(Long prodId);

}
