package com.jjc.comm.common.sys;

/**
 * Created by huoquan on 2017/10/11.
 * 用来存id跟name的
 */
public class MsgResult {
    private String id;
    private String name;

    public MsgResult() {
    }

    public MsgResult(String name) {
        this.id = "";
        this.name = name;
    }

    public MsgResult(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
