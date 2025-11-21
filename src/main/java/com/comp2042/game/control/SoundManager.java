package com.comp2042.game.control;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;
import java.util.*;

public class SoundManager {

    private static SoundManager instance;
    private MediaPlayer musicPlayer;
    private Map<String, AudioClip> soundEffects;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;
    private double musicVolume = 0.5;
    private double sfxVolume = 0.7;
    
    private List<String> currentPlaylist;
    private int currentTrackIndex = 0;
    private boolean shuffleMode = false;
    private String currentPlaylistName = "";

    private String lastMusicPath = "";
    private boolean wasPlayingPlaylist = false;

    private SoundManager() {
        soundEffects = new HashMap<>();
        currentPlaylist = new ArrayList<>();
        loadSoundEffects();
    }

    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    private void loadSoundEffects() {
        try {
            loadSound("button_click", "/sounds/button_click.wav");
            loadSound("hard_drop", "/sounds/hard_drop.wav");
            loadSound("clear_row", "/sounds/clear_row.wav");
            loadSound("board_clear", "/sounds/board_clear.wav");
            loadSound("level_up", "/sounds/level_up.wav");
        } catch (Exception e) {
            System.err.println("Error loading sound effects: " + e.getMessage());
        }
    }

    private void loadSound(String name, String path) {
        try {
            URL resource = getClass().getResource(path);
            if (resource != null) {
                AudioClip clip = new AudioClip(resource.toExternalForm());
                clip.setVolume(sfxVolume);
                soundEffects.put(name, clip);
            } else {
                System.err.println("Sound file not found: " + path);
            }
        } catch (Exception e) {
            System.err.println("Error loading sound " + name + ": " + e.getMessage());
        }
    }

    public void playSound(String soundName) {
        if (!sfxEnabled) return;

        AudioClip clip = soundEffects.get(soundName);
        if (clip != null) {
            clip.play();
        }
    }

    public void playMusic(String musicPath) {
        stopMusic();

        lastMusicPath = musicPath;
        wasPlayingPlaylist = false;

        if (!musicEnabled) {
            System.out.println("Music disabled, not playing: " + musicPath);
            return;
        }

        try {
            URL resource = getClass().getResource(musicPath);
            if (resource != null) {
                Media media = new Media(resource.toExternalForm());
                musicPlayer = new MediaPlayer(media);
                musicPlayer.setVolume(musicVolume);
                musicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                musicPlayer.play();
            } else {
                System.err.println("Music file not found: " + musicPath);
            }
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
        }
    }

    public void playPlaylist(String playlistName, List<String> musicPaths, boolean shuffle) {
        if (musicPaths == null || musicPaths.isEmpty()) {
            System.err.println("Empty playlist provided");
            return;
        }

        stopMusic();

        currentPlaylistName = playlistName;
        currentPlaylist = new ArrayList<>(musicPaths);
        shuffleMode = shuffle;
        currentTrackIndex = 0;

        if (shuffleMode) {
            Collections.shuffle(currentPlaylist);
        }

        if (musicEnabled) {
            playTrackFromPlaylist(0);
        }
    }
    private void playTrackFromPlaylist(int index) {
        if (!musicEnabled || currentPlaylist.isEmpty()) return;

        if (index < 0 || index >= currentPlaylist.size()) {
            index = 0;
        }

        currentTrackIndex = index;
        String trackPath = currentPlaylist.get(currentTrackIndex);

        try {
            URL resource = getClass().getResource(trackPath);
            if (resource != null) {
                Media media = new Media(resource.toExternalForm());
                musicPlayer = new MediaPlayer(media);
                musicPlayer.setVolume(musicVolume);


                musicPlayer.setOnEndOfMedia(() -> {
                    playNextTrackInPlaylist();
                });

                musicPlayer.play();
            } else {
                currentPlaylist.clear();
            }
        } catch (Exception e) {
            System.err.println("Error playing music: " + e.getMessage());
            e.printStackTrace();

            currentPlaylist.clear();
        }
    }

    private void playNextTrackInPlaylist() {
        if (currentPlaylist.isEmpty()) return;

        int startIndex = currentTrackIndex;
        int attempts = 0;
        int maxAttempts = currentPlaylist.size();

        while (attempts < maxAttempts) {
            currentTrackIndex++;

            if (currentTrackIndex >= currentPlaylist.size()) {
                currentTrackIndex = 0;

                if (shuffleMode) {
                    Collections.shuffle(currentPlaylist);
                }
            }

            String trackPath = currentPlaylist.get(currentTrackIndex);
            try {
                URL resource = getClass().getResource(trackPath);
                if (resource != null) {
                    Media media = new Media(resource.toExternalForm());
                    musicPlayer = new MediaPlayer(media);
                    musicPlayer.setVolume(musicVolume);

                    musicPlayer.setOnEndOfMedia(() -> {
                        playNextTrackInPlaylist();
                    });

                    musicPlayer.play();
                    return;
                } else {
                    System.err.println("Music file not found: " + trackPath);
                }
            } catch (Exception e) {
                System.err.println("Error playing music " + trackPath + ": " + e.getMessage());
            }

            attempts++;
        }

        System.err.println("Failed to play any tracks in playlist. Clearing playlist.");
        currentPlaylist.clear();
    }

    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
            musicPlayer = null;
        }
    }

    public void pauseMusic() {
        if (musicPlayer != null && musicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            musicPlayer.pause();
        }
    }

    public void resumeMusic() {
        if (musicPlayer != null && musicPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
            musicPlayer.play();
        }
    }

    public void setMusicEnabled(boolean enabled) {
        boolean wasEnabled = this.musicEnabled;
        this.musicEnabled = enabled;

        if (!enabled) {
            pauseMusic();
        } else {
            if (musicPlayer != null && musicPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                resumeMusic();
            } else if (!currentPlaylist.isEmpty()) {
                playTrackFromPlaylist(currentTrackIndex);
            }
        }
    }

    public void setSfxEnabled(boolean enabled) {
        this.sfxEnabled = enabled;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public boolean isSfxEnabled() {
        return sfxEnabled;
    }
}