package com.aircraftwar.dao;

import com.aircraftwar.application.GameDifficulty;
import com.aircraftwar.score.ScoreRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * DAO 排行榜数据持久化与排序删除单元测试。
 */
class ScoreDaoImplTest {

    @TempDir
    Path tempDir;

    private ScoreDaoImpl dao;

    @BeforeEach
    void setUp() {
        dao = new ScoreDaoImpl(GameDifficulty.EASY, tempDir);
    }

    @Test
    @DisplayName("addRecord 和 getAllRecords：添加后按分数降序排列")
    void addAndListShouldSortByScoreDescending() {
        dao.addRecord(new ScoreRecord("Alice", 100, LocalDateTime.of(2026, 1, 1, 10, 0, 0)));
        dao.addRecord(new ScoreRecord("Bob", 300, LocalDateTime.of(2026, 1, 1, 10, 1, 0)));
        dao.addRecord(new ScoreRecord("Cindy", 200, LocalDateTime.of(2026, 1, 1, 10, 2, 0)));

        List<ScoreRecord> records = dao.getAllRecords();

        assertEquals(3, records.size());
        assertEquals("Bob", records.get(0).getPlayerName());
        assertEquals(300, records.get(0).getScore());
        assertEquals("Cindy", records.get(1).getPlayerName());
        assertEquals("Alice", records.get(2).getPlayerName());
    }

    @Test
    @DisplayName("deleteByRank：按排行榜名次删除记录")
    void deleteByRankShouldRemoveSelectedRecord() {
        dao.addRecord(new ScoreRecord("Alice", 100, LocalDateTime.now()));
        dao.addRecord(new ScoreRecord("Bob", 300, LocalDateTime.now()));

        dao.deleteByRank(1);
        List<ScoreRecord> records = dao.getAllRecords();

        assertEquals(1, records.size());
        assertEquals("Alice", records.get(0).getPlayerName());
    }

    @Test
    @DisplayName("deleteByRank：非法名次应抛出异常")
    void deleteByInvalidRankShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> dao.deleteByRank(1));
    }
}
