package com.jjc.comm.common.util;


import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HttpHelper {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpHelper.class);

    public static final String ENCODE_UTF_8 = "utf-8";

    public static final String ENCODE_GBK = "gbk";

//    static {
//        Protocol https = new
//                Protocol("https", new HTTPSSecureProtocolSocketFactory(), 443);
//        Protocol.registerProtocol("https", https);
//    }

    public static String get(String strURL) {
        return get(strURL, "UTF-8");
    }

    /**
     * HTTP GET 请求
     *
     * @param strURL
     * @return
     */
    public static String get(String strURL, String encoding, int timeout) {
        HttpURLConnection urlConn = null;
        InputStream in = null;
        StringBuffer sb = new StringBuffer();

        long t1 = System.currentTimeMillis();

        try {
            URL url = new URL(strURL);

            urlConn = (HttpURLConnection) url.openConnection();
            //urlConn.addRequestProperty("Content-Encoding", "UTF-8");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("GET");
            urlConn.connect();

            //如果小于10s就重置为120s
           /* if (timeout < 10 * 1000)
            {
         	   timeout = 120 * 1000;
            }*/
            //新增超时时间
            urlConn.setConnectTimeout(timeout);
            urlConn.setReadTimeout(timeout);

            in = urlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, encoding));
            String line = rd.readLine();
            while (line != null) {
                line = line.trim();
                //去空行
                if (!line.equals(""))
                    sb.append(line);
                line = rd.readLine();
            }

        } catch (Exception e) {
            LOGGER.error("http异常", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }

            if (urlConn != null) {
                urlConn.disconnect();
            }
        }

        long t2 = System.currentTimeMillis();
        long t = (t2 - t1);
        if (t > 1000)
            LOGGER.info("httpGet use time:" + t + "ms, " + strURL);
        else
            LOGGER.info("httpGet use time:" + t + "ms");

        return sb.toString();
    }

    /**
     * HTTP GET 请求
     *
     * @param strURL
     * @return
     */
    public static String get(String strURL, String encoding) {
        HttpURLConnection urlConn = null;
        InputStream in = null;
        StringBuffer sb = new StringBuffer();

        long t1 = System.currentTimeMillis();

        try {
            URL url = new URL(strURL);

            urlConn = (HttpURLConnection) url.openConnection();
            //urlConn.addRequestProperty("Content-Encoding", "UTF-8");
            urlConn.setDoInput(true);
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("GET");
            urlConn.connect();

            in = urlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, encoding));
            String line = rd.readLine();
            while (line != null) {
                line = line.trim();
                //去空行
                if (!line.equals(""))
                    sb.append(line);
                line = rd.readLine();
            }

        } catch (Exception e) {
            LOGGER.error("http异常", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }

            if (urlConn != null) {
                urlConn.disconnect();
            }
        }

        long t2 = System.currentTimeMillis();
        long t = (t2 - t1);
        if (t > 1000)
            LOGGER.info("httpGet use time:" + t + "ms, " + strURL);
        else
            LOGGER.info("httpGet use time:" + t + "ms");

        return sb.toString();
    }


    public static String post(String strURL, String strPostData) {
        return post(strURL, strPostData, "UTF-8");
    }

    /**
     * HTTP POST 请求
     *
     * @param strURL
     * @return
     */
    public static String post(String strURL, String strPostData, String encoding) {
        HttpURLConnection urlConn = null;
        InputStream in = null;
        StringBuffer sb = new StringBuffer();

        long t1 = System.currentTimeMillis();
        try {
            URL url = new URL(strURL);

            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");

            OutputStreamWriter out = new OutputStreamWriter(urlConn.getOutputStream(), encoding);
            out.write(strPostData);
            out.flush();
            out.close();

            in = urlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, encoding));
            String line = rd.readLine();
            while (line != null) {
                line = line.trim();
                //去空行
                if (!line.equals(""))
                    sb.append(line);
                line = rd.readLine();
            }

        } catch (Exception e) {
            LOGGER.error("http异常", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }

            if (urlConn != null) {
                urlConn.disconnect();
            }
        }

        long t2 = System.currentTimeMillis();
        long t = (t2 - t1);

        if (t > 1000)
            LOGGER.info("httpPost use time:" + t + "ms, " + strURL + "?" + strPostData);
        else
            LOGGER.info("httpPost use time:" + t + "ms");

        return sb.toString();
    }

    public static String post(String strURL, String strPostData, String encoding, String header) {
        HttpURLConnection urlConn = null;
        InputStream in = null;
        StringBuffer sb = new StringBuffer();

        long t1 = System.currentTimeMillis();
        try {
            URL url = new URL(strURL);

            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-type", header);

            OutputStreamWriter out = new OutputStreamWriter(urlConn.getOutputStream(), encoding);
            out.write(strPostData);
            out.flush();
            out.close();

            in = urlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, encoding));
            String line = rd.readLine();
            while (line != null) {
                line = line.trim();
                //去空行
                if (!line.equals(""))
                    sb.append(line);
                line = rd.readLine();
            }

        } catch (Exception e) {
            LOGGER.error("http异常", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }

            if (urlConn != null) {
                urlConn.disconnect();
            }
        }

        long t2 = System.currentTimeMillis();
        long t = (t2 - t1);

        if (t > 1000)
            LOGGER.info("httpPost use time:" + t + "ms, " + strURL + "?" + strPostData);
        else
            LOGGER.info("httpPost use time:" + t + "ms");

        return sb.toString();
    }

    /*
     * HTTP POST 请求,新增超时时间
     * @param strURL
     * @return
     */
    public static String post(String strURL, String strPostData, String encoding, int timeout) {
        HttpURLConnection urlConn = null;
        InputStream in = null;
        StringBuffer sb = new StringBuffer();

        long t1 = System.currentTimeMillis();
        try {
            URL url = new URL(strURL);

            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");
         /*
           //如果小于10s就重置为120s
           if (timeout < 10 * 1000)
           {
        	   timeout = 120 * 1000;
           }*/
            //新增超时时间
            urlConn.setConnectTimeout(timeout);
            urlConn.setReadTimeout(timeout);

            OutputStreamWriter out = new OutputStreamWriter(urlConn.getOutputStream(), encoding);
            out.write(strPostData);
            out.flush();
            out.close();

            in = urlConn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(in, encoding));
            String line = rd.readLine();
            while (line != null) {
                line = line.trim();
                //去空行
                if (!line.equals(""))
                    sb.append(line);
                line = rd.readLine();
            }

        } catch (Exception e) {
            LOGGER.error("http异常", e);
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }

            if (urlConn != null) {
                urlConn.disconnect();
            }
        }

        long t2 = System.currentTimeMillis();
        long t = (t2 - t1);

        if (t > 1000)
            LOGGER.info("httpPost use time:" + t + "ms, " + strURL + "?" + strPostData);
        else
            LOGGER.info("httpPost use time:" + t + "ms");

        return sb.toString();
    }


    /**
     * JSON+HTTP POST
     *
     * @param url
     * @param data
     * @param encoding
     * @param timeout
     * @return
     * @throws Exception
     */
    public static String httpPost(String url, NameValuePair[] data, String encoding, int timeout)
            throws Exception {

        //long t1 = System.currentTimeMillis();
        String str = null;
        // TODO Auto-generated method stub
        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod(url);

        // 设置编码,httppost同时会用编码进行url.encode
        httpClient.getParams().setContentCharset(encoding);
        postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);

        //如果小于10s就重置为120s
       /* if (timeout < 10 * 1000)
        {
     	   timeout = 120 * 1000;
        }*/
        //新增超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);

        // 将表单的值放入postMethod中
        postMethod.setRequestBody(data);
        // 执行postMethod
        int statusCode = 0;
        try {
            statusCode = httpClient.executeMethod(postMethod);
        } catch (HttpException e) {
            //logger.error(e.getMessage());
        } catch (IOException e) {
            //logger.error(e.getMessage());
        }
        // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
        // 301或者302
        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
                || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
            // 从头中取出转向的地址
            Header locationHeader = postMethod.getResponseHeader("location");

        } else {
            try {
                str = postMethod.getResponseBodyAsString();
            } catch (IOException e) {
                //logger.error(e.getMessage());
            }
        }

        //long t2= System.currentTimeMillis();
        //long t = (t2-t1);

        //LOGGER.info("httpPost use time:"+t+"ms");

        return str;
    }


    /**
     * 该方法读取会去掉换行符
     *
     * @param url
     * @param data
     * @param encoding
     * @param timeout
     * @return
     * @throws Exception
     */
    public static String httpPostByAsStream(String url, NameValuePair[] data, String encoding, int timeout)
            throws Exception {

        //long t1 = System.currentTimeMillis();
        String str = null;
        // TODO Auto-generated method stub
        HttpClient httpClient = new HttpClient();

        PostMethod postMethod = new PostMethod(url);

        // 设置编码,httppost同时会用编码进行url.encode
        httpClient.getParams().setContentCharset(encoding);
        postMethod.addRequestHeader("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding);

        //如果小于10s就重置为120s
      /*  if (timeout < 10 * 1000)
        {
     	   timeout = 120 * 1000;
        }*/
        //新增超时时间
        httpClient.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
        httpClient.getHttpConnectionManager().getParams().setSoTimeout(timeout);

        // 将表单的值放入postMethod中
        postMethod.setRequestBody(data);
        // 执行postMethod
        int statusCode = 0;
        try {
            statusCode = httpClient.executeMethod(postMethod);
        } catch (HttpException e) {
            //logger.error(e.getMessage());
        } catch (IOException e) {
            //logger.error(e.getMessage());
        }
        // HttpClient对于要求接受后继服务的请求，象POST和PUT等不能自动处理转发
        // 301或者302
        if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY
                || statusCode == HttpStatus.SC_MOVED_TEMPORARILY) {
            // 从头中取出转向的地址
            Header locationHeader = postMethod.getResponseHeader("location");

        } else {
            try {
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                if (inputStream != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
                    StringBuffer stringBuffer = new StringBuffer();
                    String str2 = "";
                    while ((str2 = br.readLine()) != null) {
                        stringBuffer.append(str2);
                    }
                    str = stringBuffer.toString();
                } else {
                    LOGGER.error("httpPost inputStream 为空");
                }

            } catch (Exception e) {
                //logger.error(e.getMessage());
            }

        }

        //	long t2= System.currentTimeMillis();
        //	long t = (t2-t1);

        //	LOGGER.info("httpPost use time:"+t+"ms");

        return str;
    }


    public static String httpget(String strURL, String encoding, int timeout) {

        //long t1 = System.currentTimeMillis();

        String str = "";

        HttpClient client = new HttpClient();

        GetMethod method = null;
        try {

            client.getParams().setContentCharset(encoding);

            // 如果小于10s就重置为120s
			/*if (timeout < 10 * 1000)
			{
				timeout = 120 * 1000;
			}*/
            // 新增超时时间
            client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
            client.getHttpConnectionManager().getParams().setSoTimeout(timeout);

            method = new GetMethod(strURL);
            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
            } else {
                str = method.getResponseBodyAsString();
            }
        } catch (Exception e) {
            e.getStackTrace();
            e.printStackTrace();
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }

        //long t2= System.currentTimeMillis();
        //long t = (t2-t1);

        //LOGGER.info(strURL+"---httpPost use time:"+t+"ms"+",return msg:"+str);

        return str;
    }


    /**
     * 该方法读取会去掉换行符
     *
     * @param strURL
     * @param encoding
     * @param timeout
     * @return
     */
    public static String httpgetByAsStream(String strURL, String encoding, Integer timeout) {

        //long t1 = System.currentTimeMillis();

        String str = "";

        HttpClient client = new HttpClient();

        GetMethod method = null;
        try {

            client.getParams().setContentCharset(encoding);

			/*// 如果小于10s就重置为120s
			if (timeout < 10 * 1000)
			{
				timeout = 120 * 1000;
			}*/
            if (timeout != null) {
                // 新增超时时间
                client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
                client.getHttpConnectionManager().getParams().setSoTimeout(timeout);
            }

            method = new GetMethod(strURL);
            int statusCode = client.executeMethod(method);
            if (statusCode != 200) {
            } else {
                InputStream inputStream = method.getResponseBodyAsStream();
                if (inputStream != null) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, encoding));
                    StringBuffer stringBuffer = new StringBuffer();
                    String str2 = "";
                    while ((str2 = br.readLine()) != null) {
                        stringBuffer.append(str2);
                    }
                    str = stringBuffer.toString();
                } else {
                    LOGGER.error("httpGet inputStream 为空");
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
            e.printStackTrace();
            LOGGER.error("httpGet error:",e);
        } finally {
            if (method != null) {
                method.releaseConnection();
            }
        }

        //long t2= System.currentTimeMillis();
        //long t = (t2-t1);

        //LOGGER.info(strURL+"---httpPost use time:"+t+"ms"+",return msg:"+str);

        return str;
    }


    public static String post(String strURL, String strPostData, String encoding, Map<String, String> headMap, int timeout) {
        HttpURLConnection urlConn = null;
        InputStream in = null;
        StringBuffer sb = new StringBuffer();

        long t1 = System.currentTimeMillis();

        try {

            URL url = new URL(strURL);

            urlConn = (HttpURLConnection) url.openConnection();
            urlConn.setDoOutput(true);
            urlConn.setRequestMethod("POST");
            urlConn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=" + encoding + "");
            for (Map.Entry<String, String> entry : headMap.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                urlConn.setRequestProperty(key, value);
            }

	        /* // 如果小于10s就重置为120s
			if (timeout < 10 * 1000)
			{
				timeout = 120 * 1000;
			}
            */
            urlConn.setConnectTimeout(timeout);
            urlConn.setReadTimeout(timeout);

            OutputStreamWriter out = new OutputStreamWriter(urlConn.getOutputStream(), encoding);
            out.write(strPostData);
            out.flush();
            out.close();

            int responseCode = urlConn.getResponseCode();

            if (responseCode == 200) {
                in = urlConn.getInputStream();
            } else {
                in = urlConn.getErrorStream();
            }

            BufferedReader rd = new BufferedReader(new InputStreamReader(in, encoding));
            String line = rd.readLine();
            while (line != null) {
                line = line.trim();
                if (!line.equals(""))
                    sb.append(line);
                line = rd.readLine();
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                }

            if (urlConn != null) {
                urlConn.disconnect();
            }
        }

        long t2 = System.currentTimeMillis();
        long t = (t2 - t1);

        LOGGER.info(strURL + "---httpPost use time:" + t + "ms" + ",return msg:" + sb.toString());

        return sb.toString();
    }


    /**
     * 用于请求 webService soap
     *
     * @param soapAction
     * @param xml
     * @param sendUrl
     * @param resultMatcher
     * @param encoding
     * @param timeout
     * @return
     */
    public static String webServiceSend(String soapAction, String xml, String sendUrl, String resultMatcher, String encoding, int timeout) {
        String result = "";

        URL url;
        try {
            url = new URL(sendUrl);

            URLConnection connection = url.openConnection();
            HttpURLConnection httpconn = (HttpURLConnection) connection;
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            bout.write(xml.getBytes());
            byte[] b = bout.toByteArray();
            httpconn.setRequestProperty("Content-Length", String
                    .valueOf(b.length));
            httpconn.setRequestProperty("Content-Type",
                    "text/xml; charset=" + encoding);
            httpconn.setRequestProperty("SOAPAction", soapAction);
            httpconn.setRequestMethod("POST");
            httpconn.setDoInput(true);
            httpconn.setDoOutput(true);
            httpconn.setConnectTimeout(timeout);
            httpconn.setReadTimeout(timeout);

            OutputStream out = httpconn.getOutputStream();
            out.write(b);
            out.close();

            InputStreamReader isr = new InputStreamReader(httpconn
                    .getInputStream());
            BufferedReader in = new BufferedReader(isr);
            String inputLine;
            while (null != (inputLine = in.readLine())) {
                Pattern pattern = Pattern.compile(resultMatcher);
                Matcher matcher = pattern.matcher(inputLine);
                while (matcher.find()) {
                    result = matcher.group(1);
                }
            }
            in.close();
            isr.close();
            bout.close();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * get请求类型 参数拼接
     *
     * @param params
     * @return
     */
    public static String paramsHandler(Map<String, Object> params) {
        StringBuilder paramsUrlStr = new StringBuilder();
        if (params != null && !params.isEmpty()) {
            paramsUrlStr.append("?");
            for (Map.Entry<String, Object> item : params.entrySet()) {
                if (item.getValue() != null) {
                    paramsUrlStr.append(item.getKey()).append("=").append(item.getValue()).append("&");
                }
            }
            if (paramsUrlStr.length() > 0) {
                paramsUrlStr.setLength(paramsUrlStr.length() - 1);
            }
        }
        return paramsUrlStr.toString();
    }

    public static String postWithHeader(String strURL, String strPostData, String encoding, String header) {

        //long t1 = System.currentTimeMillis();

        String str = "";

        HttpClient client = new HttpClient();

        PostMethod method = null;

        // 如果小于10s就重置为120s
        // 新增超时时间
        //  client.getHttpConnectionManager().getParams().setConnectionTimeout(timeout);
        //  client.getHttpConnectionManager().getParams().setSoTimeout(timeout);

        method = new PostMethod(strURL);
        method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, encoding);

        try {
            RequestEntity rentity = new StringRequestEntity(strPostData, "application/x-www-form-urlencoded;charset=utf-8", encoding);
            method.setRequestEntity(rentity);

            method.setRequestHeader("Content-Type", header);

            client.executeMethod(method);
        } catch (IOException e) {
            LOGGER.error("=====postWithHeader error:",e);
        }

        // 传输数据过大，改为stream获取
        // inputStream、br会自动关闭资源
        try (InputStream inputStream = method.getResponseBodyAsStream();
             BufferedReader br = new BufferedReader(new InputStreamReader(inputStream))) {

            StringBuffer stringBuffer = new StringBuffer();
            String tempStr = null;

            while ((tempStr = br.readLine()) != null) {
                stringBuffer.append(tempStr);
            }
            str = stringBuffer.toString();

        } catch (IOException e) {
            LOGGER.error("=====postWithHeader2 error:",e);
        } finally {

            if (method != null) {
                method.releaseConnection();
            }
        }

        return str;
    }

    public static void main(String args[]) {
        String str = post("http://127.0.0.1:8080/callback/10021?gwNum=10021&password=admin", "<xml>ssss</xml>");
        System.out.println(str);

    }
}
