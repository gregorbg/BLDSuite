import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.alglib.exception.InvalidNotationException;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.algsheet.BldAlgSheet;
import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.FourBldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import com.suushiemaniac.cubing.bld.database.CubeDb;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.NoInspectionFourByFourCubePuzzle;
import puzzle.NoInspectionThreeByThreeCubePuzzle;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        //System.out.println(new CubicAlgorithmReader().parse("[U2; [U, [U'; [U2; [U, M']]]]]").plain().toFormatString());

        File excelFile = new File("/home/suushie_maniac/Schreibtisch/3Style_Gregor.xlsx");

        if (!excelFile.exists()) return;
        //BldAlgSheet source = new GregorBldExcel(excelFile);

        CubeDb target = new CubeDb("jdbc:mysql://localhost:3306/bld-algs?useSSL=false&user=root&password=localsql");

        //source.getAlg(CubicPieceType.CORNER, "PU").forEach(alg -> System.out.println(alg.toFormatString()));

        //printBadAlgorithms(source);
        //migrateExcelToDb(source, target);

        /*BreakInOptim optim = new BreakInOptim(target);

		for (char c = 'A'; c <= 'X'; c++) {
			System.out.println("" + c);
			List<String> breakIn = optim.optimizeBreakInsAfter(c, CORNER);

			for (String algCase : breakIn)
				System.out.println(algCase);

			System.out.println();
		}*/

		BldCube newCube = new FourBldCube();
		//newCube.setSolvingOrientation(0, 1);
		Puzzle tNoodle = new NoInspectionThreeByThreeCubePuzzle();
		NotationReader reader = new CubicAlgorithmReader();

		for (int i = 0; i < 10; i++) {
			//String scrString = tNoodle.generateScramble();
			String scrString = tNoodle.generateScramble();

			Algorithm scramble = reader.parse(scrString);
			scramble = new SimpleAlg(scramble.stream().filter(move -> move.getDepth() > 1).collect(Collectors.toList()));
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