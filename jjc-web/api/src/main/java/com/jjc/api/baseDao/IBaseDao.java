package com.jjc.api.baseDao;


import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

/**
 * Demo class
 *
 * @author Allion.li
 * @Date 2020/7/10
 */
public interface IBaseDao<T> extends Mapper<T>, MySqlMapper<T> {

}
