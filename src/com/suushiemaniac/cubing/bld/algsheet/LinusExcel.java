package com.suushiemaniac.cubing.bld.algsheet;

import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class LinusExcel extends BldAlgSheet {
	public static final int BLOCK_HEADER_SIZE = 2;

	public LinusExcel(File excelFile) {
		super(excelFile);
	}

	public LinusExcel(File excelFile, boolean fullCache) {
		super(excelFile, fullCache);
	}

	private int getSheetNum(PieceType pieceType) {
		if (pieceType == LetterPairImage.NOUN) {
			return 5;
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

		int lpOneIndex = ((int) letterPair.toLowerCase().charAt(0)) - 97;
		int lpTwoIndex = ((int) letterPair.toLowerCase().charAt(1)) - 97;

		if (lpOneIndex > 23 || lpTwoIndex > 23) {
			return null;
		}

		int blockCol = lpOneIndex / 12;
		int blockRow = lpOneIndex % 12;

		int innerBlockCol = lpTwoIndex / 12;
		int innerBlockRow = lpTwoIndex % 12;

		// vertical block size * blockRow + block header space + line inside block segment
		int excelRowIndex = (BLOCK_HEADER_SIZE + 12) * blockRow + BLOCK_HEADER_SIZE + innerBlockRow;
		Row excelSheetRow = sheet.getRow(excelRowIndex);

		// horizontal block size * blockColumn + sidebar + col inside block segment
		int excelColIndex = 5 * blockCol + 1 + 2 * innerBlockCol + 1;
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

	@Override
	public Set<String> getRawAlgorithms(PieceType type, String letterPair) {
		if (type == LetterPairImage.ANY) {
			return this.getRawAlgorithms(LetterPairImage.NOUN, letterPair);
		}

		return super.getRawAlgorithms(type, letterPair).stream()
				.flatMap(algStr -> Arrays.stream(algStr.split("/")))
				.collect(Collectors.toSet());
	}
}
