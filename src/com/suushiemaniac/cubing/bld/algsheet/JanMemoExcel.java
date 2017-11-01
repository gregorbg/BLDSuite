package com.suushiemaniac.cubing.bld.algsheet;

import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class JanMemoExcel extends BldAlgSheet {
	protected static final int BETWEEN_ROW_SPACING = 2;

	public JanMemoExcel(File excelFile) {
		super(excelFile);
	}

	public JanMemoExcel(File excelFile, boolean fullCache) {
		super(excelFile, fullCache);
	}

	private int getSheetNum(PieceType pieceType) {
		if (pieceType == LetterPairImage.NOUN) {
			return 0;
		}

		return -1;
	}

	@Override
	protected Cell getPrimaryCell(PieceType pieceType, String letterPair) {
		int sheetNum = this.getSheetNum(pieceType);

		if (sheetNum < 0) {
			return null;
		}

		Sheet sheet = this.workbook.getSheetAt(sheetNum);

		int lpOneIndex = ((int) letterPair.toLowerCase().replace("z", "q").charAt(0)) - 97;
		int lpTwoIndex = ((int) letterPair.toLowerCase().replace("z", "q").charAt(1)) - 97;

		if (lpOneIndex > 22 || lpTwoIndex > 22) {
			return null;
		}

		int blockColumn = lpOneIndex % 4;
		int blockRow = lpOneIndex / 4;

		// vertical block size * blockRow + line inside block segment
		int excelRowIndex = (BETWEEN_ROW_SPACING + 22) * blockRow + lpTwoIndex;

		if (lpTwoIndex > lpOneIndex) {
			excelRowIndex -= 1; // compensate for gap in double-pair exclusion
		}

		Row excelSheetRow = sheet.getRow(excelRowIndex);

		// horizontal block size * blockColumn + sidebar
		int excelColIndex = 2 * blockColumn + 1;
		return excelSheetRow.getCell(excelColIndex);
	}

	@Override
	protected List<Cell> getSecondaryCells(PieceType pieceType, String letterPair) {
		return Collections.emptyList();
	}

	@Override
	protected PieceType[] getSupportedPieceTypes() {
		return new PieceType[]{LetterPairImage.NOUN};
	}
}
