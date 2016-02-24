package com.suushiemaniac.cubing.bld.exception;

import com.suushiemaniac.cubing.bld.enumeration.CubicPieceType;

public class InvalidPieceTypeException extends Exception {
    public InvalidPieceTypeException(CubicPieceType type) {
        super("The following piece type is not supported: " + type.name());
    }
}
