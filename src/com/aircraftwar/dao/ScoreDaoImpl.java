package com.aircraftwar.dao;

import com.aircraftwar.application.GameDifficulty;
import com.aircraftwar.score.ScoreRecord;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ScoreDaoImpl implements ScoreDao {

    private final Path filePath;

    public ScoreDaoImpl(GameDifficulty difficulty) {
        this(difficulty, Paths.get("score"));
    }

    public ScoreDaoImpl(GameDifficulty difficulty, Path baseDir) {
        try {
            this.filePath = baseDir.resolve("score_" + difficulty.getFileName() + ".txt");
            Files.createDirectories(filePath.getParent());
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("初始化排行榜文件失败", e);
        }
    }

    @Override
    public void addRecord(ScoreRecord record) {
        List<ScoreRecord> records = getAllRecords();
        records.add(record);
        saveAll(records);
    }

    @Override
    public List<ScoreRecord> getAllRecords() {
        try {
            List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
            List<ScoreRecord> records = new ArrayList<>();
            for (String line : lines) {
                if (line == null || line.trim().isEmpty()) {
                    continue;
                }
                try {
                    records.add(ScoreRecord.fromFileLine(line));
                } catch (RuntimeException ignored) {
                    System.err.println("忽略损坏的排行榜记录：" + line);
                }
            }
            sort(records);
            return records;
        } catch (IOException e) {
            throw new RuntimeException("读取排行榜失败", e);
        }
    }

    @Override
    public void deleteByRank(int rank) {
        List<ScoreRecord> records = getAllRecords();
        if (rank < 1 || rank > records.size()) {
            throw new IllegalArgumentException("名次不存在：" + rank);
        }
        records.remove(rank - 1);
        saveAll(records);
    }

    private void saveAll(List<ScoreRecord> records) {
        try {
            sort(records);
            List<String> lines = new ArrayList<>();
            for (ScoreRecord record : records) {
                lines.add(record.toFileLine());
            }
            Files.write(filePath, lines, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException("保存排行榜失败", e);
        }
    }

    private void sort(List<ScoreRecord> records) {
        records.sort(Comparator
                .comparingInt(ScoreRecord::getScore).reversed()
                .thenComparing(ScoreRecord::getRecordTime));
    }
}
