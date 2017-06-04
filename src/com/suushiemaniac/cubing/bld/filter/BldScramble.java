package com.suushiemaniac.cubing.bld.filter;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.bld.analyze.*;
import com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition;
import com.suushiemaniac.cubing.bld.filter.condition.IntCondition;
import com.suushiemaniac.cubing.bld.filter.thread.ScrambleConsumer;
import com.suushiemaniac.cubing.bld.filter.thread.ScrambleProducer;
import com.suushiemaniac.cubing.bld.model.source.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;
import net.gnehzr.tnoodle.scrambles.Puzzle;
import puzzle.*;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.function.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.suushiemaniac.cubing.bld.filter.condition.BooleanCondition.*;
import static com.suushiemaniac.cubing.bld.filter.condition.IntCondition.*;

public class BldScramble {
    public static final String REGEX_UNIV = ".*";
    
    protected static boolean ALLOW_TWISTED_BUFFER = true;
    protected static boolean SHOW_DISCARDED = false;
    
    public static void setAllowTwistedBuffer(boolean allowTwistedBuffer) {
    	BldScramble.ALLOW_TWISTED_BUFFER = allowTwistedBuffer;
	}

	public static void setShowDiscarded(boolean showDiscarded) {
    	BldScramble.SHOW_DISCARDED = showDiscarded;
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

	public BldPuzzle generateAnalyzingPuzzle() {
		return this.analyzingSupplier.get();
	}

	protected Puzzle getScramblingPuzzle() {
		return this.scramblingPuzzle;
	}

	public Puzzle generateScramblingPuzzle() {
		return this.scramblingSupplier.get();
	}

	protected IntCondition getTargets(PieceType type) {
		return this.targets.getOrDefault(type, ANY());
	}

	private void writeTargets(PieceType type, IntCondition targets) {
		targets.capMin(0);
		// C=10 E=16 W=34 XC=34 TC=34
		targets.capMax(((type.getNumPiecesNoBuffer() / 2) * 3) + (type.getNumPiecesNoBuffer() % 2));

		this.targets.put(type, targets);
		// pre-solved
		// mis-orient
		// parity
	}

	public void setTargets(PieceType type, IntCondition targets) {
		this.writeProperties(type,
				targets,
				this.getBreakIns(type),
				this.hasParity(type),
				this.getPreSolved(type),
				this.getMisOriented(type),
				this.isBufferSolved(type)
		);
	}

	protected IntCondition getBreakIns(PieceType type) {
		return this.breakIns.getOrDefault(type, ANY());
	}

	private void writeBreakIns(PieceType type, IntCondition breakIns) {
		// C=7 E=11 W=23 XC=23 TC=23
		breakIns.capMin(Math.max(this.isBufferSolved(type).getNegative() ? 1 : 0, this.getTargets(type).getMin() - type.getNumPiecesNoBuffer()));
		// C=3 E=5 W=11 XC=11 TC=11
		breakIns.capMax(type.getNumPiecesNoBuffer() / 2);

		this.breakIns.put(type, breakIns);
		// targets
	}

	public void setBreakIns(PieceType type, IntCondition breakIns) {
		this.writeProperties(type,
				this.getTargets(type),
				breakIns,
				this.hasParity(type),
				this.getPreSolved(type),
				this.getMisOriented(type),
				this.isBufferSolved(type)
		);
	}

	protected BooleanCondition hasParity(PieceType type) {
		return this.parities.getOrDefault(type, MAYBE());
	}

	private void writeParity(PieceType type, BooleanCondition hasParity) {
		if (this.getTargets(type).isExact()) {
			hasParity.setValue(this.getTargets(type).getMax() % 2 == 1);
		}

		this.parities.put(type, hasParity);
	}

	public void setParity(PieceType type, BooleanCondition parity) {
		this.writeProperties(type,
				this.getTargets(type),
				this.getBreakIns(type),
				parity,
				this.getPreSolved(type),
				this.getMisOriented(type),
				this.isBufferSolved(type)
		);
	}

	protected BooleanCondition isBufferSolved(PieceType type) {
		return this.solvedBuffers.getOrDefault(type, BooleanCondition.MAYBE());
	}

	private void writeBufferSolved(PieceType type, BooleanCondition bufferSolved) {
		this.solvedBuffers.put(type, bufferSolved);
		// break-ins
	}

	public void setBufferSolved(PieceType type, BooleanCondition bufferSolved) {
		this.writeProperties(type,
				this.getTargets(type),
				this.getBreakIns(type),
				this.hasParity(type),
				this.getPreSolved(type),
				this.getMisOriented(type),
				bufferSolved
		);
	}

	protected boolean isAllowTwistedBuffer(PieceType type) {
		return this.allowTwistedBuffers.getOrDefault(type, ALLOW_TWISTED_BUFFER);
	}

	public void setAllowTwistedBuffer(PieceType type, boolean allowTwisted) {
		this.allowTwistedBuffers.put(type, allowTwisted);
	}

	protected IntCondition getPreSolved(PieceType type) {
		return this.preSolved.getOrDefault(type, ANY());
	}

	protected IntCondition getMisOriented(PieceType type) {
		return this.misOriented.getOrDefault(type, ANY());
	}

	private void writeSolvedMisOriented(PieceType type, IntCondition solved, IntCondition misOriented) {
		// C=7 E=11 W=23 XC=23 TC=23
		int leftOverMin = Math.max(0, type.getNumPiecesNoBuffer() + this.getBreakIns(type).getMax() - this.getTargets(type).getMax());
		int leftOverMax = Math.min(type.getNumPiecesNoBuffer(), type.getNumPiecesNoBuffer() + this.getBreakIns(type).getMin() - this.getTargets(type).getMin());

		solved.capMin(0);
		misOriented.capMin(0);

		// C=8 E=12 W=? XC=? TC=?
		solved.capMax(type.getNumPieces());
		// C=8 E=12 W=? XC=? TC=?
		misOriented.capMax(type.getNumPieces());

		int sumMin = solved.getMin() + misOriented.getMin();
		int sumMax = solved.getMax() + misOriented.getMax();

		if (sumMin > leftOverMax) {
			if (solved.isPrecise() || !misOriented.isPrecise()) {
				misOriented.setMin(misOriented.getMin() - sumMin + leftOverMax);
			}

			if (misOriented.isPrecise() || !solved.isPrecise()) {
				solved.setMin(solved.getMin() - sumMin + leftOverMax);
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

	public void setSolved(PieceType type, IntCondition solved) {
		this.writeProperties(type,
				this.getTargets(type),
				this.getBreakIns(type),
				this.hasParity(type),
				solved,
				this.getMisOriented(type),
				this.isBufferSolved(type)
		);
	}

	public void setMisOriented(PieceType type, IntCondition misOriented) {
		this.writeProperties(type,
				this.getTargets(type),
				this.getBreakIns(type),
				this.hasParity(type),
				this.getPreSolved(type),
				misOriented,
				this.isBufferSolved(type)
		);
	}

	public void writeProperties(PieceType type, IntCondition targets, IntCondition breakIns, BooleanCondition parity, IntCondition solved, IntCondition misOriented, BooleanCondition bufferSolved) {
		this.writeTargets(type, targets);
		this.writeBufferSolved(type, bufferSolved);
		this.writeBreakIns(type, breakIns);
		this.writeParity(type, parity);
		this.writeSolvedMisOriented(type, solved, misOriented);
	}

	protected String getMemoRegex(PieceType type) {
		return this.memoRegExp.getOrDefault(type, REGEX_UNIV);
	}

	public void setMemoRegex(PieceType type, String regex) {
		this.memoRegExp.put(type, regex);
	}

	public String getPredicateRegex(PieceType type) {
		return this.predicateRegExp.getOrDefault(type, REGEX_UNIV);
	}

	public void setPredicateRegExp(PieceType type, AlgSource algSource, Predicate<Algorithm> filter) {
		SortedSet<String> matches = new TreeSet<>();
		String[] possPairs = BruteForceUtil.genBlockString(SpeffzUtil.FULL_SPEFFZ, 2, false);

		for (String pair : possPairs) {
			matches.addAll(algSource.getAlgorithms(type, pair).stream().filter(filter).map(alg -> pair).collect(Collectors.toList()));
		}

		if (matches.size() > 0) {
			this.predicateRegExp.put(type, "(" + String.join("|", matches) + ")*" + (this.parities.get(type).getPositive() ? "[A-Z]?" : ""));
		}
	}

    public Algorithm findScrambleOnThread() {
        BldPuzzle testCube = this.getAnalyzingPuzzle();
        Puzzle tNoodle = this.getScramblingPuzzle();
        NotationReader reader = new CubicAlgorithmReader();

        do {
            String scrString = tNoodle.generateScramble();
            Algorithm scramble = reader.parse(scrString);
            testCube.parseScramble(scramble);
        } while (!this.matchingConditions(testCube));

        return testCube.getScramble();
    }

    public List<Algorithm> findScramblesOnThread(int num) {
        List<Algorithm> scrambles = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            if (i % (num / Math.min(100, num)) == 0) {
                System.out.println(i);
            }

            scrambles.add(this.findScrambleOnThread());
        }

        return scrambles;
    }

    public List<Algorithm> findScramblesThreadModel(int numScrambles, IntConsumer feedbackFunction) {
		int numThreads = Runtime.getRuntime().availableProcessors() + 1;
        BlockingQueue<Algorithm> scrambleQueue = new ArrayBlockingQueue<>(numScrambles * numThreads * numThreads);
        
        ScrambleProducer producer = new ScrambleProducer(this.generateScramblingPuzzle(), scrambleQueue);
        ScrambleConsumer consumer = new ScrambleConsumer(this.generateAnalyzingPuzzle(), this::matchingConditions, numScrambles, scrambleQueue);

		if (feedbackFunction != null) {
			consumer.registerFeedbackFunction(feedbackFunction);
		}

        // TODO MAYBE have multiple threads to check conformity?
		FutureTask<List<Algorithm>> consumerFuture = new FutureTask<>(consumer);

		for (int i = 0; i < numThreads; i++) {
            Thread genThread = new Thread(producer, "Producer " + (i + 1));
        
            genThread.setDaemon(true);
            genThread.start();
        }

        new Thread(consumerFuture, "Consumer").start();

        try {
			return consumerFuture.get();
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
			return consumer.getAlgList();
		}
	}

	public List<Algorithm> findScramblesThreadModel(int numScrambles) {
		return this.findScramblesThreadModel(numScrambles, null);
	}

    protected <T extends BldPuzzle> boolean matchingConditions(T inCube) {
		for (PieceType checkType : this.getAnalyzingPuzzle().getPieceTypes()) {
			if (!this.matchingConditions(inCube, checkType)) {
				if (SHOW_DISCARDED) {
					System.out.println("Discarded " + inCube.getStatString(true));
				}

				return false;
			}
		}

		return true;
	}
	
	protected <T extends BldPuzzle> boolean matchingConditions(T inCube, PieceType checkType) {
		boolean instance = this.getAnalyzingPuzzle().getClass().isInstance(inCube);
		return instance
				&& this.hasParity(checkType).evaluatePositive(inCube.hasParity(checkType))
				&& this.isBufferSolved(checkType).evaluatePositive(inCube.isBufferSolved(checkType, this.isAllowTwistedBuffer(checkType)))
				&& this.getBreakIns(checkType).evaluate(inCube.getBreakInCount(checkType))
				&& this.getTargets(checkType).evaluate(inCube.getStatLength(checkType))
				&& this.getPreSolved(checkType).evaluate(inCube.getPreSolvedCount(checkType))
				&& this.getMisOriented(checkType).evaluate(inCube.getMisOrientedCount(checkType))
				&& inCube.getSolutionRaw(checkType).matches(this.getMemoRegex(checkType))
				&& inCube.getSolutionRaw(checkType).matches(this.getPredicateRegex(checkType));
	}

	@Override
	public String toString() {
		return this.getStatString();
	}

	public String getStatString() {
		List<String> pieceTypeStrings = this.getAnalyzingPuzzle().getPieceTypes().stream().map(this::getStatString).collect(Collectors.toList());
		return String.join(" | ", pieceTypeStrings);
	}

	protected String getStatString(PieceType type) {
		return type.mnemonic() + ": " + (this.hasParity(type).getPositive() ? "_" : "") +
				(this.hasParity(type).isImportant() ? "! " : "? ") +
				this.getTargets(type).toString() +
				" " +
				(this.isBufferSolved(type).getPositive() ? (this.isAllowTwistedBuffer(type) ? "**" : "*") : "") +
				(this.isBufferSolved(type).isImportant() ? "! " : "? ") +
				this.getBreakIns(type).toString("#") +
				" " +
				this.getMisOriented(type).toString("~") +
				" " +
				this.getPreSolved(type).toString("+");
	}

	public boolean setSolvingOrientation(int top, int front) { // TODO later add other specifications for other puzzle types (pyra, mega…)
		return this.analyzingPuzzle instanceof BldCube
			&& ((BldCube) this.getAnalyzingPuzzle()).setSolvingOrientation(top, front);
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

    public static BldScramble cloneFrom(BldPuzzle refCube, boolean isStrict) {
    	BldScramble cloneScr = new BldScramble(refCube, scramblingFactoryFor(refCube));

    	for (PieceType type : refCube.getPieceTypes()) {
			cloneScr.writeProperties(type,
					isStrict ? EXACT(refCube.getStatLength(type)) : MAX(refCube.getStatLength(type)),
					isStrict ? EXACT(refCube.getBreakInCount(type)) : MAX(refCube.getBreakInCount(type)),
					refCube.hasParity(type) ? isStrict ? YES() : MAYBE() : NO(),
					isStrict ? EXACT(refCube.getPreSolvedCount(type)) : MIN(refCube.getPreSolvedCount(type)),
					isStrict ? EXACT(refCube.getMisOrientedCount(type)) : MAX(refCube.getMisOrientedCount(type)),
					refCube.isBufferSolved(type) ? isStrict ? YES() : MAYBE() : NO()
			);

			cloneScr.setAllowTwistedBuffer(type, !isStrict || refCube.isBufferSolvedAndMisOriented(type));
		}

		return cloneScr;
	}

	protected static Supplier<Puzzle> scramblingFactoryFor(BldPuzzle refCube) { // FIXME preliminary
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

	public static BldScramble fromStatString(String statString, BldPuzzle refCube, boolean isStrict) {
		BldScramble statScr = new BldScramble(refCube, scramblingFactoryFor(refCube));

		for (String pieceStatString : statString.split("\\|")) {
			Pattern statPattern = Pattern.compile("([A-Za-z]+?):(_?)(0|[1-9][0-9]*)(\\*?)(#*)(~*)(\\+*)");
			Matcher statMatcher = statPattern.matcher(pieceStatString.replaceAll("\\s", ""));

			if (statMatcher.find()) {
				String mnemonic = statMatcher.group(1);
				PieceType type = findTypeByMnemonic(refCube, mnemonic);

				int targets = Integer.parseInt(statMatcher.group(3));
				int breakIns = statMatcher.group(5).length();
				boolean hasParity = statMatcher.group(2).length() > 0;
				int preSolved = statMatcher.group(7).length();
				int misOriented = statMatcher.group(6).length();
				boolean bufferSolved = statMatcher.group(4).length() > 0;

				statScr.writeProperties(type,
						isStrict ? EXACT(targets) : MAX(targets),
						isStrict ? EXACT(breakIns) : MAX(breakIns),
						hasParity ? isStrict ? YES() : MAYBE() : NO(),
						isStrict ? EXACT(preSolved) : MIN(preSolved),
						isStrict ? EXACT(misOriented) : MAX(misOriented),
						bufferSolved ? isStrict ? YES() : MAYBE() : NO()
				);
			}
		}

		return statScr;
	}

	protected static PieceType findTypeByMnemonic(BldPuzzle refCube, String mnemonic) {
		for (PieceType type : refCube.getPieceTypes()) {
			if (type.mnemonic().equalsIgnoreCase(mnemonic)) {
				return type;
			}
		}

		return null;
	}
}