package com.suushiemaniac.cubing.bld.database;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.alglib.lang.CubicAlgorithmReader;
import com.suushiemaniac.cubing.alglib.util.ParseUtils;
import com.suushiemaniac.cubing.bld.analyze.cube.FiveBldCube;
import com.suushiemaniac.cubing.bld.model.AlgSource;
import com.suushiemaniac.cubing.bld.model.enumeration.PieceType;
import com.suushiemaniac.cubing.bld.util.SpeffzUtil;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class CubeDb implements AlgSource {
    private Connection conn;

    private FiveBldCube refCube;

    public CubeDb(String connString) throws SQLException {
        this.conn = DriverManager.getConnection(connString);
        this.refCube = new FiveBldCube("");
    }

    public void addAlgorithm(PieceType type, String letterPair, String alg) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getScheme(type.name()));

        boolean duplicate = readAlgorithm(type, speffz).contains(alg);

        if (!duplicate) {
            PreparedStatement stat = conn.prepareStatement("INSERT INTO Algorithms (`type`, `case`, alg) VALUES (?, ?, ?)");
            stat.setString(1, type.name().toLowerCase());
            stat.setString(2, speffz);
            stat.setString(3, alg);
            stat.execute();
        }
    }

    public void addLpi(String letterPair, String image) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getScheme("lpi"));

        boolean duplicate = readLpi(speffz).contains(image);

        if (!duplicate) {
            PreparedStatement stat = conn.prepareStatement("INSERT INTO Images (`case`, image, property) VALUES (?, ?, ?)");
            stat.setString(1, speffz);
            stat.setString(2, image);
            stat.setString(3, "");
            stat.execute();
        }
    }

    public List<String> readAlgorithm(PieceType type, String letterPair) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getScheme(type.name()));

        PreparedStatement stat = conn.prepareStatement("SELECT DISTINCT alg FROM Algorithms WHERE `type` = ? AND `case` = ?");
        stat.setString(1, type.name().toLowerCase());
        stat.setString(2, speffz);

        ResultSet search = stat.executeQuery();

        ArrayList<String> temp = new ArrayList<>();
        while (search.next()) temp.add(search.getString("alg"));
        return temp;
    }

    public List<String> readLpi(String letterPair) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getScheme("lpi"));

        PreparedStatement stat = conn.prepareStatement("SELECT DISTINCT image FROM Algorithms WHERE `case` = ?");
        stat.setString(1, speffz);

        ResultSet search = stat.executeQuery();

        ArrayList<String> temp = new ArrayList<>();
        while (search.next()) temp.add(search.getString("alg"));
        return temp;
    }

    public Map<String, List<String>> getAllLpiWords() throws SQLException {
        ResultSet res = this.conn.createStatement().executeQuery("SELECT * FROM Images");
        Map<String, List<String>> words = new HashMap<>();

        while (res.next()) {
            String lpKey = res.getString("case");

            List<String> otherLps = words.get(lpKey);
            if (otherLps == null) otherLps = new ArrayList<>();

            otherLps.add(res.getString("image"));

            words.put(lpKey, otherLps);
        }

        return words;
    }

    public Map<String, List<String>> getAllAlgorithms(PieceType type) throws SQLException {
        PreparedStatement stat = this.conn.prepareStatement("SELECT * FROM Algorithms WHERE `type` = ?");
        stat.setString(1, type.name().toLowerCase());

        ResultSet res = stat.executeQuery();
        Map<String, List<String>> words = new HashMap<>();

        while (res.next()) {
            String lpKey = res.getString("case");

            List<String> otherLps = words.get(lpKey);
            if (otherLps == null) otherLps = new ArrayList<>();

            otherLps.add(res.getString("alg"));

            words.put(lpKey, otherLps);
        }

        return words;
    }

    private void resetScore(String lpi) throws SQLException {
        PreparedStatement stat = this.conn.prepareStatement("UPDATE Images SET score = 0 WHERE image = ?");
        stat.setString(1, lpi);

        stat.execute();
    }

    private void increaseScore(String lpi) throws SQLException {
        PreparedStatement stat = this.conn.prepareStatement("UPDATE Images SET score = score + 1 WHERE image = ?");
        stat.setString(1, lpi);

        stat.execute();
    }

    public void removeAlgorithm(PieceType type, String letterPair, String alg) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getScheme(type.name()));

        PreparedStatement stat = conn.prepareStatement("DELETE FROM Algorithms WHERE `type` = ? AND `case` = ? AND alg = ?");
        stat.setString(1, type.name().toLowerCase());
        stat.setString(2, speffz);
        stat.setString(3, alg);

        stat.execute();
    }

    public void removeLpi(String letterPair, String image) throws SQLException {
        String speffz = SpeffzUtil.normalize(letterPair, this.refCube.getScheme("lpi"));

        PreparedStatement stat = conn.prepareStatement("DELETE FROM Images WHERE `case` = ? AND image = ?");
        stat.setString(1, speffz);
        stat.setString(2, image);

        stat.execute();
    }

    public void updateAlgorithm(PieceType pieceType, String letterPair, String oldAlg, String newAlg) throws SQLException {
        this.removeAlgorithm(pieceType, letterPair, oldAlg);
        this.addAlgorithm(pieceType, letterPair, newAlg);
    }

    public void updateLpi(String letterPair, String oldImage, String newImage) throws SQLException {
        this.removeLpi(letterPair, oldImage);
        this.addLpi(letterPair, newImage);
    }

    public void closeConnection() throws SQLException {
        conn.close();
    }

    public String getDatabaseConnString() throws SQLException {
        return this.conn.getMetaData().getURL();
    }

    @Override
    public List<Algorithm> getAlg(PieceType type, String letterPair) {
        return this.getRawAlg(type, letterPair).stream().map(rawAlg -> new CubicAlgorithmReader().parse(rawAlg)).collect(Collectors.toList());
    }

    @Override
    public List<String> getRawAlg(PieceType type, String letterPair) {
        try {
            return this.readAlgorithm(type, letterPair);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
