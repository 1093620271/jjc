package com.jjc.comm.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ServletUtils工具类：提供一些Http与Servlet工具的方法
 */
public class ServletUtils {

	private static Logger logger = LoggerFactory.getLogger(ServletUtils.class);

	// -- Content Type 定义 --//
	public static final String EXCEL_TYPE = "application/vnd.ms-excel";
	public static final String HTML_TYPE = "text/html";
	public static final String JS_TYPE = "text/javascript";
	public static final String JSON_TYPE = "application/json";
	public static final String XML_TYPE = "text/xml";
	public static final String TEXT_TYPE = "text/plain";

	// -- Header 定义 --//
	public static final String AUTHENTICATION_HEADER = "Authorization";

	private ServletUtils() {
		throw new AssertionError();
	}

    /**
     * 设置客户端缓存过期时间 的Header.
     */
	public static void setExpiresHeader(HttpServletResponse response, long expiresSeconds) {
		// Http 1.0 header
		response.setDateHeader("Expires", System.currentTimeMillis() + expiresSeconds * 1000);
		// Http 1.1 header
		response.setHeader("Cache-Control", "private, max-age=" + expiresSeconds);
	}

    /**
     * 设置禁止客户端缓存的Header.
     */
	public static void setDisableCacheHeader(HttpServletResponse response) {
		// Http 1.0 header
		response.setDateHeader("Expires", 1L);
		response.addHeader("Pragma", "no-cache");
		// Http 1.1 header
		response.setHeader("Cache-Control", "no-cache, no-store, max-age=0");
	}

    /**
     * 设置LastModified Header.
     */
	public static void setLastModifiedHeader(HttpServletResponse response, long lastModifiedDate) {
		response.setDateHeader("Last-Modified", lastModifiedDate);
	}

    /**
     * 设置Etag Header.
     */
	public static void setEtag(HttpServletResponse response, String etag) {
		response.setHeader("ETag", etag);
	}

    /**
     * 根据浏览器If-Modified-Since Header, 计算文件是否已被修改.
     * 如果无修改, checkIfModify返回false ,设置304 not modify status.
     * @param lastModified 内容的最后修改时间.
     */
	public static boolean checkIfModifiedSince(HttpServletRequest request, HttpServletResponse response,
			long lastModified) {
		long ifModifiedSince = request.getDateHeader("If-Modified-Since");
		if ((ifModifiedSince != -1) && (lastModified < ifModifiedSince + 1000)) {
			response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
			return false;
		}
		return true;
	}

    /**
     * 根据浏览器 If-None-Match Header, 计算Etag是否已无效.
     * 如果Etag有效, checkIfNoneMatch返回false, 设置304 not modify status.
     * @param etag 内容的ETag.
     */
	public static boolean checkIfNoneMatchEtag(HttpServletRequest request, HttpServletResponse response, String etag) {
		String headerValue = request.getHeader("If-None-Match");
		if (headerValue != null) {
			boolean conditionSatisfied = false;
			if (!"*".equals(headerValue)) {
				StringTokenizer commaTokenizer = new StringTokenizer(headerValue, ",");

				while (!conditionSatisfied && commaTokenizer.hasMoreTokens()) {
					String currentToken = commaTokenizer.nextToken();
					if (currentToken.trim().equals(etag)) {
						conditionSatisfied = true;
					}
				}
			} else {
				conditionSatisfied = true;
			}

			if (conditionSatisfied) {
				response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
				response.setHeader("ETag", etag);
				return false;
			}
		}
		return true;
	}

    /**
     * 设置让浏览器弹出下载对话框的Header.
     * @param fileName 下载后的文件名.
     */
	public static void setFileDownloadHeader(HttpServletResponse response, String fileName) {
		try {
			// 中文文件名支持
			String encodedfileName = new String(fileName.getBytes(), "ISO8859-1");
			response.setHeader("Content-Disposition", "attachment; filename=\"" + encodedfileName + "\"");
		} catch (UnsupportedEncodingException e) {
			logger.error("ServletUtils.setFileDownloadHeader", e);
		}
	}

	/**
	 * 获取当前请求对象
	 */
	public static HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
	}

    /**
     * 获取项目名称路径
     */
	public static String getContentpath() {
		return getRequest().getContextPath();
	}

	/**
	 * 获取项目绝对路径
	 */
	public static String getRealPath() {
		return getRequest().getSession().getServletContext().getRealPath("/");
	}

	/**
	 * getAttribute这个方法是提取放置在某个共享区间的对象
	 * @param name
	 * @return
	 */
	public static Object getAttribute(String name) {
		return getRequest().getSession().getAttribute(name);
	}

	/**
	 * getParameter系列的方法主要用于处理“请求数据”，是服务器端程序获取浏览器所传递参数的主要接口。
	 * @param name 表单name属性
	 * @return
	 */
	public static String getParameter(String name) {
		return getRequest().getParameter(name);
	}

	/**
	 * getParameterValues这个方法是获得传过来的参数名相同的一个数组;
	 * @param name
	 * @return
	 */
	public static String[] getParameterValues(String name) {
		return getRequest().getParameterValues(name);
	}

	/**
	 * 获取当前网络ip
	 * @return
	 */
	public static String getIpAddr() {
		String ipAddress = getRequest().getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = getRequest().getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = getRequest().getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = getRequest().getRemoteAddr();
			if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					logger.error("ServletUtils.getIpAddr", e);
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15 && ipAddress.indexOf(',') > 0) {
			ipAddress = ipAddress.substring(0, ipAddress.indexOf(','));
		}
		return ipAddress;
	}

	public static String getIpAddr(HttpServletRequest request) {
		String ipAddress = request.getHeader("x-forwarded-for");
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
			ipAddress = request.getRemoteAddr();
			if ("127.0.0.1".equals(ipAddress) || "0:0:0:0:0:0:0:1".equals(ipAddress)) {
				// 根据网卡取本机配置的IP
				InetAddress inet = null;
				try {
					inet = InetAddress.getLocalHost();
				} catch (UnknownHostException e) {
					logger.error("ServletUtils.getIpAddr", e);
				}
				ipAddress = inet.getHostAddress();
			}
		}
		// 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
		if (ipAddress != null && ipAddress.length() > 15 && ipAddress.indexOf(',') > 0) {
			ipAddress = ipAddress.substring(0, ipAddress.indexOf(','));
		}
		return ipAddress;
	}

	/**
	 * 获取当前用户游览器信息
	 */
	public static String getHeader() {
		return getRequest().getHeader("User-Agent");
	}

	/**
	 * 获取系统版本信息
	 * @param request
	 * @return
	 */
	public static String getRequestSystemInfo(HttpServletRequest request){
		String systenInfo = null;
		String header = request.getHeader("user-agent");
		if(header == null || header.equals("")){
			return "";
		}
		//得到用户的操作系统
		if (header.indexOf("NT 6.0") > 0){
			systenInfo = "Windows Vista/Server 2008";
		} else if (header.indexOf("NT 5.2") > 0){
			systenInfo = "Windows Server 2003";
		} else if (header.indexOf("NT 5.1") > 0){
			systenInfo = "Windows XP";
		} else if (header.indexOf("NT 6.0") > 0){
			systenInfo = "Windows Vista";
		} else if (header.indexOf("NT 6.1") > 0){
			systenInfo = "Windows 7";
		} else if (header.indexOf("NT 6.2") > 0){
			systenInfo = "Windows Slate";
		} else if (header.indexOf("NT 6.3") > 0){
			systenInfo = "Windows 9";
		} else if (header.indexOf("NT 5") > 0){
			systenInfo = "Windows 2000";
		} else if (header.indexOf("NT 4") > 0){
			systenInfo = "Windows NT4";
		} else if (header.indexOf("Me") > 0){
			systenInfo = "Windows Me";
		} else if (header.indexOf("98") > 0){
			systenInfo = "Windows 98";
		} else if (header.indexOf("95") > 0){
			systenInfo = "Windows 95";
		} else if (header.indexOf("Mac") > 0){
			systenInfo = "Mac";
		} else if (header.indexOf("Unix") > 0){
			systenInfo = "UNIX";
		} else if (header.indexOf("Linux") > 0){
			systenInfo = "Linux";
		} else if (header.indexOf("SunOS") > 0){
			systenInfo = "SunOS";
		}
		return systenInfo;
	}

	/**
	 * 获取来访者的主机名称
	 * @param ip
	 * @return
	 */
	public static String getHostName(String ip){
		InetAddress inet;
		try {
			inet = InetAddress.getByName(ip);
			return inet.getHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return "";
	}


	/**
	 * 获取MAC地址
	 *
	 * @return 返回MAC地址
	 */
	public static String getMacAddress(String ip){
		String macAddress = "";
		macAddress = getMacInWindows(ip).trim();
		if(macAddress==null||"".equals(macAddress)){
			macAddress = getMacInLinux(ip).trim();
		}
		return macAddress;
	}

	/**
	 * 命令获取mac地址
	 * @param cmd
	 * @return
	 */
	private static String callCmd(String[] cmd) {
		String result = "";
		String line = "";
		try {
			Process proc = Runtime.getRuntime().exec(cmd);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);
			while ((line = br.readLine ()) != null) {
				result += line;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 *
	 *
	 *
	 * @param cmd
	 *            第一个命令
	 * @param another
	 *            第二个命令
	 * @return 第二个命令的执行结果
	 *
	 */

	private static String callCmd(String[] cmd, String[] another) {
		String result = "";
		String line = "";
		try {
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec(cmd);
			proc.waitFor(); // 已经执行完第一个命令，准备执行第二个命令
			proc = rt.exec(another);
			InputStreamReader is = new InputStreamReader(proc.getInputStream());
			BufferedReader br = new BufferedReader(is);
			while ((line = br.readLine ()) != null) {
				result += line;
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * @param ip
	 *            目标ip
	 * @return Mac Address
	 *
	 */

	private static String getMacInWindows(final String ip){
		String result = "";
		String[] cmd = {"cmd","/c","ping " + ip};
		String[] another = {"cmd","/c","arp -a"};
		String cmdResult = callCmd(cmd,another);
		result = filterMacAddress(ip,cmdResult,"-");
		return result;
	}
	/**
	 *
	 * @param ip
	 *            目标ip
	 * @return Mac Address
	 *
	 */
	private static String getMacInLinux(final String ip){
		String result = "";
		String[] cmd = {"/bin/sh","-c","ping " +  ip + " -c 2 && arp -a" };
		String cmdResult = callCmd(cmd);
		result = filterMacAddress(ip,cmdResult,":");
		return result;
	}

	/**
	 *
	 *
	 *
	 * @param ip
	 *            目标ip,一般在局域网内
	 *
	 * @param sourceString
	 *            命令处理的结果字符串
	 *
	 * @param macSeparator
	 *            mac分隔符号
	 *
	 * @return mac地址，用上面的分隔符号表示
	 *
	 */

	private static String filterMacAddress(final String ip, final String sourceString, final String macSeparator) {
		String result = "";
		String regExp = "((([0-9,A-F,a-f]{1,2}" + macSeparator + "){1,5})[0-9,A-F,a-f]{1,2})";
		Pattern pattern = Pattern.compile(regExp);
		Matcher matcher = pattern.matcher(sourceString);
		while(matcher.find()){
			result = matcher.group(1);
			if(sourceString.indexOf(ip) <= sourceString.lastIndexOf(matcher.group(1))) {
				break; // 如果有多个IP,只匹配本IP对应的Mac.
			}
		}
		return result;
	}

}
