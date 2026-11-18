package com.aircraftwar.application;

import com.aircraftwar.ui.StartMenu;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Toolkit;

/**
 * 程序入口
 */
public class Main {

    public static final int WINDOW_WIDTH = 512;
    public static final int WINDOW_HEIGHT = 768;

    public static final CardLayout CARD_LAYOUT = new CardLayout();
    public static final JPanel MAIN_PANEL = new JPanel(CARD_LAYOUT);

    public static JFrame frame;

    public static void main(String[] args) {
        System.out.println("Hello Aircraft War");

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        frame = new JFrame("Aircraft War");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setResizable(false);
        frame.setBounds(
                ((int) screenSize.getWidth() - WINDOW_WIDTH) / 2,
                0,
                WINDOW_WIDTH,
                WINDOW_HEIGHT
        );
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        StartMenu startMenu = new StartMenu();
        MAIN_PANEL.add(startMenu, "start");

        frame.add(MAIN_PANEL);
        frame.setVisible(true);

        CARD_LAYOUT.show(MAIN_PANEL, "start");
    }

    public static void startGame(GameDifficulty difficulty) {
        Game game;

        if (difficulty == GameDifficulty.EASY) {
            game = new EasyGame();
        } else if (difficulty == GameDifficulty.NORMAL) {
            game = new NormalGame();
        } else {
            game = new HardGame();
        }

        MAIN_PANEL.add(game, "game");
        CARD_LAYOUT.show(MAIN_PANEL, "game");

        frame.revalidate();
        frame.repaint();

        game.requestFocusInWindow();
        game.action();
    }

    public static void showStartMenu() {
        CARD_LAYOUT.show(MAIN_PANEL, "start");
        frame.revalidate();
        frame.repaint();
    }
}