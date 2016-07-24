package com.suushiemaniac.cubing.bld.util;

import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class SpeffzUtil {
    private static String[] fullSpeffz = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X"};
    private static String[] cornerMapping = {"UBL", "URB", "UFR", "ULF", "LUB", "LFU", "LDF", "LBD", "FUL", "FRU", "FDR", "FLD", "RUF", "RBU", "RDB", "RFD", "BUR", "BLU", "BDL", "BRD", "DFL", "DRF", "DBR", "DLB"};
    private static String[] edgeMapping = {"UB", "UR", "UF", "UL", "LU", "LF", "LD", "LB", "FU", "FR", "FD", "FL", "RU", "RB", "RD", "RF", "BU", "BL", "BD", "BR", "DF", "DR", "DB", "DL"};
    private static String[] xCenterMapping = {"Ubl", "Urb", "Ufr", "Ulf", "Lub", "Lfu", "Ldf", "Lbd", "Ful", "Fru", "Fdr", "Fld", "Ruf", "Rbu", "Rdb", "Rfd", "Bur", "Blu", "Bdl", "Brd", "Dfl", "Drf", "Dbr", "Dlb"};
    private static String[] wingMapping = {"DFr", "UBr", "URf", "UFl", "ULb", "LUf", "LFd", "LDb", "LBu", "FUr", "FRd", "FDl", "FLu", "RUb", "RBd", "RDf", "RFu", "BUl", "BLd", "BDr", "BRu", "DRb", "DBl", "DLf"};
    private static String[] tCenterMapping = {"Df", "Dr", "Db", "Dl", "Ub", "Ur", "Uf", "Ul", "Lu", "Lf", "Ld", "Lb", "Fu", "Fr", "Fd", "Fl", "Ru", "Rb", "Rd", "Rf", "Bu", "Bl", "Bd", "Br"};

    public static String[] fullSpeffz() {
        return fullSpeffz;
    }

    public static String speffzToSticker(String speffz, PieceType type) {
        if (!(type instanceof CubicPieceType)) return "";
        switch ((CubicPieceType) type) {
            case CORNER:
                return ArrayUtil.mutualIndex(speffz, fullSpeffz, cornerMapping);
            case EDGE:
                return ArrayUtil.mutualIndex(speffz, fullSpeffz, edgeMapping);
            case XCENTER:
                return ArrayUtil.mutualIndex(speffz, fullSpeffz, xCenterMapping);
            case WING:
                return ArrayUtil.mutualIndex(speffz, fullSpeffz, wingMapping);
            case TCENTER:
                return ArrayUtil.mutualIndex(speffz, fullSpeffz, tCenterMapping);
            default:
                return "";
        }
    }

    public static String stickerToSpeffz(String sticker, PieceType type) {
        if (!(type instanceof CubicPieceType)) return "";
        switch ((CubicPieceType) type) {
            case CORNER:
                return ArrayUtil.mutualIndex(sticker, cornerMapping, fullSpeffz);
            case EDGE:
                return ArrayUtil.mutualIndex(sticker, edgeMapping, fullSpeffz);
            case XCENTER:
                return ArrayUtil.mutualIndex(sticker, xCenterMapping, fullSpeffz);
            case WING:
                return ArrayUtil.mutualIndex(sticker, wingMapping, fullSpeffz);
            case TCENTER:
                return ArrayUtil.mutualIndex(sticker, tCenterMapping, fullSpeffz);
            default:
                return "";
        }
    }

    public static String normalize(String denormLp, String[] denormScheme) {
        String[] denormSingleLetters = denormLp.split("");

        return ArrayUtil.mutualIndex(denormSingleLetters[0], denormScheme, fullSpeffz)
                + ArrayUtil.mutualIndex(denormSingleLetters[1], denormScheme, fullSpeffz);
    }
}