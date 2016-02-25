package com.suushiemaniac.cubing.bld.model.enumeration;

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
}