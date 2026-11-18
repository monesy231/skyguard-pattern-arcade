package com.aircraftwar.strategy;

import com.aircraftwar.aircraft.AbstractAircraft;
import com.aircraftwar.aircraft.HeroAircraft;
import com.aircraftwar.bullet.BaseBullet;
import com.aircraftwar.bullet.EnemyBullet;
import com.aircraftwar.bullet.HeroBullet;

import java.util.List;

public interface ShootStrategy {


    List<BaseBullet> shoot(AbstractAircraft aircraft);
    default BaseBullet createBullet(AbstractAircraft aircraft, int x, int y, int speedX, int speedY) {
        if (aircraft instanceof HeroAircraft) {
            return new HeroBullet(x, y, speedX, speedY, aircraft.getPower());
        }
        return new EnemyBullet(x, y, speedX, speedY, aircraft.getPower());
    }
}
