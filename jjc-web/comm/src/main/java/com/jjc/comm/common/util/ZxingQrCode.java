package com.jjc.comm.common.util;

import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.OutputStream;
import java.util.Hashtable;

public class ZxingQrCode {
	private static final String CHARSET = "utf-8";
	private static final String FORMAT = "JPG";
	// 二维码尺寸
	private static final int QRCODE_SIZE = 300;
	// LOGO宽度
	private static final int LOGO_WIDTH = 60;
	// LOGO高度
	private static final int LOGO_HEIGHT = 60;
	//默认压缩
	private static final boolean needCompress = true;

	public enum BillType {
		billOrder("bill"), //订单二维码
		payOrder("pay"),  //收款二维码
		transport("send"), //配送二维码
		register("user"); //注册用户二维码
		//enterprise("ent"); //店铺二维码  ---  特殊 场景二维码

		String val;
		private BillType(String val){
			this.val = val;
		}
		public String toString(){
			return val;
		}
	}

	//直接使用 java application 测试
	public static void main(String[] args) throws Exception {
		//String text = "http://www.baidu.com";
		//String logoPath = "D:\\weilai-qiyeyun\\src\\qiyeyun\\src\\main\\webapp\\static\\images\\users.png";
		//ZxingQrCode.createQRCode(text, logoPath, "d:\\", "testQrcode");
//		ZxingQrCode.createQRCode("721591660*775812*15765508953",
//				null,"D:/attach/orderQrCode/","aaa");
		String content="20614699";
		String destPath="C:\\Users\\Administrator\\Desktop";
		String fileName="aaa";
		ZxingQrCode.createQRCodeByLevelQ(content,fileName,destPath);
	}

	public static void createQRCodeByLevelQ(String content, String fileName, String destPath) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.Q);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 0);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, 125, 125,
				hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		// 插入图片
		//ZxingQrCode.insertImage(image, logoPath, needCompress);
		ToolUtil.mkdirs(destPath);
		ImageIO.write(image, FORMAT, new File(destPath + "/" + fileName));
	}

	/**
	 * 生成二维码
	 */

	public static String getBillQrCode(BillType billType, String idValue) {
		return getBillQrCode(billType, idValue, null, ToolUtil.ROOT_PATH);
	}


	public static String getBillQrCode(BillType billType, String idValue, String sysPath) {
		return getBillQrCode(billType, idValue, null, sysPath);
	}

	public static String getBillQrCode(BillType billType, String idValue, String orderType, String sysPath) {
		idValue = idValue == null ? "":idValue;

		if(billType == BillType.billOrder){
			orderType = orderType == null ? "md":orderType;
		}

		if(ToolUtil.isEmpty(sysPath)){
			sysPath = ToolUtil.ROOT_PATH;
		}

		String content = billType.toString()+";"+idValue+";"+orderType;
		String destPath = "attach/orderQrCode/";
		String fileName = billType+"-"+idValue;
		String filePathForUrl = destPath + fileName +"."+ FORMAT.toLowerCase();
		String filePath = sysPath + filePathForUrl;
		//判断是否存在
		File f = new File(filePath);
		if(f.exists()){
			return filePathForUrl;
		}
		try {
			createQRCode(content, null, sysPath + destPath, fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return filePathForUrl;
	}


	/**
	 * 外部调用生成二维码 1
	 *
	 * @param content
	 *            内容
	 * @param logoPath
	 *            LOGO地址
	 * @param destPath
	 *            存放目录
	 * @param fileName
	 *            二维码文件名
	 * @param needCompress
	 *            是否压缩LOGO
	 * @throws Exception
	 */
	public static String createQRCode(String content, String logoPath, String destPath, String fileName) throws Exception {
		BufferedImage image = ZxingQrCode.createImage(content, logoPath, needCompress);
		ToolUtil.mkdirs(destPath);
		fileName = fileName.substring(0, fileName.indexOf(".")>0?fileName.indexOf("."):fileName.length())
				+ "." + FORMAT.toLowerCase();
		ImageIO.write(image, FORMAT, new File(destPath + "/" + fileName));
		return fileName;
	}

	/**
	 *  外部调用生成二维码2    ---   输出流的形式
	 *
	 * @param content
	 *            内容
	 * @param logoPath
	 *            LOGO地址
	 * @param output
	 *            输出流
	 * @param needCompress
	 *            是否压缩LOGO
	 * @throws Exception
	 */
	public static void createQRCode(String content, String logoPath, OutputStream output)
			throws Exception {
		BufferedImage image = ZxingQrCode.createImage(content, logoPath, needCompress);
		ImageIO.write(image, FORMAT, output);
	}


	/**
	 * 解析二维码
	 * @param file 二维码图片
	 * @return
	 * @throws Exception
	 */
	public static String decode(File file) throws Exception {
		BufferedImage image;
		image = ImageIO.read(file);
		if (image == null) {
			return null;
		}
		BufferedImageLuminanceSource source = new BufferedImageLuminanceSource(image);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Result result;
		Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>();
		hints.put(DecodeHintType.CHARACTER_SET, CHARSET);
		result = new MultiFormatReader().decode(bitmap, hints);
		String resultStr = result.getText();
		return resultStr;
	}

	/**
	 * 解析二维码
	 * @param path  二维码图片地址
	 * @return
	 * @throws Exception
	 */
	public static String decode(String path) throws Exception {
		return ZxingQrCode.decode(new File(path));
	}


	private static BufferedImage createImage(String content, String logoPath, boolean needCompress) throws Exception {
		Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
		hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
		hints.put(EncodeHintType.CHARACTER_SET, CHARSET);
		hints.put(EncodeHintType.MARGIN, 1);
		BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE,
				hints);
		int width = bitMatrix.getWidth();
		int height = bitMatrix.getHeight();
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
			}
		}
		if (logoPath == null || "".equals(logoPath)) {
			return image;
		}
		// 插入图片
		ZxingQrCode.insertImage(image, logoPath, needCompress);
		return image;
	}

	/**
	 * 插入LOGO
	 *
	 * @param source
	 *            二维码图片
	 * @param logoPath
	 *            LOGO图片地址
	 * @param needCompress
	 *            是否压缩
	 * @throws Exception
	 */
	private static void insertImage(BufferedImage source, String logoPath, boolean needCompress) throws Exception {
		File file = new File(logoPath);
		if (!file.exists()) {
			throw new Exception("logo file not found.");
		}
		Image src = ImageIO.read(new File(logoPath));
		int width = src.getWidth(null);
		int height = src.getHeight(null);
		if (needCompress) { // 压缩LOGO
			if (width > LOGO_WIDTH) {
				width = LOGO_WIDTH;
			}
			if (height > LOGO_HEIGHT) {
				height = LOGO_HEIGHT;
			}
			Image image = src.getScaledInstance(width, height, Image.SCALE_SMOOTH);
			BufferedImage tag = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
			Graphics g = tag.getGraphics();
			g.drawImage(image, 0, 0, null); // 绘制缩小后的图
			g.dispose();
			src = image;
		}
		// 插入LOGO
		Graphics2D graph = source.createGraphics();
		int x = (QRCODE_SIZE - width) / 2;
		int y = (QRCODE_SIZE - height) / 2;
		graph.drawImage(src, x, y, width, height, null);
		Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
		graph.setStroke(new BasicStroke(3f));
		graph.draw(shape);
		graph.dispose();
	}


}
