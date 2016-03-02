package com.suushiemaniac.cubing.bld;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.bld.algsheet.BldAlgSheet;
import com.suushiemaniac.cubing.bld.algsheet.GregorBldExcel;
import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import com.suushiemaniac.cubing.bld.filter.ThreeBldScramble;
import com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.optim.BreakInOptim;
import com.suushiemaniac.cubing.bld.verify.Verificator;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        //ThreeBldScramble.fromStatString("C:  4  # ~ +++ | E: 14  ###").findScrambleThreadModel(1, 12);
        //new ThreeMassAnalyzer().analyzeScrambleDist(10000);
        //ThreeBldScramble.fromStatString("C:  8  #  | E: 12  #").findScrambleThreadModel(12, 5);
        //optimizeBreakIns();
        //evaluateOPAlgs();
        //developAndPrintComm(CubicPieceType.CORNER, "TX");
        //threeBldTraining(12);
        for (int j = 0; j < 12; j++) {
            String scr = ThreeBldScramble.levelScramble(j).findScrambleOnThread();
            ThreeBldCube cube = new ThreeBldCube(scr);
            System.out.println(j + ":\t" + scr + ":\t\t" + cube.getStatString());
        }
    }

    public static void threeBldTraining(int num) {
        ThreeBldScramble.mostCommonScramble().findScrambleThreadModel(num, 16);
    }

    public static void evaluateOPAlgs(List<Algorithm> algList) {
        algList.sort((o1, o2) -> Boolean.compare(o1.getSubGroup().hasRotation(), o2.getSubGroup().hasRotation()));
        algList.sort((o1, o2) -> o1.getSubGroup().toFormatString().compareTo(o2.getSubGroup().toFormatString()));
        algList.sort((o1, o2) -> Integer.compare(o1.getSubGroup().size(), o2.getSubGroup().size()));
    }

    public static void optimizeBreakIns() {
        File excelFile = new File("/home/suushie_maniac/Schreibtisch/3Style_Gregor.xlsx");
        BldAlgSheet g = new GregorBldExcel(excelFile);
        BreakInOptim optim = new BreakInOptim(g);
        ThreeBldCube cube = new ThreeBldCube("");
        for (char c = 'A'; c < 'Y'; c++) {
            for (Algorithm alg : optim.optimizeBreakInsAfter(c, CubicPieceType.CORNER)) {
                cube.parseScramble(alg.plain().toFormatString());
                System.out.println(alg.moveLength() + ": " + alg.toFormatString() + " : " + alg.getSubGroup().toFormatString() + " // " + new StringBuilder(cube.getCornerPairs()).reverse().toString());
            }
            System.out.println();
        }
    }

    public static void developAndPrintComm(PieceType type, String lp) {
        File excelFile = new File("/home/suushie_maniac/Schreibtisch/3Style_Gregor.xlsx");
        BldAlgSheet g = new GregorBldExcel(excelFile);
        for (Algorithm alg : g.getAlg(type, lp)) {
            System.out.println(alg.plain().toFormatString());
            System.out.println(alg.inverse().plain().toFormatString());
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
