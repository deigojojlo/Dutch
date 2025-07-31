package main.java.util;


import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Random;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundUtil {

    public static final String pathToSounds = Path.of("src", "main", "resources", "sounds").toString();
    private static final Random random = new Random();

    private static Clip ambientClip;
    private static final String[] CARD_FLIP_SOUNDS = {
        "FLIP_CARD1.wav",
        "FLIP_CARD2.wav",
        "FLIP_CARD3.wav"
    };

    /**
     * Plays a random card flip sound from the predefined list.
     */
    public static void playRandomCardFlip() {
        int index = random.nextInt(CARD_FLIP_SOUNDS.length);
        playSound(CARD_FLIP_SOUNDS[index]);
    }

    /**
     * Plays a short sound effect once.
     */
    public static void playSound(String soundFileName) {
        File soundFile = Path.of(pathToSounds, soundFileName).toFile();

        if (!soundFile.exists()) {
            System.err.println("Sound file not found: " + soundFile.getAbsolutePath());
            return;
        }

        try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile)) {
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error playing sound: " + soundFile.getAbsolutePath());
        }
    }

    /**
     * Plays a looping ambient music track from the resources.
     */
    public static void playAmbientLoop(String filename) {
        File soundFile = Path.of(pathToSounds, filename).toFile();

        if (!soundFile.exists()) {
            System.err.println("Sound file not found: " + soundFile.getAbsolutePath());
            return;
        }

        try (AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile)) {
            ambientClip = AudioSystem.getClip();
            ambientClip.open(audioIn);
            ambientClip.loop(Clip.LOOP_CONTINUOUSLY);
            ambientClip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Failed to play ambient music: " + soundFile.getAbsolutePath());
        }
    }

    /**
     * Stops any currently playing ambient music loop.
     */
    public static void stopAmbientLoop() {
        if (ambientClip != null && ambientClip.isRunning()) {
            ambientClip.stop();
            ambientClip.flush();
            ambientClip.close();
        }
    }
}
