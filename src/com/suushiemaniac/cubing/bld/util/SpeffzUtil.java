package com.suushiemaniac.cubing.bld.util;

import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;

import java.util.Arrays;
import java.util.stream.Collectors;

public class SpeffzUtil {
    public static final String[] FULL_SPEFFZ = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X"};

    public static final String[] CORNER_MAPPING = {"UBL", "URB", "UFR", "ULF", "LUB", "LFU", "LDF", "LBD", "FUL", "FRU", "FDR", "FLD", "RUF", "RBU", "RDB", "RFD", "BUR", "BLU", "BDL", "BRD", "DFL", "DRF", "DBR", "DLB"};
    public static final String[] EDGE_MAPPING = {"UB", "UR", "UF", "UL", "LU", "LF", "LD", "LB", "FU", "FR", "FD", "FL", "RU", "RB", "RD", "RF", "BU", "BL", "BD", "BR", "DF", "DR", "DB", "DL"};
    public static final String[] X_CENTER_MAPPING = {"Ubl", "Urb", "Ufr", "Ulf", "Lub", "Lfu", "Ldf", "Lbd", "Ful", "Fru", "Fdr", "Fld", "Ruf", "Rbu", "Rdb", "Rfd", "Bur", "Blu", "Bdl", "Brd", "Dfl", "Drf", "Dbr", "Dlb"};
    public static final String[] WING_MAPPING = {"UBr", "URf", "UFl", "ULb", "LUf", "LFd", "LDb", "LBu", "FUr", "FRd", "FDl", "FLu", "RUb", "RBd", "RDf", "RFu", "BUl", "BLd", "BDr", "BRu", "DFr", "DRb", "DBl", "DLf"};
    public static final String[] T_CENTER_MAPPING = {"Ub", "Ur", "Uf", "Ul", "Lu", "Lf", "Ld", "Lb", "Fu", "Fr", "Fd", "Fl", "Ru", "Rb", "Rd", "Rf", "Bu", "Bl", "Bd", "Br", "Df", "Dr", "Db", "Dl"};

    public static String[] fullSpeffz() {
        return FULL_SPEFFZ;
    }

    public static String speffzToSticker(String speffz, PieceType type) {
		return ArrayUtil.mutualIndex(speffz, FULL_SPEFFZ, getMapping(type));
    }

    public static String stickerToSpeffz(String sticker, PieceType type) {
		return ArrayUtil.mutualIndex(sticker, getMapping(type), FULL_SPEFFZ);
    }

    public static String normalize(String denormLetters, String[] denormScheme) {
        return mapLetters(denormLetters, denormScheme, FULL_SPEFFZ);
    }

    public static String denormalize(String speffzLetters, String[] denormScheme) {
    	return mapLetters(speffzLetters, FULL_SPEFFZ, denormScheme);
	}

    protected static String mapLetters(String letters, String[] originScheme, String[] targetScheme) {
		String[] letterList = letters.split("");
		return String.join("", Arrays.stream(letterList).map(letter -> ArrayUtil.mutualIndex(letter, originScheme, targetScheme)).collect(Collectors.toList()));
	}

    public static String[] getMapping(PieceType type) {
        if (!(type instanceof CubicPieceType)) return null;
    	switch ((CubicPieceType) type) {
            case CORNER:
                return CORNER_MAPPING;
			case EDGE:
				return EDGE_MAPPING;
			case XCENTER:
				return X_CENTER_MAPPING;
			case WING:
				return WING_MAPPING;
			case TCENTER:
				return T_CENTER_MAPPING;
			default:
				return null;
        }
    }
}