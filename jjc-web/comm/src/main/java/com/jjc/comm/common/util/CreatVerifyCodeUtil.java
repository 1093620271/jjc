package com.jjc.comm.common.util;

import java.util.Random;

/**
 * Created by tony on 2019/3/16 0016.
 */
public class CreatVerifyCodeUtil {

    /**
     * 生成验证码
     * @return
     */
    public static String CreatVerifyCode()
    {
        String vc = "";
        Random rNum = new Random();//随机生成类
        int num1 = rNum.nextInt(10);//返回指定范围内的随机数
        int num2 = rNum.nextInt(10);
        int num3 = rNum.nextInt(10);
        int num4 = rNum.nextInt(10);

        Integer[] nums = { num1, num2, num3, num4 };
        for (int i = 0; i < nums.length; i++)//循环添加四个随机生成数
        {
            vc += nums[i];
        }
        return vc;
    }
}
