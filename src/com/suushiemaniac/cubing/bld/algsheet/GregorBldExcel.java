package com.suushiemaniac.cubing.bld.algsheet;

import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class GregorBldExcel extends BldAlgSheet {
    protected static final int HEADER_SPACING = 2;
    protected static final int BETWEEN_COL_SPACING = 3;
    protected static final int TOTAL_COL_COEFF = 4;

    public GregorBldExcel(File excelFile) {
        super(excelFile);
    }

    private int getSheetNum(PieceType pieceType) {
        if (!(pieceType instanceof CubicPieceType)) return -1;

        CubicPieceType[] sheetOrderTypes = new CubicPieceType[]{CORNER, EDGE, WING, XCENTER, TCENTER};
        int index = ArrayUtil.binarySearch(pieceType, sheetOrderTypes);

        return index == -1 ? index : 2 * index;
    }

    @Override
    protected Cell getPrimaryCell(PieceType pieceType, String letterPair) {
        int sheetNum = this.getSheetNum(pieceType);
        if (sheetNum < 0) return null;

        Sheet sheet = this.workbook.getSheetAt(sheetNum);

        int targetsPerPiece = pieceType.getTargetsPerPiece(), numNonBufferPieces = pieceType.getNumPiecesNoBuffer();
        if (pieceType == XCENTER || pieceType == TCENTER) { //big cube center special rules
            targetsPerPiece = 4;
            numNonBufferPieces = 6;
        }

        int totalRowCoeff = HEADER_SPACING + ((targetsPerPiece + 1) * numNonBufferPieces) + BETWEEN_COL_SPACING - 1;

        for (int colNum = 0; colNum < numNonBufferPieces; colNum++)
            for (int rowNum = 0; rowNum < targetsPerPiece; rowNum++)
                for (int block = 0; block < numNonBufferPieces; block++)
                    for (int line = 0; line < targetsPerPiece; line++) {
                        Row row = sheet.getRow((rowNum * totalRowCoeff) + HEADER_SPACING + ((targetsPerPiece + 1) * block) + line);

                        String lp1 = sheet.getRow(rowNum * totalRowCoeff).getCell(colNum * TOTAL_COL_COEFF + HEADER_SPACING).getStringCellValue();
                        lp1 = "" + lp1.charAt(lp1.length() - 1);

                        String lp2 = row.getCell((colNum * TOTAL_COL_COEFF) + 1).getStringCellValue();

                        if (letterPair.equals(lp1 + lp2)) {
                            Cell cell = row.getCell((colNum * TOTAL_COL_COEFF) + 2);
                            if (!cell.getStringCellValue().equals("/"))
                                return cell;
                        }
                    }

        return null;
    }

    @Override
    protected List<Cell> getSecondaryCells(PieceType pieceType, String letterPair) {
        return Collections.emptyList();
    }

    @Override
    protected PieceType[] getSupportedPieceTypes() {
        return new CubicPieceType[]{CORNER, EDGE, WING, XCENTER, TCENTER};
    }

    @Override
    public Set<String> getRawAlg(PieceType type, String letterPair) {
        List<String> baseAlgs = super.getRawAlg(type, letterPair);
        List<String> algs = new ArrayList<>();

        for (String alg : baseAlgs) {
            if (alg.replace("#ENNVAU", "#NV").startsWith("#"))
                algs.addAll(this.getAlg(type, alg.substring(1)).stream().map(invAlg -> invAlg.inverse().toFormatString()).collect(Collectors.toList()));
            else
                algs.add(alg);
        }

        return algs;
    }
}