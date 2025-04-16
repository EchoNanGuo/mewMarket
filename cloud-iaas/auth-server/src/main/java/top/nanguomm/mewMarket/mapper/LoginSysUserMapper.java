package top.nanguomm.mewMarket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.nanguomm.mewMarket.domain.LoginSysUser;

import java.util.Set;

@Mapper
public interface LoginSysUserMapper extends BaseMapper<LoginSysUser> {
    /**
     * 根据用户标识查询用户权限集合
     * @param userId
     * @return
     */
    Set<String> selectPermsByUserId(Long userId);

}