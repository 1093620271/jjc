package com.jjc.comm.common.util.serialUtils; /**
 * createtime : 2018年6月1日 上午9:47:36
 */

import com.alibaba.fastjson.JSON;

import com.jjc.comm.common.sys.Result;
import com.jjc.comm.common.util.SerializeUtil;
import gnu.io.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO
 * @author XWF
 */
public final class RXTXtest {
    public static final Logger LOGGER = LoggerFactory.getLogger(RXTXtest.class);
    SerialPort  serialPort = null;
    // 缓存原始数据
    List<SerialCommand> CommandBuffer = new ArrayList<SerialCommand>();
    // 原始数据锁
    final Object CommandBuffer_Lock = new Object();
    InputStream in;
    OutputStream out;

    // 串口号
    String port = "";
    // 最大发送字节
    int maxNum = 4000;
    // 发送最小延时
    int sendDelay = 100;

    // 数据处理回调
    CmdHandler cmdHandler = null;
    // 数据处理线程池
    ExecutorService executor = null;
    // 数据回调
    public interface CmdHandler{
        Object invoke(SerialCommand serialCommand);
    }

    /**
     * 测试使用
     * @param args
     */
    public static void main(String[] args){
        RXTXtest server =  new RXTXtest("COM3",9600,4000,100 );
        // SerialCommand command = new SerialCommand("testData");
        //启动一个线程每2s向串口发送数据，发送1000次hello
        new Thread(new Runnable() {
            @Override
            public void run() {
                int i = 1;
                while(i<1000) {
                    HttpCommand httpCommand = new HttpCommand();
                    httpCommand.url = "url";
                    httpCommand.data = "dtat";
                    SerialCommand  command = new SerialCommand( httpCommand.toJson());
                    server.sendCommand(command);//发送数据
                    i++;
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();

        RXTXtest server2 =   new RXTXtest("COM4", 9600,4000,100 ,new CmdHandler() {
            @Override
            public Result invoke(SerialCommand httpCommand) {
                Result result = new Result();
                result.setMsg("哈哈哈哈哈哈哈哈哈哈哈哈哈哈");
                return result;
            }
        });

        //        closeSerialPort(serialPort);
    }


    // 缓存原始数据
    LinkedList<Byte> DataBuffer = new LinkedList<Byte>();
    // 原始数据锁
    final Object DataBuffer_Lock = new Object();
    // 原始数据写入缓存
    private void setPackage(byte[] datas){
        if(datas == null){
            return ;
        }
        synchronized (DataBuffer_Lock){
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < datas.length; i++)
            {
                DataBuffer.add(datas[i]);
            }
        }
    }

    /**
     *  一下参数用于防止包丢失造成的数据一直异常
     */
    // 最大丢失毫秒数
    int maxLossTimes = 5*1000;
    long befLossTime = 0;
    // 当前缓存长度
    long currentBuffLen = 0;
    // 之前缓存长度
    long befBuffLen =0;

    // 从原始数据获取一个包
    private SerialCommand getStrPackag(){
        String DU_START_FLAG = SerialCommand.DU_START_FLAG;
        String DU_END_FLAG = SerialCommand.DU_END_FLAG;
        synchronized (DataBuffer_Lock){

            // 数据不足
            if(DataBuffer.size()<=DU_START_FLAG.length()){
                return null;
            }
            // 头标识没用 数据异常
            for(int i=0;i<DU_START_FLAG.length();i++){
                if(DataBuffer.get(i)!=DU_START_FLAG.getBytes()[i]){
                    DataBuffer.clear();
                    return null;
                }
            }

            // 长度不足 数据不足
            if(DataBuffer.size()<=SerialCommand.ALEN_MAX + SerialCommand.JSON_BEFLEN){
                return null;
            }
            byte[] strLen = new byte[SerialCommand.ALEN_MAX];
            for(int i=0;i<SerialCommand.ALEN_MAX ;i++){
                strLen[i] = DataBuffer.get(i+SerialCommand.JSON_BEFLEN);
            }
            int dataLessLen = Integer.parseInt(new String(strLen));
            // 数据不足
            if(DataBuffer.size()<dataLessLen){
                // 当前数量
                currentBuffLen = DataBuffer.size();
                if(currentBuffLen != befBuffLen){
                    befBuffLen = currentBuffLen;
                    befLossTime =  System.currentTimeMillis();
                }else{
                   long tm = System.currentTimeMillis();
                    // 丢失
                    if(tm>=maxLossTimes + befLossTime){
                        DataBuffer.clear();
                        return null;
                    }
                }
                return null;
            }

            byte[] old = new byte[DataBuffer.size()];
            /*for (int i = 0; i < DataBuffer.size(); i++)
            {
                  old[i] =  DataBuffer.get(i);
            }*/
            {
                int i = 0;
                Iterator iter = DataBuffer.iterator();//采用Iterator迭代器进行数据的操作
                while (iter.hasNext()) {
                    old[i++] = (byte) iter.next();
                }
            }

            if(DataBuffer.size()==0){
                return null;
            }
            String str = new String(old);
            int startPos = str.indexOf(DU_START_FLAG);
            int endPos = str.indexOf(DU_END_FLAG);
            // 数据异常
            if(endPos==-1 || startPos==-1){
                DataBuffer.clear();
                return null;
            }
            // 数据不足
            if(startPos == -1 && DataBuffer.size()<=DU_START_FLAG.length()){
                return null;
            }
            // 数据完全
            else if(startPos ==0 && endPos!=-1){
                byte[] raw =str.substring(startPos ,endPos + DU_END_FLAG.length()).getBytes();
                for(int i=0;i<raw.length;i++){
                    DataBuffer.remove(0);
                }
                String pack = str.substring(startPos + DU_START_FLAG.length() ,endPos);
                SerialCommand result =   JSON.parseObject(pack,SerialCommand.class);
                return result;
            }
            // 错误数据
            else if(startPos == -1 ){
                DataBuffer.clear();
            }
        }
        return null;
    }

    // 从原始数据获取一个包
    private SerialCommand getRawPackage(){
        String DU_START_FLAG = SerialCommand.DU_START_FLAG;
        String DU_END_FLAG = SerialCommand.DU_END_FLAG;
        synchronized (DataBuffer_Lock){
            byte[] old = new byte[DataBuffer.size()];
            for (int i = 0; i < DataBuffer.size(); i++)
            {
                old[i] = DataBuffer.get(i);
            }
            if(DataBuffer.size()==0){
                return null;
            }
            int startPos = getStartPos(old,0);
            int endPos =  -1;
            boolean findEnd = false;
            if(old.length >=DU_START_FLAG.length() +  DU_END_FLAG.length()){
                byte[] endBytes = DU_END_FLAG.getBytes();
                for(int i=DU_START_FLAG.length();i<old.length - DU_END_FLAG.length()+1 ;i++){
                    int j=0;
                    for(;j<endBytes.length;j++){
                        if(old[i+j] != endBytes[j]){
                                break;
                        }
                    }
                    if(j == endBytes.length){
                        findEnd = true;
                        endPos = i;
                    }
                }
            }
            // 数据不足
            if(startPos == -1 && DataBuffer.size()<=DU_START_FLAG.length()){
                return null;
            }
            // 数据完全
            else if(startPos ==0 && endPos!=-1){
                for(int i=0;i<endPos+DU_END_FLAG.length();i++){
                    DataBuffer.remove(0);
                }
                byte[] useDatas = new byte[endPos-DU_START_FLAG.length()];
                System.arraycopy(old, startPos + DU_START_FLAG.length(), useDatas, 0, useDatas.length);
                SerialCommand result = (SerialCommand) SerializeUtil.deserialize(useDatas);
                return result;
            }
            // 错误数据
            else if(startPos == -1 ){
                DataBuffer.clear();
            }
        }
        return null;
    }

    private SerialCommand getPackage(){
         SerialCommand command =  null;
        if(SerialCommand.DATA_TYPE_BYTE){
            return command = getRawPackage();
        }else{
            return command = getStrPackag();
        }
    }

    private int getStartPos(byte[] old,int startPost){
        String DU_START_FLAG = SerialCommand.DU_START_FLAG;
        String DU_END_FLAG = SerialCommand.DU_END_FLAG;
        if(old.length >=DU_START_FLAG.length() ){
            int i=startPost;
            for(;i<DU_START_FLAG.length() ;i++){
                if(old[i] != DU_START_FLAG.getBytes()[i]){
                    break;
                }
            }
            if(i == DU_START_FLAG.length()){
                return 0;
            }
        }
        return -1;
    }

    /**
     * 初始化
     */
    public void init(String port, int rate) {
        //获得系统端口列表
        getSystemPort();
        //开启端口COM2，波特率9600
        serialPort = openSerialPort(port,rate);
        if(serialPort == null){
            LOGGER.info("打开串口："+port+"失败");
        }else{
            LOGGER.info("打开串口："+port+"成功");
        }
/*
        try {
            in = serialPort.getInputStream();
            out = serialPort.getOutputStream();

        } catch (IOException e) {
            e.printStackTrace();
        }*/

        // 一直读取
       /* new Thread(new Runnable() {
            @Override
            public void run() {
                readDataNoWait();
            }
        }).start();*/

        //
        //设置串口的listener
        setListenerToSerialPort(serialPort, new SerialPortEventListener() {
            @Override
            public void serialEvent(SerialPortEvent arg0) {
                if(arg0.getEventType() == SerialPortEvent.DATA_AVAILABLE) {//数据通知
                    byte[] bytes = readData();
                }
            }
        });



        executor = Executors.newFixedThreadPool(5);
        // 获取包
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(10);
                        final  SerialCommand command =  getPackage();
                        if(command!=null){
                            // 默认处理 [接收回复]
                            setCommand(command);
                            // 特殊处理 [接收处理]
                            if(cmdHandler!=null){
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Object result = cmdHandler.invoke(command);
                                        if(result !=null ){
                                            /*command.getData= result.toString();//JSON.toJSONString(result);
                                            sendData(command.gSend());*/
                                            if(SerialCommand.DATA_TYPE_BYTE){
                                                command.byteSendData= (byte[]) result;//JSON.toJSONString(result);
                                            }else{
                                                command.getData=  result.toString();//JSON.toJSONString(result);
                                            }
                                            sendData(command.gSend());
                                        }
                                    }
                                });
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
//        closeSerialPort(serialPort);
    }



    /**
     * 构造函数
     * @param port
     * @param rate
     */
    public RXTXtest(String port, int rate, int maxNum, int sendDelay) {
        this.port = port;
        this.maxNum = maxNum;
        this.sendDelay = sendDelay;
        init(port,rate);
    }

    /**
     * 构造函数
     * @param port
     * @param rate
     * @param cmdHandler
     */
    public RXTXtest(String port, int rate, int maxNum, int sendDelay, CmdHandler cmdHandler) {
        this.cmdHandler = cmdHandler;
        this.port = port;
        this.maxNum = maxNum;
        this.sendDelay = sendDelay;
        init(port,rate);
    }


    // 发送并获取一个命令
    public void sendCommand(SerialCommand command){
        long startTime = System.currentTimeMillis();
        addCommand(command);
        readTimes = 0;
        sendTimes = 0;
        while(true){
            try {
                Thread.sleep(10);
                long currentTime = System.currentTimeMillis();
                // 超时直接返回
                if(currentTime > startTime+ command.maxTimes){
                    removeCommand(command);
                    LOGGER.error("命令超时:" + new String(command.gSend()));
                    return ;
                }
                // 结束直接返回
                if(isFinalCmd(command.cuuid)){
                    removeCommand(command);
                    LOGGER.info("命令完成:" + new String(command.gSend()));
                    return ;
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    // 判断命令是否完成
    private boolean isFinalCmd(String cuuid){
        synchronized (CommandBuffer_Lock){
            for(int i=0;i<CommandBuffer.size();i++){
                SerialCommand node = CommandBuffer.get(i);
                if(node.cuuid.equals(cuuid) && node.getFlag == SerialCommand.END_FLAG){
                    return true;
                }
            }
        }
        return false;
    }
    // 添加一个命令
    private void addCommand(SerialCommand command){
        synchronized (CommandBuffer_Lock){
            CommandBuffer.add(command);
            sendData(command.gSend());
            //sendData(command.gRawSend());
            command.sendFlag = SerialCommand.END_FLAG;
        }
    }
    // 移除一个命令
    private void removeCommand(SerialCommand command){
        synchronized (CommandBuffer_Lock){
            CommandBuffer.remove(command);
        }
    }
    // 设置一个命令
    private void setCommand(SerialCommand responseCmd){
        synchronized (CommandBuffer_Lock){
            for(int i=0;i<CommandBuffer.size();i++){
                SerialCommand node = CommandBuffer.get(i);
                if(node.cuuid.equals(responseCmd.cuuid)){
                    if(SerialCommand.DATA_TYPE_BYTE){
                        node.byteSendData = responseCmd.byteSendData;
                    }else{
                        node.getData = responseCmd.getData;
                    }
                    node.getFlag = SerialCommand.END_FLAG;
                    break;
                }
            }
        }
    }


    /**
     * 获得系统可用的端口名称列表
     * @return 可用端口名称列表
     */
    @SuppressWarnings("unchecked")
    public List<String> getSystemPort(){
        List<String> systemPorts = new ArrayList<>();
        //获得系统可用的端口
        Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
        while(portList.hasMoreElements()) {
            String portName = portList.nextElement().getName();//获得端口的名字
            systemPorts.add(portName);
        }
        return systemPorts;
    }

    /**
     * 开启串口
     * @param serialPortName 串口名称
     * @param baudRate 波特率
     * @return 串口对象
     */
    public   SerialPort openSerialPort(String serialPortName, int baudRate) {
        try {
            //通过端口名称得到端口
            CommPortIdentifier portIdentifier = CommPortIdentifier.getPortIdentifier(serialPortName);
            //打开端口，（自定义名字，打开超时时间）
            CommPort commPort = portIdentifier.open(serialPortName, 2222);
            /*commPort.setInputBufferSize(20000);
            commPort.setOutputBufferSize(20000);*/
            //判断是不是串口
            if (commPort instanceof SerialPort) {
                SerialPort serialPort = (SerialPort) commPort;
                /* int kk = serialPort.getInputBufferSize();
                int ew = serialPort.getOutputBufferSize();
                serialPort.setInputBufferSize(20000);
                serialPort.setOutputBufferSize(20000);*/
                //设置串口参数（波特率，数据位8，停止位1，校验位无）
                serialPort.setSerialPortParams(baudRate, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);


                return serialPort;
            }
            else {
                //是其他类型的端口
                throw new NoSuchPortException();
            }
        } catch (NoSuchPortException e) {
            e.printStackTrace();
        } catch (PortInUseException e) {
            e.printStackTrace();
        } catch (UnsupportedCommOperationException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭串口
     */
    public void closeSerialPort() {
        if(serialPort != null) {
            serialPort.close();
            serialPort = null;
        }
    }

    // 写锁
    final Object send_lock = new Object();
    /**
     * 向串口发送数据
     * @param data 发送的数据
     */
   /* private void sendData(byte[] data) {
        OutputStream os = null;
        synchronized (send_lock){
            try {
                os = serialPort.getOutputStream();//获得串口的输出流
                int buffLen = this.maxNum; // 串口最大缓存
                byte[] buffer = new byte[0];
                // 发送完整的
                int nums = data.length/buffLen;
                int yu = data.length%buffLen;
                boolean befFlag = false;
                for(int i=0;i<nums;i++){
                    buffer = new byte[buffLen];
                    for(int j=0;j<buffLen;j++){
                        buffer[j] = data[i*buffLen+j];
                    }
                    //os.write(data);
                    //os.flush();
                    sendDataOne(buffer);
                    try {
                        if(this.sendDelay>0 && befFlag){
                            Thread.sleep(this.sendDelay);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    befFlag = true;
                }
                if(yu > 0){
                    buffer = new byte[yu];
                    for(int j=0;j<yu;j++){
                        buffer[j] = data[nums*buffLen+j];
                    }
                    try {
                        if(this.sendDelay>0 && befFlag){
                            Thread.sleep(this.sendDelay);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    os.write(data);
                    os.flush();
                    //sendDataOne(buffer);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (os != null) {
                        os.close();
                        os = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }*/
    private void sendData(byte[] data) {
        synchronized (send_lock){
                int buffLen = this.maxNum; // 串口最大缓存
                byte[] buffer = new byte[0];
                // 发送完整的
                int nums = data.length/buffLen;
                int yu = data.length%buffLen;
                boolean befFlag = false;
                for(int i=0;i<nums;i++){
                    buffer = new byte[buffLen];
                    for(int j=0;j<buffLen;j++){
                        buffer[j] = data[i*buffLen+j];
                    }
                    sendDataOne(buffer);
                    try {
                        if(this.sendDelay>0){
                            Thread.sleep(this.sendDelay);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
                if(yu > 0){
                    buffer = new byte[yu];
                    for(int j=0;j<yu;j++){
                        buffer[j] = data[nums*buffLen+j];
                    }
                    try {
                        if(this.sendDelay>0){
                            Thread.sleep(this.sendDelay);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sendDataOne(buffer);
                }
        }
    }
    long sendTimes = 0;
    private   void sendDataOne(byte[] data) {
        OutputStream os = null;
        try {
                os = serialPort.getOutputStream();//获得串口的输出流
                os.write(data);
                os.flush();
                LOGGER.info(port + "一次发送        "+sendTimes+++"    ："+data.length);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (os != null) {
                    os.close();
                    os = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    long readTimes = 0;

    /**
     * 从串口读取数据
     * @return 读取的数据
     */
    private byte[] readData() {
        InputStream is = null;
        byte[] bytes = null;
        try {
            is = serialPort.getInputStream();//获得串口的输入流
            int bufflenth = is.available();//获得数据长度
            while (bufflenth != 0) {
                LOGGER.info(port + "一次读取    "+readTimes+++"    ："+bufflenth);
                bytes = new byte[bufflenth];//初始化byte数组
                is.read(bytes);
                bufflenth = is.available();
                setPackage(bytes);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }

    private byte[] readDataNoWait() {
        InputStream is = null;
        byte[] bytes = null;
        try {
            is = serialPort.getInputStream();//获得串口的输入流
            while (true) {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                int bufflenth = is.available();
                bytes = new byte[bufflenth];//初始化byte数组
                is.read(bytes);
                if(bufflenth>0){
                    LOGGER.info(port + "一次读取："+bufflenth);
                    setPackage(bytes);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                    is = null;
                }
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
        return bytes;
    }
    /**
     * 给串口设置监听
     * @param serialPort
     * @param listener
     */
    private   void setListenerToSerialPort(SerialPort serialPort, SerialPortEventListener listener) {
        try {
            //给串口添加事件监听
            serialPort.addEventListener(listener);
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
        serialPort.notifyOnDataAvailable(true);//串口有数据监听
        serialPort.notifyOnBreakInterrupt(true);//中断事件监听
    }

}
