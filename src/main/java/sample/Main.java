package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import lombok.val;

import java.util.Arrays;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        val box = new HBox();
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
    //    primaryStage.setFullScreen(true);
        primaryStage.setScene(new Scene(root));
        Arrays.stream("5 ".split(" ")).mapToInt(Integer::valueOf).max().getAsInt();
     //   primaryStage.set
        primaryStage.show();
        primaryStage.setMaximized(true);
        primaryStage.setTitle("Artyficial City");

    }


    public static void main(String[] args) {
        launch(args);
    }
}
