import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.exception.InvalidNotationException;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.algsheet.BldAlgSheet;
import com.suushiemaniac.cubing.bld.analyze.cube.BldCube;
import com.suushiemaniac.cubing.bld.analyze.cube.BldPuzzle;
import com.suushiemaniac.cubing.bld.analyze.cube.ThreeBldCube;
import com.suushiemaniac.cubing.bld.analyze.stat.MassAnalyzer;
import com.suushiemaniac.cubing.bld.analyze.stat.ThreeMassAnalyzer;
import com.suushiemaniac.cubing.bld.database.CubeDb;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.CubicPuzzle;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import com.suushiemaniac.cubing.wca.comp.Event;
import com.suushiemaniac.cubing.wca.result.Scramble;
import net.gnehzr.tnoodle.scrambles.Puzzle;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Set;

import static com.suushiemaniac.cubing.bld.model.enumeration.CubicPieceType.*;

public class Main {
    public static void main(String[] args) throws SQLException, IOException {
        //System.out.println(new CubicAlgorithmReader().parse("[U2; [U, [U'; [U2; [U, M']]]]]").plain().toFormatString());

        CubeDb target = new CubeDb("jdbc:mysql://localhost:3306/bld-algs?useSSL=false&user=root&password=localsql");

		CubicPuzzle puzzle = CubicPuzzle.fromSize(3, "BLD");

		BldPuzzle newCube = puzzle.getAnalyzingPuzzle();
		NotationReader reader = puzzle.getReader();

		Algorithm superSolve = reader.parse("U2 F2 R2 F U2 R2 U2 F2 R2 B' F2 U L2 B' U2 B L B' D2 R'");
		newCube.setAlgSource(target);

		if (newCube instanceof ThreeBldCube) {
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 6; j++) {
					if (i == j) {
						continue;
					}

					((ThreeBldCube) newCube).setSolvingOrientation(i, j);
					newCube.parseScramble(superSolve);
					System.out.print(i + ", " + j + ": ");
					System.out.println(newCube.getStatString());
				}
			}
		}

		/*Event threeBlind = Event.fromID("333bf");
		Scramble[] scrambles = Scramble.allForEvent(threeBlind);

		for (Scramble scramble : scrambles) {
			if (scramble.getScramble().length() <= 0 || scramble.isExtra()) {
				continue;
			}

			if (!scramble.getCompetitionId().equals("Euro2016")) {
				continue;
			}

			Algorithm parsedScramble = reader.parse(scramble.getScramble());
			newCube.parseScramble(parsedScramble);

			System.out.println(scramble.getCompetitionId());
			System.out.println(scramble.getRoundId());
			System.out.println(scramble.getGroupId());
			System.out.println(scramble.getScrambleNum());
			System.out.println(scramble.getScramble());
			System.out.println("SOLUTION");
			System.out.println(newCube.getSolutionPairs(true));
			System.out.println();
		}*/

		//MassAnalyzer threeMass = new ThreeMassAnalyzer();
		//threeMass.analyzeProperties(Arrays.stream(scrambles).filter(scr -> scr.getScramble().length() > 0).map(scr -> reader.parse(scr.getScramble())).collect(Collectors.toList()));
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