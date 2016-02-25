package com.suushiemaniac.cubing.bld.algsheet;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.exception.InvalidPieceTypeException;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public abstract class BldAlgSheet {
    private File excelFile;

    public BldAlgSheet(File excelFile) {
        this.excelFile = excelFile;
    }

    protected Workbook getWorkbook() {
        try {
            FileInputStream fis = new FileInputStream(this.excelFile);
            Workbook wb = new XSSFWorkbook(fis);
            fis.close();
            return wb;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void writeWorkbook(Workbook workbook) {
        try {
            FileOutputStream fos = new FileOutputStream(excelFile);
            workbook.write(fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, List<Algorithm>> algsFromExcel(CubicPieceType type) throws InvalidPieceTypeException {
        Workbook wb = this.getWorkbook();
        return wb == null ? null : this.algsFromExcel(wb, type);
    }

    public Map<String, List<String>> algStringsFromExcel(CubicPieceType type) throws InvalidPieceTypeException {
        Workbook wb = this.getWorkbook();
        return wb == null ? null : this.algStringsFromExcel(wb, type);
    }

    protected abstract Map<String, List<Algorithm>> algsFromExcel(Workbook wb, CubicPieceType type) throws InvalidPieceTypeException;

    protected abstract Map<String, List<String>> algStringsFromExcel(Workbook wb, CubicPieceType type) throws InvalidPieceTypeException;
}