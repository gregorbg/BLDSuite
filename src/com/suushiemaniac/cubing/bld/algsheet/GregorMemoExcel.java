package com.suushiemaniac.cubing.bld.algsheet;

import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GregorMemoExcel extends BldAlgSheet {
	protected static final int BETWEEN_ROW_SPACING = 3;
	protected static final int BETWEEN_COL_SPACING = 1;

	public GregorMemoExcel(File excelFile) {
		super(excelFile);
	}

	private int getSheetNum(PieceType pieceType) {
		if (pieceType instanceof LetterPairImage) {
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

		int lpOneIndex = ((int) letterPair.toLowerCase().charAt(0)) - 97;
		int lpTwoIndex = ((int) letterPair.toLowerCase().charAt(1)) - 97;

		if (lpOneIndex > 23 || lpTwoIndex > 23) {
			return null;
		}

		int blockColumn = lpOneIndex / 4;
		int blockRow = lpOneIndex % 4;

		//noinspection SuspiciousMethodCalls
		int column = Arrays.asList(LetterPairImage.NOUN, LetterPairImage.ADJECTIVE, LetterPairImage.VERB).indexOf(pieceType);
		int row = lpTwoIndex + 1;

		// overall header line + vertical block size * blockRow + line inside block segment
		int excelRowIndex = 1 + (BETWEEN_ROW_SPACING + 25) * blockRow + row;
		Row excelSheetRow = sheet.getRow(excelRowIndex);

		// horizontal block size * blockColumn + sidebar + col inside block segment
		int excelColIndex = (BETWEEN_COL_SPACING + 5) * blockColumn + 2 + column;
		return excelSheetRow.getCell(excelColIndex);
	}

	@Override
	protected List<Cell> getSecondaryCells(PieceType pieceType, String letterPair) {
		return Collections.emptyList();
	}

	@Override
	protected PieceType[] getSupportedPieceTypes() {
		return new PieceType[]{LetterPairImage.NOUN, LetterPairImage.ADJECTIVE, LetterPairImage.VERB};
	}

	@Override
	public Set<String> getRawAlgorithms(PieceType type, String letterPair) {
		if (type == LetterPairImage.ANY) {
			return Stream.of(LetterPairImage.NOUN, LetterPairImage.ADJECTIVE, LetterPairImage.VERB)
					.flatMap(subType -> this.getRawAlgorithms(subType, letterPair).stream())
					.collect(Collectors.toSet());
		}

		return super.getRawAlgorithms(type, letterPair).stream()
				.map(algStr -> algStr.replace("ENNVAU", "NV"))
				.flatMap(algStr -> Arrays.stream(algStr.split(" / ")))
				.collect(Collectors.toSet());
	}
}
