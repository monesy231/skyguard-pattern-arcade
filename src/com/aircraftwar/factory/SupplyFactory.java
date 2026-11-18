package com.aircraftwar.factory;
import com.aircraftwar.supply.AbstractSupply;
import com.aircraftwar.supply.BloodSupply;
import com.aircraftwar.supply.BombSupply;
import com.aircraftwar.supply.FirePlusSupply;
import com.aircraftwar.supply.FireSupply;
import com.aircraftwar.supply.FreezeSupply;

import java.util.concurrent.ThreadLocalRandom;

public class SupplyFactory {

    private static final int SUPPLY_SPEED_Y = 6;

    public AbstractSupply createSupply(SupplyType type, int locationX, int locationY) {
        switch (type) {
            case BLOOD:
                return new BloodSupply(locationX, locationY, 0, SUPPLY_SPEED_Y);
            case FIRE:
                return new FireSupply(locationX, locationY, 0, SUPPLY_SPEED_Y);
            case FIRE_PLUS:
                return new FirePlusSupply(locationX, locationY, 0, SUPPLY_SPEED_Y);
            case BOMB:
                return new BombSupply(locationX, locationY, 0, SUPPLY_SPEED_Y);
            case FREEZE:
                return new FreezeSupply(locationX, locationY, 0, SUPPLY_SPEED_Y);
            default:
                throw new IllegalArgumentException("Unsupported supply type: " + type);
        }
    }

    public AbstractSupply createRandomSupply(SupplyType[] supplyTypes, int locationX, int locationY) {
        if (supplyTypes == null || supplyTypes.length == 0) {
            return null;
        }
        int index = ThreadLocalRandom.current().nextInt(supplyTypes.length);
        SupplyType supplyType = supplyTypes[index];
        return createSupply(supplyType, locationX, locationY);
    }
}