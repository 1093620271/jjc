package com.jjc.comm.common.auth;


import com.jjc.comm.common.util.ToolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 定时刷新redis的token
 * @author huoquan
 * @date 2018/11/8.
 */
//@Component
public class TokenTask {
    private static Logger logger = LoggerFactory.getLogger(TokenTask.class);
    /**
     * token刷新时间 60分钟
     */
    public final static long REFRESH_TIME = 60 * 1000 * 60;
    /**
     * token有效时间,用于新老token过度，定义为比刷新时间多5分钟
     */
    public final static long EFFECTIVE_TIME = REFRESH_TIME+(5*1000);
    /**
     * token在redis的前缀
     */
    public final static String AUTH_TOKEN = "auth_token";
    /**
     * token在刷新时间的key值
     */
    public final static String AUTH_TOKENTIME = "auth_tokenTime";

    /**
     * token的自定义钥匙：token生成过程：md5（时间戳+自定义钥匙）
     */
    public final static String TOKEN_KEY = "_token123";


    /**
     * 获取token,使用redis，现在改为内部暂时简单传个token就好了
     * @return
     */
    public String getToken() {
//        long nowTime = System.currentTimeMillis();
//        ValueOperations<String, String> ops = template.opsForValue();
//        String timeOut=ops.get(AUTH_TOKENTIME); //过期时间
//        Boolean isRefreshToken=false;  //是否刷新token标识
//        if(ToolUtil.isNotEmpty(timeOut)){
//            if(nowTime>Long.parseLong(timeOut)){
//                //已经过期了，重新刷新token
//                isRefreshToken=true;
//            }
//        }else{
//            //不存在，重新刷新token
//            isRefreshToken=true;
//        }
//        if(isRefreshToken){
//            //存token操作
//            ops.set(AUTH_TOKENTIME,nowTime+REFRESH_TIME+"");  //存过期时间
//            String token = ToolUtil.strToMd5(nowTime+TOKEN_KEY);   //创建token值：md5（时间戳+自定义钥匙）
//            ops.set(AUTH_TOKEN + token,"token",EFFECTIVE_TIME, TimeUnit.MILLISECONDS);  //设置过期时间，单位毫秒
//            logger.info("创建token："+token+",设置的失效时间："+ops.get(AUTH_TOKENTIME));
//            System.setProperty(AUTH_TOKEN, token);
//        }
//        return System.getProperty(AUTH_TOKEN);
//        long nowTime = System.currentTimeMillis();
//        return SecurityUtil.encryptAES(nowTime+"");
        return TOKEN_KEY;
    }

    /**
     * 验证token
     * @param key
     * @return
     */
    public boolean checkToken(String key) {
        if (ToolUtil.isNotEmpty(key)) {
            try{
                //解密key
//                String time=SecurityUtil.decryptAES(key);
//                if(ToolUtil.isNotEmpty(time)){
//                    //解密成功
//                    long nowTime = System.currentTimeMillis();
//                    //判断时间不超过10分钟为有效token
//                    if(nowTime<=(Long.parseLong(time)+10*60*1000)){
//                        logger.info("验证的token通过");
//                        return true;
//                    }
//                }
                if(TOKEN_KEY.equals(key)){
                    logger.info("验证的token通过:"+key);
                    return true;
                }
            }catch (Exception e){
                logger.error("验证的token出错：",e);
            }
        }
        return false;
    }

}
