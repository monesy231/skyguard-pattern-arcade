package com.aircraftwar.music;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class MusicThread extends Thread {

    private final String filename;
    private final boolean loop;
    private volatile boolean running = true;
    private Clip clip;

    public MusicThread(String filename) {
        this(filename, false);
    }

    public MusicThread(String filename, boolean loop) {
        this.filename = filename;
        this.loop = loop;
        setName("MusicThread-" + filename);
        setDaemon(true);
    }

    @Override
    public void run() {
        try (AudioInputStream audioInputStream =
                     AudioSystem.getAudioInputStream(new File(filename))) {

            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

            if (loop) {
                clip.loop(Clip.LOOP_CONTINUOUSLY);
            } else {
                clip.start();
            }

            while (running && clip != null && clip.isOpen()) {
                if (!loop && !clip.isRunning()) {
                    break;
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // 如果是正常停止音乐，不打印“播放失败”
                    if (!running) {
                        break;
                    }
                    Thread.currentThread().interrupt();
                    break;
                }
            }

        } catch (Exception e) {
            // 只有真正播放失败时才输出错误
            if (running) {
                System.err.println("音频播放失败：" + filename + "，原因：" + e.getMessage());
            }
        } finally {
            closeClip();
        }
    }

    public void stopMusic() {
        running = false;
        closeClip();
        interrupt();
    }

    private synchronized void closeClip() {
        if (clip != null) {
            if (clip.isRunning()) {
                clip.stop();
            }
            clip.close();
            clip = null;
        }
    }
}