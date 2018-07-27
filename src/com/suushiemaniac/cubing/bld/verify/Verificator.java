package com.suushiemaniac.cubing.bld.verify;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.alg.SimpleAlg;
import com.suushiemaniac.cubing.alglib.alg.SubGroup;
import com.suushiemaniac.cubing.alglib.alg.commutator.Commutator;
import com.suushiemaniac.cubing.alglib.alg.commutator.PureComm;
import com.suushiemaniac.cubing.alglib.alg.commutator.SetupComm;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;
import com.suushiemaniac.cubing.alglib.move.CubicMove;
import com.suushiemaniac.cubing.alglib.move.Move;
import com.suushiemaniac.cubing.alglib.move.modifier.CubicModifier;
import com.suushiemaniac.cubing.alglib.util.ParseUtils;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.model.source.AlgSource;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import com.suushiemaniac.cubing.bld.util.ClosureUtil;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Verificator {
	protected static String[] fullLetterPairs = BruteForceUtil.genBlockString(BruteForceUtil.ALPHABET, 2, false);

	private NotationReader reader;
	private AlgSource source;
	private BldPuzzle model;

	public Verificator(NotationReader reader, AlgSource source, BldPuzzle model) {
		this.reader = reader;
		this.source = source;
		this.model = model;
	}

	public Map<String, Map<String, Boolean>> verifyAll(PieceType type) {
		return Arrays.stream(fullLetterPairs)
				.collect(Collectors.toMap(
						Function.identity(),
						possPair -> this.verifySingleCase(type, possPair),
						(a, b) -> b));
	}

	public Map<String, Boolean> verifySingleCase(PieceType type, String letterPair) {
		return Optional.of(this.source.getRawAlgorithms(type, letterPair))
				.orElse(Collections.emptySet()).stream()
				.collect(Collectors.toMap(
						Function.identity(),
						alg -> ParseUtils.isParseable(alg, this.reader)
								&& this.model.solves(type, this.reader.parse(alg), letterPair, true),
						(a, b) -> b));
	}

	public Map<String, String> attemptFixFor(PieceType type, String letterPair) {
		return Optional.of(this.source.getRawAlgorithms(type, letterPair))
				.orElse(Collections.emptySet()).stream()
				.filter(alg -> ParseUtils.isParseable(alg, this.reader))
				.filter(alg -> !this.model.solves(type, this.reader.parse(alg), letterPair, true))
				.collect(Collectors.toMap(
						Function.identity(),
						alg -> this.fixAlgorithm(alg, type, letterPair),
						(a, b) -> b));
	}

	public String fixAlgorithm(String rawAlg, PieceType type, String letterPair) {
		Algorithm alg = this.reader.parse(rawAlg);

		List<Algorithm> reparations = this.computePossibleReparations(alg);

		for (Algorithm repairedAlg : reparations) {
			if (this.model.solves(type, repairedAlg, letterPair, true)) {
				return repairedAlg.toFormatString();
			}
		}

		return null;
	}

	protected List<Algorithm> computePossibleReparations(Algorithm alg) {
		if (alg instanceof SimpleAlg) {
			SimpleAlg simpleAlg = (SimpleAlg) alg;

			List<List<Move>> reparations = new ArrayList<>();
			reparations.add(new ArrayList<>());

			for (int i = 0; i < simpleAlg.algLength(); i++) {
				List<List<Move>> currentReparations = new ArrayList<>();
				List<Move> alternatives = this.computePossibleAlternatives(simpleAlg.nMove(i));

				for (List<Move> preReparation : reparations) {
					for (Move alt : alternatives) {
						List<Move> nextReparation = new ArrayList<>(preReparation);
						nextReparation.add(alt);

						currentReparations.add(nextReparation);
					}
				}

				reparations = currentReparations;
			}

			return reparations.stream()
					.map(SimpleAlg::new)
					.collect(Collectors.toList());
		} else if (alg instanceof Commutator) {
			Algorithm firstPart;
			Algorithm secondPart;

			final BiFunction<Algorithm, Algorithm, Commutator> generator;
			boolean reverse = false;

			List<Algorithm> reparations = new ArrayList<>();

			if (alg instanceof PureComm) {
				PureComm pureComm = (PureComm) alg;

				firstPart = pureComm.getPartA();
				secondPart = pureComm.getPartB();

				generator = PureComm::new;
				reverse = true;
			} else if (alg instanceof SetupComm) {
				SetupComm pureComm = (SetupComm) alg;

				firstPart = pureComm.getSetup();
				secondPart = pureComm.getInner();

				generator = SetupComm::new;
			} else {
				return Collections.emptyList();
			}

			List<Algorithm> firstPartReparations = this.computePossibleReparations(firstPart);
			List<Algorithm> secondPartReparations = this.computePossibleReparations(secondPart);

			reparations.addAll(firstPartReparations.stream()
					.map(rep -> generator.apply(rep, secondPart))
					.collect(Collectors.toList()));

			reparations.addAll(secondPartReparations.stream()
					.map(rep -> generator.apply(firstPart, rep))
					.collect(Collectors.toList()));

			if (reverse) {
				reparations.addAll(firstPartReparations.stream()
						.map(rep -> ClosureUtil.reversingArguments(generator).apply(rep, secondPart))
						.collect(Collectors.toList()));

				reparations.addAll(secondPartReparations.stream()
						.map(rep -> ClosureUtil.reversingArguments(generator).apply(firstPart, rep))
						.collect(Collectors.toList()));
			}

			return reparations;
		} else {
			return Collections.emptyList();
		}
	}

	protected List<Move> computePossibleAlternatives(Move original) {
		if (original instanceof CubicMove) {
			CubicMove move = (CubicMove) original;

			return Arrays.stream(CubicModifier.values())
					//.filter(modif -> modif != move.getModifier())
					.map(modif -> new CubicMove(
							move.getPlane(),
							modif,
							move.getDepth()))
					.collect(Collectors.toList());
		}

		return Collections.emptyList();
	}

	public Map<String, List<String>> findMatchingSubGroup(PieceType type, SubGroup group) {
		Map<String, List<String>> sameGroupMap = new HashMap<>();

		for (String possPair : fullLetterPairs) {
			sameGroupMap.put(possPair, new ArrayList<>());

			Set<String> algStringList = this.source.getRawAlgorithms(type, possPair);

			if (algStringList != null) {
				for (String alg : algStringList) {
					if (ParseUtils.isParseable(alg, this.reader) && this.reader.parse(alg).getSubGroup().sameOrLargerSubGroup(group)) {
						sameGroupMap.get(possPair).add(alg);
					}
				}
			}
		}

		return sameGroupMap;
	}

	public Map<String, Set<String>> checkParseable(PieceType type) {
		Map<String, Set<String>> unparseableMap = new HashMap<>();

		for (String possPair : fullLetterPairs) {
			Set<String> algStringList = this.source.getRawAlgorithms(type, possPair);

			if (algStringList != null) {
				Set<String> unparseable = algStringList.stream()
						.filter(alg -> !ParseUtils.isParseable(alg, this.reader))
						.collect(Collectors.toSet());

				unparseableMap.put(possPair, unparseable);
			}
		}

		return unparseableMap;
	}
}