package com.suushiemaniac.cubing.bld.model.enumeration;

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;

public enum CubicPieceType implements PieceType {
    CENTER(1, 6, 0, "Center", "Ce"),
    CORNER(3, 8, 378, "Corner", "C"),
    EDGE(2, 12, 440, "Edge", "E"),
    XCENTER(1, 24, 460, "X-Center", "X"),
    WING(1, 24, 506, "Wing", "W"),
    TCENTER(1, 24, 460, "T-Center", "T"),
    INNERXCENTER(1, 24, 460, "Inner X-Center", "iX"),
    INNERWING(1, 24, 506, "Inner Wing", "iW"),
    LEFTOBLIQUE(1, 24, 460, "Left Oblique", "LO"),
    RIGHTOBLIQUE(1, 24, 460, "Right Oblique", "RO"),
    INNERTCENTER(1, 24, 460, "Inner T-Center", "iT");

    public static final NotationReader READER = new CubicAlgorithmReader();

    public static String[] nameArray() {
        String[] names = new String[values().length];
        for (int i = 0; i < values().length; i++) names[i] = values()[i].name();
        return names;
    }

    private int targetsPerPiece, numPieces, numAlgs;
	private String human, mnemonic;

    CubicPieceType(int targetsPerPiece, int numPieces, int numAlgs, String human, String mnemonic) {
        this.targetsPerPiece = targetsPerPiece;
        this.numPieces = numPieces;
        this.numAlgs = numAlgs;

        this.human = human;
        this.mnemonic = mnemonic;
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
		return this.human;
    }

    @Override
    public String mnemonic() {
        return this.mnemonic;
    }
}