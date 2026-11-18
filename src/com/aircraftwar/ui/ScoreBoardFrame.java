package com.aircraftwar.ui;

import com.aircraftwar.application.GameDifficulty;
import com.aircraftwar.application.Main;
import com.aircraftwar.score.ScoreBoardService;
import com.aircraftwar.score.ScoreRecord;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ScoreBoardFrame extends JFrame {

    private final GameDifficulty difficulty;
    private final ScoreBoardService scoreBoardService;
    private final DefaultTableModel tableModel;
    private final JTable scoreTable;

    public ScoreBoardFrame(GameDifficulty difficulty, int currentScore) {
        super("排行榜 - " + difficulty.getDisplayName() + "模式");
        this.difficulty = difficulty;
        this.scoreBoardService = new ScoreBoardService(difficulty);

        int result = JOptionPane.showConfirmDialog(
                this,
                "游戏结束！本局得分：" + currentScore + "\n是否保存本局记录？",
                "保存成绩",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            String playerName = JOptionPane.showInputDialog(this, "请输入玩家姓名：", "默认玩家");
            if (playerName == null || playerName.trim().isEmpty()) {
                playerName = "默认玩家";
            }
            scoreBoardService.saveScore(playerName, currentScore);
            scoreBoardService.printScoreBoard();
        }

        JLabel titleLabel = new JLabel(difficulty.getDisplayName() + "模式得分排行榜", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));

        tableModel = new DefaultTableModel(new Object[]{"名次", "玩家名", "得分", "记录时间"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        scoreTable = new JTable(tableModel);
        scoreTable.setRowHeight(28);

        JButton deleteButton = new JButton("删除选中记录");
        deleteButton.addActionListener(e -> deleteSelectedRecord());

        JButton refreshButton = new JButton("刷新排行榜");
        refreshButton.addActionListener(e -> reloadTable());

        JButton returnMenuButton = new JButton("返回主菜单");
        returnMenuButton.addActionListener(e -> {
            dispose();
            Main.showStartMenu();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(deleteButton);
        bottomPanel.add(refreshButton);
        bottomPanel.add(returnMenuButton);

        setLayout(new BorderLayout(10, 10));
        add(titleLabel, BorderLayout.NORTH);
        add(new JScrollPane(scoreTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        setSize(640, 420);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        reloadTable();
    }

    private void reloadTable() {
        tableModel.setRowCount(0);
        List<ScoreRecord> records = scoreBoardService.listScores();
        for (int i = 0; i < records.size(); i++) {
            ScoreRecord record = records.get(i);
            tableModel.addRow(new Object[]{
                    i + 1,
                    record.getPlayerName(),
                    record.getScore(),
                    record.getFormattedTime()
            });
        }
    }

    private void deleteSelectedRecord() {
        int selectedRow = scoreTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "请先选择一条记录。", "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        int rank = (int) tableModel.getValueAt(selectedRow, 0);
        int result = JOptionPane.showConfirmDialog(
                this,
                "确定删除第 " + rank + " 名记录吗？",
                "确认删除",
                JOptionPane.YES_NO_OPTION
        );
        if (result == JOptionPane.YES_OPTION) {
            scoreBoardService.deleteByRank(rank);
            reloadTable();
        }
    }
}
