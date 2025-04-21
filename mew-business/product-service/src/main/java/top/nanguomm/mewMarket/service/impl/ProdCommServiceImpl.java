package top.nanguomm.mewMarket.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.util.StringUtils;
import top.nanguomm.mewMarket.mapper.ProdCommMapper;
import top.nanguomm.mewMarket.domain.ProdComm;
import top.nanguomm.mewMarket.service.ProdCommService;
@Service
public class ProdCommServiceImpl extends ServiceImpl<ProdCommMapper, ProdComm> implements ProdCommService{

    @Override
    public Boolean replyAndExamineProdComm(ProdComm prodComm) {
        // 获取商品评论内容
        String replyContent = prodComm.getReplyContent();
        // 判断评论是否有值
        if(StringUtils.hasText(replyContent)){
            prodComm.setReplyTime(new Date());
            prodComm.setReplySts(1);
        }
        return baseMapper.updateById(prodComm) > 0;
    }
}
