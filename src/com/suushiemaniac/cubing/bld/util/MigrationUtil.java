package com.suushiemaniac.cubing.bld.util;

import com.suushiemaniac.cubing.bld.database.CubeDb;
import com.suushiemaniac.cubing.bld.database.LegacyCubeDb;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.sql.SQLException;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public abstract class MigrationUtil {
    public static void migrateContents(LegacyCubeDb legacyDb, CubeDb db) throws SQLException {
        String[] allLpis = BruteForceUtil.genBlockString(BruteForceUtil.ALPHABET, 2, false);
        PieceType[] allPieceTypes = new PieceType[]{CORNER, EDGE, WING, XCENTER, TCENTER};

        for (String lpi : allLpis) {
            for (PieceType pieceType : allPieceTypes) {
                try {
                    for (String alg : legacyDb.readAlgorithm(lpi, pieceType)) {
                        db.addAlgorithm(pieceType, lpi, alg);
                    }
                } catch (NullPointerException e) {
                    System.out.println("NPE @ " + pieceType.name() + " : " + lpi);
                    return;
                } catch (ArrayIndexOutOfBoundsException ex) {
                    System.out.println("AIOOB @ " + pieceType.name() + " : " + lpi);
                }
            }

            /*for (String image : legacyDb.readLpi(lpi)) {
                db.addLpi(lpi, image);
            }*/
        }
    }
}
