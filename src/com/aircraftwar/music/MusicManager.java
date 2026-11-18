package com.aircraftwar.music;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MusicManager {

    public static final String BGM = "bgm";
    public static final String BOSS_BGM = "boss_bgm";

    public static final String BGM_PATH = "src/videos/bgm.wav";
    public static final String BOSS_BGM_PATH = "src/videos/bgm_boss.wav";
    public static final String BOMB_EXPLOSION_PATH = "src/videos/bomb_explosion.wav";
    public static final String BULLET_HIT_PATH = "src/videos/bullet_hit.wav";
    public static final String GAME_OVER_PATH = "src/videos/game_over.wav";
    public static final String GET_SUPPLY_PATH = "src/videos/get_supply.wav";

    private static volatile boolean soundEnabled = true;

    private final Map<String, MusicThread> loopThreads = new ConcurrentHashMap<>();

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
    }

    public void loop(String key, String path) {
        stop(key);
        if (!soundEnabled) {
            return;
        }
        MusicThread thread = new MusicThread(path, true);
        loopThreads.put(key, thread);
        thread.start();
    }

    public void playOnce(String path) {
        if (!soundEnabled) {
            return;
        }
        new MusicThread(path, false).start();
    }

    public void toggleSound(boolean hasBoss) {
        boolean nextState = !soundEnabled;
        setSoundEnabled(nextState);
        if (!nextState) {
            stopAllLoopMusic();
        } else {
            loop(BGM, BGM_PATH);
            if (hasBoss) {
                loop(BOSS_BGM, BOSS_BGM_PATH);
            }
        }
    }

    public void stop(String key) {
        MusicThread thread = loopThreads.remove(key);
        if (thread != null) {
            thread.stopMusic();
        }
    }

    public void stopAllLoopMusic() {
        for (String key : loopThreads.keySet()) {
            stop(key);
        }
    }
}
