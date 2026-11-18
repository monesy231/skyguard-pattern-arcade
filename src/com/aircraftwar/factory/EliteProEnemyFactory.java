package com.aircraftwar.factory;
import com.aircraftwar.aircraft.AbstractEnemy;
import com.aircraftwar.aircraft.EliteProEnemy;
import com.aircraftwar.application.ImageManager;

public class EliteProEnemyFactory extends EnemyFactory {

    private static final int ELITE_PRO_ENEMY_HP = 85;
    private static final int ELITE_PRO_ENEMY_SPEED_Y = 5;

    @Override
    public AbstractEnemy createEnemy() {
        return createEnemy(ELITE_PRO_ENEMY_HP, ELITE_PRO_ENEMY_SPEED_Y);
    }

    public AbstractEnemy createEnemy(int hp, int speedY) {
        return new EliteProEnemy(
                randomLocationX(ImageManager.ELITE_PRO_ENEMY_IMAGE.getWidth()),
                randomLocationY(ImageManager.ELITE_PRO_ENEMY_IMAGE.getHeight()),
                randomSideSpeed(),
                speedY,
                hp
        );
    }
}
