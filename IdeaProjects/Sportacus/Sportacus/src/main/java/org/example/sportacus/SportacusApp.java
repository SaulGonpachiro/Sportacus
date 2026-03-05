package org.example.sportacus;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SportacusApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                SportacusApp.class.getResource("fxml/login.fxml")
        );
        Scene scene = new Scene(loader.load(), 1400, 900);
        stage.setTitle("Sportacus");
        stage.getIcons().add(new javafx.scene.image.Image(
                SportacusApp.class.getResourceAsStream("images/logo.png")));
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
