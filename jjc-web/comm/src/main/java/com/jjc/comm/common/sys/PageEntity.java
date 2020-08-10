package com.jjc.comm.common.sys;

/**
 * 封装查询列表实体
 * @author huoquan
 * @date 2018/8/23.
 */
public class PageEntity {

    // 当前页码，默认第一页
    private int pageNum = 1;

    // 页面大小，默认20
    private int pageSize = 20;

    /**
     * 排序字段，标准查询有效，
     * 实例： updatedate desc, name asc
     */
    private String orderBy = "";


    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }


    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }
}
