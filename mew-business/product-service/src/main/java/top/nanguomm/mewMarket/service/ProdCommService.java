package top.nanguomm.mewMarket.service;

import top.nanguomm.mewMarket.domain.ProdComm;
import com.baomidou.mybatisplus.extension.service.IService;
public interface ProdCommService extends IService<ProdComm>{


    /**
     * 回复和审核商品评论
      * @param prodComm
     * @return
     */
    Boolean replyAndExamineProdComm(ProdComm prodComm);
}
