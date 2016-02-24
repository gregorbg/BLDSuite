package com.suushiemaniac.cubing.bld.exception;

public class InvalidExcelMapSizeError extends Error {
    public InvalidExcelMapSizeError(int size, int expectedSize) {
        super("Expected size " + expectedSize + ", found: " + size);
    }
}
