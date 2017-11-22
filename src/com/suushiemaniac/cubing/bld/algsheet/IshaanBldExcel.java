package com.suushiemaniac.cubing.bld.algsheet;

import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.util.ArrayUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.CORNER;
import static com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType.EDGE;

public class IshaanBldExcel extends BldAlgSheet {
	public IshaanBldExcel(File excelFile, boolean fullCache) {
		super(excelFile, fullCache);
	}

	public IshaanBldExcel(File excelFile) {
		this(excelFile, false);
	}

	private int getSheetNum(PieceType pieceType) {
		if (pieceType instanceof CubicPieceType) {
			CubicPieceType[] sheetOrderTypes = new CubicPieceType[]{EDGE, CORNER};
			return ArrayUtil.binarySearch(pieceType, sheetOrderTypes);
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

		char firstIndex = letterPair.charAt(0);
		char secondIndex = letterPair.charAt(1);

		int border = pieceType.getNumPiecesNoBuffer() * pieceType.getTargetsPerPiece();

		Row horizontalRow = sheet.getRow(0);

		for (int i = 0; i < border; i++) {
			String horizontalTitle = horizontalRow.getCell(2 + i).getStringCellValue();

			if (horizontalTitle.charAt(horizontalTitle.length() - 2) == firstIndex) {
				for (int j = 0; j < border; j++) {
					Row verticalRow = sheet.getRow(2 + j);
					String verticalTitle = verticalRow.getCell(0).getStringCellValue();

					if (verticalTitle.charAt(verticalTitle.length() - 2) == secondIndex) {
						Cell candidate = verticalRow.getCell(2 + i);
						String candidateAlg = candidate.getStringCellValue().trim();

						if (candidateAlg.length() > 0) {
							return candidate;
						}
					}
				}
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
		return new PieceType[]{CubicPieceType.CORNER, CubicPieceType.EDGE};
	}

	@Override
	public Set<String> getRawAlgorithms(PieceType type, String letterPair) {
		Set<String> baseAlgs = super.getRawAlgorithms(type, letterPair);
		Set<String> algs = new HashSet<>();

		Pattern extract = Pattern.compile("^(.*)\\s*\\(([A-Za-z]{2})\\)$");

		for (String alg : baseAlgs) {
			Matcher algMatcher = extract.matcher(alg.trim());

			if (algMatcher.find()) {
				alg = algMatcher.group(1).trim();
				String actualPair = algMatcher.group(2).toUpperCase().trim();

				if (!actualPair.equals(letterPair.toUpperCase())) {
					continue;
				}

				if ((alg.contains(",") || alg.contains(":") || alg.contains(";")) && !(alg.startsWith("[") && alg.endsWith("]"))) {
					alg = "[" + alg + "]";
				}

				algs.add(alg.replace("2'", "2"));
			}
		}

		return algs;
	}
}
