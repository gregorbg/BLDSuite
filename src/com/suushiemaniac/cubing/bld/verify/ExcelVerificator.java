package com.suushiemaniac.cubing.bld.verify;

import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.algsheet.BldAlgSheet;
import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.exception.InvalidPieceTypeException;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ExcelVerificator extends Verificator {
    private BldAlgSheet algSheet;

    public ExcelVerificator(NotationReader parser, BldAlgSheet algSheet) {
        super(parser);
        this.algSheet = algSheet;
    }

    @Override
    protected List<String> getAlgStrings(PieceType type, String letterPair) {
        try {
            Map<String, List<String>> algStringMap = this.algSheet.algStringsFromExcel(type);
            return algStringMap.get(letterPair);
        } catch (InvalidPieceTypeException e) {
            e.printStackTrace();
            return Collections.singletonList(e.getMessage());
        }
    }
}
