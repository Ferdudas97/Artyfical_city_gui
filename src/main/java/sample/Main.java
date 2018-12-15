package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.RadioButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.val;

import java.awt.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        val box = new HBox();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
    //    primaryStage.setFullScreen(true);
        primaryStage.setScene(new Scene(root));

     //   primaryStage.set
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
