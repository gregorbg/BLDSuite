package com.suushiemaniac.cubing.bld.model.enumeration.piece;

import com.suushiemaniac.cubing.alglib.lang.ImageStringReader;
import com.suushiemaniac.cubing.alglib.lang.NotationReader;

public enum LetterPairImage implements PieceType {
	ANY("<any>"), NOUN("NN"), VERB("VV"), ADJECTIVE("ADJ");

	public static LetterPairImage fromToken(String token) {
		for (LetterPairImage img : values()) {
			if (img.mnemonic().equals(token)) {
				return img;
			}
		}

		return null;
	}

	String token;

	LetterPairImage(String token) {
		this.token = token;
	}

	@Override
	public int getTargetsPerPiece() {
		return 1;
	}

	@Override
	public int getNumPieces() {
		return 26;
	}

	@Override
	public int getNumPiecesNoBuffer() {
		return 25; // TODO 26 here as well??
	}

	@Override
	public int getNumAlgs() {
		return 1;
	}

	@Override
	public String humanName() {
		return "LPI";
	}

	@Override
	public String mnemonic() {
		return this.token;
	}

	@Override
	public NotationReader getReader() {
		return new ImageStringReader();
	}

	@Override
	public String toString() {
		return this.mnemonic();
	}
}
