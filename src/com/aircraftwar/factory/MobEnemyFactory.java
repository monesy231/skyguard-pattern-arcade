package com.aircraftwar.factory;
import com.aircraftwar.aircraft.AbstractEnemy;
import com.aircraftwar.aircraft.MobEnemy;
import com.aircraftwar.application.ImageManager;

public class MobEnemyFactory extends EnemyFactory {

    private static final int MOB_ENEMY_HP = 25;
    private static final int MOB_ENEMY_SPEED_Y = 7;

    @Override
    public AbstractEnemy createEnemy() {
        return createEnemy(MOB_ENEMY_HP, MOB_ENEMY_SPEED_Y);
    }

    public AbstractEnemy createEnemy(int hp, int speedY) {
        return new MobEnemy(
                randomLocationX(ImageManager.MOB_ENEMY_IMAGE.getWidth()),
                randomLocationY(ImageManager.MOB_ENEMY_IMAGE.getHeight()),
                0,
                speedY,
                hp
        );
    }
}
