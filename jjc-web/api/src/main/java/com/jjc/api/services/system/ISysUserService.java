package com.jjc.api.services.system;

import com.jjc.api.model.system.SysUser;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Demo class
 *
 * @author Allion.li
 * @Date 2020/7/10
 */
public interface ISysUserService {

    int deleteByPrimaryKey(Integer id);

    int insert(SysUser sysUser);

    int insertSelective(SysUser sysUser);

    SysUser selectByPrimarykey(Integer id);

    SysUser selectByLoginId(String userName);

    List<SysUser> listAll();

    List<SysUser> selectByExample(Example example);

    int updateByPrimaryKey(SysUser sysUser);

    int updateByPrimaryKeySelective(SysUser sysUser);

}
