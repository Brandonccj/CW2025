package com.comp2042.game.controller.audio;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;
import java.util.*;

/**
 * Singleton manager for all game audio including music and sound effects.
 * Handles playlist management, volume control, and audio state.
 */
public class SoundManager {

    private static SoundManager instance;
    private MediaPlayer musicPlayer;
    private Map<String, AudioClip> soundEffects;
    private boolean musicEnabled = true;
    private boolean sfxEnabled = true;
    private double musicVolume = 0.5;
    private double sfxVolume = 0.5;

    private List<String> currentPlaylist;
    private int currentTrackIndex = 0;
    private boolean shuffleMode = false;
    private String currentPlaylistName = "";
    private String pendingMusicTrack = null; // Track what music should play when unmuted

    private SoundManager() {
        soundEffects = new HashMap<>();
        currentPlaylist = new ArrayList<>();
        loadSoundEffects();
    }

    /**
     * Returns the singleton instance of SoundManager.
     *
     * @return the SoundManager instance
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }

    /**
     * Loads all sound effect files into memory.
     */
    private void loadSoundEffects() {
        try {
            loadSound("button_click", "/sounds/button_click.wav");
            loadSound("hard_drop", "/sounds/hard_drop.wav");
            loadSound("clear_row", "/sounds/clear_row.wav");
            loadSound("gameover", "/sounds/gameover.wav");
            loadSound("level_up", "/sounds/level_up.wav");
            loadSound("zen_clear", "/sounds/zen_clear.wav");
            loadSound("hold_piece", "/sounds/hold_piece.wav");
        } catch (Exception e) {
            System.err.println("Error loading sound effects: " + e.getMessage());
        }
    }

    /**
     * Loads a single sound effect from the specified path.
     *
     * @param name the identifier name for the sound
     * @param path the resource path to the sound file
     */
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

    /**
     * Plays a sound effect by name if sound effects are enabled.
     *
     * @param soundName the name of the sound effect to play
     */
    public void playSound(String soundName) {
        if (!sfxEnabled) return;

        AudioClip clip = soundEffects.get(soundName);
        if (clip != null) {
            clip.play();
        }
    }

    /**
     * Plays a single music track on loop.
     * Stops any currently playing music.
     *
     * @param musicPath the resource path to the music file
     */
    public void playMusic(String musicPath) {
        stopMusic();

        // Clear playlist info since this is single track
        currentPlaylist.clear();
        currentPlaylistName = "";
        pendingMusicTrack = musicPath;

        if (!musicEnabled) {
            // Don't play now, but remember what to play when enabled
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

    /**
     * Plays a playlist of music tracks.
     * Supports shuffle mode and automatic track progression.
     *
     * @param playlistName the name of the playlist
     * @param musicPaths list of resource paths to music files
     * @param shuffle whether to shuffle the playlist
     */
    public void playPlaylist(String playlistName, List<String> musicPaths, boolean shuffle) {
        if (musicPaths == null || musicPaths.isEmpty()) {
            System.err.println("Empty playlist provided");
            return;
        }

        stopMusic();

        currentPlaylistName = playlistName;
        currentPlaylist = new ArrayList<>(musicPaths);
        pendingMusicTrack = null; // Clear single track
        shuffleMode = shuffle;
        currentTrackIndex = 0;

        if (shuffleMode) {
            Collections.shuffle(currentPlaylist);
        }

        if (musicEnabled) {
            playTrackFromPlaylist(0);
        }
        // If music disabled, playlist info is stored and will play when enabled
    }

    /**
     * Plays a specific track from the current playlist.
     * Sets up automatic progression to next track when finished.
     *
     * @param index the index of the track to play
     */
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
            currentPlaylist.clear();
        }
    }

    /**
     * Plays the next track in the playlist.
     * Handles looping and reshuffling when reaching the end.
     */
    private void playNextTrackInPlaylist() {
        if (currentPlaylist.isEmpty()) return;

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

    /**
     * Stops all music playback and clears playlist information.
     */
    public void stopMusic() {
        if (musicPlayer != null) {
            musicPlayer.stop();
            musicPlayer.dispose();
            musicPlayer = null;
        }
        // Clear pending music info when explicitly stopping
        pendingMusicTrack = null;
        currentPlaylist.clear();
        currentPlaylistName = "";
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

    /**
     * Enables or disables music playback.
     * When enabled, resumes paused music or starts pending track.
     *
     * @param enabled true to enable music, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;

        if (!enabled) {
            // When disabling, pause current music
            pauseMusic();
        } else {
            // When enabling music
            if (musicPlayer != null && musicPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                // Resume paused music
                resumeMusic();
            } else if (pendingMusicTrack != null) {
                // Play pending single track music
                playMusic(pendingMusicTrack);
            } else if (!currentPlaylist.isEmpty()) {
                // Resume playlist from current position
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