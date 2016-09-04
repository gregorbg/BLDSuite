import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.exception.InvalidNotationException;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.algsheet.BldAlgSheet;
import com.suushiemaniac.cubing.bld.algsheet.GregorBldExcel;
import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.FiveBldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import com.suushiemaniac.cubing.bld.database.CubeDb;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.optim.BreakInOptim;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.NoInspectionFiveByFiveCubePuzzle;
import puzzle.NoInspectionThreeByThreeCubePuzzle;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        //System.out.println(new CubicAlgorithmReader().parse("[U2; [U, [U'; [U2; [U, M']]]]]").plain().toFormatString());

        File excelFile = new File("/home/suushie_maniac/Schreibtisch/3Style_Gregor.xlsx");

        if (!excelFile.exists()) return;
        BldAlgSheet source = new GregorBldExcel(excelFile);

        CubeDb target = new CubeDb("jdbc:mysql://localhost:3306/bld-algs?useSSL=false&user=root&password=localsql");

        //source.getAlg(CubicPieceType.CORNER, "PU").forEach(alg -> System.out.println(alg.toFormatString()));

        //printBadAlgorithms(source);
        //migrateExcelToDb(source, target);

		Set<Algorithm> algList = target.getAlg(CORNER, "VN");
		for (Algorithm alg : algList) {
			System.out.println(alg.toFormatString());
		}

        BreakInOptim optim = new BreakInOptim(target, false);
		System.out.println(optim.optimizeBreakInTargetsAfter("P", CORNER));
		System.out.println(optim.optimizeBreakInTargetsAfter("K", CORNER));
		System.out.println(optim.optimizeBreakInTargetsAfter("V", CORNER));
		System.out.println(optim.optimizeBreakInTargetsAfter("W", CORNER));
		System.out.println(optim.optimizeBreakInTargetsAfter("O", CORNER));
		System.out.println(optim.optimizeBreakInTargetsAfter("T", CORNER));

		BldCube newCube = new ThreeBldCube();
		newCube.setAlgSource(target);
		Puzzle tNoodle = new NoInspectionThreeByThreeCubePuzzle();
		NotationReader reader = new CubicAlgorithmReader();

		for (int i = 0; i < 50; i++) {
			//String scrString = tNoodle.generateScramble();
			String scrString = tNoodle.generateScramble();

			Algorithm scramble = reader.parse(scrString);
			newCube.parseScramble(scramble);

			System.out.println(scrString);
			System.out.println(newCube.getSolutionPairs(true));
			System.out.println();
		}

		//System.out.println(new FiveBldCube("").jsonPermutations().toFormatString());
	}

    public static void printBadAlgorithms(AlgSource source) {
        CubicAlgorithmReader reader = new CubicAlgorithmReader();

        for (String lp : BruteForceUtil.genBlockString(SpeffzUtil.FULL_SPEFFZ, 2, false, false)) {
            for (PieceType pt : Arrays.asList(CORNER, EDGE, WING, XCENTER, TCENTER)) {
                Set<String> rawAlgs = source.getRawAlg(pt, lp);
                if (rawAlgs == null) continue;

                for (String alg : rawAlgs) {
                    try {
                        reader.parse(alg);
                    } catch (InvalidNotationException e) {
                        System.out.println("bad alg @ " + pt.name() + ":" + lp);
                    }
                }
            }
        }
    }

    public static void migrateExcelToDb(BldAlgSheet excel, CubeDb sql) throws SQLException {
        CubicAlgorithmReader reader = new CubicAlgorithmReader();

        for (String lp : BruteForceUtil.genBlockString(SpeffzUtil.FULL_SPEFFZ, 2, false, false)) {
            for (PieceType pt : Arrays.asList(CORNER, EDGE, WING, XCENTER, TCENTER)) {
                Set<String> rawAlgs = excel.getRawAlg(pt, lp);
                if (rawAlgs == null) continue;

                for (String alg : rawAlgs) {
                    try {
                        reader.parse(alg);
                        sql.addAlgorithm(pt, lp, alg);
                    } catch (InvalidNotationException e) {
                        System.out.println("bad alg @ " + pt.name() + ":" + lp);
                    }
                }
            }
        }
    }
}