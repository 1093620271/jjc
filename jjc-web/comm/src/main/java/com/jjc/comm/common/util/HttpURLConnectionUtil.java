package com.jjc.comm.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 原生的http请求
 * @author huoquan
 * @date 2019/7/1.
 */
public class HttpURLConnectionUtil {
    private static final Logger logger = LoggerFactory.getLogger(HttpRequestUtils.class);
    /**
     * http get请求
     * @param httpUrl 链接
     * @return 响应数据
     */
    public static String doHttpGet(String httpUrl){
        //链接
        HttpURLConnection connection=null;
        InputStream is=null;
        BufferedReader br = null;
        StringBuilder result=new StringBuilder();
        try {
            //创建连接
            URL url=new URL(httpUrl);
            connection= (HttpURLConnection) url.openConnection();
            //设置请求方式
            connection.setRequestMethod("GET");
            //设置连接超时时间
            connection.setConnectTimeout(15000);
            //设置读取超时时间
            connection.setReadTimeout(15000);
            //开始连接
            connection.connect();
            //获取响应数据
            if(connection.getResponseCode()== HttpURLConnection.HTTP_OK){
                //获取返回的数据
                is=connection.getInputStream();
                if(is!=null){
                    br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String temp;
                    while ((temp=br.readLine())!=null){
                        result.append(temp);
                    }
                }
                return result.toString();
            }else{
                logger.info("http请求不为200::"+connection);
            }
        }catch (Exception e) {
            logger.error("doGet请求失败",e);
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                connection.disconnect();// 关闭远程连接
            }
        }
        return null;
    }

    /**
     * post请求
     * @param httpUrl 链接
     * @param param 参数
     * @return
     */
    public static String doHttpPost(String httpUrl, String param) {
        StringBuilder result=new StringBuilder();
        //连接
        HttpURLConnection connection=null;
        OutputStream os=null;
        InputStream is=null;
        BufferedReader br=null;
        try {
            //创建连接对象
            URL url=new URL(httpUrl);
            //创建连接
            connection= (HttpURLConnection) url.openConnection();
            //设置请求方法
            connection.setRequestMethod("POST");
            //设置连接超时时间
            connection.setConnectTimeout(15000);
            //设置读取超时时间
            connection.setReadTimeout(15000);
            //设置是否可读取
            connection.setDoOutput(true);
            //设置响应是否可读取
            connection.setDoInput(true);
            //设置参数类型
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("Content-Type", "application/json");
            //拼装参数
            if(param!=null&&!"".equals(param)){
                //设置参数
                os=connection.getOutputStream();
                //拼装参数
                os.write(param.getBytes("UTF-8"));
            }
            //设置权限
            //设置请求头等
            //开启连接
            //connection.connect();
            //读取响应
            if(connection.getResponseCode()== HttpURLConnection.HTTP_OK){
                is=connection.getInputStream();
                if(is!=null){
                    br=new BufferedReader(new InputStreamReader(is,"UTF-8"));
                    String temp;
                    if((temp=br.readLine())!=null){
                        result.append(temp);
                    }
                }
                return result.toString();
            }else{
                logger.info("http请求不为200::"+connection);
            }
            //关闭连接
        }catch (Exception e) {
            logger.error("doPost请求失败",e);
        }finally {
            if(br!=null){
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(os!=null){
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(is!=null){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(connection!=null){
                //关闭连接
                connection.disconnect();
            }
        }
        return null;
    }

    /**
     * 发送https请求，java原生方法，以免出现解析不了主机
     * @param url
     * @param data
     * @return
     */
    public static String doHttpsPost(String url, String data) {
        DataOutputStream wr = null;
        BufferedReader in = null;
        HttpsURLConnection con = null;
        try {
            URL obj = new URL(url);
            //https请求
            con = (HttpsURLConnection) obj.openConnection();
//            if(headers != null) {
//                for(Header header : headers) {
//                    con.setRequestProperty(header.getName(), header.getValue());
//                }
//            }
            con.setRequestProperty("Content-Type", " application/json");
            con.setConnectTimeout(15000);
            // 添加请求头
            con.setRequestMethod("POST");
            // 发送Post请求
            con.setDoOutput(true);
            wr = new DataOutputStream(con.getOutputStream());
            wr.writeBytes(data);
            wr.flush();
            int responseCode = con.getResponseCode();
            if (responseCode == HttpsURLConnection.HTTP_OK){
                in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                return response.toString();
            } else {
                logger.error("Https请求服务器异常，返回异常码：{}", responseCode);
            }
        } catch (Exception e) {
            logger.error("Https请求失败,{},data:{}", url, data, e);
        } finally {
            if (wr != null) {
                try {
                    wr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            if(con != null){
                con.disconnect();
            }
        }
        return null;
    }
}
