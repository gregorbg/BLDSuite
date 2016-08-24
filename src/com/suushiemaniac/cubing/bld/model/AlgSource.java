package com.suushiemaniac.cubing.bld.model;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.Set;

public interface AlgSource {
    Set<Algorithm> getAlg(PieceType type, String letterPair);

    Set<String> getRawAlg(PieceType type, String letterPair);
}
