package com.aircraftwar.application;

import com.aircraftwar.aircraft.AbstractEnemy;
import com.aircraftwar.aircraft.BossEnemy;
import com.aircraftwar.aircraft.EliteEnemy;
import com.aircraftwar.aircraft.ElitePlusEnemy;
import com.aircraftwar.aircraft.EliteProEnemy;
import com.aircraftwar.aircraft.HeroAircraft;
import com.aircraftwar.basic.AbstractFlyingObject;
import com.aircraftwar.bullet.BaseBullet;
import com.aircraftwar.bullet.EnemyBullet;
import com.aircraftwar.factory.BossEnemyFactory;
import com.aircraftwar.factory.EliteEnemyFactory;
import com.aircraftwar.factory.ElitePlusEnemyFactory;
import com.aircraftwar.factory.EliteProEnemyFactory;
import com.aircraftwar.factory.MobEnemyFactory;
import com.aircraftwar.factory.SupplyFactory;
import com.aircraftwar.factory.SupplyType;
import com.aircraftwar.music.MusicManager;
import com.aircraftwar.observer.BombPropSubject;
import com.aircraftwar.observer.FreezePropSubject;
import com.aircraftwar.observer.PropSubject;
import com.aircraftwar.supply.AbstractSupply;
import com.aircraftwar.supply.BombSupply;
import com.aircraftwar.supply.FreezeSupply;
import com.aircraftwar.ui.ScoreBoardFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 模板模式中的抽象类角色：定义游戏主流程 action() 的算法骨架。
 * EasyGame、NormalGame、HardGame 只重写难度差异步骤。
 */
public abstract class Game extends JPanel {

    private static final double ELITE_SUPPLY_PROBABILITY = 0.60;
    private static final double ELITE_PLUS_SUPPLY_PROBABILITY = 0.75;
    private static final double ELITE_PRO_SUPPLY_PROBABILITY = 0.95;

    private static final SupplyType[] ELITE_SUPPLY_POOL = {
            SupplyType.BLOOD, SupplyType.FIRE, SupplyType.FIRE_PLUS
    };
    private static final SupplyType[] ELITE_PLUS_SUPPLY_POOL = {
            SupplyType.BLOOD, SupplyType.FIRE, SupplyType.FIRE_PLUS, SupplyType.BOMB
    };

    protected int mobEnemyHp = 25;
    protected int mobEnemySpeedY = 7;
    protected int eliteEnemyHp = 45;
    protected int eliteEnemySpeedY = 7;
    protected int elitePlusEnemyHp = 65;
    protected int elitePlusEnemySpeedY = 5;
    protected int eliteProEnemyHp = 85;
    protected int eliteProEnemySpeedY = 5;
    protected int bossEnemyHp = 280;

    protected int enemyMaxNumber = 5;
    protected int bossScoreThreshold = 1200;
    protected int nextBossScoreThreshold = bossScoreThreshold;

    protected double enemySpawnCycle = 30;
    protected double heroShootCycle = 22;
    protected double enemyShootCycle = 22;

    protected double mobProbability = 0.38;
    protected double eliteProbability = 0.64;
    protected double elitePlusProbability = 0.85;

    private int backGroundTop = 0;
    protected final Timer timer;
    protected final int timeInterval = 40;

    protected final GameDifficulty difficulty;
    protected final HeroAircraft heroAircraft;
    protected final List<AbstractEnemy> enemyAircrafts;
    protected final List<BaseBullet> heroBullets;
    protected final List<BaseBullet> enemyBullets;
    protected final List<AbstractSupply> supplies;
    protected final MusicManager musicManager = new MusicManager();

    private final SupplyFactory supplyFactory = new SupplyFactory();
    private final MobEnemyFactory mobEnemyFactory = new MobEnemyFactory();
    private final EliteEnemyFactory eliteEnemyFactory = new EliteEnemyFactory();
    private final ElitePlusEnemyFactory elitePlusEnemyFactory = new ElitePlusEnemyFactory();
    private final EliteProEnemyFactory eliteProEnemyFactory = new EliteProEnemyFactory();
    private final BossEnemyFactory bossEnemyFactory = new BossEnemyFactory();

    protected int score = 0;
    private int enemySpawnCounter = 0;
    private int heroShootCounter = 0;
    private int enemyShootCounter = 0;
    private int gameTimeTick = 0;
    private boolean gameOverHandled = false;
    private boolean started = false;
    private volatile boolean paused = false;

    public Game(GameDifficulty difficulty) {
        this.difficulty = difficulty;
        ImageManager.setBackgroundImage(difficulty);

        heroAircraft = HeroAircraft.getInstance();
        heroAircraft.resetState();

        enemyAircrafts = new LinkedList<>();
        heroBullets = new LinkedList<>();
        enemyBullets = new LinkedList<>();
        supplies = new LinkedList<>();

        new HeroController(this, heroAircraft);
        this.timer = new Timer("game-action-timer", true);

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int keyCode = e.getKeyCode();
                if (keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_SPACE) {
                    togglePause();
                } else if (keyCode == KeyEvent.VK_M) {
                    musicManager.toggleSound(hasBossEnemy());
                    repaint();
                }
            }
        });
    }

    public boolean isPaused() {
        return paused;
    }

    public void togglePause() {
        paused = !paused;
        repaint();
    }

    /**
     * 模板方法：固定游戏主循环流程，不允许子类修改整体流程。
     */
    public final void action() {
        if (started) {
            return;
        }
        started = true;
        initDifficultyConfig();
        musicManager.loop(MusicManager.BGM, MusicManager.BGM_PATH);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (paused) {
                    repaint();
                    return;
                }
                gameTimeTick++;
                spawnEnemyAction();
                shootAction();
                bulletsMoveAction();
                aircraftsMoveAction();
                suppliesMoveAction();
                crashCheckAction();
                increaseDifficultyIfNeeded();
                postProcessAction();
                repaint();
                checkResultAction();
            }
        };
        timer.schedule(task, 0, timeInterval);
    }

    protected abstract void initDifficultyConfig();

    protected abstract void increaseDifficultyIfNeeded();

    protected abstract boolean canCreateBoss();

    protected abstract void afterBossCreated();

    private void spawnEnemyAction() {
        enemySpawnCounter++;
        if (enemySpawnCounter < enemySpawnCycle) {
            return;
        }
        enemySpawnCounter = 0;

        if (canCreateBoss() && score >= nextBossScoreThreshold && !hasBossEnemy()) {
            BossEnemy bossEnemy = createBossEnemy();
            enemyAircrafts.add(bossEnemy);
            System.out.println(difficulty.getDisplayName() + "模式：Boss 出场，血量=" + bossEnemy.getHp());
            musicManager.loop(MusicManager.BOSS_BGM, MusicManager.BOSS_BGM_PATH);
            nextBossScoreThreshold += bossScoreThreshold;
            afterBossCreated();
            return;
        }

        if (enemyAircrafts.size() >= enemyMaxNumber) {
            return;
        }
        enemyAircrafts.add(createCommonEnemyAircraft());
    }

    private void shootAction() {
        heroShootCounter++;
        enemyShootCounter++;

        if (heroShootCounter >= heroShootCycle) {
            heroShootCounter = 0;
            heroBullets.addAll(heroAircraft.shoot());
        }

        if (enemyShootCounter >= enemyShootCycle) {
            enemyShootCounter = 0;
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                if (!enemyAircraft.notValid()) {
                    enemyBullets.addAll(enemyAircraft.shoot());
                }
            }
        }
    }

    private void bulletsMoveAction() {
        for (BaseBullet bullet : heroBullets) {
            bullet.forward();
        }
        for (BaseBullet bullet : enemyBullets) {
            bullet.forward();
        }
    }

    private void aircraftsMoveAction() {
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            enemyAircraft.forward();
        }
    }

    private void suppliesMoveAction() {
        for (AbstractSupply supply : supplies) {
            supply.forward();
        }
    }

    /**
     * 碰撞检测：敌机子弹、英雄子弹、敌机撞击、道具拾取。
     */
    private void crashCheckAction() {
        for (BaseBullet bullet : enemyBullets) {
            if (bullet.notValid()) {
                continue;
            }
            if (heroAircraft.crash(bullet)) {
                heroAircraft.decreaseHp(bullet.getPower());
                bullet.vanish();
            }
        }

        for (BaseBullet bullet : heroBullets) {
            if (bullet.notValid()) {
                continue;
            }
            for (AbstractEnemy enemyAircraft : enemyAircrafts) {
                if (enemyAircraft.notValid()) {
                    continue;
                }
                if (enemyAircraft.crash(bullet)) {
                    musicManager.playOnce(MusicManager.BULLET_HIT_PATH);
                    enemyAircraft.decreaseHp(bullet.getPower());
                    bullet.vanish();
                    if (enemyAircraft.notValid()) {
                        score += enemyAircraft.getScore();
                        if (enemyAircraft instanceof BossEnemy) {
                            musicManager.stop(MusicManager.BOSS_BGM);
                        }
                        supplies.addAll(createSupplies(enemyAircraft));
                    }
                    break;
                }
            }
        }

        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            if (enemyAircraft.notValid()) {
                continue;
            }
            if (enemyAircraft.crash(heroAircraft) || heroAircraft.crash(enemyAircraft)) {
                enemyAircraft.vanish();
                if (enemyAircraft instanceof BossEnemy) {
                    musicManager.stop(MusicManager.BOSS_BGM);
                }
                heroAircraft.decreaseHp(Integer.MAX_VALUE);
            }
        }

        for (AbstractSupply supply : supplies) {
            if (supply.notValid()) {
                continue;
            }
            if (heroAircraft.crash(supply)) {
                musicManager.playOnce(MusicManager.GET_SUPPLY_PATH);
                if (supply instanceof BombSupply) {
                    supply.activate(heroAircraft);
                    score += triggerBombProp();
                    musicManager.playOnce(MusicManager.BOMB_EXPLOSION_PATH);
                } else if (supply instanceof FreezeSupply) {
                    supply.activate(heroAircraft);
                    triggerFreezeProp();
                } else {
                    supply.activate(heroAircraft);
                }
                supply.vanish();
            }
        }
    }

    private int triggerBombProp() {
        PropSubject bombSubject = new BombPropSubject();
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            if (!enemyAircraft.notValid()) {
                bombSubject.addObserver(enemyAircraft);
            }
        }
        for (BaseBullet bullet : enemyBullets) {
            if (!bullet.notValid() && bullet instanceof EnemyBullet) {
                bombSubject.addObserver((EnemyBullet) bullet);
            }
        }
        return bombSubject.notifyObservers();
    }

    private void triggerFreezeProp() {
        PropSubject freezeSubject = new FreezePropSubject();
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            if (!enemyAircraft.notValid()) {
                freezeSubject.addObserver(enemyAircraft);
            }
        }
        for (BaseBullet bullet : enemyBullets) {
            if (!bullet.notValid() && bullet instanceof EnemyBullet) {
                freezeSubject.addObserver((EnemyBullet) bullet);
            }
        }
        freezeSubject.notifyObservers();
    }

    /**
     * 后处理：删除无效子弹、敌机、道具。
     */
    private void postProcessAction() {
        boolean bossInvalid = false;
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            if (enemyAircraft instanceof BossEnemy && enemyAircraft.notValid()) {
                bossInvalid = true;
                break;
            }
        }
        enemyBullets.removeIf(AbstractFlyingObject::notValid);
        heroBullets.removeIf(AbstractFlyingObject::notValid);
        enemyAircrafts.removeIf(AbstractFlyingObject::notValid);
        supplies.removeIf(AbstractFlyingObject::notValid);
        if (bossInvalid || !hasBossEnemy()) {
            musicManager.stop(MusicManager.BOSS_BGM);
        }
    }

    private void checkResultAction() {
        if (heroAircraft.getHp() <= 0 && !gameOverHandled) {
            gameOverHandled = true;
            timer.cancel();
            musicManager.stopAllLoopMusic();
            musicManager.playOnce(MusicManager.GAME_OVER_PATH);
            System.out.println("Game Over!");
            SwingUtilities.invokeLater(() -> new ScoreBoardFrame(difficulty, score).setVisible(true));
        }
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop - Main.WINDOW_HEIGHT, null);
        g.drawImage(ImageManager.BACKGROUND_IMAGE, 0, this.backGroundTop, null);
        if (!paused) {
            this.backGroundTop += 1;
            if (this.backGroundTop == Main.WINDOW_HEIGHT) {
                this.backGroundTop = 0;
            }
        }

        paintImageWithPositionRevised(g, enemyBullets);
        paintImageWithPositionRevised(g, heroBullets);
        paintImageWithPositionRevised(g, enemyAircrafts);
        paintImageWithPositionRevised(g, supplies);

        g.drawImage(
                ImageManager.HERO_IMAGE,
                heroAircraft.getLocationX() - ImageManager.HERO_IMAGE.getWidth() / 2,
                heroAircraft.getLocationY() - ImageManager.HERO_IMAGE.getHeight() / 2,
                null
        );

        paintScoreAndLife(g);
        paintBossHpBar(g);
        if (paused) {
            paintPauseOverlay(g);
        }
    }

    private void paintImageWithPositionRevised(Graphics g, List<? extends AbstractFlyingObject> objects) {
        if (objects.isEmpty()) {
            return;
        }

        for (AbstractFlyingObject object : objects) {
            BufferedImage image = object.getImage();
            assert image != null : objects.getClass().getName() + " has no image!";
            g.drawImage(
                    image,
                    object.getLocationX() - image.getWidth() / 2,
                    object.getLocationY() - image.getHeight() / 2,
                    null
            );
        }
    }

    private void paintScoreAndLife(Graphics g) {
        int x = 12;
        int y = 26;
        g.setColor(Color.RED);
        g.setFont(new Font("SansSerif", Font.BOLD, 20));
        g.drawString("MODE: " + difficulty.getDisplayName(), x, y);
        y += 22;
        g.drawString("SCORE: " + this.score, x, y);
        y += 22;
        g.drawString("LIFE: " + this.heroAircraft.getHp() + " / " + this.heroAircraft.getMaxHp(), x, y);

        int barWidth = 150;
        int barHeight = 10;
        int barY = y + 8;
        double hpRatio = Math.max(0.0, Math.min(1.0,
                this.heroAircraft.getHp() / (double) Math.max(1, this.heroAircraft.getMaxHp())));
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(x, barY, barWidth, barHeight);
        g.setColor(hpRatio > 0.5 ? new Color(46, 204, 113)
                : (hpRatio > 0.25 ? new Color(241, 196, 15) : new Color(231, 76, 60)));
        g.fillRect(x, barY, (int) Math.round(barWidth * hpRatio), barHeight);
        g.setColor(Color.WHITE);
        g.drawRect(x, barY, barWidth, barHeight);

        int statusX = Main.WINDOW_WIDTH - 172;
        g.setColor(new Color(0, 0, 0, 130));
        g.fillRoundRect(statusX - 8, 8, 168, 44, 8, 8);
        g.setColor(Color.WHITE);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        g.drawString("AUDIO: " + (MusicManager.isSoundEnabled() ? "ON" : "OFF") + " (M)", statusX, 25);
        g.drawString("PAUSE: P / SPACE", statusX, 44);
    }

    private void paintBossHpBar(Graphics g) {
        BossEnemy boss = findActiveBossEnemy();
        if (boss == null) {
            return;
        }
        int barWidth = 300;
        int barHeight = 14;
        int barX = (Main.WINDOW_WIDTH - barWidth) / 2;
        int barY = 106;
        double hpRatio = Math.max(0.0, Math.min(1.0,
                boss.getHp() / (double) Math.max(1, boss.getMaxHp())));

        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        String label = "BOSS HP: " + boss.getHp() + " / " + boss.getMaxHp();
        int textWidth = g.getFontMetrics().stringWidth(label);
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRoundRect(barX - 4, barY - 18, barWidth + 8, barHeight + 22, 6, 6);
        g.setColor(Color.WHITE);
        g.drawString(label, (Main.WINDOW_WIDTH - textWidth) / 2, barY - 4);

        g.setColor(new Color(60, 20, 20));
        g.fillRect(barX, barY, barWidth, barHeight);
        g.setColor(new Color(220, 38, 38));
        g.fillRect(barX, barY, (int) Math.round(barWidth * hpRatio), barHeight);
        g.setColor(Color.WHITE);
        g.drawRect(barX, barY, barWidth, barHeight);
    }

    private void paintPauseOverlay(Graphics g) {
        g.setColor(new Color(0, 0, 0, 115));
        g.fillRect(0, 0, Main.WINDOW_WIDTH, Main.WINDOW_HEIGHT);

        int boxWidth = 320;
        int boxHeight = 100;
        int boxX = (Main.WINDOW_WIDTH - boxWidth) / 2;
        int boxY = (Main.WINDOW_HEIGHT - boxHeight) / 2;

        g.setColor(new Color(20, 24, 38, 230));
        g.fillRoundRect(boxX, boxY, boxWidth, boxHeight, 16, 16);
        g.setColor(Color.WHITE);
        g.drawRoundRect(boxX, boxY, boxWidth, boxHeight, 16, 16);

        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        String title = "游戏已暂停";
        int titleW = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (Main.WINDOW_WIDTH - titleW) / 2, boxY + 42);

        g.setFont(new Font("SansSerif", Font.PLAIN, 15));
        String hint = "按 P 或空格键继续";
        int hintW = g.getFontMetrics().stringWidth(hint);
        g.drawString(hint, (Main.WINDOW_WIDTH - hintW) / 2, boxY + 74);
    }

    protected AbstractEnemy createCommonEnemyAircraft() {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        double probability = random.nextDouble();
        if (probability < mobProbability) {
            return mobEnemyFactory.createEnemy(mobEnemyHp, mobEnemySpeedY);
        }
        if (probability < eliteProbability) {
            return eliteEnemyFactory.createEnemy(eliteEnemyHp, eliteEnemySpeedY);
        }
        if (probability < elitePlusProbability) {
            return elitePlusEnemyFactory.createEnemy(elitePlusEnemyHp, elitePlusEnemySpeedY);
        }
        return eliteProEnemyFactory.createEnemy(eliteProEnemyHp, eliteProEnemySpeedY);
    }

    protected BossEnemy createBossEnemy() {
        return bossEnemyFactory.createEnemy(bossEnemyHp);
    }

    private BossEnemy findActiveBossEnemy() {
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            if (!enemyAircraft.notValid() && enemyAircraft instanceof BossEnemy) {
                return (BossEnemy) enemyAircraft;
            }
        }
        return null;
    }

    protected boolean hasBossEnemy() {
        return findActiveBossEnemy() != null;
    }

    protected boolean isDifficultyUpdateTime() {
        int updatePeriodTicks = 10_000 / timeInterval;
        return gameTimeTick > 0 && gameTimeTick % updatePeriodTicks == 0;
    }

    protected void speedUpCurrentEnemies(int deltaY) {
        for (AbstractEnemy enemyAircraft : enemyAircrafts) {
            if (!(enemyAircraft instanceof BossEnemy) && !enemyAircraft.notValid()) {
                int speedY = enemyAircraft.getSpeedY();
                if (speedY > 0) {
                    enemyAircraft.setSpeedY(speedY + deltaY);
                }
            }
        }
    }

    protected int getGameTimeSeconds() {
        return gameTimeTick * timeInterval / 1000;
    }

    private List<AbstractSupply> createSupplies(AbstractEnemy enemyAircraft) {
        List<AbstractSupply> droppedSupplies = new ArrayList<>();
        ThreadLocalRandom random = ThreadLocalRandom.current();
        int locationX = enemyAircraft.getLocationX();
        int locationY = enemyAircraft.getLocationY();

        if (enemyAircraft instanceof EliteEnemy) {
            if (random.nextDouble() < ELITE_SUPPLY_PROBABILITY) {
                droppedSupplies.add(supplyFactory.createRandomSupply(ELITE_SUPPLY_POOL, locationX, locationY));
            }
            return droppedSupplies;
        }

        if (enemyAircraft instanceof ElitePlusEnemy) {
            if (random.nextDouble() < ELITE_PLUS_SUPPLY_PROBABILITY) {
                droppedSupplies.add(supplyFactory.createRandomSupply(ELITE_PLUS_SUPPLY_POOL, locationX, locationY));
            }
            return droppedSupplies;
        }

        if (enemyAircraft instanceof EliteProEnemy) {
            if (random.nextDouble() < ELITE_PRO_SUPPLY_PROBABILITY) {
                droppedSupplies.add(supplyFactory.createRandomSupply(SupplyType.values(), locationX, locationY));
            }
            return droppedSupplies;
        }

        if (enemyAircraft instanceof BossEnemy) {
            for (int i = 0; i < 3; i++) {
                int dropX = Math.max(30, Math.min(Main.WINDOW_WIDTH - 30, locationX + (i - 1) * 32));
                droppedSupplies.add(supplyFactory.createRandomSupply(SupplyType.values(), dropX, locationY));
            }
        }
        return droppedSupplies;
    }
}
