package com.aircraftwar.dao;

import com.aircraftwar.score.ScoreRecord;

import java.util.List;

public interface ScoreDao {
    void addRecord(ScoreRecord record);

    List<ScoreRecord> getAllRecords();

    void deleteByRank(int rank);
}
