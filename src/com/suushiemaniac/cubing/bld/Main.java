package com.suushiemaniac.cubing.bld;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.bld.algsheet.BldAlgSheet;
import com.suushiemaniac.cubing.bld.algsheet.GregorBldExcel;
import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.optim.BreakInOptim;
import com.suushiemaniac.cubing.bld.verify.Verificator;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //new ThreeMassAnalyzer().analyzeScrambleDist(10000);
        //ThreeBldScramble.fromStatString("C:  8  #  | E: 12  #").findScrambleThreadModel(12, 5);
        optimizeBreakIns();
    }

    public static void optimizeBreakIns() {
        File excelFile = new File("/home/suushie_maniac/Schreibtisch/3Style_Gregor.xlsx");
        BldAlgSheet g = new GregorBldExcel(excelFile);
        BreakInOptim optim = new BreakInOptim(g);
        ThreeBldCube cube = new ThreeBldCube("");
        for (Algorithm alg : g.getAlg(CubicPieceType.CORNER, "TX")) {
            System.out.println(alg.plain().toFormatString());
            System.out.println(alg.inverse().plain().toFormatString());
            System.out.println();
        }
        for (char c = 'A'; c < 'Y'; c++) {
            for (Algorithm alg : optim.optimizeBreakInsAfter(c, CubicPieceType.CORNER)) {
                cube.parseScramble(alg.plain().toFormatString());
                System.out.println(alg.moveLength() + ": " + alg.toFormatString() + " : " + alg.getSubGroup().toFormatString() + " // " + new StringBuilder(cube.getCornerPairs()).reverse().toString());
            }
            System.out.println();
        }
    }

    public static void checkAndPrintParseableSolveable() {
        File excelFile = new File("/home/suushie_maniac/Schreibtisch/3Style_Gregor.xlsx");
        BldAlgSheet g = new GregorBldExcel(excelFile);
        Verificator verif = new Verificator(new CubicAlgorithmReader(), g);

        System.out.println("Unparseable " + CubicPieceType.EDGE.name());
        Map<String, List<String>> unparseable = verif.checkParseable(CubicPieceType.EDGE);
        int totalUnparseable = 0;
        for (String key : unparseable.keySet())
            for (String alg : unparseable.get(key)) {
                System.out.println(key + ": " + alg);
                totalUnparseable++;
            }
        System.out.println("TOTAL: " + totalUnparseable);
        System.out.println();

        System.out.println("Incorrect " + CubicPieceType.CORNER.name() + " algs");
        Map<String, Map<String, Boolean>> fullSolutionsMap = verif.verifyAll(CubicPieceType.CORNER);
        int totalIncorrect = 0;
        for (String key : fullSolutionsMap.keySet()) {
            Map<String, Boolean> solutionMap = fullSolutionsMap.get(key);
            for (String alg : solutionMap.keySet())
                if (!solutionMap.get(alg)) {
                    System.out.println(key + ": " + alg);
                    totalIncorrect++;
                }
        }
        System.out.println("TOTAL: " + totalIncorrect);
        System.out.println();
    }
}
