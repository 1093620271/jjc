package com.jjc.comm.common.util;



import com.jjc.comm.common.exception.InvalidParamException;
import com.jjc.comm.common.exception.ServiceException;

/**
 * 针对数据库数字+字母的自增
 * Created by huoquan on 2017/11/6.
 */
public class IncreaseNumber {
    public static void main(String[] args) {
        String addStr="A01ZZ";
        try {
            System.out.println(IncreaseNumber.addOne(addStr));
        } catch (Exception e) {
            throw new ServiceException(e.getMessage());  //如果在service层中，捕获异常抛出给信息提示用户
        }
    }
    /**
     * 获取Asc码
     * @param st
     * @return
     */
    public static int getAscNum(String st) {
        byte[] gc = st.getBytes();
        int ascNum = (int) gc[0];
        return ascNum;
    }

    /**
     * ASC转字符
     *
     * @param num
     * @return
     */
    public static String getNumAsc(int num) {
        if(num==58)
            num=65;
        char a = (char)num;
        return a+"";
    }

    /**
     * 传入一个字符串，自动加1
     * @param str
     * @return
     * @throws Exception 抛出异常
     */
    public static String addOne(String str) throws Exception {
        String addStr="";
        if(ToolUtil.isEmpty(str))
            throw new InvalidParamException("该字符串为空，无法增加");  //如果是一位数，而且是最大Z，则不增加了
        String lastStr=str.substring(str.length()-1,str.length());  //截取最后一个字符
        addStr=str.substring(0,str.length()-1);
        if(lastStr.equals("Z")){
            if(str.length()==1){  //1位数
                throw new InvalidParamException("序号已达到最大值");  //如果是一位数，而且是最大Z，则不增加了
            }else{  //大于1位数
                String str2=str.substring(str.length()-2,str.length()-1);  //截取倒数第二一个字符
                if(str2.equals("Z"))
                    throw new InvalidParamException("序号已达到最大值");  //如果是倒数第二位数，而且是最大Z，则不增加了
                else{
                    addStr=str.substring(0,str.length()-2);
                    addStr=addStr+getNumAsc(getAscNum(str2)+1)+"0";
                }
            }
        }else{
            addStr+=getNumAsc(getAscNum(lastStr)+1);  //自动加1
        }
        return addStr;
    }

}
