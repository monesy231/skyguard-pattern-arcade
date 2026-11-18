package com.aircraftwar.score;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScoreRecord {

    public static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final String playerName;
    private final int score;
    private final LocalDateTime recordTime;

    public ScoreRecord(String playerName, int score, LocalDateTime recordTime) {
        if (playerName == null || playerName.trim().isEmpty()) {
            playerName = "默认玩家";
        }
        if (score < 0) {
            throw new IllegalArgumentException("score cannot be negative");
        }
        this.playerName = playerName.trim();
        this.score = score;
        this.recordTime = recordTime;
    }

    public String getPlayerName() {
        return playerName;
    }

    public int getScore() {
        return score;
    }

    public LocalDateTime getRecordTime() {
        return recordTime;
    }

    public String getFormattedTime() {
        return recordTime.format(FORMATTER);
    }

    public String toFileLine() {
        return escape(playerName) + "\t" + score + "\t" + getFormattedTime();
    }

    public static ScoreRecord fromFileLine(String line) {
        String[] parts = line.split("\t", 3);
        if (parts.length != 3) {
            throw new IllegalArgumentException("bad score line: " + line);
        }
        return new ScoreRecord(
                unescape(parts[0]),
                Integer.parseInt(parts[1]),
                LocalDateTime.parse(parts[2], FORMATTER)
        );
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\t", "\\t");
    }

    private static String unescape(String text) {
        return text.replace("\\t", "\t").replace("\\\\", "\\");
    }
}
