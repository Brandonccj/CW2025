package com.comp2042.game.controller.audio;

import javafx.application.Platform;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

/**
 * Test class for SoundManager.
 * Tests audio state management, singleton pattern, and enable/disable functionality.
 */
class SoundManagerTest {

    private SoundManager soundManager;

    @BeforeAll
    static void initJavaFX() {
        try {
            Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // Already initialized
        }
    }

    @BeforeEach
    void setUp() {
        soundManager = SoundManager.getInstance();
        // Reset to default state
        soundManager.setMusicEnabled(true);
        soundManager.setSfxEnabled(true);
    }

    @Test
    @DisplayName("Should be singleton instance")
    void testSingletonPattern() {
        SoundManager instance1 = SoundManager.getInstance();
        SoundManager instance2 = SoundManager.getInstance();

        assertSame(instance1, instance2);
    }

    @Test
    @DisplayName("Should start with music enabled by default")
    void testMusicEnabledByDefault() {
        SoundManager freshInstance = SoundManager.getInstance();

        assertTrue(freshInstance.isMusicEnabled());
    }

    @Test
    @DisplayName("Should start with SFX enabled by default")
    void testSfxEnabledByDefault() {
        SoundManager freshInstance = SoundManager.getInstance();

        assertTrue(freshInstance.isSfxEnabled());
    }

    @Test
    @DisplayName("Should toggle music enabled state")
    void testSetMusicEnabled() {
        soundManager.setMusicEnabled(false);
        assertFalse(soundManager.isMusicEnabled());

        soundManager.setMusicEnabled(true);
        assertTrue(soundManager.isMusicEnabled());
    }

    @Test
    @DisplayName("Should toggle SFX enabled state")
    void testSetSfxEnabled() {
        soundManager.setSfxEnabled(false);
        assertFalse(soundManager.isSfxEnabled());

        soundManager.setSfxEnabled(true);
        assertTrue(soundManager.isSfxEnabled());
    }

    @Test
    @DisplayName("Should persist music state across multiple toggles")
    void testMusicStatePersistence() {
        soundManager.setMusicEnabled(false);
        soundManager.setMusicEnabled(true);
        soundManager.setMusicEnabled(false);

        assertFalse(soundManager.isMusicEnabled());
    }

    @Test
    @DisplayName("Should persist SFX state across multiple toggles")
    void testSfxStatePersistence() {
        soundManager.setSfxEnabled(false);
        soundManager.setSfxEnabled(true);
        soundManager.setSfxEnabled(false);

        assertFalse(soundManager.isSfxEnabled());
    }

    @Test
    @DisplayName("Should handle playSound when SFX disabled")
    void testPlaySoundWhenDisabled() {
        soundManager.setSfxEnabled(false);

        // Should not throw exception even when disabled
        assertDoesNotThrow(() -> soundManager.playSound("button_click"));
    }

    @Test
    @DisplayName("Should handle playSound when SFX enabled")
    void testPlaySoundWhenEnabled() {
        soundManager.setSfxEnabled(true);

        // Should not throw exception
        assertDoesNotThrow(() -> soundManager.playSound("button_click"));
    }

    @Test
    @DisplayName("Should handle playSound with non-existent sound")
    void testPlayNonExistentSound() {
        // Should not throw exception for non-existent sound
        assertDoesNotThrow(() -> soundManager.playSound("non_existent_sound"));
    }

    @Test
    @DisplayName("Should handle playMusic call")
    void testPlayMusic() {
        // Should not throw exception
        assertDoesNotThrow(() -> soundManager.playMusic("/sounds/menu_music.mp3"));
    }

    @Test
    @DisplayName("Should handle stopMusic call")
    void testStopMusic() {
        soundManager.playMusic("/sounds/menu_music.mp3");

        // Should not throw exception
        assertDoesNotThrow(() -> soundManager.stopMusic());
    }

    @Test
    @DisplayName("Should handle pauseMusic call")
    void testPauseMusic() {
        soundManager.playMusic("/sounds/menu_music.mp3");

        // Should not throw exception
        assertDoesNotThrow(() -> soundManager.pauseMusic());
    }

    @Test
    @DisplayName("Should handle resumeMusic call")
    void testResumeMusic() {
        soundManager.playMusic("/sounds/menu_music.mp3");
        soundManager.pauseMusic();

        // Should not throw exception
        assertDoesNotThrow(() -> soundManager.resumeMusic());
    }

    @Test
    @DisplayName("Should handle playPlaylist with valid playlist")
    void testPlayPlaylist() {
        List<String> playlist = Arrays.asList(
                "/sounds/normal_music_1.mp3",
                "/sounds/normal_music_2.mp3"
        );

        assertDoesNotThrow(() ->
                soundManager.playPlaylist("test_playlist", playlist, false)
        );
    }

    @Test
    @DisplayName("Should handle playPlaylist with shuffle")
    void testPlayPlaylistWithShuffle() {
        List<String> playlist = Arrays.asList(
                "/sounds/zen_music_1.mp3",
                "/sounds/zen_music_2.mp3",
                "/sounds/zen_music_3.mp3"
        );

        assertDoesNotThrow(() ->
                soundManager.playPlaylist("shuffled_playlist", playlist, true)
        );
    }

    @Test
    @DisplayName("Should handle empty playlist gracefully")
    void testEmptyPlaylist() {
        List<String> emptyPlaylist = Arrays.asList();

        // Should not throw exception, just handle gracefully
        assertDoesNotThrow(() ->
                soundManager.playPlaylist("empty", emptyPlaylist, false)
        );
    }

    @Test
    @DisplayName("Should handle null playlist gracefully")
    void testNullPlaylist() {
        // Should not throw exception, just handle gracefully
        assertDoesNotThrow(() ->
                soundManager.playPlaylist("null_playlist", null, false)
        );
    }

    @Test
    @DisplayName("Music and SFX states should be independent")
    void testIndependentAudioStates() {
        soundManager.setMusicEnabled(true);
        soundManager.setSfxEnabled(false);

        assertTrue(soundManager.isMusicEnabled());
        assertFalse(soundManager.isSfxEnabled());

        soundManager.setMusicEnabled(false);
        soundManager.setSfxEnabled(true);

        assertFalse(soundManager.isMusicEnabled());
        assertTrue(soundManager.isSfxEnabled());
    }

    @Test
    @DisplayName("Should handle multiple stop calls")
    void testMultipleStopCalls() {
        soundManager.playMusic("/sounds/menu_music.mp3");
        soundManager.stopMusic();

        // Should not throw on second stop
        assertDoesNotThrow(() -> soundManager.stopMusic());
    }

    @Test
    @DisplayName("Should handle pause without playing")
    void testPauseWithoutPlaying() {
        soundManager.stopMusic(); // Ensure nothing is playing

        // Should not throw exception
        assertDoesNotThrow(() -> soundManager.pauseMusic());
    }

    @Test
    @DisplayName("Should handle resume without pause")
    void testResumeWithoutPause() {
        // Should not throw exception
        assertDoesNotThrow(() -> soundManager.resumeMusic());
    }
}