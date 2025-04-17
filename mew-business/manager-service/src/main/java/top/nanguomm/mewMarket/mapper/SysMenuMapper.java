package top.nanguomm.mewMarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import top.nanguomm.mewMarket.domain.SysMenu;

import java.util.Set;

public interface SysMenuMapper extends BaseMapper<SysMenu> {
    Set<SysMenu> selectUserMenuListByUserId(Long loginUserId);
}