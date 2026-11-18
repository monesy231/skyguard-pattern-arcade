package com.aircraftwar.factory;
import com.aircraftwar.aircraft.AbstractEnemy;
import com.aircraftwar.aircraft.EliteEnemy;
import com.aircraftwar.application.ImageManager;

public class EliteEnemyFactory extends EnemyFactory {

    private static final int ELITE_ENEMY_HP = 45;
    private static final int ELITE_ENEMY_SPEED_Y = 7;

    @Override
    public AbstractEnemy createEnemy() {
        return createEnemy(ELITE_ENEMY_HP, ELITE_ENEMY_SPEED_Y);
    }

    public AbstractEnemy createEnemy(int hp, int speedY) {
        return new EliteEnemy(
                randomLocationX(ImageManager.ELITE_ENEMY_IMAGE.getWidth()),
                randomLocationY(ImageManager.ELITE_ENEMY_IMAGE.getHeight()),
                0,
                speedY,
                hp
        );
    }
}
