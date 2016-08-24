package com.suushiemaniac.cubing.bld.model.enumeration;

import com.suushiemaniac.cubing.alglib.lang.NotationReader;

public interface PieceType {
    int getTargetsPerPiece();

    int getNumPieces();

    int getNumPiecesNoBuffer();

    int getNumAlgs();

    String name();

    String humanName();

	String mnemonic();

    NotationReader getReader();
}
