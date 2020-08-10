package com.jjc.comm.common.util;


import com.github.junrar.Archive;
import com.github.junrar.rarfile.FileHeader;
import com.jjc.comm.common.exception.InvalidParamException;
import com.jjc.comm.common.exception.ServiceException;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.SocketException;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * FTP上传下载
 * @author huoquan
 * @date 2018/12/27.
 */
public class FtpUtil {
    private static Logger logger = LogManager.getLogger();
    private static final byte[] LOCK = { 0 };
    private static FTPClient ftpClient = null;
    private static final String FILELOCK = "Token.lock";

    public static void main(String[] args) throws Exception {

        logger.info( new Date() +"===============*******");
        uploadServerByFtp();
//        String host = "192.168.1.126";
//        int port = 21;
//        String username = "rd";
//        String password = "rd.123";
//        String localUpPath = "C:\\Users\\Administrator\\Desktop\\apk\\222.apk";
//        String remotePath = "mcRoot";
//        FtpUtil ftpClient = new FtpUtil(host, port, username, password,remotePath);
//
//        // FTP上传文件
//        File file = new File(localUpPath);
//        boolean delflag =  ftpClient.removeDir("test");
//
//        // ftpClient.removeFile("test3");
//        ftpClient.uploadfiles(file);
//        // FTP下载文件
//        //ftpClient.downLoadFile(remotePath, localDnPath);
//
//        ftpClient.close();
        logger.info( new Date() +"===============*******");
    }

    //FTP传输到数据库服务器
    public static boolean uploadServerByFtp(){
        boolean flag = true;
        //客户端数据文件路径
        String client_path = "C:\\Users\\Administrator\\Desktop\\apk\\222.apk";
        //服务器上的存放数据文件路径
        String server_path = "/mcRoot/";
        String hostname = "192.168.1.126";
        String ftpusername =  "rd";
        String ftppwd = "rd.123";
        int port = 21;//查找路径下的指定txt文件，然后采用FTP上传
        File file_name = new File(client_path);
        if(!file_name.exists()){
        return false;
        }
        //创建ftp客户端
        FTPClient ftpClient = new FTPClient();
        ftpClient.setControlEncoding("utf-8");
        //主动模式
        ftpClient.enterLocalActiveMode();
        String getfileName = file_name.getName();
        String getfileNamePath = file_name.getPath();
//        if((getfileName.substring(getfileName.lastIndexOf(".")).trim().equals(".txt"))){
        try {
                //链接ftp服务器
                ftpClient.connect(hostname, port);
                //登录ftp
                ftpClient.login(ftpusername, ftppwd);
                int reply = ftpClient.getReplyCode();
                if (!FTPReply.isPositiveCompletion(reply)) {
                ftpClient.disconnect();
                    logger.info("Returns a 530 password username error or the current user does not have permission to close the FTP connection");
                    return false;
                }
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.setBufferSize(1024*1024);
                String server_file_name = server_path+ getfileName;

                InputStream input = new FileInputStream(getfileNamePath);
                OutputStream out = ftpClient.storeFileStream(server_file_name);
                byte[] byteArray = new byte[4096];
                int read = 0;
                while ((read = input.read(byteArray)) != -1) {
                out.write(byteArray, 0, read);
                }
                out.close();
                ftpClient.logout();
            } catch (SocketException e) {
                flag = false;
                e.printStackTrace();
            } catch (IOException e) {
                flag = false;
                e.printStackTrace();
            } catch (Exception e) {
                flag = false;
                e.printStackTrace();
            }finally {
        if (ftpClient.isConnected()) {
            try {
                    ftpClient.disconnect();
                    } catch (IOException ioe) {
                        ioe.printStackTrace();
                    }
                }
            }
//        }
        return flag;
    }

    public FtpUtil() {

    }

    /**
     * 初始化
     *
     * @param host IP
     * @param port 端口
     * @param username 用户名
     * @param password 密码
     * @throws InvalidParamException e
     */
    public FtpUtil(String host, int port, String username, String password, String root) throws InvalidParamException {
        init(host, port, username, password, root);
    }

    /**
     * 获取FTP连接
     * @param host
     * @param port
     * @param username
     * @param password
     * @throws InvalidParamException
     */
    private void init(String host, int port, String username, String password, String root) throws InvalidParamException {
        synchronized (LOCK) {
            if (ftpClient == null) {
                ftpClient = new FTPClient();
                ftpClient.setBufferSize(1024*1024);
            }
            try {
                ftpClient.connect(host, port);// 连接FTP服务器
            } catch (Exception e) {
                logger.error("FTP[" + host + ":" + port + "]连接失败!", e);
                throw new InvalidParamException("FTP[" + host + ":" + port + "]连接失败!");
            }
            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            if (FTPReply.isPositiveCompletion(ftpClient.getReplyCode())) {
                try {
                    ftpClient.login(username, password);
                } catch (Exception e) {
                    logger.error("FTP用户[" + username + "]登陆失败!", e);
                    throw new InvalidParamException("FTP用户[" + username + "]登陆失败!");
                }
            } else {
                logger.error("FTP连接出错");
                throw new InvalidParamException("FTP连接出错!");
            }
            logger.info("用户[" + username + "]登陆[" + host + "]成功.");
//            properties.setProperty("userName", username);
//            properties.setProperty("hostName", host);
            try {
                //设置根目录
                if(ToolUtil.isNotEmpty(root)){
                    String[] pathes = root.split("/");
                    for (String onepath : pathes) {
                        if (onepath == null || "".equals(onepath.trim())) {
                            continue;
                        }
                        onepath = new String(onepath.getBytes("gbk"),
                                "iso-8859-1");
                        if (!ftpClient.changeWorkingDirectory(onepath)) {
                            ftpClient.makeDirectory(onepath);// 创建FTP服务器目录
                            ftpClient.changeWorkingDirectory(onepath);// 改变FTP服务器目录
                        }
                    }
                }
                // 设置被动模式
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileTransferMode(FTPClient.STREAM_TRANSFER_MODE);
                ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
            } catch (Exception e) {
                logger.error("FTP初始化出错", e);
                throw new InvalidParamException("FTP初始化出错!");
            }
        }
    }

    /**
     * 关闭FTP客户端
     * @throws InvalidParamException e
     */
    public void close() throws InvalidParamException {
        synchronized (LOCK) {
            try {
                ftpClient.logout();
                ftpClient.disconnect();
            } catch (IOException e) {
                logger.error("FTP退出登录出错", e);
                ftpClient = null;
                throw new InvalidParamException("FTP退出登录出错!");
            }
            logger.info(
                    "用户退出登录");
        }
    }

    /**
     * 上传
     *
     * @param remotePath 上传目录
     * @param localPath 本地目录
     * @return boolean
     * @throws InvalidParamException e
     */
    public boolean uploadFile(String localPath, String remotePath) throws InvalidParamException {
        synchronized (LOCK) {
            File file = new File(localPath);
//            File[] files = file.listFiles();
//            for (int i = 0; i < files.length; i++) {
//                if (!uploadFiles(files[i], remotePath)) {
//                    return false;
//                }
//            }
//            return files.length > 0;
            return uploadFile(file,remotePath);
        }
    }

    /**
     * 上传单个文件
     * @param localeFile
     * @param remotePath
     * @return
     * @throws InvalidParamException
     */
    public boolean uploadFile(File localeFile, String remotePath) throws InvalidParamException {
        synchronized (LOCK) {
            FileInputStream fis = null;
            try {
                if (remotePath != null && !"".equals(remotePath.trim())) {
                    String[] pathes = remotePath.split("/");
                    for (String onepath : pathes) {
                        if (onepath == null || "".equals(onepath.trim())) {
                            continue;
                        }
                        onepath = new String(onepath.getBytes("gbk"),
                                "iso-8859-1");
                        if (!ftpClient.changeWorkingDirectory(onepath)) {
                            ftpClient.makeDirectory(onepath);// 创建FTP服务器目录
                            ftpClient.changeWorkingDirectory(onepath);// 改变FTP服务器目录
                        }
                    }
                }
                fis = new FileInputStream(localeFile);
                Boolean result=ftpClient.storeFile(new String(localeFile.getName().getBytes("gbk"),
                        "iso-8859-1"), fis);
                if(result){
                    logger.info("[" + localeFile.getAbsolutePath() + "]上传成功!");
                }
                return result;
            } catch (IOException e) {
                logger.error("FTP上传[" + localeFile.getAbsolutePath() + "]出错!", e);
                throw new InvalidParamException("FTP上传[" + localeFile.getAbsolutePath() + "]出错!");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    }

    /**
     * 上传文件
     * @param bytes
     * @param remotePath
     * @param filName
     * @return
     * @throws InvalidParamException
     */
    public boolean uploadFile(byte[] bytes, String remotePath, String filName) throws InvalidParamException {
        synchronized (LOCK) {
            InputStream fis = null;
            try {
                if (remotePath != null && !"".equals(remotePath.trim())) {
                    String[] pathes = remotePath.split("/");
                    for (String onepath : pathes) {
                        if (onepath == null || "".equals(onepath.trim())) {
                            continue;
                        }
                        onepath = new String(onepath.getBytes("gbk"),
                                "iso-8859-1");
                        if (!ftpClient.changeWorkingDirectory(onepath)) {
                            ftpClient.makeDirectory(onepath);// 创建FTP服务器目录
                            ftpClient.changeWorkingDirectory(onepath);// 改变FTP服务器目录
                        }
                    }
                }
                fis=new ByteArrayInputStream(bytes);
                Boolean result=ftpClient.storeFile(new String(filName.getBytes("gbk"),
                        "iso-8859-1"), fis);
                if(result){
                    logger.info("[" + filName + "]上传成功!");
                }
                return result;
            } catch (IOException e) {
                logger.error("FTP上传[" + filName + "]出错!", e);
                throw new InvalidParamException("FTP上传[" + filName + "]出错!");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {

                    }
                }
            }
        }
    }

    /**
     * 递归上传文件
     *
     * @param localeDirectory 本地目录
     * @param remotePath 上传目录
     * @return boolean
     * @throws InvalidParamException e
     */
    public boolean uploadFiles(String localeDirectory, String remotePath) throws InvalidParamException {
        synchronized (LOCK) {
            FileInputStream fis = null;
            try {
                File localeFile = new File(localeDirectory);
                if (localeFile.isDirectory()) {
                    if (remotePath != null && !"".equals(remotePath.trim())) {
                        String[] pathes = remotePath.split("/");
                        for (String onepath : pathes) {
                            if (onepath == null || "".equals(onepath.trim())) {
                                continue;
                            }
                            onepath = new String(onepath.getBytes("gbk"),
                                    "iso-8859-1");
                            if (!ftpClient.changeWorkingDirectory(onepath)) {
                                ftpClient.makeDirectory(onepath);// 创建FTP服务器目录
                                ftpClient.changeWorkingDirectory(onepath);// 改变FTP服务器目录
                            }
                        }
                    }
                    boolean flag = false;
                    File[] files = localeFile.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        fis = new FileInputStream(files[i]);
                        flag=ftpClient.storeFile(new String(files[i].getName().getBytes("gbk"),
                                "iso-8859-1"), fis);
                        if(flag){
                            logger.info("[" + files[i].getAbsolutePath() + "]上传成功!");
                        }
                    }
                    return flag;
                } else{
                    return uploadFile(localeFile,remotePath);
                }
            } catch (Exception e) {
                logger.error("FTP上传[" + localeDirectory + "]出错!", e);
                throw new InvalidParamException("FTP上传[" + localeDirectory + "]出错!");
            } finally {
                if (fis != null) {
                    try {
                        fis.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    }

    /**
     *
     * @param file 上传的文件或文件夹
     * @throws Exception
     */
    public void uploadfiles(File file) throws Exception {
        synchronized (LOCK) {
            if(file.isDirectory()){
                //特殊处理，temp_code开头的文件夹不生成
                if(!"temp_code".equals(file.getName())){
                    ftpClient.makeDirectory(file.getName());
                    ftpClient.changeWorkingDirectory(file.getName());
                }
                String[] files = file.list();
                for (int i = 0; i < files.length; i++) {
                    File file1 = new File(file.getPath()+"\\"+files[i] );
                    if(file1.isDirectory()){
                        uploadfiles(file1);
                        ftpClient.changeToParentDirectory();
                    }else{
                        File file2 = new File(file.getPath()+"\\"+files[i]);
                        FileInputStream input = new FileInputStream(file2);
                        ftpClient.storeFile(file2.getName(), input);
                        logger.info("[" + file2.getAbsolutePath() + "]上传成功!");
                        input.close();
                    }
                }
            }else{
                File file2 = new File(file.getPath());
                FileInputStream input = new FileInputStream(file2);
                ftpClient.storeFile(file2.getName(), input);
                logger.info("[" + file2.getAbsolutePath() + "]上传成功!");
                input.close();
            }
        }
    }

    /**
     * 下载
     *
     * @param remotePath 下载目录
     * @param localPath 本地目录
     * @return boolean
     * @throws InvalidParamException e
     */
    public boolean downLoadFile(String remotePath, String localPath) throws InvalidParamException {
        synchronized (LOCK) {
            try {
                if (ftpClient.changeWorkingDirectory(remotePath)) {// 转移到FTP服务器目录
                    FTPFile[] files = ftpClient.listFiles();
                    if (files.length > 0) {
                        File localdir = new File(localPath);
                        if (!localdir.exists()) {
                            localdir.mkdir();
                        }
                    }
                    for (FTPFile ff : files) {
                        if (!downLoadFile(ff, localPath)) {
                            return false;
                        }
                    }
                    return files.length > 0;
                }
            } catch (IOException e) {
                logger.error("FTP下载[" + localPath + "]出错!", e);
                throw new InvalidParamException("FTP下载[" + localPath + "]出错!");
            }
            return false;
        }
    }

    /**
     * 递归下载文件
     *
     * @param ftpFile 下载文件/目录
     * @param localPath 本地目录
     * @return boolean
     */
    public boolean downLoadFile(FTPFile ftpFile, String localPath) {
        // 当前处理文件本地路径
        String fileLocalPath = localPath + "/" + ftpFile.getName();
        if (ftpFile.isFile()) {// down file
            if (ftpFile.getName().indexOf("?") == -1) {
                OutputStream outputStream = null;
                try {
                    File localFile = new File(fileLocalPath);
                    if (!localFile.getParentFile().exists()) {
                        localFile.getParentFile().mkdirs();
                    }
                    outputStream = new FileOutputStream(localFile);
                    ftpClient.retrieveFile(ftpFile.getName(), outputStream);
                    outputStream.flush();
                    outputStream.close();
                    logger.info("[" + localFile.getAbsolutePath() + "]下载成功!");
                    return true;
                } catch (Exception e) {
                    logger.error("FTP下载[" + fileLocalPath + "]出错!", e);
                    throw new InvalidParamException("FTP下载[" + fileLocalPath + "]出错!");
                } finally {
                    try {
                        if (outputStream != null){
                            outputStream.close();
                        }
                    } catch (IOException e) {

                    }
                }
            }
        } else { // deal dirctory
            File file = new File(fileLocalPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            try {
                // enter relative workdirectory
                if (ftpClient.changeWorkingDirectory(ftpFile.getName())) {
                    logger.info("[" + file.getAbsolutePath() + "]目录");
                    FTPFile[] files = null;
                    files = ftpClient.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        downLoadFile(files[i], fileLocalPath);
                    }
                    ftpClient.changeToParentDirectory();// return parent
                    // directory
                    return true;
                }
            } catch (Exception e) {
                logger.error("FTP下载[" + fileLocalPath + "]出错!", e);
                throw new InvalidParamException("FTP下载[" + fileLocalPath + "]出错!");
            }
        }
        return false;
    }

    /** 获得目录下最大文件名 */
    public String getMaxFileName(String remotePath) {
        try {
            ftpClient.changeWorkingDirectory(remotePath);
            FTPFile[] files = ftpClient.listFiles();
            Arrays.sort(files, new Comparator<FTPFile>() {
                public int compare(FTPFile o1, FTPFile o2) {
                    return o2.getName().compareTo(o1.getName());
                }
            });
            return files[0].getName();
        } catch (IOException e) {
            logger.error("FTP访问目录[" + remotePath + "]出错!", e);
            throw new InvalidParamException("FTP访问目录[" + remotePath + "]出错!");
        }
    }

    /**
     * 递归删除文件及文件夹
     * @author tangw 2010-12-26
     * @param dir
     * @return true || false
     */
    public boolean removeDir(String dir){
        FTPFile[] files = new FTPFile[0];
        try {
            if(ToolUtil.isEmpty(dir)){
                return false;
            }
            if(!ftpClient.changeWorkingDirectory(dir)){
                return false;
            }
            files = ftpClient.listFiles();
            for (FTPFile file : files) {
                if (file.isFile()) {
                    ftpClient.deleteFile(file.getName());
                } else if (file.isDirectory()) {
                    boolean del = removeDir(file.getName());
                    if(!del){
                        return false;
                    }
                }
            }
            ftpClient.changeToParentDirectory();
            boolean del = ftpClient.removeDirectory(dir);
            if(!del){
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }


    /**
     * 连接参数
     *
     * @param properties <br>
     *            FTPHOSTNAME:IP; FTPPORT:端口; FTPUSERNAME:用户名; FTPPASSWORD:密码
     */
//    public void setProperties(Properties properties) {
//        this.properties = properties;
//    }

    /**
     * 连接参数
     *
     *            FTPHOSTNAME:IP; FTPPORT:端口; FTPUSERNAME:用户名; FTPPASSWORD:密码
     */
//    public void setProperties(Map<String, String> properties) {
//        this.properties = new Properties();
//        String[] key = { "FTPHOSTNAME", "FTPPORT", "FTPUSERNAME", "FTPPASSWORD" };
//        for (int i = 0; i < key.length; i++) {
//            this.properties.put(key[i], properties.get(key[i]));
//        }
//    }

    public boolean uploadMap(byte[] bytes, String orgId, String ext){
        // 临时目录
        String filePath =  ToolUtil.getUUID();
        // 压缩包
        String fileName = "map."+ext;
        // 输出目录
        String zipOutDir = orgId;
        FileUtils.saveFile(bytes,filePath,fileName);
        File file = new File(filePath+"/"+fileName);
        try {
            // 删除ftp目录
            if(!removeDir(zipOutDir)){
                // throw new Exception("删除ftp目录失败");
            }
            if (ext.equals("rar")) {
               unRar(file,filePath+"/"+zipOutDir);
            } else if (ext.equals("zip")){
                unZip(file,filePath+"/"+zipOutDir);
            }
            // 上传
            uploadfiles(new File(filePath+"/"+zipOutDir));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }finally {
            // 删除本地文件
            FileUtils.deleteDirectory(filePath);
        }
        return true;
    }

    // 解压zip
    public static boolean unZip(File zipFile, String outDir) throws IOException {
        Boolean code = false;
        File outFileDir = new File(outDir);
        if (!outFileDir.exists()) {
                boolean isMakDir = outFileDir.mkdirs();
        }
        ZipFile zip = new ZipFile(zipFile);
        try {
            for (Enumeration enumeration = zip.entries(); enumeration.hasMoreElements(); ) {
                ZipEntry entry = (ZipEntry) enumeration.nextElement();
                String zipEntryName = entry.getName();
                InputStream in = zip.getInputStream(entry);

                if (entry.isDirectory()) {      //处理压缩文件包含文件夹的情况
                    File fileDir = new File(outDir +"/"+ zipEntryName);
                    fileDir.mkdir();
                    continue;
                }
                File file = new File(outDir, zipEntryName);
                file.createNewFile();
                OutputStream out = new FileOutputStream(file);
                byte[] buff = new byte[1024];
                int len;
                while ((len = in.read(buff)) > 0) {
                    out.write(buff, 0, len);
                }
                in.close();
                out.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            zip.close();
        }
        return code;
    }
    // 解压rar
    public static boolean unRar(File rarFile, String outDir) throws Exception {
        File outFileDir = new File(outDir);
        if (!outFileDir.exists()) {
            outFileDir.mkdirs();
        }
        Archive archive = new Archive(rarFile);
        try {
            FileHeader fileHeader = archive.nextFileHeader();
            if(fileHeader == null){
//                throw new Exception("解压异常");
                throw new ServiceException("解压异常");
            }
            while (fileHeader != null) {
                if (fileHeader.isDirectory()) {
                    fileHeader = archive.nextFileHeader();
                    continue;
                }
                File out = new File(outDir +"/"+ fileHeader.getFileNameString());
                if (!out.exists()) {
                    if (!out.getParentFile().exists()) {
                        out.getParentFile().mkdirs();
                    }
                    out.createNewFile();
                }
                FileOutputStream os = new FileOutputStream(out);
                archive.extractFile(fileHeader, os);
                os.close();
                fileHeader = archive.nextFileHeader();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            archive.close();
        }
        return true;
    }

    /**
     * 递归遍历目录下面指定的文件名
     *
     * @param pathName 需要遍历的目录，必须以"/"开始和结束
     * @param ext      文件的扩展名
     * @throws IOException
     */
    public static List<FTPFile> getList(String pathName, String ext) throws IOException {
        List<FTPFile> fTPFiles =new ArrayList<>();
        if (pathName.startsWith("/") && pathName.endsWith("/")) {
            //更换目录到当前目录
            ftpClient.changeWorkingDirectory(pathName);
            FTPFile[] files = ftpClient.listFiles();
            for (FTPFile file : files) {
                if (file!=null&&file.getSize()>0) {

                    int lastIndexOf = file.getName().lastIndexOf(".");
                    //获取文件的后缀名 .jpg
                    String suffix = file.getName().substring(lastIndexOf+1);
                    if (ToolUtil.isNotEmpty(ext) && ext.equals(suffix)) {
                        fTPFiles.add(file);
                    }
                }
            }
        }
        return fTPFiles;
    }
}
