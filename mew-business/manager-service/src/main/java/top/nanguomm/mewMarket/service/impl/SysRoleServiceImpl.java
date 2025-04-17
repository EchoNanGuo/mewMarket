package top.nanguomm.mewMarket.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import top.nanguomm.mewMarket.constant.ManagerConstants;
import top.nanguomm.mewMarket.domain.SysRole;
import top.nanguomm.mewMarket.mapper.SysRoleMapper;
import top.nanguomm.mewMarket.service.SysRoleService;
@Service
@CacheConfig(cacheNames = "top.nanguomm.mewMarket.service.impl.SysRoleServiceImpl")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService{

    /**
     * 查询的是系统中所有角色数据（全量查询）
     * 全量查询是需要将数据存放到缓存中
     * @return
     */
    @Override
    @Cacheable(key = ManagerConstants.SYS_ALL_ROLE_KEY)
    public List<SysRole> querySysRoleList() {
        return baseMapper.selectList(new LambdaQueryWrapper<SysRole>()
                .orderByDesc(SysRole::getCreateTime));
    }
}
