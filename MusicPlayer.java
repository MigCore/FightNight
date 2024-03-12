/**
 * CS351 Julian Fong Arcade Game Project 3
 *
 * Music player class: This class will handle the background music that will play for the menu and game
 */

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class MusicPlayer {

    private MediaPlayer musicPlayer;

    public MusicPlayer(Media song){
        musicPlayer = new MediaPlayer(song);
        makeInfinite(musicPlayer);
    }

    /**
     * @param nextSong The song to be played
     * @return returns the media player to be added to whatever javafx scene is up on screen
     */
    public MediaPlayer changeSong(Media nextSong){
        musicPlayer.pause();
        musicPlayer = new MediaPlayer(nextSong);
        makeInfinite(musicPlayer);
        return musicPlayer;
    }

    public void startSong(){
        musicPlayer.play();
    }

    private void makeInfinite(MediaPlayer player){
        player.setOnEndOfMedia(() -> player.seek(player.getStartTime()));
    }

}
