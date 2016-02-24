package com.suushiemaniac.cubing.bld;

import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.bld.algsheet.BldAlgSheet;
import com.suushiemaniac.cubing.bld.algsheet.GregorBldExcel;
import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import com.suushiemaniac.cubing.bld.analyze.stat.MassAnalyzer;
import com.suushiemaniac.cubing.bld.enumeration.CubicPieceType;
import com.suushiemaniac.cubing.bld.verify.ExcelVerificator;
import com.suushiemaniac.cubing.bld.verify.Verificator;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.NoInspectionThreeByThreeCubePuzzle;

import java.io.File;
import java.util.List;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Puzzle threeNoodle = new NoInspectionThreeByThreeCubePuzzle();
        ThreeBldCube threeAnalyze = new ThreeBldCube("");
        for (int i = 0; i < 100; i++) {
            String scramble = threeNoodle.generateScramble();
            threeAnalyze.parseScramble(scramble);
            System.out.println(threeAnalyze.getStatString());
            if (threeAnalyze.isCornerBufferSolved()) System.out.println("CBuf: " + scramble);
            if (threeAnalyze.isEdgeBufferSolved()) System.out.println("EBuf: " + scramble);
            System.out.println();
        }
        //new ThreeMassAnalyzer().analyzeProperties(1000);
    }

    public static void checkAndPrintParseableSolveable() {
        File excelFile = new File("/home/suushie_maniac/Schreibtisch/3Style_Gregor.xlsx");
        BldAlgSheet g = new GregorBldExcel(excelFile);
        Verificator verif = new ExcelVerificator(new CubicAlgorithmReader(), g);

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

    public static void scrambleDistAnalysis(MassAnalyzer analyzer, int numCubes) {
        analyzer.analyzeScrambleDist(numCubes);
    }
}
