package com.comp2042.game.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class PauseMenu extends StackPane {

    private final Button resumeButton;
    private final Button restartButton;
    private final Button mainMenuButton;

    public PauseMenu() {
        setPrefSize(350, 350);
        setMinSize(350, 350);
        setMaxSize(350, 350);

        Rectangle background = new Rectangle(350, 350);
        background.setArcWidth(30);
        background.setArcHeight(30);
        background.setFill(Color.rgb(0, 0, 0, 0.85));
        background.setStroke(Color.rgb(255, 255, 0));
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

