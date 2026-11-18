package com.aircraftwar.observer;

import com.aircraftwar.aircraft.BossEnemy;
import com.aircraftwar.aircraft.EliteProEnemy;
import com.aircraftwar.aircraft.MobEnemy;
import com.aircraftwar.bullet.EnemyBullet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 观察者模式（炸弹与冰冻道具广播通知）单元测试。
 */
class PropObserverTest {

    @Test
    @DisplayName("BombPropSubject：清除普通敌机与敌机子弹，削减王牌敌机血量，Boss 免疫")
    void bombSubjectShouldNotifyObserversPolymorphically() {
        MobEnemy mob = new MobEnemy(100, 100, 0, 7, 25);
        EliteProEnemy elitePro = new EliteProEnemy(200, 150, 3, 5, 85);
        BossEnemy boss = new BossEnemy(256, 80, 3, 0, 300);
        EnemyBullet bullet = new EnemyBullet(150, 200, 0, 6, 10);

        BombPropSubject bombSubject = new BombPropSubject();
        bombSubject.addObserver(mob);
        bombSubject.addObserver(elitePro);
        bombSubject.addObserver(boss);
        bombSubject.addObserver(bullet);

        int gainedScore = bombSubject.notifyObservers();

        assertTrue(mob.notValid());
        assertTrue(bullet.notValid());
        assertEquals(45, elitePro.getHp(), "王牌敌机受到 40 点炸弹伤害后应剩余 45 血量");
        assertFalse(elitePro.notValid());
        assertEquals(300, boss.getHp());
        assertFalse(boss.notValid());
        assertEquals(15, gainedScore, "仅坠毁的普通敌机贡献 15 分");
    }

    @Test
    @DisplayName("FreezePropSubject：普通敌机永久冻结，王牌敌机减速，Boss 不受影响")
    void freezeSubjectShouldApplyDifferentEffectsByEnemyType() {
        MobEnemy mob = new MobEnemy(100, 100, 0, 8, 25);
        EliteProEnemy elitePro = new EliteProEnemy(200, 150, 4, 6, 85);
        BossEnemy boss = new BossEnemy(256, 80, 3, 0, 300);
        EnemyBullet bullet = new EnemyBullet(150, 200, 0, 8, 10);

        FreezePropSubject freezeSubject = new FreezePropSubject();
        freezeSubject.addObserver(mob);
        freezeSubject.addObserver(elitePro);
        freezeSubject.addObserver(boss);
        freezeSubject.addObserver(bullet);

        int gainedScore = freezeSubject.notifyObservers();

        assertEquals(0, gainedScore);
        assertEquals(0, mob.getSpeedY());
        assertEquals(0, bullet.getSpeedY());
        assertEquals(2, elitePro.getSpeedX());
        assertEquals(3, elitePro.getSpeedY());
        assertEquals(3, boss.getSpeedX());
    }
}
