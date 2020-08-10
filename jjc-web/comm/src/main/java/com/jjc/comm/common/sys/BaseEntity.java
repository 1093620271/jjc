package com.jjc.comm.common.sys;


/**
 * @author huoquan
 * @date 2018/8/23.
 */
public class BaseEntity<T,RowPage> {

    private T entity;

    private RowPage rowPage;

    public BaseEntity() {
    }

    public BaseEntity(T entity) {
        this.entity = entity;
    }


    public BaseEntity(T entity, RowPage rowPage) {
        this.entity = entity;
        this.rowPage = rowPage;
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public RowPage getRowPage() {
        return rowPage;
    }

    public void setRowPage(RowPage rowPage) {
        this.rowPage = rowPage;
    }
}
