package com.comp2042.game.view;

import com.comp2042.game.event.GameMode;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

/**
 * Pause menu overlay panel with game control options.
 * Provides buttons for resume, restart, and returning to main menu.
 */

public class PauseMenu extends StackPane {

    private final Button resumeButton;
    private final Button restartButton;
    private final Button mainMenuButton;
    private final Rectangle background;

    public PauseMenu() {
        setPrefSize(350, 350);
        setMinSize(350, 350);
        setMaxSize(350, 350);

        background = new Rectangle(350, 350);
        background.setArcWidth(30);
        background.setArcHeight(30);
        background.setFill(Color.rgb(0, 0, 0, 0.85));
        background.setStroke(Color.rgb(156, 39, 176));
        background.setStrokeWidth(3);

        VBox content = new VBox(25);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(40));

        Label pauseLabel = new Label("PAUSED");
        pauseLabel.getStyleClass().add("pauseTitle");

        resumeButton = new Button("RESUME");
        resumeButton.getStyleClass().add("pauseButton");
        resumeButton.setPrefWidth(220);

        restartButton = new Button("RESTART");
        restartButton.getStyleClass().add("pauseButton");
        restartButton.setPrefWidth(220);

        mainMenuButton = new Button("MAIN MENU");
        mainMenuButton.getStyleClass().add("pauseButton");
        mainMenuButton.setPrefWidth(220);

        content.getChildren().addAll(
                pauseLabel,
                resumeButton,
                restartButton,
                mainMenuButton
        );

        getChildren().addAll(background, content);
        setAlignment(Pos.CENTER);
    }

    public void setGameMode(GameMode mode) {
        if (mode == GameMode.ZEN) {
            background.setStroke(Color.rgb(77, 208, 225)); // Cyan (#4DD0E1)
        } else {
            background.setStroke(Color.rgb(156, 39, 176)); // Purple (#9C27B0)
        }
    }

    public Button getResumeButton() {
        return resumeButton;
    }

    public Button getRestartButton() {
        return restartButton;
    }

    public Button getMainMenuButton() {
        return mainMenuButton;
    }
}