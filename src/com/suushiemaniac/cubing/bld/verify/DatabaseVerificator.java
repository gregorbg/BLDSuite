package com.suushiemaniac.cubing.bld.verify;

import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.database.CubeH2;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class DatabaseVerificator extends Verificator {
    private CubeH2 databaseToCheck;

    public DatabaseVerificator(NotationReader parser, CubeH2 databaseToCheck) {
        super(parser);
        this.databaseToCheck = databaseToCheck;
    }

    @Override
    protected List<String> getAlgStrings(PieceType type, String letterPair) {
        try {
            return this.databaseToCheck.readAlgorithm(letterPair, type);
        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.singletonList(e.getMessage());
        }
    }
}