package com.jjc.comm.common.util;

import com.google.gson.Gson;
import com.jjc.comm.common.exception.InvalidParamException;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HttpRequest
 * 实现 HttpClient post 传输数据上传文件等
 * @author pyi
 *
 */
public class HttpRequestUtils {

    /**
     * 定义发送数据的类型,binary方式是可以通过将文件转换成String文本之后通过raw方式进行传输
     */
    public static final String raw = "raw";
    public static final String formUrlencoded = "formUrlencoded";
    public static final String formData = "formData";
//	private static final String binary = "binary";


    private static final Logger LOG = LoggerFactory.getLogger(HttpRequestUtils.class);

    /**
     * 实现post请求
     * @param headMap 请求头参数
     * @param MultipartMap 文件上传的参数
     * @param url
     * @param requestParamsType 请求的类型
     * @param rawContext(当requestParamsType为raw时的文本内容)
     * @return
     */
    public static String doPost(Map<String, String> headMap, Map<String, Object> MultipartMap, String url, String requestParamsType, String rawContext){

        try {
            /**
             * 设置超时
             * setConnectTimeout：设置连接超时时间，单位毫秒。
             setConnectionRequestTimeout：设置从connect Manager获取Connection 超时时间，单位毫秒。这个属性是新加的属性，
             因为目前版本是可以共享连接池的。
             setSocketTimeout：请求获取数据的超时时间，单位毫秒。 如果访问一个接口，多少时间内无法返回数据，就直接放弃此次调用。
             */
//            RequestConfig requestConfig=RequestConfig.custom().setConnectTimeout(5000)
//                    .setConnectionRequestTimeout(5000).build();
            HttpClient client = getHttpClient(url);
            HttpPost post = new HttpPost(url);
//            post.setConfig(requestConfig);
            //设置头信息
            if(headMap!=null){
                for(String key:headMap.keySet()){
                    post.addHeader(key, headMap.get(key));
                }
            }
            if(raw.equals(requestParamsType)){
                //普通json文本或是xml文本
                post.setEntity(new StringEntity(rawContext, "utf-8"));
            }else if(formUrlencoded.equals(requestParamsType)){
                //设置参数信息
                List<NameValuePair> httpEntityList = new ArrayList<>();
                if(MultipartMap!=null){
                    for(String key:MultipartMap.keySet()){
                        httpEntityList.add(new BasicNameValuePair(key, MultipartMap.get(key).toString()));
                    }
                }
                post.setEntity(new UrlEncodedFormEntity(httpEntityList, "utf-8"));
            }else if(formData.equals("formData")){
                //上传文件
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                if(MultipartMap!=null){
                    for(String key:MultipartMap.keySet()){
                        if(MultipartMap.get(key) instanceof String){
                            builder.addTextBody(key, MultipartMap.get(key).toString());
                        }
                        if(MultipartMap.get(key) instanceof File){
                            builder.addPart(key, new FileBody((File)MultipartMap.get(key)));
                        }
                    }
                }
                post.setEntity(builder.build());
            }else{
                LOG.error("doPost fail.请求参数类型错误.");
                return "error";
            }
            HttpResponse response = client.execute(post);
            if(response.getStatusLine().getStatusCode()==200){
                return EntityUtils.toString(response.getEntity(), "utf-8");
            }else{
                LOG.error("doPost fail.通讯失败.");
            }
        } catch (UnsupportedEncodingException e) {
            LOG.error("doPost fail.参数转换异常,e={}",e);
            throw new InvalidParamException("参数转换异常:"+e.getMessage());
        } catch (ClientProtocolException e) {
            LOG.error("doPost fail.客户端协议异常,e={}",e);
            throw new InvalidParamException("客户端协议异常:"+e.getMessage());
        } catch (IOException e) {
            LOG.error("doPost fail.io流异常,e={}",e);
            throw new InvalidParamException("io流异常:"+e.getMessage());
        }
        return "error";
    }

    /**
     * 根据传过来的url获取HttpClient对象
     * 若是https请求则创建一个信任所有证书的对象
     * @return
     */
    public static HttpClient getHttpClient(String url){
        if(StringUtils.isNotBlank(url)&&url.startsWith("https")){
            LOG.info("创建信任所有证书的https请求.");
            try {
                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                        return false;
                    }
                }).build();
                SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(sslContext,SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
                return HttpClients.custom().setSSLSocketFactory(scsf).build();
            } catch (KeyManagementException | NoSuchAlgorithmException | KeyStoreException e) {
                e.printStackTrace();
            }
        }
        return HttpClients.createDefault();
    }

    private static String charSet = "UTF-8";
//    private static CloseableHttpClient httpClient = null;
//    private static CloseableHttpResponse response = null;

    public static CloseableHttpClient createSSLClientDefault(){
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {
                //信任所有
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext);
            return HttpClients.custom().setSSLSocketFactory(sslsf).build();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
        return  HttpClients.createDefault();
    }
    /**
     * https的post请求
     * @param url
     * @param jsonStr
     * @return
     */
    public static String doHttpsPost(String url, String jsonStr) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = createSSLClientDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Content-Type", "application/json");

            StringEntity se = new StringEntity(jsonStr);
            se.setContentType("text/json");
            se.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(se);

            response = httpClient.execute(httpPost);
            if (response != null) {
                HttpEntity resEntity = response.getEntity();
                if (resEntity != null) {
                    return EntityUtils.toString(resEntity, charSet);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if(httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * http的post请求(用于key-value格式的参数)
     * @param url
     * @param param
     * @return
     */
    public static byte[] doHttpPost(String url, Map<String, String> param, Map<String, String> heads){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            //请求发起客户端
            httpClient = HttpClients.createDefault();
            //参数集合
            List<NameValuePair> postParams = new ArrayList<NameValuePair>();
            //遍历参数并添加到集合
            for(Map.Entry<String, String> entry:param.entrySet()){
                postParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
            }
            LOG.info("url::"+url);
            //通过post方式访问
            HttpPost post = new HttpPost(url);
            HttpEntity paramEntity = new UrlEncodedFormEntity(postParams,charSet);
            post.setEntity(paramEntity);
            //填加头
            if(heads!=null){
                for(Map.Entry<String, String> entry:heads.entrySet()){
                    post.addHeader(entry.getKey(), entry.getValue());
                }
            }
            response = httpClient.execute(post);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity valueEntity = response.getEntity();
                byte[] content = EntityUtils.toByteArray(valueEntity);
                //jsonObject = JSONObject.fromObject(content);
                return content;
            }else{
                LOG.info("http请求不为200::"+response);
            }
        } catch (UnsupportedEncodingException e) {
            LOG.info("http请求出错::"+e);
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            LOG.info("http请求出错::"+e);
            e.printStackTrace();
        } catch (IOException e) {
            LOG.info("http请求出错::"+e);
            e.printStackTrace();
        }finally{
            if(httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    /**
     * 发送http请求
     * 普通json文本或是xml文本
     * post.setEntity(new StringEntity(rawContext, "utf-8"));
     * @param url
     * @param param
     * @return
     */
    public static String doHttpPostByRaw(String url, Map<String, String> param){
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            //请求发起客户端
            httpClient = HttpClients.createDefault();
            Gson gson = new Gson();
            String rawContext = gson.toJson(param);
            LOG.info("url::{},参数::{}",url,rawContext);
            //通过post方式访问
            HttpPost post = new HttpPost(url);
            post.setEntity(new StringEntity(rawContext, "utf-8"));
            response = httpClient.execute(post);
            StatusLine status = response.getStatusLine();
            int state = status.getStatusCode();
            if (state == HttpStatus.SC_OK) {
                HttpEntity valueEntity = response.getEntity();
                String content = EntityUtils.toString(valueEntity);
                //jsonObject = JSONObject.fromObject(content);
                return content;
            }else{
                LOG.info("http请求不为200::"+response);
            }
        } catch (UnsupportedEncodingException e) {
            LOG.info("http请求出错::"+e);
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            LOG.info("http请求出错::"+e);
            e.printStackTrace();
        } catch (IOException e) {
            LOG.info("http请求出错::"+e);
            e.printStackTrace();
        }finally{
            if(httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * http的post请求（用于请求json格式的参数）
     * @param url
     * @param jsonStr
     * @return
     */
    public static String doHttpPost(String url, String jsonStr) {
        CloseableHttpClient httpClient = null;
        CloseableHttpResponse response = null;
        try {
            httpClient = HttpClients.createDefault();

            // 创建httpPost
            HttpPost httpPost = new HttpPost(url);
            httpPost.setHeader("Accept", "application/json");

            StringEntity entity = new StringEntity(jsonStr, charSet);
            entity.setContentType("text/json");
            entity.setContentEncoding(new BasicHeader("Content-Type", "application/json"));
            httpPost.setEntity(entity);
            //发送post请求
            response = httpClient.execute(httpPost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity responseEntity = response.getEntity();
                String jsonString = EntityUtils.toString(responseEntity);
                return jsonString;
            }else{
                LOG.info("http请求不为200::"+response);
            }
        }catch(Exception e) {
            LOG.info("http请求出错::"+e);
        }finally {
            if(httpClient != null){
                try {
                    httpClient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * http的Get请求
     * @param url
     * @param param
     * @return
     */
    public static byte[] doHttpGet(String url, Map<String, String> param, Map<String, String> heads) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            httpclient = HttpClients.createDefault();
            if(param != null && !param.isEmpty()) {
                //参数集合
                List<NameValuePair> getParams = new ArrayList<NameValuePair>();
                for(Map.Entry<String, String> entry:param.entrySet()){
                    getParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
                url +="?"+EntityUtils.toString(new UrlEncodedFormEntity(getParams), "UTF-8");
            }
            //发送gey请求
            LOG.info("url::"+url);
            HttpGet httpGet = new HttpGet(url);

            //填加头
            if(heads!=null){
                for(Map.Entry<String, String> entry:heads.entrySet()){
                    httpGet.addHeader(entry.getKey(), entry.getValue());
                }
            }

            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toByteArray(response.getEntity());
            }else{
                LOG.info("http请求不为200::"+response);
            }
        }catch(Exception e) {
            LOG.info("http请求出错::"+e);
        }finally{
            if(httpclient != null){
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static String doHttpGet(String url) {
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;

        try {
            httpclient = HttpClients.createDefault();
            //发送gey请求
            if(!url.contains("api/ITG/")){
                LOG.info("url::"+url);
            }
            HttpGet httpGet = new HttpGet(url);
            response = httpclient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                return EntityUtils.toString(response.getEntity());
            }else{
                LOG.info("http请求不为200::"+response);
            }
        }catch(Exception e) {
            LOG.info("http请求出错::"+e);
        }finally{
            if(httpclient != null){
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(response != null){
                try {
                    response.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static void main(String[] args) {
        //接受字符串信息
        String rawContext="";
        try {
            rawContext = FileUtils.readFileToString(new File("E:\\temp\\temp.txt"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        String result = doPost(null, null, "http://10.11.255.123:8090/api", HttpRequestUtils.raw, rawContext);
        System.out.println(result);

        //文件上传
		/*Map<String,Object> map = new HashMap<>();
		map.put("merchantId", "xjf20170406");
		map.put("customerId", "20170406120000");
		map.put("fileType", "03");
		map.put("fileName", "zixingche.jpg");
		map.put("file", new File("E:\\temp\\bike_left.jpg"));
		String result = doPost(null,map, "http://test.boccfc.cc/paygate/uploadImage.json", HttpRequestUtils.formData, null);
		System.out.println(result);*/
    }




}
