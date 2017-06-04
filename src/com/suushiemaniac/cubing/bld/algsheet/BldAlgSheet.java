package com.suushiemaniac.cubing.bld.algsheet;

import com.suushiemaniac.cubing.alglib.alg.Algorithm;
import com.suushiemaniac.cubing.bld.model.enumeration.piece.PieceType;
import com.suushiemaniac.cubing.bld.model.source.FileAlgSource;
import com.suushiemaniac.cubing.bld.util.BruteForceUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public abstract class BldAlgSheet extends FileAlgSource {
    protected Workbook workbook;
    private Map<PieceType, Map<String, Set<String>>> cache;

    public BldAlgSheet(File excelFile) {
        super(excelFile);

        this.cache();
    }

    public void cache() {
        this.workbook = this.getWorkbook();
        this.cache = this.readAll();
    }

    protected Workbook getWorkbook() {
        try (FileInputStream fis = new FileInputStream(this.file)) {
            return new XSSFWorkbook(fis);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected void writeWorkbook() {
        try (FileOutputStream fos = new FileOutputStream(this.file)) {
            this.workbook.write(fos);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.workbook = this.getWorkbook();
    }

    protected Map<PieceType, Map<String, Set<String>>> readAll() {
        String[] possPairs = BruteForceUtil.genBlockString(BruteForceUtil.ALPHABET, 2, false);
        Map<PieceType, Map<String, Set<String>>> cache = new HashMap<>();

        for (PieceType type : this.getSupportedPieceTypes()) {
            Map<String, Set<String>> subCache = new HashMap<>();

            for (String pair : possPairs) {
                Set<String> current = new HashSet<>();

                Cell primaryCell = this.getPrimaryCell(type, pair);
                if (primaryCell != null)
                    current.add(primaryCell.getStringCellValue());

                for (Cell c : this.getSecondaryCells(type, pair))
                    current.add(c.getStringCellValue());

                if (current.size() > 0)
                    subCache.put(pair, current);
            }

            if (subCache.size() > 0)
                cache.put(type, subCache);

        }

        return cache;
    }

    public void setAlgorithm(PieceType pieceType, String letterPair, String algorithm) {
        this.getPrimaryCell(pieceType, letterPair).setCellValue(algorithm);
        this.cache.get(pieceType).get(letterPair).add(algorithm);
        this.writeWorkbook();
    }

    public void setAlgorithm(PieceType pieceType, String letterPair, Algorithm algorithm) {
        this.setAlgorithm(pieceType, letterPair, algorithm.toFormatString());
    }

    protected abstract Cell getPrimaryCell(PieceType pieceType, String letterPair);

    protected abstract List<Cell> getSecondaryCells(PieceType pieceType, String letterPair);

    protected abstract PieceType[] getSupportedPieceTypes();

    @Override
    public Set<String> getRawAlgorithms(PieceType type, String letterPair) {
        return this.cache.get(type).getOrDefault(letterPair, Collections.emptySet());
    }
}