package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.*;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import com.suushiemaniac.cubing.bld.filter.thread.ScrambleConsumer;
import com.suushiemaniac.cubing.bld.filter.thread.ScrambleProducer;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.*;

public class BldScramble {
    public static final String REGEX_UNIV = ".*";
    
    public static boolean ALLOW_TWISTED_BUFFER = true;
    
    public static void setAllowTwistedBuffer(boolean allowTwistedBuffer) {
    	BldScramble.ALLOW_TWISTED_BUFFER = allowTwistedBuffer;
	}

	protected BldPuzzle analyzingPuzzle;
	protected Supplier<BldPuzzle> analyzingSupplier;

	protected Puzzle scramblingPuzzle;
    protected Supplier<Puzzle> scramblingSupplier;

	protected Map<PieceType, BooleanCondition> parities;
	protected Map<PieceType, BooleanCondition> solvedBuffers;

	protected Map<PieceType, Boolean> allowTwistedBuffers;
	
	protected Map<PieceType, IntCondition> targets;
	protected Map<PieceType, IntCondition> breakIns;
	protected Map<PieceType, IntCondition> preSolved;
	protected Map<PieceType, IntCondition> misOriented;
	
	protected Map<PieceType, String> memoRegExp;
	protected Map<PieceType, String> predicateRegExp;

	public BldScramble(BldPuzzle analyzingPuzzle, Supplier<Puzzle> scramblingSupplier) {
		this.analyzingPuzzle = analyzingPuzzle;
		this.analyzingSupplier = analyzingPuzzle::clone;

		this.scramblingPuzzle = scramblingSupplier.get();
		this.scramblingSupplier = scramblingSupplier;

		this.parities = new HashMap<>();
		this.solvedBuffers = new HashMap<>();

		this.allowTwistedBuffers = new HashMap<>();

		this.targets = new HashMap<>();
		this.breakIns = new HashMap<>();
		this.preSolved = new HashMap<>();
		this.misOriented = new HashMap<>();

		this.memoRegExp = new HashMap<>();
		this.predicateRegExp = new HashMap<>();
	}

	protected BldPuzzle getAnalyzingPuzzle() {
		return this.analyzingPuzzle;
	}

	protected BldPuzzle generateAnalyzingPuzzle() {
		return this.analyzingSupplier.get();
	}

	protected Puzzle getScramblingPuzzle() {
		return this.scramblingPuzzle;
	}

	protected Puzzle generateScramblingPuzzle() {
		return this.scramblingSupplier.get();
	}

	public void setTargets(PieceType type, IntCondition targets) {
		targets.capMin(0);
		// C=10 E=16 W=34 XC=34 TC=34
		targets.capMax(((type.getNumPiecesNoBuffer() / 2) * 3) + (type.getNumPiecesNoBuffer() % 2));

		this.targets.put(type, targets);
		// pre-solved
		// mis-orient
		// parity
	}

	public void setBreakIns(PieceType type, IntCondition breakIns) {
		// C=7 E=11 W=23 XC=23 TC=23
		breakIns.capMin(Math.max(0, this.targets.get(type).getMin() - type.getNumPiecesNoBuffer()));
		// C=3 E=5 W=11 XC=11 TC=11
		breakIns.capMax(type.getNumPiecesNoBuffer() / 2);

		this.breakIns.put(type, breakIns);
		// targets
	}

	public void setParity(PieceType type, BooleanCondition hasParity) {
		if (this.targets.get(type).isExact()) {
			hasParity.setValue(this.targets.get(type).getMax() % 2 == 1);
		}

		this.parities.put(type, hasParity);
	}

	public void setBufferSolved(PieceType type, BooleanCondition bufferSolved) {
		this.solvedBuffers.put(type, bufferSolved); // TODO work out relations to other scramble properties!!
		// targets
		// break-ins
	}

	public void setAllowTwistedBuffer(PieceType type, boolean allowTwisted) {
		this.allowTwistedBuffers.put(type, allowTwisted);
	}

	public void setMemoRegex(PieceType type, String regex) {
		this.memoRegExp.put(type, regex);
	}

	public void filterExecution(PieceType type, AlgSource algSource, Predicate<Algorithm> filter) {
		SortedSet<String> matches = new TreeSet<>();
		String[] possPairs = BruteForceUtil.genBlockString(SpeffzUtil.FULL_SPEFFZ, 2, false);

		for (String pair : possPairs) {
			matches.addAll(algSource.getAlg(type, pair).stream().filter(filter).map(alg -> pair).collect(Collectors.toList()));
		}

		if (matches.size() > 0) {
			this.predicateRegExp.put(type, "(" + String.join("|", matches) + ")*" + (this.parities.get(type).getPositive() ? "[A-Z]?" : ""));
		}
	}

	public void setSolvedMisOriented(PieceType type, IntCondition solved, IntCondition misOriented) {
		// C=7 E=11 W=23 XC=23 TC=23
		int leftOverMin = Math.max(0, type.getNumPiecesNoBuffer() + this.breakIns.get(type).getMax() - this.targets.get(type).getMax());

		solved.capMin(0);
		misOriented.capMin(0);

		// C=8 E=12 W=? XC=? TC=?
		solved.capMax(type.getNumPieces());
		// C=8 E=12 W=? XC=? TC=?
		misOriented.capMax(type.getNumPieces());

		int sumMin = solved.getMin() + misOriented.getMin();
		int sumMax = solved.getMax() + misOriented.getMax();

		if (sumMin > leftOverMin) {
			if (solved.isPrecise() || !misOriented.isPrecise()) {
				misOriented.setMin(misOriented.getMin() - sumMin + leftOverMin);
			}

			if (misOriented.isPrecise() || !solved.isPrecise()) {
				solved.setMin(solved.getMin() - sumMin + leftOverMin);
			}
		} else if (sumMax < leftOverMin) {
			if (solved.isPrecise() || !misOriented.isPrecise()) {
				misOriented.setMax(misOriented.getMax() + leftOverMin - sumMax);
			}

			if (misOriented.isPrecise() || !solved.isPrecise()) {
				solved.setMax(misOriented.getMax() + leftOverMin - sumMax);
			}
		}

		this.preSolved.put(type, solved);
		this.misOriented.put(type, misOriented);
		// targets
	}

    public String findScrambleOnThread() {
        BldPuzzle testCube = this.getAnalyzingPuzzle();
        Puzzle tNoodle = this.getScramblingPuzzle();
        NotationReader reader = new CubicAlgorithmReader();

        String scramble;
        
        do {
            scramble = tNoodle.generateScramble();
            testCube.parseScramble(reader.parse(scramble));
        } while (!this.matchingConditions(testCube));

        return scramble;
    }

    public List<String> findScramblesOnThread(int num) {
        List<String> scrambles = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            if (i % (num / Math.min(100, num)) == 0) {
                System.out.println(i);
            }

            scrambles.add(this.findScrambleOnThread());
        }

        return scrambles;
    }

    public void findScrambleThreadModel(int numScrambles, int numThreads) {
        BlockingQueue<Algorithm> scrambleQueue = new ArrayBlockingQueue<>(50);
        
        ScrambleProducer producer = new ScrambleProducer(this.generateScramblingPuzzle(), scrambleQueue);
        ScrambleConsumer consumer = new ScrambleConsumer(this.generateAnalyzingPuzzle(), this::matchingConditions, numScrambles, scrambleQueue);

        for (int i = 0; i < numThreads; i++) {
            Thread genThread = new Thread(producer, "Producer " + (i + 1));
        
            genThread.setDaemon(true);
            genThread.start();
        }

        new Thread(consumer, "Consumer").start();
    }

    protected <T extends BldPuzzle> boolean matchingConditions(T inCube) {
		boolean matches = true;

		for (PieceType checkType : this.getAnalyzingPuzzle().getPieceTypes()) {
			matches &= this.matchingConditions(inCube, checkType);
		}

		return matches;
	}
	
	protected <T extends BldPuzzle> boolean matchingConditions(T inCube, PieceType checkType) {
		return this.getAnalyzingPuzzle().getClass().isInstance(inCube)
				&& this.parities.get(checkType).evaluatePositive(inCube.hasParity(checkType))
				&& this.solvedBuffers.get(checkType).evaluatePositive(inCube.isBufferSolved(checkType, this.allowTwistedBuffers.getOrDefault(checkType, ALLOW_TWISTED_BUFFER)))
				&& this.breakIns.get(checkType).evaluate(inCube.getBreakInCount(checkType))
				&& this.targets.get(checkType).evaluate(inCube.getStatLength(checkType))
				&& this.preSolved.get(checkType).evaluate(inCube.getPreSolvedCount(checkType))
				&& this.misOriented.get(checkType).evaluate(inCube.getMisOrientedCount(checkType))
				&& inCube.getSolutionPairs(checkType).replaceAll("\\s*", "").matches(this.memoRegExp.getOrDefault(checkType, REGEX_UNIV))
				&& inCube.getSolutionPairs(checkType).replaceAll("\\s*", "").matches(this.predicateRegExp.getOrDefault(checkType, REGEX_UNIV));
	}

	@Override
	public String toString() {
		List<String> pieceTypeStrings = new ArrayList<>();

		for (PieceType type : this.getAnalyzingPuzzle().getPieceTypes()) {
			pieceTypeStrings.add(this.toString(type));
		}

		return String.join(" | ", pieceTypeStrings);
	}

	protected String toString(PieceType type) {
		StringBuilder cornerStat = new StringBuilder(type.mnemonic() + ": ");

		cornerStat.append(this.parities.get(type).getPositive() ? "_" : " ");
		cornerStat.append(this.parities.get(type).isImportant() ? " " : "? ");

		cornerStat.append(this.targets.get(type).toString());
		cornerStat.append(" ");
		cornerStat.append(this.solvedBuffers.get(type).getPositive() ? "*" : " ");
		cornerStat.append(this.solvedBuffers.get(type).isImportant() ? "" : "?");
		cornerStat.append(" ");

		cornerStat.append(this.breakIns.get(type).toString("#"));
		cornerStat.append(" ");

		cornerStat.append(this.misOriented.get(type).toString("~"));
		cornerStat.append(" ");

		cornerStat.append(this.preSolved.get(type).toString("+"));

		return cornerStat.toString();
	}

    public boolean setBuffer(PieceType type, String newBuffer) {
        return this.getAnalyzingPuzzle().setBuffer(type, newBuffer);
    }

    public boolean setBuffer(PieceType type, int newBuffer) {
        return this.getAnalyzingPuzzle().setBuffer(type, newBuffer);
    }

    protected static int getNumInStatArray(int[] stat, int pos, int offset, int scale) {
        int mem = 0;
        
        for (int i = 0; i < stat.length; i++) {
            mem += stat[i];
            
            if (pos < mem) {
				return offset + scale * i;
			}
        }
        
        return 0;
    }

    protected static int getNumInStatArray(int[] stat, int pos) {
        return getNumInStatArray(stat, pos, 0, 1);
    }

    public static BldScramble cloneFrom(BldCube refCube, boolean isStrict) {
    	BldScramble cloneScr = new BldScramble(refCube, scramblingFactoryFor(refCube));

    	for (PieceType type : refCube.getPieceTypes()) {
    		cloneScr.setParity(type, refCube.hasParity(type) ? isStrict ? YES() : MAYBE() : NO());
			cloneScr.setBreakIns(type, isStrict ? EXACT(refCube.getBreakInCount(type)) : MAX(refCube.getBreakInCount(type)));
			cloneScr.setTargets(type, isStrict ? EXACT(refCube.getStatLength(type)) : MAX(refCube.getStatLength(type)));
			cloneScr.setSolvedMisOriented(type, isStrict ? EXACT(refCube.getPreSolvedCount(type)) : MIN(refCube.getPreSolvedCount(type)), isStrict ? EXACT(refCube.getMisOrientedCount(type)) : MAX(refCube.getMisOrientedCount(type)));
			cloneScr.setBufferSolved(type, refCube.isBufferSolved(type) ? isStrict ? YES() : MAYBE() : NO());
			cloneScr.setAllowTwistedBuffer(type, !isStrict || refCube.isBufferSolvedAndMisOriented(type));
		}

		return cloneScr;
	}

	protected static Supplier<Puzzle> scramblingFactoryFor(BldCube refCube) { // FIXME preliminary
		if (refCube instanceof SevenBldCube) {
			return () -> new CubePuzzle(7);
		} else if (refCube instanceof SixBldCube) {
			return () -> new CubePuzzle(6);
		} else if (refCube instanceof FiveBldCube) {
			return NoInspectionFiveByFiveCubePuzzle::new;
		} else if (refCube instanceof FourBldCube) {
			return NoInspectionFourByFourCubePuzzle::new;
		} else if (refCube instanceof ThreeBldCube) {
			return NoInspectionThreeByThreeCubePuzzle::new;
		} else if (refCube instanceof TwoBldCube) {
			return TwoByTwoCubePuzzle::new;
		}

		return null;
	}
}