package com.jjc.comm.common.util;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

/**
 * 利用HttpClient进行post请求的工具类
 * @ClassName: HttpClientUtil
 * @Description: TODO
 * @author Devin <xxx>
 * @date 2017年2月7日 下午1:43:38
 * https://www.cnblogs.com/luchangyou/p/6375166.html
 */
public class SSLUtil {
    private static final Logger logger = LoggerFactory.getLogger(SSLUtil.class);
    @SuppressWarnings("resource")
    public static String doPost(String url, String jsonstr){
        HttpClient httpClient = null;
        HttpPost httpPost = null;
        String result = null;
        try{
            httpClient = new SSLClient();
            httpPost = new HttpPost(url);

            httpPost.setHeader("Accept", "application/json");
            httpPost.addHeader("Content-Type", "application/json");
            StringEntity se = new StringEntity(jsonstr);
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);
            HttpResponse response = httpClient.execute(httpPost);
            if(response != null){
                HttpEntity resEntity = response.getEntity();
                if(resEntity != null){
                    result =  getResponseString(resEntity);
                }
            }
        }catch(Exception ex){
            logger.info("https请求出错::"+ex);
        }
        return result;
    }

    /**
     * 解析网络响应信息，如果为gzip格式，先解压再转换
     * @param entity 网络返回的HttpEntity信息
     * @return 返回网络响应信息，数据类型为<code>String</code>
     *
     */
    @SuppressWarnings("deprecation")
    private static String getResponseString(HttpEntity entity){
        try {
            if ((entity.getContentEncoding() != null)
                    && entity.getContentEncoding().getValue().contains("gzip")) {
                GZIPInputStream gzip = new GZIPInputStream(
                        new ByteArrayInputStream(EntityUtils.toByteArray(entity)));
                InputStreamReader isr = new InputStreamReader(gzip);
                BufferedReader br = new BufferedReader(isr);
                StringBuilder sb = new StringBuilder();
                String temp;
                while((temp = br.readLine()) != null){
                    sb.append(temp);
                    sb.append("\r\n");
                }
                isr.close();
                gzip.close();

                return sb.toString();
            } else {
                return EntityUtils.toString(entity);
            }
        }catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

}
