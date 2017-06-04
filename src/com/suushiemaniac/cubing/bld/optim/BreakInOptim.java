package com.suushiemaniac.cubing.bld.optim;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.analyze.FiveBldCube;
import com.suushiemaniac.cubing.bld.model.source.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;

import java.util.*;
import java.util.stream.Collectors;

public class BreakInOptim {
    private AlgSource source;
	private BldPuzzle refCube;

	private Map<PieceType, Map<String, List<String>>> cache;

    public BreakInOptim(AlgSource source, BldPuzzle refCube, boolean fullCache) {
        this.source = source;
		this.refCube = refCube;

		this.cache = new HashMap<>();

		for (PieceType type : refCube.getPieceTypes()) {
			HashMap<String, List<String>> typeMap = new HashMap<>();

			if (fullCache) {
				for (String letter : refCube.getLetteringScheme(type)) {
					typeMap.put(letter, this.optimizeBreakInTargetsAfter(letter, type));
				}
			}

			this.cache.put(type, typeMap);
		}
    }

    public BreakInOptim(AlgSource source, BldPuzzle refCube) {
    	this(source, refCube, true);
	}

    public BreakInOptim(AlgSource source, boolean fullCache) {
    	this(source, new FiveBldCube(), fullCache);
	}

    public BreakInOptim(AlgSource source) {
    	this(source, new FiveBldCube(), true);
	}

    public List<String> optimizeBreakInTargetsAfter(String target, PieceType type) {
    	List<String> cache = this.cache.getOrDefault(type, new HashMap<>()).get(target);

    	if (cache != null) {
    		return cache;
		}

        List<Algorithm> algList = new ArrayList<>();
		Map<Algorithm, String> targetMap = new HashMap<>();

        for (String t : this.refCube.getLetteringScheme(type)) {
            Set<Algorithm> sourceList = this.source.getAlgorithms(type, target + t);
            if (sourceList == null) continue;

			for (Algorithm alg : sourceList) {
				algList.add(alg);
				targetMap.put(alg, t);
			}
        }

        algList.sort(AlgComparator.INST());

    	List<String> optimizedList = algList.stream()
				.map(targetMap::get)
				.collect(Collectors.toList());

		this.cache.getOrDefault(type, new HashMap<>()).put(target, optimizedList);
		return optimizedList;
    }

    public List<Algorithm> optimizeBreakInAlgorithmsAfter(String target, PieceType type) {
		return this.optimizeBreakInTargetsAfter(target, type).stream()
				.flatMap(t -> this.source.getAlgorithms(type, target + t).stream())
				.collect(Collectors.toList());
    }
}