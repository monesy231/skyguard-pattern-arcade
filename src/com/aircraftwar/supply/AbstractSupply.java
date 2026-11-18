package com.aircraftwar.supply;

import com.aircraftwar.aircraft.HeroAircraft;
import com.aircraftwar.application.Main;
import com.aircraftwar.basic.AbstractFlyingObject;

/**
 * 所有道具的抽象父类
 */
public abstract class AbstractSupply extends AbstractFlyingObject {

    public AbstractSupply(int locationX, int locationY, int speedX, int speedY) {
        super(locationX, locationY, speedX, speedY);
    }

    @Override
    public void forward() {
        super.forward();
        if (locationY >= Main.WINDOW_HEIGHT) {
            vanish();
        }
    }

    public abstract void activate(HeroAircraft heroAircraft);
}
