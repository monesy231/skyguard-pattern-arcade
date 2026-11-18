package com.aircraftwar.strategy;

import com.aircraftwar.aircraft.AbstractAircraft;
import com.aircraftwar.bullet.BaseBullet;

import java.util.Collections;
import java.util.List;

public class NoShootStrategy implements ShootStrategy {

    @Override
    public List<BaseBullet> shoot(AbstractAircraft aircraft) {
        return Collections.emptyList();
    }
}
