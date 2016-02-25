package com.suushiemaniac.cubing.bld.model;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;

import java.util.List;

public interface AlgSource {
    List<Algorithm> getAlg(PieceType type, String letterPair);

    List<String> getRawAlg(PieceType type, String letterPair);
}
