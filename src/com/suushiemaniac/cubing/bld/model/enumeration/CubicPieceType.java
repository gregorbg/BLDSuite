package com.suushiemaniac.cubing.bld.model.enumeration;

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;

public enum CubicPieceType implements PieceType {
    CENTER(0, 6, 0),
    CORNER(3, 8, 378),
    EDGE(2, 12, 440),
    XCENTER(1, 24, 460),
    WING(1, 24, 506),
    TCENTER(1, 24, 460),
    INNERXCENTER(1, 24, 460),
    INNERWING(1, 24, 506),
    LEFTOBLIQUE(1, 24, 460),
    RIGHTOBLIQUE(1, 24, 460),
    INNERTCENTER(1, 24, 460);

    public static final NotationReader READER = new CubicAlgorithmReader();

    public static String[] nameArray() {
        String[] names = new String[values().length];
        for (int i = 0; i < values().length; i++) names[i] = values()[i].name();
        return names;
    }

    private int targetsPerPiece, numPieces, numAlgs;

    CubicPieceType(int targetsPerPiece, int numPieces, int numAlgs) {
        this.targetsPerPiece = targetsPerPiece;
        this.numPieces = numPieces;
        this.numAlgs = numAlgs;
    }

    public int getTargetsPerPiece() {
        return this.targetsPerPiece;
    }

    public int getNumPieces() {
        return this.numPieces;
    }

    public int getNumPiecesNoBuffer() {
        return this.numPieces - 1;
    }

    public int getNumAlgs() {
        return this.numAlgs;
    }

    public NotationReader getReader() {
        return CubicPieceType.READER;
    }

    @Override
    public String humanName() {
		switch (this) {
			case CENTER:
				return "Center";
			case CORNER:
				return "Corner";
			case EDGE:
				return "Edge";
			case XCENTER:
				return "X-Center";
			case WING:
				return "Wing";
			case TCENTER:
				return "T-Center";
			case INNERXCENTER:
				return "Inner X-Center";
			case INNERWING:
				return "Inner Wing";
			case LEFTOBLIQUE:
				return "Left Oblique";
			case RIGHTOBLIQUE:
				return "Right Oblique";
			case INNERTCENTER:
				return "Inner T-Center";
			default:
				return "";
		}
    }

    @Override
    public String mnemonic() {
        switch (this) {
            case CENTER:
                return "Ce";
            case CORNER:
                return "C";
            case EDGE:
                return "E";
            case XCENTER:
                return "X";
            case WING:
                return "W";
            case TCENTER:
                return "T";
            case INNERXCENTER:
                return "iX";
            case INNERWING:
                return "iW";
            case LEFTOBLIQUE:
                return "LO";
            case RIGHTOBLIQUE:
                return "RO";
            case INNERTCENTER:
                return "iT";
            default:
                return "";
        }
    }
}