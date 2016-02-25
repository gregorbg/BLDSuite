package com.suushiemaniac.cubing.bld.algsheet;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.exception.InvalidNotationException;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.bld.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.exception.InvalidExcelMapSizeError;
import com.suushiemaniac.cubing.bld.exception.InvalidPieceTypeException;
import com.suushiemaniac.cubing.bld.util.ExcelUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.util.*;

public class GregorBldExcel extends BldAlgSheet {
    public GregorBldExcel(File excelFile) {
        super(excelFile);
    }

    private int getPageNum(CubicPieceType type) {
        switch (type) {
            case CORNER:
                return 0;
            case EDGE:
                return 2;
            case XCENTER:
                return 6;
            case WING:
                return 4;
            case TCENTER:
                return 8;
            default:
                return -1;
        }
    }

    private int getPrintPageNum(CubicPieceType type) {
        return this.getPageNum(type) + 1;
    }

    private Map<String, String> getAlgStringsFromExcel(Workbook wb, int sheetNum, int numNonBufferPieces, int targetsPerPiece, boolean includeInverse) {
        Map<String, String> excelStringMap = new LinkedHashMap<String, String>();
        Sheet sheet = wb.getSheetAt(sheetNum);
        String letterPair, lp1, lp2;
        int headerSpacing = 2, betweenColSpacing = 3;
        int totalColCoeff = 4, totalRowCoeff = headerSpacing + ((targetsPerPiece + 1) * numNonBufferPieces) + betweenColSpacing - 1;

        for (int colNum = 0; colNum < numNonBufferPieces; colNum++)
            for (int rowNum = 0; rowNum < targetsPerPiece; rowNum++) {
                lp1 = sheet.getRow(rowNum * totalRowCoeff).getCell((colNum * totalColCoeff) + headerSpacing).getStringCellValue();
                lp1 = String.valueOf(lp1.charAt(lp1.length() - 1));
                for (int block = 0; block < numNonBufferPieces; block++)
                    for (int line = 0; line < targetsPerPiece; line++) {
                        Row row = sheet.getRow((rowNum * totalRowCoeff) + headerSpacing + ((targetsPerPiece + 1) * block) + line);
                        lp2 = row.getCell((colNum * totalColCoeff) + 1).getStringCellValue();
                        letterPair = lp1 + lp2;
                        String algString = row.getCell((colNum * totalColCoeff) + 2).getStringCellValue();
                        if (!algString.trim().equals("/"))
                            if (includeInverse || !algString.trim().startsWith("#"))
                                excelStringMap.put(letterPair, algString.replace("#ENNVAU", "#NV"));
                    }
            }

        return excelStringMap;
    }

    public Map<String, Algorithm> getAlgsFromExcel(Workbook wb, CubicPieceType type) {
        Map<String, String> excelStringMap = getAlgStringsFromExcel(wb, this.getPageNum(type), type.getNumPiecesNoBuffer(), type.getTargetsPerPiece(), true);

        Map<String, Algorithm> algMap = new HashMap<String, Algorithm>();
        if (excelStringMap.size() == type.getNumAlgs()) {
            CubicAlgorithmReader cubicReader = new CubicAlgorithmReader();
            for (String key : excelStringMap.keySet()) {
                String algString = excelStringMap.get(key);
                Algorithm parseAlg;
                if (algString.startsWith("#")) parseAlg = cubicReader.parse(excelStringMap.get(algString.substring(1))).inverse();
                else parseAlg = cubicReader.parse(algString);
                algMap.put(key, parseAlg);
            }
            return algMap;
        } else throw new InvalidExcelMapSizeError(excelStringMap.size(), type.getNumAlgs());
    }

    @Override
    protected Map<String, List<String>> algStringsFromExcel(Workbook wb, CubicPieceType type) throws InvalidPieceTypeException {
        return this.wrapToListMap(this.getAlgStringsFromExcel(wb, this.getPageNum(type), type.getNumPiecesNoBuffer(), type.getTargetsPerPiece(), false));
    }

    @Override
    protected Map<String, List<Algorithm>> algsFromExcel(Workbook wb, CubicPieceType type) throws InvalidPieceTypeException {
        return this.wrapToListMap(this.getAlgsFromExcel(wb, type));
    }

    private <T> Map<String, List<T>> wrapToListMap(Map<String, T> singleEntryMap) {
        Map<String, List<T>> toReturn = new HashMap<String, List<T>>();
        for (String key : singleEntryMap.keySet())
            toReturn.put(key, Collections.singletonList(singleEntryMap.get(key)));
        return toReturn;
    }

    public void writeAlgSetToSpreadsheet(Workbook wb, Map<String, Algorithm> algMap, CubicPieceType type) {
        if (wb == null) return;
        int targetsPerPiece = type.getTargetsPerPiece();
        int numTargets = type.getNumPiecesNoBuffer();
        List<String> letterList = Arrays.asList(SpeffzUtil.fullSpeffz());
        Sheet sheet = wb.createSheet("Sheet" + System.currentTimeMillis());
        int headerSpacing = 2, betweenColSpacing = 3;
        int totalColCoeff = 4, totalRowCoeff = headerSpacing + ((targetsPerPiece + 1) * numTargets) + betweenColSpacing + 1;
        for (int colNum = 0; colNum < numTargets; colNum++)
            for (int rowNum = 0; rowNum < targetsPerPiece; rowNum++) {
                int i = colNum * targetsPerPiece + rowNum;
                Row headerRow = sheet.getRow(rowNum * totalRowCoeff);
                if (headerRow == null) headerRow = sheet.createRow(rowNum * totalRowCoeff);
                headerRow.createCell(colNum * totalColCoeff).setCellValue(SpeffzUtil.speffzToSticker(letterList.get(0), type) + " - buffer");
                headerRow.createCell(colNum * totalColCoeff + 1).setCellValue("letter");
                headerRow.createCell(colNum * totalColCoeff + 2).setCellValue(SpeffzUtil.speffzToSticker(letterList.get(i), type) + " - " + letterList.get(i));
                for (int block = 0; block < numTargets; block++)
                    for (int line = 0; line < targetsPerPiece; line++) {
                        int j = block * targetsPerPiece + line;
                        Row blockRow = sheet.getRow((rowNum * totalRowCoeff) + headerSpacing + ((targetsPerPiece + 1) * block) + line);
                        if (blockRow == null) blockRow = sheet.createRow((rowNum * totalRowCoeff) + headerSpacing + ((targetsPerPiece + 1) * block) + line);
                        Algorithm alg = algMap.get(letterList.get(i) + letterList.get(j));
                        String algString = alg == null ? "/" : alg.toFormatString();
                        if (colNum > block) algString = "#" + letterList.get(j) + letterList.get(i);
                        blockRow.createCell(colNum * totalColCoeff).setCellValue(SpeffzUtil.speffzToSticker(letterList.get(j), type));
                        blockRow.createCell(colNum * totalColCoeff + 1).setCellValue(letterList.get(j));
                        blockRow.createCell(colNum * totalColCoeff + 2).setCellValue(algString);
                    }
            }
        writeWorkbook(wb);
    }

    public void referencePrint(Workbook wb, CubicPieceType type) {
        if (wb == null) return;
        Sheet sheet = wb.getSheetAt(this.getPrintPageNum(type));
        String sheetName = sheet.getSheetName();
        String origSheetName = sheetName.replace("Print", "");
        if (sheetName.contains("Print"))
            for (Row row : sheet)
                for (Cell cell : row)
                    if (cell.getStringCellValue().length() > 1 && !cell.getStringCellValue().equals("letter"))
                        cell.setCellFormula(origSheetName + "!" + ExcelUtil.excelCellBinarySearch(cell.getStringCellValue(), sheet));
        writeWorkbook(wb);
    }

    public void fixMissingPrintReferences(Workbook wb, CubicPieceType type) {
        if (wb == null) return;
        int targetsPerPiece = type.getTargetsPerPiece();
        int numTargets = type.getNumPiecesNoBuffer();
        Sheet sheet = wb.getSheetAt(this.getPrintPageNum(type));
        String sheetName = sheet.getSheetName();
        String origSheetName = sheetName.replace("Print", "");
        if (sheetName.contains("Print")) for (Row row : sheet) for (Cell cell : row)
            if (cell.getCellType() == Cell.CELL_TYPE_FORMULA) {
                String cellFormula = cell.getCellFormula();
                if (cellFormula.contains("A0")) {
                    String primaryLetter = sheet.getRow(2).getCell(cell.getColumnIndex()).getStringCellValue();
                    String secondaryLetter = row.getCell(0).getStringCellValue();
                    int totalRowCoeff = (targetsPerPiece + 1) * numTargets + 3 + 1, totalColCoeff = 4;
                    Sheet origSheet = wb.getSheet(origSheetName);
                    for (int colNum = 0; colNum < numTargets; colNum++) for (int rowNum = 0; rowNum < targetsPerPiece; rowNum++) {
                        Row workingRow = origSheet.getRow(rowNum * totalRowCoeff);
                        String tempCellString = workingRow.getCell((colNum * totalColCoeff) + 2).getStringCellValue();
                        String testPrimary = tempCellString.substring(tempCellString.length() - 1);
                        if (testPrimary.equals(primaryLetter))
                            for (int line = 0; line < numTargets * targetsPerPiece; line++) {
                                int tempRowNum = rowNum * totalRowCoeff + 2 + (line + (line / targetsPerPiece));
                                tempCellString = origSheet.getRow(tempRowNum).getCell((colNum * totalColCoeff) + 1).getStringCellValue();
                                String testSecondary = tempCellString.substring(tempCellString.length() - 1);
                                if (testSecondary.equals(secondaryLetter))
                                    cell.setCellFormula(origSheetName + "!" + ExcelUtil.excelColumnNames[(colNum * totalColCoeff) + 2] + (tempRowNum + 1));
                            }
                    }
                }
            }
        writeWorkbook(wb);
    }
}
