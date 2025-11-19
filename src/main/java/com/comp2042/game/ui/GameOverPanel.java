package com.comp2042.game.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;

public class GameOverPanel extends StackPane {

    private final Label timeLabel;
    private final Label scoreLabel;
    private final Label highScoreLabel;
    private final Label linesLabel;

    public GameOverPanel() {
        // Set size explicitly
        setPrefSize(350, 400);
        setMinSize(350, 400);
        setMaxSize(350, 400);

        Rectangle background = new Rectangle(350, 400);
        background.setArcWidth(30);
        background.setArcHeight(30);
        background.setFill(Color.rgb(0, 0, 0, 0.85));
        background.setStroke(Color.rgb(38, 168, 183));
        background.setStrokeWidth(3);

        VBox content = new VBox(20);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(30));

        Label gameOverLabel = new Label("GAME OVER");
        gameOverLabel.getStyleClass().add("gameOverTitle");

        timeLabel = new Label("Time: 0:00");
        timeLabel.getStyleClass().add("gameOverStat");

        scoreLabel = new Label("Score: 0");
        scoreLabel.getStyleClass().add("gameOverStat");

        highScoreLabel = new Label("High Score: 0");
        highScoreLabel.getStyleClass().add("gameOverStat");

        linesLabel = new Label("Lines Cleared: 0");
        linesLabel.getStyleClass().add("gameOverStat");

        Label instructionLabel = new Label("Press N for New Game");
        instructionLabel.getStyleClass().add("gameOverInstruction");

        content.getChildren().addAll(
                gameOverLabel,
                timeLabel,
                scoreLabel,
                highScoreLabel,
                linesLabel,
                instructionLabel
        );

        getChildren().addAll(background, content);

        setAlignment(Pos.CENTER);
        setVisible(true);

    }

    public void updateStats(String time, int score, int highScore, int lines) {
        timeLabel.setText("Time: " + time);
        scoreLabel.setText("Score: " + score);
        highScoreLabel.setText("High Score: " + highScore);
        linesLabel.setText("Lines Cleared: " + lines);
    }
}