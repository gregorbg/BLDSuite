package com.suushiemaniac.cubing.bld.util;

import com.suushiemaniac.cubing.bld.database.CubeDb;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class MemoUtil {
    public static List<String> genMemoTree(String pairs, CubeDb db, int lpiListSizeLimit) throws SQLException {
        List<String> treeList = new ArrayList<>(Collections.singletonList(""));

        for (String pair : pairs.split("\\s+?")) {
            if (pair.length() != 2) continue;

            List<String> words = db.readLpi(pair);
            words = words.subList(0, Math.min(lpiListSizeLimit, words.size()));
            List<String> oldMemoStrings = new ArrayList<>(treeList);

            for (String oldMemo : oldMemoStrings) {
                for (String word : words) {
                    treeList.add(oldMemo + (oldMemo.length() > 0 ? " // " : "") + word);
                }
            }

            treeList.removeAll(oldMemoStrings);
        }

        return treeList;
    }

    public static List<String> genMemoTree(String pairs, CubeDb db) throws SQLException {
        return genMemoTree(pairs, db, Integer.MAX_VALUE);
    }
}
