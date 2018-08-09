package com.suushiemaniac.cubing.bld.model.enumeration.piece;

import com.suushiemaniac.cubing.alglib.lang.NotationReader;

public interface PieceType {
	int getTargetsPerPiece();

	int getNumPieces();

	default int getNumPiecesNoBuffer() {
		return this.getNumPieces() - 1;
	}

	int getNumAlgs();

	String name();

	String humanName();

	String mnemonic();

	NotationReader getReader();
}
