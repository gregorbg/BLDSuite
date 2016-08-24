package com.suushiemaniac.cubing.bld.model.enumeration;

import com.suushiemaniac.cubing.alglib.lang.MegaminxAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;

public enum DodecahedronPieceType implements PieceType {
    CORNER, EDGE, CENTER;

    public static final NotationReader READER = new MegaminxAlgorithmReader();

    @Override
    public int getTargetsPerPiece() {
        return 0;
    }

    @Override
    public int getNumPieces() {
        return 0;
    }

    @Override
    public int getNumPiecesNoBuffer() {
        return 0;
    }

    @Override
    public int getNumAlgs() {
        return 0;
    }

    @Override
    public NotationReader getReader() {
        return DodecahedronPieceType.READER;
    }

	@Override
	public String humanName() {
		switch (this) {
			case CORNER:
				return "Corner";
			case EDGE:
				return "Edge";
			case CENTER:
				return "Center";
			default:
				return "";
		}
	}

	@Override
	public String mnemonic() {
		switch (this) {
			case CORNER:
				return "C";
			case EDGE:
				return "E";
			case CENTER:
				return "Ce";
			default:
				return "";
		}
	}
}
