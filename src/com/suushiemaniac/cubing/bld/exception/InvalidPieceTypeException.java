package com.suushiemaniac.cubing.bld.exception;

import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

public class InvalidPieceTypeException extends Exception {
    public InvalidPieceTypeException(PieceType type) {
        super("The following piece type is not supported: " + type.name());
    }
}
