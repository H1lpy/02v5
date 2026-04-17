package ru.demo.sessia5;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class Students extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("student-view.fxml"));
        Scene scene = new Scene(loader.load(), 800, 400);
        stage.setTitle("Студенты");
        stage.setScene(scene);
        stage.show();
    }
}
