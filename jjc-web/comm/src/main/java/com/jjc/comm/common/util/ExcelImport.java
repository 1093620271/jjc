package com.jjc.comm.common.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.apache.poi.ss.usermodel.Cell.CELL_TYPE_BLANK;

public final class ExcelImport {

	private POIFSFileSystem fs;
	private static  Sheet sheet;
	private static Row row;
	private static Workbook wb = null;
	//03版
	private static final String OFFICE_EXCEL_XLS = ".xls";
	//07版
	private static final String OFFICE_EXCEL_XLSX = ".xlsx";
	//private Workbook wb;
	/**
	 * 读取Excel表格表头的内容
	 * @param
	 * @return String 表头内容的数组
	 */
	/**
	 * spring MVC 文件处理方法(.xls)
	 * @param file
	 * @return 电子工作簿
	 */
	private static Workbook loadXLS(MultipartFile file) {
		Workbook workbook = null;
		try {
			POIFSFileSystem fs = new POIFSFileSystem(file.getInputStream());
			workbook = new HSSFWorkbook(fs);
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("文件解析错误！");
		}
		return workbook;
	}

	/**
	 * spring MVC 文件处理方法(.xlsx)
	 * @param upfile
	 * @return 电子工作簿
	 */
	private static Workbook loadXLSX(MultipartFile upfile) {
		Workbook workbook = null;
		OPCPackage op;
		try {
			op = OPCPackage.open(upfile.getInputStream());
			workbook = new XSSFWorkbook(op);
		} catch (InvalidFormatException e) {
			e.printStackTrace();
			System.err.println("文件解析错误");
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("文件提取错误！");
		}

		return workbook;
	}
	public String[] readExcelTitle(InputStream is, MultipartFile file) {
		String filePath="";
		Workbook workbook=null;
		if(file.getOriginalFilename().endsWith(OFFICE_EXCEL_XLS)){
			workbook = loadXLS(file);//加载Excel文件
		}else if(file.getOriginalFilename().endsWith(OFFICE_EXCEL_XLSX)){
			workbook = loadXLSX(file);//加载Excel文件
		}
		Sheet sheet = workbook.getSheetAt(0);
		Row row = sheet.getRow(0);
		// 标题总列数
		int colNum = row.getPhysicalNumberOfCells();
		String[] title = new String[colNum];
		for (int i = 0; i < colNum; i++) {
			title[i] = getStringCellValue(row.getCell(i));
		}
		return title;
	}

	/**
	 * 读取Excel数据内容
	 * @param
	 * @return Map 包含单元格数据内容的Map对象
	 */
	public List<Map<String, String>> readExcelContent(InputStream is, MultipartFile file) {
		String[] titles = readExcelTitle(is,file);//获取标题
		List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();//结果集
		Workbook workbook=null;
		if(file.getOriginalFilename().endsWith(OFFICE_EXCEL_XLS)){
			workbook = loadXLS(file);//加载Excel文件
		}else if(file.getOriginalFilename().endsWith(OFFICE_EXCEL_XLSX)){
			workbook = loadXLSX(file);//加载Excel文件
		}
		Sheet sheet = workbook.getSheetAt(0);
		// 得到总行数
		int rowNum = sheet.getLastRowNum();
		// 正文内容应该从第二行开始,第一行为表头的标题
		for (int i = 1; i <=rowNum; i++) {
			Row row = sheet.getRow(i);
			if(row!=null){
				int colNum = row.getLastCellNum();
				Map<String, String> m = new HashMap<String, String>();
				for (int j = 0; j < colNum; j++){
					m.put(titles[j], getCellString(row.getCell(j)).trim());
				}
				mapList.add(m);
			}
		}
		return mapList;
	}
	/**
	 * 获取单元格数据内容为字符串类型的数据
	 *
	 * @param cell Excel单元格
	 * @return String 单元格数据内容
	 */
	private String getStringCellValue(Cell cell) {
		String strCell = "";
		switch (cell.getCellType()) {
			case HSSFCell.CELL_TYPE_STRING:
				strCell = cell.getStringCellValue();
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				strCell = String.valueOf(cell.getNumericCellValue());
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				strCell = String.valueOf(cell.getBooleanCellValue());
				break;

			case CELL_TYPE_BLANK:
				strCell = "";
				break;
			default:
				strCell = "";
				break;
		}
		if (strCell.equals("") || strCell == null) {
			return "";
		}
		if (cell == null) {
			return "";
		}
		return strCell;
	}


	//把EXCEL Cell原有数据转换成String类型
	private String getCellString(Cell cell) {
		if(cell==null) return "";
		String cellSring="";
		switch (cell.getCellType()) {
			case Cell.CELL_TYPE_STRING: // 字符串
				cellSring = cell.getStringCellValue();
				break;
			case Cell.CELL_TYPE_NUMERIC: // 数字

				// 判断当前的cell是否为Date
				if (HSSFDateUtil.isCellDateFormatted(cell)) {
					Date date = cell.getDateCellValue();
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					cellSring = sdf.format(date);

				}
				// 如果是纯数字
				else {

					cell.setCellType(Cell.CELL_TYPE_STRING);
					cellSring=cell.getStringCellValue();
				}

				break;
			case Cell.CELL_TYPE_BOOLEAN: // Boolean
				cellSring= String.valueOf(cell.getBooleanCellValue());
				break;
			case Cell.CELL_TYPE_FORMULA: // 公式
				cellSring= String.valueOf(cell.getCellFormula());
				break;
			case CELL_TYPE_BLANK: // 空值
				cellSring="";
				break;
			case Cell.CELL_TYPE_ERROR: // 故障
				cellSring="";
				break;
			default:
				cellSring="ERROR";
				break;
		}
		return cellSring;
	}


	/**
	 * 获取单元格数据内容为日期类型的数据
	 *
	 * @param cell
	 *            Excel单元格
	 * @return String 单元格数据内容
	 */
	private String getDateCellValue(HSSFCell cell) {
		String result = "";
		try {
			int cellType = cell.getCellType();
			if (cellType == HSSFCell.CELL_TYPE_NUMERIC) {
				Date date = cell.getDateCellValue();
				result = (date.getYear() + 1900) + "-" + (date.getMonth() + 1)
						+ "-" + date.getDate();
			} else if (cellType == HSSFCell.CELL_TYPE_STRING) {
				String date = getStringCellValue(cell);
				result = date.replaceAll("[年月]", "-").replace("日", "").trim();
			} else if (cellType == CELL_TYPE_BLANK) {
				result = "";
			}
		} catch (Exception e) {
			System.out.println("日期格式不正确!");
			e.printStackTrace();
		}
		return result;
	}

	/**
	 * 根据HSSFCell类型设置数据
	 * @param cell
	 * @return
	 */
	private String getCellFormatValue(HSSFCell cell) {
		String cellvalue = "";
		if (cell != null) {
			// 判断当前Cell的Type
			switch (cell.getCellType()) {
				// 如果当前Cell的Type为NUMERIC
				case HSSFCell.CELL_TYPE_NUMERIC:
				case HSSFCell.CELL_TYPE_FORMULA: {
					// 判断当前的cell是否为Date
					if (HSSFDateUtil.isCellDateFormatted(cell)) {
						// 如果是Date类型则，转化为Data格式

						//方法1：这样子的data格式是带时分秒的：2011-10-12 0:00:00
						//cellvalue = cell.getDateCellValue().toLocaleString();

						//方法2：这样子的data格式是不带带时分秒的：2011-10-12
						Date date = cell.getDateCellValue();
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
						cellvalue = sdf.format(date);

					}
					// 如果是纯数字
					else {

						// 取得当前Cell的数值
						cellvalue = String.valueOf(cell.getNumericCellValue());
					}
					break;
				}
				// 如果当前Cell的Type为STRIN
				case HSSFCell.CELL_TYPE_STRING:
					// 取得当前的Cell字符串
					cellvalue = cell.getRichStringCellValue().getString();
					break;
				// 默认的Cell值
				default:
					cellvalue = " ";
			}
		} else {
			cellvalue = "";
		}
		return cellvalue;

	}

}
