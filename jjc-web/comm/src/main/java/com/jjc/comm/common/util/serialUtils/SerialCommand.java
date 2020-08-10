package com.jjc.comm.common.util.serialUtils;

import com.alibaba.fastjson.JSON;
import com.jjc.comm.common.util.SerializeUtil;
import com.jjc.comm.common.util.ToolUtil;


import java.io.Serializable;

public class SerialCommand implements Serializable {

    // AES 加密字符串
    public final static String AES_PASSWORD = "11337799";

    // 数据分割标记
    public final static String DU_START_FLAG = "!@#$`!";
    public final static String DU_END_FLAG = "!`$#@!";

    // 是否完成标记
    public final static int END_FLAG = 1;
    public final static int START_FLAG = 0;

    // 类型
    public final static int TYPE_HTTP_FLAG = 0;
    public final static int TYPE_MQ_FLAG = 1;

    // 数据类型
    public  static boolean DATA_TYPE_BYTE = false;

    // 数据长度
    public  static int ALEN_MAX =  9;

    // json前部分长度
    public static int JSON_BEFLEN = 15;

    // 1M=1024K 1K=1024B
    //所以500*1024*1024=524288000
    public String aLen = String.format("%0"+ALEN_MAX+"d",0);
    public String cuuid;
    public String sendData; // 发送数据
    public String getData;  // JSON
    public byte[] byteSendData; // 原始数据
    public int type = TYPE_HTTP_FLAG; // 数据类型
    public int sendFlag = 0; // 0未发送  1发送成功
    public int getFlag = 0; // 0未接收 1 接收完成
    public int maxTimes = 5000; // 5秒
    public SerialCommand(String sendData) {
        init();
        this.sendData = sendData;
    }
    public void init(){
        this.cuuid = ToolUtil.getUUID();
    }
    public SerialCommand() {
        init();
    }



    public byte[] gSend(){
        if(DATA_TYPE_BYTE){
            return gRawSend();
        }
        return gStrSend();
    }

    private byte[] gStrSend(){
        // this.getFlag = START_FLAG;
        // this.sendFlag = START_FLAG;
        this.aLen = String.format("%0"+ALEN_MAX+"d", DU_START_FLAG.length() + DU_END_FLAG.length() + JSON.toJSONString(this).length());
        String sendData =  DU_START_FLAG+ JSON.toJSONString(this) +DU_END_FLAG;
        return sendData.getBytes();
    }

    private byte[] gRawSend(){
        // this.getFlag = START_FLAG;
        // this.sendFlag = START_FLAG;
        byte[] serData = SerializeUtil.serialize(this);
        byte[] send = new byte[DU_START_FLAG.length() + DU_END_FLAG.length() +serData.length ];
        int bytePost = 0;
        for(int i=0;i<DU_START_FLAG.length();i++){
            send[bytePost++] = DU_START_FLAG.getBytes()[i];
        }
        for(int i=0;i<serData.length;i++){
            send[bytePost++] = serData[i];
        }
        for(int i=0;i<DU_END_FLAG.length();i++){
            send[bytePost++] = DU_END_FLAG.getBytes()[i];
        }
        return send;
    }
}
