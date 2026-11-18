package com.aircraftwar.factory;
import com.aircraftwar.aircraft.AbstractEnemy;
import com.aircraftwar.aircraft.ElitePlusEnemy;
import com.aircraftwar.application.ImageManager;

public class ElitePlusEnemyFactory extends EnemyFactory {

    private static final int ELITE_PLUS_ENEMY_HP = 65;
    private static final int ELITE_PLUS_ENEMY_SPEED_Y = 5;

    @Override
    public AbstractEnemy createEnemy() {
        return createEnemy(ELITE_PLUS_ENEMY_HP, ELITE_PLUS_ENEMY_SPEED_Y);
    }

    public AbstractEnemy createEnemy(int hp, int speedY) {
        return new ElitePlusEnemy(
                randomLocationX(ImageManager.ELITE_PLUS_ENEMY_IMAGE.getWidth()),
                randomLocationY(ImageManager.ELITE_PLUS_ENEMY_IMAGE.getHeight()),
                randomSideSpeed(),
                speedY,
                hp
        );
    }
}
