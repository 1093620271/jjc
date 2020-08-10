package com.jjc.comm.common.util;

public class ExcelImportModel {

    public  static final String STRING_TYPE=null;
    public  static final String PIC_TYPE="1";
    public  static final String TIME_TYPE="2";
    public  static final String INT_TYPE="3";
    public  static final String SYSKEY_TYPE="4";

    private String oldName;
    private String newName;
    private String format;
    private String type;//类型  null: string , 1:图片 ， 2：时间 ，3 整形,4：系统key
    public ExcelImportModel(){}
    public ExcelImportModel(String oldName, String newName, String format, String type) {
        this.oldName = oldName;
        this.newName = newName;
        this.format = format;
        this.type = type;
    }

    public String getOldName() {
        return oldName;
    }

    public void setOldName(String oldName) {
        this.oldName = oldName;
    }

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
