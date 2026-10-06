package org.bci.utilities;

import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelToPojoUtils {

	public static <T> List<T> getDataAsPojo(String filePath, String sheetName, Class<T> clazz) {

		List<T> dataList = new ArrayList<>();

		try (FileInputStream fis = new FileInputStream(filePath); Workbook workbook = new XSSFWorkbook(fis)) {

			Sheet sheet = workbook.getSheet(sheetName);
			if (sheet == null) {
				throw new RuntimeException("Sheet not found: " + sheetName);
			}

			Row headerRow = sheet.getRow(0);
			if (headerRow == null) {
				throw new RuntimeException("Header row is missing in sheet: " + sheetName);
			}

			// Map column name -> index
			Map<String, Integer> columnMap = new HashMap<>();
			for (Cell cell : headerRow) {
				columnMap.put(cell.getStringCellValue().trim(), cell.getColumnIndex());
			}

			// Read data rows
			for (int i = 1; i <= sheet.getLastRowNum(); i++) {

			    Row row = sheet.getRow(i);
			    if (row == null)
			        continue;

			    //  Skip completely empty rows
			    boolean isRowEmpty = true;
			    DataFormatter formatter = new DataFormatter();

			    for (Cell cell : row) {
			        if (cell != null && !formatter.formatCellValue(cell).trim().isEmpty()) {
			            isRowEmpty = false;
			            break;
			        }
			    }

			    if (isRowEmpty)
			        continue;

			    T obj = clazz.getDeclaredConstructor().newInstance();

			    for (Field field : clazz.getDeclaredFields()) {
			        field.setAccessible(true);

			        if (!columnMap.containsKey(field.getName())) {
			            continue;
			        }

			        Cell cell = row.getCell(columnMap.get(field.getName()));
			        Object value = getCellValue(cell, field.getType());
			        field.set(obj, value);
			    }

			    dataList.add(obj);
			}


		} catch (Exception e) {
			throw new RuntimeException("Excel to POJO mapping failed", e);
		}

		return dataList;
	}

	// ================= CORE CELL CONVERSION =================
	private static Object getCellValue(Cell cell, Class<?> fieldType) {

		if (cell == null) {
			return "";
		}

		DataFormatter formatter = new DataFormatter();

		switch (cell.getCellType()) {

		case STRING:
			return cell.getStringCellValue().trim();

		case NUMERIC:

			if (DateUtil.isCellDateFormatted(cell)) {
				Date date = cell.getDateCellValue();
				return new SimpleDateFormat("ddMMyyyy").format(date);
			}

			// Numeric but NOT date
			if (fieldType == int.class || fieldType == Integer.class) {
				return (int) cell.getNumericCellValue();
			}

			if (fieldType == double.class || fieldType == Double.class) {
				return cell.getNumericCellValue();
			}

			// fallback - treat as string
			return formatter.formatCellValue(cell);

		case BOOLEAN:
			return cell.getBooleanCellValue();

		default:
			return formatter.formatCellValue(cell);
		}
	}

}
