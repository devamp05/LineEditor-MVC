package com.example.asn4;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class EditorApp extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        MainUI mainUI = new MainUI();
        Scene scene = new Scene(mainUI);
        stage.setScene(scene);
        stage.setTitle("CMPT 381 A4");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
