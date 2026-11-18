package com.aircraftwar.application;

import com.aircraftwar.aircraft.HeroAircraft;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * 英雄机控制类
 * 监听鼠标，控制英雄机的移动
 */
public class HeroController {
    private Game game;
    private HeroAircraft heroAircraft;
    private MouseAdapter mouseAdapter;

    public HeroController(Game game, HeroAircraft heroAircraft){
        this.game = game;
        this.heroAircraft = heroAircraft;

        mouseAdapter = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                game.requestFocusInWindow();
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (game.isPaused()) {
                    return;
                }
                int x = e.getX();
                int y = e.getY();
                if ( x<0 || x>Main.WINDOW_WIDTH || y<0 || y>Main.WINDOW_HEIGHT){
                    // 防止超出边界
                    return;
                }
                heroAircraft.setLocation(x, y);
            }
        };

        game.addMouseListener(mouseAdapter);
        game.addMouseMotionListener(mouseAdapter);
    }


}
