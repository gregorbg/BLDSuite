package com.suushiemaniac.cubing.bld.util

import com.suushiemaniac.cubing.bld.model.cycle.PieceCycle
import com.suushiemaniac.cubing.bld.model.cycle.ThreeCycle
import com.suushiemaniac.cubing.bld.model.enumeration.piece.CubicPieceType
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType

import com.suushiemaniac.cubing.bld.util.StringUtil.contentSetEquals
import com.suushiemaniac.cubing.bld.util.StringUtil.toCharStrings
import com.suushiemaniac.cubing.bld.util.ArrayUtil.applyIndex

object SpeffzUtil {
    val FULL_SPEFFZ = arrayOf("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X")

    val CORNER_MAPPING = arrayOf("UBL", "URB", "UFR", "ULF", "LUB", "LFU", "LDF", "LBD", "FUL", "FRU", "FDR", "FLD", "RUF", "RBU", "RDB", "RFD", "BUR", "BLU", "BDL", "BRD", "DFL", "DRF", "DBR", "DLB")
    val EDGE_MAPPING = arrayOf("UB", "UR", "UF", "UL", "LU", "LF", "LD", "LB", "FU", "FR", "FD", "FL", "RU", "RB", "RD", "RF", "BU", "BL", "BD", "BR", "DF", "DR", "DB", "DL")
    val X_CENTER_MAPPING = arrayOf("Ubl", "Urb", "Ufr", "Ulf", "Lub", "Lfu", "Ldf", "Lbd", "Ful", "Fru", "Fdr", "Fld", "Ruf", "Rbu", "Rdb", "Rfd", "Bur", "Blu", "Bdl", "Brd", "Dfl", "Drf", "Dbr", "Dlb")
    val WING_MAPPING = arrayOf("UBr", "URf", "UFl", "ULb", "LUf", "LFd", "LDb", "LBu", "FUr", "FRd", "FDl", "FLu", "RUb", "RBd", "RDf", "RFu", "BUl", "BLd", "BDr", "BRu", "DFr", "DRb", "DBl", "DLf")
    val T_CENTER_MAPPING = arrayOf("Ub", "Ur", "Uf", "Ul", "Lu", "Lf", "Ld", "Lb", "Fu", "Fr", "Fd", "Fl", "Ru", "Rb", "Rd", "Rf", "Bu", "Bl", "Bd", "Br", "Df", "Dr", "Db", "Dl")

    fun Int.targetToSticker(type: PieceType): String {
        return getMapping(type)[this]
    }

    fun String.stickerToTarget(type: PieceType): Int {
        return getMapping(type).indexOf(this)
    }

    private fun getMapping(type: PieceType): Array<String> {
        if (type !is CubicPieceType) return arrayOf()

        return when (type) {
            CubicPieceType.CORNER -> CORNER_MAPPING
            CubicPieceType.EDGE -> EDGE_MAPPING
            CubicPieceType.XCENTER -> X_CENTER_MAPPING
            CubicPieceType.WING -> WING_MAPPING
            CubicPieceType.TCENTER -> T_CENTER_MAPPING
            else -> arrayOf()
        }
    }

    fun String.normalize(denormScheme: Array<String>): String {
        return mapLetters(this, denormScheme, FULL_SPEFFZ)
    }

    fun String.denormalize(denormScheme: Array<String>): String {
        return mapLetters(this, FULL_SPEFFZ, denormScheme)
    }

    private fun mapLetters(letters: String, originScheme: Array<String>, targetScheme: Array<String>): String {
        return letters.toCharStrings().joinToString("") { originScheme.applyIndex(it, targetScheme) }
    }

    fun PieceCycle.toSpeffz(): String {
        return this.toTargetString(FULL_SPEFFZ)
    }

    fun PieceCycle.toTargetString(scheme: Array<String>): String {
        return this.getAllTargets().joinToString("") { scheme[it] }
    }

    fun String.toThreeCycle(buffer: Int = 0): PieceCycle {
        return ThreeCycle(buffer, FULL_SPEFFZ.indexOf(this[0].toString()), FULL_SPEFFZ.indexOf(this[1].toString()))
    }

    fun String.stickerEquals(thatSticker: String): Boolean {
        return if (this.isEmpty() || thatSticker.isEmpty()) false
            else this.contentSetEquals(thatSticker) && this[0] == thatSticker[0]

    }
}