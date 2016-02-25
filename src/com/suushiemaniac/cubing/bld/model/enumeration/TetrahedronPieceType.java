package com.suushiemaniac.cubing.bld.model.enumeration;

public enum TetrahedronPieceType implements PieceType {
    TIP, CENTER, EDGE;

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
}
