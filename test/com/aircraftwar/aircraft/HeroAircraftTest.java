package com.aircraftwar.aircraft;

import com.aircraftwar.bullet.BaseBullet;
import com.aircraftwar.strategy.CircleShootStrategy;
import com.aircraftwar.strategy.ScatterShootStrategy;
import com.aircraftwar.strategy.StraightShootStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 英雄机核心状态、移动、弹道切换与版本恢复单元测试。
 * 至少测试 3 个方法，其中 decreaseHp、increaseHp、shoot 来自父类 AbstractAircraft。
 */
class HeroAircraftTest {

    private HeroAircraft heroAircraft;

    @BeforeEach
    void setUp() {
        heroAircraft = HeroAircraft.getInstance();
        heroAircraft.resetState();
    }

    @Test
    @DisplayName("父类方法 decreaseHp：扣血后血量减少，且未死亡时仍有效")
    void decreaseHpShouldReduceHp() {
        int oldHp = heroAircraft.getHp();

        heroAircraft.decreaseHp(50);

        assertEquals(oldHp - 50, heroAircraft.getHp());
        assertFalse(heroAircraft.notValid());
    }

    @Test
    @DisplayName("父类方法 decreaseHp：扣到 0 后英雄机失效")
    void decreaseHpToZeroShouldMakeHeroInvalid() {
        heroAircraft.decreaseHp(heroAircraft.getHp());

        assertEquals(0, heroAircraft.getHp());
        assertTrue(heroAircraft.notValid());
    }

    @Test
    @DisplayName("父类方法 increaseHp：回血不能超过最大生命值")
    void increaseHpShouldNotExceedMaxHp() {
        heroAircraft.decreaseHp(100);

        heroAircraft.increaseHp(30);
        assertEquals(180, heroAircraft.getHp());

        heroAircraft.increaseHp(1000);
        assertEquals(250, heroAircraft.getHp());
    }

    @Test
    @DisplayName("英雄机方法 forward：英雄机不通过 forward 自动移动")
    void forwardShouldNotMoveHero() {
        int oldX = heroAircraft.getLocationX();
        int oldY = heroAircraft.getLocationY();

        heroAircraft.forward();

        assertEquals(oldX, heroAircraft.getLocationX());
        assertEquals(oldY, heroAircraft.getLocationY());
    }

    @Test
    @DisplayName("父类方法 shoot：默认直射模式可以产生子弹")
    void shootShouldCreateBulletInStraightMode() {
        List<BaseBullet> bullets = heroAircraft.shoot();

        assertNotNull(bullets);
        assertFalse(bullets.isEmpty());
        assertTrue(heroAircraft.getShootStrategy() instanceof StraightShootStrategy);
    }

    @Test
    @DisplayName("英雄机火力道具方法：散射/环形生效后可按版本恢复直射")
    void firePowerShouldRecoverByVersion() {
        int scatterVersion = heroAircraft.activateScatterShoot();
        assertTrue(heroAircraft.getShootStrategy() instanceof ScatterShootStrategy);

        int circleVersion = heroAircraft.activateCircleShoot();
        assertTrue(heroAircraft.getShootStrategy() instanceof CircleShootStrategy);

        heroAircraft.recoverStraightShoot(scatterVersion);
        assertTrue(heroAircraft.getShootStrategy() instanceof CircleShootStrategy,
                "旧版本恢复任务不应覆盖更新的火力道具效果");

        heroAircraft.recoverStraightShoot(circleVersion);
        assertTrue(heroAircraft.getShootStrategy() instanceof StraightShootStrategy);
    }
}
