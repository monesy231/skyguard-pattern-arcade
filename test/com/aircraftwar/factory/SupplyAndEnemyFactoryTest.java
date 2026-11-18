package com.aircraftwar.factory;

import com.aircraftwar.aircraft.AbstractEnemy;
import com.aircraftwar.aircraft.BossEnemy;
import com.aircraftwar.aircraft.EliteEnemy;
import com.aircraftwar.aircraft.ElitePlusEnemy;
import com.aircraftwar.aircraft.EliteProEnemy;
import com.aircraftwar.aircraft.MobEnemy;
import com.aircraftwar.supply.AbstractSupply;
import com.aircraftwar.supply.BloodSupply;
import com.aircraftwar.supply.BombSupply;
import com.aircraftwar.supply.FirePlusSupply;
import com.aircraftwar.supply.FireSupply;
import com.aircraftwar.supply.FreezeSupply;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 简单工厂与工厂方法模式单元测试。
 */
class SupplyAndEnemyFactoryTest {

    @Test
    @DisplayName("SupplyFactory：按类型创建对应的 5 种补给道具")
    void supplyFactoryShouldCreateExpectedSubclassForEachType() {
        SupplyFactory factory = new SupplyFactory();

        assertTrue(factory.createSupply(SupplyType.BLOOD, 100, 200) instanceof BloodSupply);
        assertTrue(factory.createSupply(SupplyType.FIRE, 100, 200) instanceof FireSupply);
        assertTrue(factory.createSupply(SupplyType.FIRE_PLUS, 100, 200) instanceof FirePlusSupply);
        assertTrue(factory.createSupply(SupplyType.BOMB, 100, 200) instanceof BombSupply);
        assertTrue(factory.createSupply(SupplyType.FREEZE, 100, 200) instanceof FreezeSupply);
    }

    @Test
    @DisplayName("SupplyFactory：空池返回 null，限定池仅产出池内类型")
    void randomSupplyShouldRespectCandidatePool() {
        SupplyFactory factory = new SupplyFactory();

        assertNull(factory.createRandomSupply(new SupplyType[0], 50, 50));
        assertNull(factory.createRandomSupply(null, 50, 50));

        SupplyType[] pool = {SupplyType.BLOOD, SupplyType.FIRE};
        for (int i = 0; i < 20; i++) {
            AbstractSupply supply = factory.createRandomSupply(pool, 120, 180);
            assertNotNull(supply);
            assertTrue(supply instanceof BloodSupply || supply instanceof FireSupply);
        }
    }

    @Test
    @DisplayName("EnemyFactory 子工厂：创建各类型敌机并支持自定义参数")
    void enemyFactoriesShouldCreateConcreteEnemiesWithConfiguredStats() {
        AbstractEnemy mob = new MobEnemyFactory().createEnemy(40, 9);
        AbstractEnemy elite = new EliteEnemyFactory().createEnemy(60, 8);
        AbstractEnemy elitePlus = new ElitePlusEnemyFactory().createEnemy(80, 6);
        AbstractEnemy elitePro = new EliteProEnemyFactory().createEnemy(100, 6);
        BossEnemy boss = new BossEnemyFactory().createEnemy(420);

        assertTrue(mob instanceof MobEnemy);
        assertEquals(40, mob.getHp());
        assertEquals(9, mob.getSpeedY());

        assertTrue(elite instanceof EliteEnemy);
        assertEquals(60, elite.getHp());

        assertTrue(elitePlus instanceof ElitePlusEnemy);
        assertEquals(80, elitePlus.getHp());

        assertTrue(elitePro instanceof EliteProEnemy);
        assertEquals(100, elitePro.getHp());

        assertNotNull(boss);
        assertEquals(420, boss.getHp());
    }
}
