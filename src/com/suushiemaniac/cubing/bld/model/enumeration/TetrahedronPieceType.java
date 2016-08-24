package com.suushiemaniac.cubing.bld.model.enumeration;

import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.alglib.lang.PyraminxAlgorithmReader;

public enum TetrahedronPieceType implements PieceType {
    TIP, CENTER, EDGE;

    public static final NotationReader READER = new PyraminxAlgorithmReader();

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
        return TetrahedronPieceType.READER;
    }

    @Override
    public String humanName() {
        switch (this) {
            case TIP:
                return "Tip";
            case CENTER:
                return "Center";
            case EDGE:
                return "Edge";
            default:
                return "";
        }
    }

    @Override
    public String mnemonic() {
        switch (this) {
            case TIP:
                return "T";
            case CENTER:
                return "C";
            case EDGE:
                return "E";
            default:
                return "";
        }
    }
}
