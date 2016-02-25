package com.suushiemaniac.cubing.bld.model.enumeration;

public interface PieceType {
    int getTargetsPerPiece();

    int getNumPieces();

    int getNumPiecesNoBuffer();

    int getNumAlgs();

    String name();
}
