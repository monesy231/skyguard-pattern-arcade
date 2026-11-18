package com.aircraftwar.strategy;

import com.aircraftwar.aircraft.AbstractAircraft;
import com.aircraftwar.aircraft.HeroAircraft;
import com.aircraftwar.bullet.BaseBullet;
import com.aircraftwar.strategy.ShootStrategy;

import java.util.LinkedList;
import java.util.List;

public class CircleShootStrategy implements ShootStrategy {

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        List<BaseBullet> bullets = new LinkedList<>();
        int shootNum = Math.max(1, aircraft.getShootNum());
        int bulletSpeed = aircraft instanceof HeroAircraft ? 8 : 6;

        for (int i = 0; i < shootNum; i++) {
            double angle = 2 * Math.PI * i / shootNum;
            int speedX = (int) Math.round(bulletSpeed * Math.cos(angle));
            int speedY = (int) Math.round(bulletSpeed * Math.sin(angle));
            bullets.add(createBullet(
                    aircraft,
                    aircraft.getLocationX(),
                    aircraft.getLocationY(),
                    speedX,
                    speedY
            ));
        }
        return bullets;
    }
}
