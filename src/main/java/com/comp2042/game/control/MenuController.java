package com.comp2042.game.control;

import javafx.scene.Scene;
import com.comp2042.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class MenuController {

  @FXML
   private StackPane instructionsOverlay;

   @FXML
   private void startNormalMode() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameLayout.fxml"));
            Parent gameRoot = loader.load();
            GuiController guiController = loader.getController();

            Stage stage = (Stage) instructionsOverlay.getScene().getWindow();

            Scene gameScene = new Scene(gameRoot, 620, 600);
            stage.setScene(gameScene);

            new GameController(guiController);

        } catch (Exception e) {
            e.printStackTrace();
       }
   }

   @FXML
   private void startZenMode() {
       System.out.println("Zen Mode - Coming Soon!");
   }

   @FXML
   private void showInstructions() {
        instructionsOverlay.setVisible(true);
   }

   @FXML
   private void hideInstructions() {
        instructionsOverlay.setVisible(false);
   }

   @FXML
   private void exitGame() {
        System.exit(0);
   }
}
