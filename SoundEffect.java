/**
 * CS351 Julian Fong Arcade Game Project 3
 *
 * Sound Effect class: This is the class that will play sound effects of various kinds, it works by making a new thread
 * that will not interrupt the music or game calculations
 */

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundEffect {

    //File path variables
    private final static String hitSoundPath = "hitSound2.wav";
    private final static String kickSoundPath = "kickSound1.wav";
    private final static String deathSoundPath = "deathPunch.wav";
    private final static String punchSoundPath = "swingPunch1.wav";
    private final static String pathPreface = "resources/com/JFong/CS351Project3_fightnight/SoundEffects/";

    /**
     * Plays a punch swing sound effect
     */
    public static void playPunchSound(){
        playSound(punchSoundPath);
    }

    /**
     * Plays the death punch sound effect
     */
    public static void playDeathPunchSound(){
        playSound(deathSoundPath);
    }

    /**
     * Plays a kick swing sound effect
     */
    public static void playKickSound(){
        playSound(kickSoundPath);
    }


    /**
     * Plays a hit sound effect
     */
    public static void playHitSound(){
        playSound(hitSoundPath);
    }


    /**
     * https://stackoverflow.com/questions/26305/how-can-i-play-sound-in-java
     * @param url The name of the file to be played
     * This method plays a sound effect given a wav file
     */
    public static synchronized void playSound(final String url) {
        // The wrapper thread is unnecessary, unless it blocks on the
        // Clip finishing; see comments.
        new Thread(() -> {
            try {
                File effectFile = new File(pathPreface + url);
                Clip clip = AudioSystem.getClip();
                AudioInputStream inputStream = AudioSystem.getAudioInputStream(effectFile);
                clip.open(inputStream);
                clip.start();
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
        }).start();
    }

}
