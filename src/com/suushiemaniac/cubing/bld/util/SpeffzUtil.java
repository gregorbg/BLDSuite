package com.suushiemaniac.cubing.bld.util;

import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

public class SpeffzUtil {
    public static final String[] FULL_SPEFFZ = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X"};

    public static final String[] CORNER_MAPPING = {"UBL", "URB", "UFR", "ULF", "LUB", "LFU", "LDF", "LBD", "FUL", "FRU", "FDR", "FLD", "RUF", "RBU", "RDB", "RFD", "BUR", "BLU", "BDL", "BRD", "DFL", "DRF", "DBR", "DLB"};
    public static final String[] EDGE_MAPPING = {"UB", "UR", "UF", "UL", "LU", "LF", "LD", "LB", "FU", "FR", "FD", "FL", "RU", "RB", "RD", "RF", "BU", "BL", "BD", "BR", "DF", "DR", "DB", "DL"};
    public static final String[] X_CENTER_MAPPING = {"Ubl", "Urb", "Ufr", "Ulf", "Lub", "Lfu", "Ldf", "Lbd", "Ful", "Fru", "Fdr", "Fld", "Ruf", "Rbu", "Rdb", "Rfd", "Bur", "Blu", "Bdl", "Brd", "Dfl", "Drf", "Dbr", "Dlb"};
    public static final String[] WING_MAPPING = {"DFr", "UBr", "URf", "UFl", "ULb", "LUf", "LFd", "LDb", "LBu", "FUr", "FRd", "FDl", "FLu", "RUb", "RBd", "RDf", "RFu", "BUl", "BLd", "BDr", "BRu", "DRb", "DBl", "DLf"};
    public static final String[] T_CENTER_MAPPING = {"Df", "Dr", "Db", "Dl", "Ub", "Ur", "Uf", "Ul", "Lu", "Lf", "Ld", "Lb", "Fu", "Fr", "Fd", "Fl", "Ru", "Rb", "Rd", "Rf", "Bu", "Bl", "Bd", "Br"};

    public static String[] fullSpeffz() {
        return FULL_SPEFFZ;
    }

    public static String speffzToSticker(String speffz, PieceType type) {
        if (!(type instanceof CubicPieceType)) return "";
        switch ((CubicPieceType) type) {
            case CORNER:
                return ArrayUtil.mutualIndex(speffz, FULL_SPEFFZ, CORNER_MAPPING);
            case EDGE:
                return ArrayUtil.mutualIndex(speffz, FULL_SPEFFZ, EDGE_MAPPING);
            case XCENTER:
                return ArrayUtil.mutualIndex(speffz, FULL_SPEFFZ, X_CENTER_MAPPING);
            case WING:
                return ArrayUtil.mutualIndex(speffz, FULL_SPEFFZ, WING_MAPPING);
            case TCENTER:
                return ArrayUtil.mutualIndex(speffz, FULL_SPEFFZ, T_CENTER_MAPPING);
            default:
                return "";
        }
    }

    public static String stickerToSpeffz(String sticker, PieceType type) {
        if (!(type instanceof CubicPieceType)) return "";
        switch ((CubicPieceType) type) {
            case CORNER:
                return ArrayUtil.mutualIndex(sticker, CORNER_MAPPING, FULL_SPEFFZ);
            case EDGE:
                return ArrayUtil.mutualIndex(sticker, EDGE_MAPPING, FULL_SPEFFZ);
            case XCENTER:
                return ArrayUtil.mutualIndex(sticker, X_CENTER_MAPPING, FULL_SPEFFZ);
            case WING:
                return ArrayUtil.mutualIndex(sticker, WING_MAPPING, FULL_SPEFFZ);
            case TCENTER:
                return ArrayUtil.mutualIndex(sticker, T_CENTER_MAPPING, FULL_SPEFFZ);
            default:
                return "";
        }
    }

    public static String normalize(String denormLp, String[] denormScheme) {
        String[] denormSingleLetters = denormLp.split("");

        return ArrayUtil.mutualIndex(denormSingleLetters[0], denormScheme, FULL_SPEFFZ)
                + ArrayUtil.mutualIndex(denormSingleLetters[1], denormScheme, FULL_SPEFFZ);
    }
}