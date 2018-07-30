package com.contrachequeUI.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.contrachequeUI.exception.BusinessException;
import com.contrachequeUI.model.ItemBean;

public class CsvAndXlsxUtil {

	public static List<ItemBean> getListFromCSV(final String filePath) throws IOException {
		File inputF = new File(filePath);
		InputStream inputFS = new FileInputStream(inputF);
		BufferedReader lines = new BufferedReader(new InputStreamReader(inputFS));

		@SuppressWarnings("resource")
		List<ItemBean> resultMap = lines.lines().map(line -> {
			String[] item = line.split(";");
			if (item.length == 3 && item[2] != null)
				return new ItemBean(item[0], Integer.valueOf(item[1]), item[2]);
			else
				return new ItemBean(item[0], Integer.valueOf(item[1]));

		}).collect(Collectors.toList());
		lines.close();
		return resultMap;
	}

	public static List<ItemBean> getListFromCsvToCreateContraCheque(final String filePath) throws IOException {
		File inputF = new File(filePath);
		InputStream inputFS = new FileInputStream(inputF);
		BufferedReader lines = new BufferedReader(new InputStreamReader(inputFS, "UTF8"));

		@SuppressWarnings("resource")
		List<ItemBean> resultMap = lines.lines().map(line -> {
			String[] item = line.split(";");
			try {
				return new ItemBean(item[0], item[1], item[2], item[3], item[4], Integer.valueOf(item[5]));
			} catch (NumberFormatException e) {
				return new ItemBean(item[0], item[1], item[2], item[3], item[4], 0);
			}
		}).collect(Collectors.toList());
		lines.close();
		return resultMap;
	}

	@SuppressWarnings("resource")
	public static List<ItemBean> getListFromXlsxToCreateContraCheque(final String xlsxPath) throws BusinessException {
		List<ItemBean> list = new ArrayList<ItemBean>();
		XSSFWorkbook wb;

		try {
			wb = new XSSFWorkbook(new FileInputStream(xlsxPath));
		} catch (IOException e) {
			LogUtil.printLine("Erro na leitura do arquivo: " + xlsxPath);
			throw new BusinessException(e.getMessage());
		}

		XSSFSheet sheet = wb.getSheetAt(0);
		
		sheet.forEach(row -> {
			if (row.getRowNum() > 0) {
				if (row.getCell(0) != null && row.getCell(1) != null && row.getCell(2) != null && row.getCell(3) != null
						&& row.getCell(4) != null && row.getCell(5) != null) {
					try {
						list.add(new ItemBean(row.getCell(0).toString(), row.getCell(1).toString(),
								row.getCell(2).toString(), row.getCell(3).toString(), row.getCell(4).toString(),
								(int) Double.parseDouble(row.getCell(5).toString().toString())));
					} catch (NumberFormatException e) {
						LogUtil.printLine("Erro na linha do arquivo: " + row.getRowNum());
						e.printStackTrace();
						try {
							throw new BusinessException(e.getMessage());
						} catch (BusinessException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		try {
			wb.close();
		} catch (IOException e) {
			LogUtil.printLine("Erro ao fechar o arquivo: " + xlsxPath);
			throw new BusinessException(e.getMessage());
		}
		return list;
	}

	@SuppressWarnings("resource")
	public static List<ItemBean> getListFromXlsx(final String xlsxPath) throws BusinessException {
		List<ItemBean> list = new ArrayList<ItemBean>();
		XSSFWorkbook wb;

		try {
			wb = new XSSFWorkbook(new FileInputStream(xlsxPath));
		} catch (IOException e) {
			LogUtil.printLine("Erro na leitura do arquivo: " + xlsxPath);
			throw new BusinessException(e.getMessage());
		}

		XSSFSheet sheet = wb.getSheetAt(0);
		Iterator<Row> sheetIterator = sheet.iterator();
		
		sheet.forEach(row -> {
			if (row.getRowNum() > 1) {
				if (row.getCell(0) != null && row.getCell(1) != null && row.getCell(2) != null && row.getCell(3) != null
						&& row.getCell(4) != null) {
					try {
						list.add(new ItemBean(row.getCell(3).toString(), /*ID*/ 
								row.getCell(0).toString(), /*Nome*/
								row.getCell(6).toString(), /*Cargo*/
								row.getCell(32).toString())); /*Lideranca*/
					} catch (NumberFormatException e) {
						LogUtil.printLine("Erro na linha do arquivo: " + row.getRowNum());
						e.printStackTrace();
						try {
							throw new BusinessException(e.getMessage());
						} catch (BusinessException e1) {
							e1.printStackTrace();
						}
					}
				}
			}
		});

		try {
			wb.close();
		} catch (IOException e) {
			LogUtil.printLine("Erro ao fechar o arquivo: " + xlsxPath);
			throw new BusinessException(e.getMessage());
		}
		return list;
	}
}
