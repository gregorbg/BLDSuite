package com.suushiemaniac.cubing.bld.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.ArrayList;
import java.util.Collections;

public class ExcelUtil {
    public static String[] excelColumnNames = BruteForceUtil.genBlockString(BruteForceUtil.ALPHABET, 3, true, true);

    public static String excelCellBinarySearch(String toSearch, Sheet sheet) {
        if (sheet == null) return "A0";
        for (Row row : sheet)
            for (Cell cell : row) {
                if (cell.getStringCellValue().equals(toSearch))
                    return excelColumnNames[cell.getColumnIndex()] + (row.getRowNum() + 1);
            }
        return "A0";
    }
}
