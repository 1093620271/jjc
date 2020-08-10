package com.jjc.comm.common.util;


import com.jjc.comm.common.sys.BackMsg;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Map;

public final class ExcelExport {

	private static final Logger log = LoggerFactory.getLogger(ExcelExport.class);

	//路径
	private final static String excelPath ="D:\\";


	public static void exportExcelAndSave(HttpServletResponse response,
                                          List<Map<String, Object>> dataList, String[] titles, String[] fields,
                                          String sheetName, String topTitle){

		BackMsg backMsg = BackMsgUtil.buildMsg(true, MessageHelper.EXPFAIL);
		StringBuffer errorMsg = new StringBuffer();

		try {
			// 第一步，创建一个webbook，对应一个Excel文件
			HSSFWorkbook wb = new HSSFWorkbook();
	        System.out.println("创建工作簿");

	        // 第二步，在webbook中添加一个sheet,对应Excel文件中的sheet
	        if(null == sheetName || "".equals(sheetName))
	        	sheetName = "数据页";
        	Sheet sheet = wb.createSheet(sheetName);
			sheet.setColumnWidth((short) 0, (short) 9000);// 设置列宽
			sheet.setColumnWidth((short) 1, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 2, (short) 9000);// 设置列宽
			/*sheet.setColumnWidth((short) 3, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 4, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 5, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 6, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 7, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 8, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 9, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 10, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 11, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 12, (short) 6000);// 设置列宽
			sheet.setColumnWidth((short) 13, (short) 6000);// 设置列宽*/

        	Row row=sheet.createRow(0);
	        //创建单元格（excel的单元格，参数为列索引，可以是0～255之间的任何一个
        	Cell cell=row.createCell(0);
	        //设置单元格内容
			HSSFCellStyle style = (HSSFCellStyle) wb.createCellStyle();// 设置表头样式
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);// 设置居中

			//设置这些样式
			style.setFillForegroundColor(HSSFColor.SKY_BLUE.index);
			style.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
			style.setBorderBottom(HSSFCellStyle.BORDER_THIN);
			style.setBorderLeft(HSSFCellStyle.BORDER_THIN);
			style.setBorderRight(HSSFCellStyle.BORDER_THIN);
			style.setBorderTop(HSSFCellStyle.BORDER_THIN);
			style.setAlignment(HSSFCellStyle.ALIGN_CENTER);
			//生成一个字体
			HSSFFont font = wb.createFont();
			font.setColor(HSSFColor.VIOLET.index);
			font.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
			//把字体应用到当前的样式
			style.setFont(font);
			cell.setCellStyle(style);
			cell.setCellValue(topTitle);


	        //合并单元格CellRangeAddress构造参数依次表示起始行，截至行，起始列， 截至列
	        sheet.addMergedRegion(new CellRangeAddress(0,0,0,titles.length-1));
	        row = sheet.createRow((int) 1);

	        for(int i=0;i<titles.length;i++){
	        	Cell cellItem = row.createCell(i);
		        cellItem.setCellValue(titles[i]);
		        cellItem.setCellType(XSSFCell.CELL_TYPE_STRING);
				cellItem.setCellStyle(style);
	        }

	        StringBuffer str = new StringBuffer("");
	        for (int i = 0; i<dataList.size() ; i++){
	            row = sheet.createRow((int) i + 2);
	            // 第四步，创建单元格，并设置值
	            for(int j=0;j<fields.length;j++){
	            	try {
	            		str.delete(0, str.length());
	            		if(null != dataList.get(i).get(fields[j].toLowerCase()))
	            			str.append( dataList.get(i).get(fields[j].toLowerCase()).toString() );
	            		row.createCell(j).setCellValue(str.toString());
					} catch (Exception e) {
						errorMsg.append("第"+i+"行，第"+j+"列数据错误。");
						continue;
					}
	            }
	        }

	        System.out.println("保存");

	        //保存到服务器
	        String filePath = DateUtils.getDate()+ToolUtil.getUUID().hashCode()+".xls";
	        System.out.println("filePath："+filePath);

	        File file1 = new File(excelPath);
			if(!file1.exists() && !file1.isDirectory()){
			    file1.mkdirs();
			}


			/*FileOutputStream output=new FileOutputStream(excelPath+filePath);
			wb.write(output);
			output.close();*/

			response.reset();
			response.setContentType("application/vnd.ms-excel");
			response.addHeader("Content-Disposition", "attachment;filename="+filePath);
			OutputStream out = response.getOutputStream();
			wb.write(out);

		} catch (Exception e) {
			e.printStackTrace();
		}


	}


	public static void getExcelOfFile(HttpServletResponse response, String filePath){
		try {
	        File file = new File(excelPath+filePath);
	        String filename = file.getName();
	        InputStream fis;
			fis = new BufferedInputStream(new FileInputStream(excelPath+filePath));
	        byte[] buffer = new byte[fis.available()];
	        fis.read(buffer);
	        fis.close();
	        // 清空response
	        response.reset();
	        // 设置response的Header
	        response.addHeader("Content-Disposition", "attachment;filename="
	                + new String(filename.getBytes()));
	        response.addHeader("Content-Length", "" + file.length());
	        OutputStream toClient = new BufferedOutputStream(
	                response.getOutputStream());
	        response.setContentType("application/vnd.ms-excel;charset=gb2312");

			toClient.write(buffer);
	        toClient.flush();
	        toClient.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			log.error("导出的excel不存在："+excelPath+filePath);
		} catch (IOException e) {
			e.printStackTrace();
			log.error("导出excel发送读写错误："+excelPath+filePath);
		}
	}

}
