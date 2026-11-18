package com.aircraftwar.factory;
import com.aircraftwar.aircraft.AbstractEnemy;
import com.aircraftwar.aircraft.BossEnemy;
import com.aircraftwar.application.ImageManager;
import com.aircraftwar.application.Main;

public class BossEnemyFactory extends EnemyFactory {

    private static final int BOSS_ENEMY_HP = 280;

    @Override
    public AbstractEnemy createEnemy() {
        return createEnemy(BOSS_ENEMY_HP);
    }

    public BossEnemy createEnemy(int hp) {
        return new BossEnemy(
                Main.WINDOW_WIDTH / 2,
                ImageManager.BOSS_ENEMY_IMAGE.getHeight(),
                randomSideSpeed(),
                0,
                hp
        );
    }
}