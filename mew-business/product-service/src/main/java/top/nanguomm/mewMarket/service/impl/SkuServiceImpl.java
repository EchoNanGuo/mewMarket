package top.nanguomm.mewMarket.service.impl;

import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.nanguomm.mewMarket.domain.Sku;
import top.nanguomm.mewMarket.mapper.SkuMapper;
import top.nanguomm.mewMarket.service.SkuService;
@Service
public class SkuServiceImpl extends ServiceImpl<SkuMapper, Sku> implements SkuService{

}
