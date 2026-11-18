package com.aircraftwar.score;

import com.aircraftwar.application.GameDifficulty;
import com.aircraftwar.dao.ScoreDao;
import com.aircraftwar.dao.ScoreDaoImpl;

import java.time.LocalDateTime;
import java.util.List;

public class ScoreBoardService {

    private final ScoreDao scoreDao;

    public ScoreBoardService(GameDifficulty difficulty) {
        this(new ScoreDaoImpl(difficulty));
    }

    public ScoreBoardService(ScoreDao scoreDao) {
        this.scoreDao = scoreDao;
    }

    public void saveScore(String playerName, int score) {
        scoreDao.addRecord(new ScoreRecord(playerName, score, LocalDateTime.now()));
    }

    public List<ScoreRecord> listScores() {
        return scoreDao.getAllRecords();
    }

    public void deleteByRank(int rank) {
        scoreDao.deleteByRank(rank);
    }

    public void printScoreBoard() {
        List<ScoreRecord> records = listScores();
        System.out.println("****************");
        System.out.println("得分排行榜");
        System.out.println("****************");
        System.out.printf("%-6s%-12s%-8s%-20s%n", "名次", "玩家名", "得分", "记录时间");
        for (int i = 0; i < records.size(); i++) {
            ScoreRecord record = records.get(i);
            System.out.printf("%-6d%-12s%-8d%-20s%n",
                    i + 1,
                    record.getPlayerName(),
                    record.getScore(),
                    record.getFormattedTime());
        }
    }
}
