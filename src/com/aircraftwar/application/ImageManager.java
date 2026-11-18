package com.aircraftwar.application;

import com.aircraftwar.aircraft.BossEnemy;
import com.aircraftwar.aircraft.EliteEnemy;
import com.aircraftwar.aircraft.ElitePlusEnemy;
import com.aircraftwar.aircraft.EliteProEnemy;
import com.aircraftwar.aircraft.HeroAircraft;
import com.aircraftwar.aircraft.MobEnemy;
import com.aircraftwar.bullet.EnemyBullet;
import com.aircraftwar.bullet.HeroBullet;
import com.aircraftwar.supply.BloodSupply;
import com.aircraftwar.supply.BombSupply;
import com.aircraftwar.supply.FirePlusSupply;
import com.aircraftwar.supply.FireSupply;
import com.aircraftwar.supply.FreezeSupply;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 统一管理图片加载和访问
 */
public class ImageManager {

    private static final Map<String, BufferedImage> CLASSNAME_IMAGE_MAP = new HashMap<>();

    public static BufferedImage BACKGROUND_IMAGE;
    public static BufferedImage HERO_IMAGE;
    public static BufferedImage HERO_BULLET_IMAGE;
    public static BufferedImage ENEMY_BULLET_IMAGE;
    public static BufferedImage MOB_ENEMY_IMAGE;
    public static BufferedImage ELITE_ENEMY_IMAGE;
    public static BufferedImage ELITE_PLUS_ENEMY_IMAGE;
    public static BufferedImage ELITE_PRO_ENEMY_IMAGE;
    public static BufferedImage BOSS_ENEMY_IMAGE;
    public static BufferedImage BLOOD_SUPPLY_IMAGE;
    public static BufferedImage FIRE_SUPPLY_IMAGE;
    public static BufferedImage FIRE_PLUS_SUPPLY_IMAGE;
    public static BufferedImage BOMB_SUPPLY_IMAGE;
    public static BufferedImage FREEZE_SUPPLY_IMAGE;

    static {
        try {
            // 加载背景图片
            BACKGROUND_IMAGE = ImageIO.read(new FileInputStream("src/images/bg.jpg"));

            // 加载飞机图片
            HERO_IMAGE = ImageIO.read(new FileInputStream("src/images/hero.png"));
            MOB_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/mob.png"));
            ELITE_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elite.png"));
            ELITE_PLUS_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePlus.png"));
            ELITE_PRO_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/elitePro.png"));
            BOSS_ENEMY_IMAGE = ImageIO.read(new FileInputStream("src/images/boss.png"));

            // 加载子弹图片
            HERO_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_hero.png"));
            ENEMY_BULLET_IMAGE = ImageIO.read(new FileInputStream("src/images/bullet_enemy.png"));

            // 加载道具图片
            BLOOD_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_blood.png"));
            FIRE_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bullet.png"));
            FIRE_PLUS_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bulletPlus.png"));
            BOMB_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_bomb.png"));
            FREEZE_SUPPLY_IMAGE = ImageIO.read(new FileInputStream("src/images/prop_freeze.png"));

            // 映射类与图片
            CLASSNAME_IMAGE_MAP.put(HeroAircraft.class.getName(), HERO_IMAGE);
            CLASSNAME_IMAGE_MAP.put(MobEnemy.class.getName(), MOB_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteEnemy.class.getName(), ELITE_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(ElitePlusEnemy.class.getName(), ELITE_PLUS_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EliteProEnemy.class.getName(), ELITE_PRO_ENEMY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BossEnemy.class.getName(), BOSS_ENEMY_IMAGE);

            CLASSNAME_IMAGE_MAP.put(HeroBullet.class.getName(), HERO_BULLET_IMAGE);
            CLASSNAME_IMAGE_MAP.put(EnemyBullet.class.getName(), ENEMY_BULLET_IMAGE);

            CLASSNAME_IMAGE_MAP.put(BloodSupply.class.getName(), BLOOD_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FireSupply.class.getName(), FIRE_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FirePlusSupply.class.getName(), FIRE_PLUS_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(BombSupply.class.getName(), BOMB_SUPPLY_IMAGE);
            CLASSNAME_IMAGE_MAP.put(FreezeSupply.class.getName(), FREEZE_SUPPLY_IMAGE);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static BufferedImage get(String className) {
        return CLASSNAME_IMAGE_MAP.get(className);
    }

    public static BufferedImage get(Object obj) {
        if (obj == null) {
            return null;
        }
        return get(obj.getClass().getName());
    }

    public static void setBackgroundImage(GameDifficulty difficulty) {
        try {
            BACKGROUND_IMAGE = ImageIO.read(
                    new FileInputStream("src/images/" + difficulty.getBackgroundImageName())
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}