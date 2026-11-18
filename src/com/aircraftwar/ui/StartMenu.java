package com.aircraftwar.ui;

import com.aircraftwar.application.GameDifficulty;
import com.aircraftwar.application.Main;
import com.aircraftwar.music.MusicManager;

import javax.swing.*;
import java.awt.*;

public class StartMenu extends JPanel {

    public StartMenu() {
        setLayout(new GridBagLayout());

        JLabel titleLabel = new JLabel("飞机大战 - 请选择游戏难度");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));

        JButton easyButton = createButton("简单模式");
        JButton normalButton = createButton("普通模式");
        JButton hardButton = createButton("困难模式");

        JCheckBox soundCheckBox = new JCheckBox("开启游戏音效", MusicManager.isSoundEnabled());
        soundCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 16));
        soundCheckBox.setFocusPainted(false);

        easyButton.addActionListener(e -> {
            MusicManager.setSoundEnabled(soundCheckBox.isSelected());
            Main.startGame(GameDifficulty.EASY);
        });
        normalButton.addActionListener(e -> {
            MusicManager.setSoundEnabled(soundCheckBox.isSelected());
            Main.startGame(GameDifficulty.NORMAL);
        });
        hardButton.addActionListener(e -> {
            MusicManager.setSoundEnabled(soundCheckBox.isSelected());
            Main.startGame(GameDifficulty.HARD);
        });

        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.insets = new Insets(16, 0, 16, 0);

        c.gridy = 0;
        add(titleLabel, c);
        c.gridy = 1;
        add(easyButton, c);
        c.gridy = 2;
        add(normalButton, c);
        c.gridy = 3;
        add(hardButton, c);
        c.gridy = 4;
        add(soundCheckBox, c);
    }

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("SansSerif", Font.BOLD, 18));
        button.setPreferredSize(new Dimension(180, 55));
        return button;
    }
}
