package com.suushiemaniac.cubing.bld.database;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.analyze.BldPuzzle;
import com.suushiemaniac.cubing.bld.analyze.FiveBldCube;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.LetterPairImage;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.model.source.DatabaseAlgSource;
import com.suushiemaniac.cubing.bld.optim.AlgComparator;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class CubeDb extends DatabaseAlgSource {
    private BldPuzzle refCube;

    public CubeDb(String connString) throws SQLException {
        this(connString, new FiveBldCube());
    }

    public CubeDb(String connString, BldPuzzle puzzle) throws SQLException {
        super(DriverManager.getConnection(connString));
        this.refCube = puzzle;
    }

    public Set<String> readAlgorithm(PieceType type, String letterPair) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getLetteringScheme(type));

        String bufferStd = SpeffzUtil.normalize(this.refCube.getBufferTarget(type), this.refCube.getLetteringScheme(type));
        String bufferPos = SpeffzUtil.speffzToSticker(bufferStd, type);

        PreparedStatement stat = conn.prepareStatement("SELECT DISTINCT alg FROM Algorithms WHERE `type` = ? AND `case` = ? AND buffer = ? ORDER BY score DESC");
        stat.setString(1, type.mnemonic());
        stat.setString(2, speffz);
        stat.setString(3, bufferPos);

        ResultSet search = stat.executeQuery();

        Set<String> temp = new HashSet<>();
        while (search.next()) temp.add(search.getString("alg"));
        return temp;
    }

    public Set<String> readLpi(PieceType type, String letterPair) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("SELECT DISTINCT image FROM Images WHERE `case` = ? AND `language` = ? AND ? IN (token, ?) AND score > ?");
        stat.setString(1, letterPair);
        stat.setString(2, this.refCube.getLetterPairLanguage().toLowerCase());
		stat.setString(3, type.mnemonic());
		stat.setString(4, LetterPairImage.ANY.mnemonic());
		stat.setInt(5, -1);

        ResultSet search = stat.executeQuery();

        Set<String> temp = new HashSet<>();
        while (search.next()) temp.add(search.getString("image"));
        return temp;
    }

    public boolean addAlgorithm(PieceType type, String letterPair, Set<Algorithm> algs) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("INSERT INTO Algorithms (`type`, `case`, alg, buffer, score, review, inserted_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, NOW(), NOW())");

        for (Algorithm alg : algs) {
            String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getLetteringScheme(type));

            String bufferStd = SpeffzUtil.normalize(this.refCube.getBufferTarget(type), this.refCube.getLetteringScheme(type));
            String bufferPos = SpeffzUtil.speffzToSticker(bufferStd, type);

            stat.setString(1, type.mnemonic());
            stat.setString(2, speffz);
            stat.setString(3, alg.toFormatString());
            stat.setString(4, bufferPos);
            stat.setFloat(5, AlgComparator.scoreAlg(alg));
            stat.setInt(6, 0);

            stat.execute();
        }

        return true; // TODO
    }

    public boolean addLpi(PieceType type, String letterPair, Set<Algorithm> images) throws SQLException {
    	if (type == LetterPairImage.ANY) {
    		return false;
		}

        PreparedStatement stat = conn.prepareStatement("INSERT INTO Images (`case`, image, token, `language`, inserted_at, updated_at) VALUES (?, ?, ?, ?, NOW(), NOW())");

        for (Algorithm image : images) {
			stat.setString(1, letterPair);
			stat.setString(2, image.toFormatString());
			stat.setString(3, type.mnemonic());
			stat.setString(4, this.refCube.getLetterPairLanguage().toLowerCase());

			stat.execute();
        }

        return true; // TODO
    }

    public Map<String, Set<String>> getAllAlgorithms(PieceType type) throws SQLException {
        PreparedStatement stat = this.conn.prepareStatement("SELECT * FROM Algorithms WHERE `type` = ?");
        stat.setString(1, type.mnemonic());

        ResultSet res = stat.executeQuery();
        Map<String, Set<String>> words = new HashMap<>();

        while (res.next()) {
            String lpKey = res.getString("case");

            Set<String> otherLps = words.getOrDefault(lpKey, new HashSet<>());
            otherLps.add(res.getString("alg"));

            words.put(lpKey, otherLps);
        }

        return words;
    }

    public Map<String, Set<String>> getAllLpiWords() throws SQLException {
        ResultSet res = this.conn.createStatement().executeQuery("SELECT * FROM Images");
        Map<String, Set<String>> words = new HashMap<>();

        while (res.next()) {
            String lpKey = res.getString("case");

            Set<String> otherLps = words.getOrDefault(lpKey, new HashSet<>());
            otherLps.add(res.getString("image"));

            words.put(lpKey, otherLps);
        }

        return words;
    }

    protected boolean updateAlg(PieceType type, Algorithm oldAlg, Algorithm newAlg) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("UPDATE Algorithms SET alg = ?, score = ?, updated_at = NOW() WHERE `type` = ? AND alg = ?");

        stat.setString(1, newAlg.toFormatString());
        stat.setFloat(2, AlgComparator.scoreAlg(newAlg));
        stat.setString(3, type.mnemonic());
        stat.setString(4, oldAlg.toFormatString());

        return stat.executeUpdate() > 0;
    }

    protected boolean updateLpi(PieceType type, Algorithm oldImage, Algorithm newImage) throws SQLException {
		PreparedStatement stat = conn.prepareStatement("UPDATE Images SET image = ?, updated_at = NOW() WHERE image = ? AND `token` = ? AND `language` = ?");

		stat.setString(1, newImage.toFormatString());
		stat.setString(2, oldImage.toFormatString());
		stat.setString(3, type.mnemonic());
		stat.setString(4, this.refCube.getLetterPairLanguage().toLowerCase());

		return stat.executeUpdate() > 0;
    }

    protected boolean removeAlgorithm(PieceType type, Algorithm alg) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("DELETE FROM Algorithms WHERE `type` = ? AND alg = ? AND buffer = ?");

        String bufferStd = SpeffzUtil.normalize(this.refCube.getBufferTarget(type), this.refCube.getLetteringScheme(type));
        String bufferPos = SpeffzUtil.speffzToSticker(bufferStd, type);

        stat.setString(1, type.mnemonic());
        stat.setString(2, alg.toFormatString());
        stat.setString(3, bufferPos);

        return stat.executeUpdate() > 0;
    }

    protected boolean removeLpi(Algorithm image) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("DELETE FROM Images WHERE image = ? AND `language` = ?");
        stat.setString(1, image.toFormatString());
        stat.setString(2, this.refCube.getLetterPairLanguage().toLowerCase());

        return stat.executeUpdate() > 0;
    }

    protected boolean removeAlgorithmFor(PieceType type, String letterPair) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getLetteringScheme(type));

        PreparedStatement stat = conn.prepareStatement("DELETE FROM Algorithms WHERE `type` = ? AND `case` = ?");
        stat.setString(1, type.mnemonic());
        stat.setString(2, speffz);

        return stat.executeUpdate() > 0;
    }

    protected boolean removeLpiFor(String letterPair) throws SQLException {
        PreparedStatement stat = conn.prepareStatement("DELETE FROM Images WHERE `case` = ?");
        stat.setString(1, letterPair);

        return stat.executeUpdate() > 0;
    }

    public void increaseScore(String lpi) throws SQLException {
        PreparedStatement stat = this.conn.prepareStatement("UPDATE Images SET score = score + 1 WHERE image = ?");
        stat.setString(1, lpi);

        stat.execute();
    }

    public void resetScore(String lpi) throws SQLException {
        PreparedStatement stat = this.conn.prepareStatement("UPDATE Images SET score = 0 WHERE image = ?");
        stat.setString(1, lpi);

        stat.execute();
    }

    @Override
    public Set<String> getRawAlgorithms(PieceType type, String letterPair) {
        try {
            return type instanceof LetterPairImage ? this.readLpi(type, letterPair) : this.readAlgorithm(type, letterPair);
        } catch (SQLException e) {
            e.printStackTrace();
            return new HashSet<>();
        }
    }

    @Override
    public boolean addAlgorithms(PieceType type, String letterPair, Set<Algorithm> algorithms) {
        try {
            return type instanceof LetterPairImage ? this.addLpi(type, letterPair, algorithms) : this.addAlgorithm(type, letterPair, algorithms);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateAlgorithm(PieceType type, Algorithm oldAlg, Algorithm newAlg) {
        try {
            return type instanceof LetterPairImage ? this.updateLpi(type, oldAlg, newAlg) : this.updateAlg(type, oldAlg, newAlg);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteAlgorithm(PieceType type, Algorithm algorithm) {
        try {
            return type instanceof LetterPairImage ? this.removeLpi(algorithm) : this.removeAlgorithm(type, algorithm);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteAlgorithms(PieceType type, String letterPair) {
        try {
            return type instanceof LetterPairImage ? this.removeLpiFor(letterPair) : this.removeAlgorithmFor(type, letterPair);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}