package com.jjc.api.services.system.impl;

import com.jjc.api.dao.system.ISysUserDao;
import com.jjc.api.model.system.SysUser;
import com.jjc.api.services.system.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

/**
 * Demo class
 *
 * @author Allion.li
 * @Date 2020/7/10
 */
@Service
public class SysUserServiceImpl implements ISysUserService {
    @Autowired
    private ISysUserDao iSysUserDao;

    @Override
    public int deleteByPrimaryKey(Integer id) {
        int row  =iSysUserDao.deleteByPrimaryKey(id);
        return row;
    }

    @Override
    public int insert(SysUser sysUser) {

        return iSysUserDao.insert(sysUser);
    }

    @Override
    public int insertSelective(SysUser sysUser) {
        return iSysUserDao.insertSelective(sysUser);
    }

    @Override
    public SysUser selectByPrimarykey(Integer id) {
        return iSysUserDao.selectByPrimaryKey(id);
    }

    @Override
    public SysUser selectByLoginId(String userName) {
        Example example = new Example(SysUser.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("isDelete","0");
        criteria.andEqualTo("loginId",userName);
        return iSysUserDao.selectByExample(example).get(0);
    }


    @Override
    public List<SysUser> listAll() {
        return iSysUserDao.selectAll();
    }

    @Override
    public List<SysUser> selectByExample(Example example) {
        return iSysUserDao.selectByExample(example);
    }

    @Override
    public int updateByPrimaryKey(SysUser sysUser) {
        return iSysUserDao.updateByPrimaryKey(sysUser);
    }

    @Override
    public int updateByPrimaryKeySelective(SysUser sysUser) {
        return iSysUserDao.updateByPrimaryKeySelective(sysUser);
    }




}
