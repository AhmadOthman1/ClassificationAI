/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classificationai;

import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author HI-TECH
 */
public class ClassificationAi extends Application{

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("FXMLClassificationMain.fxml"));
        //stage.initStyle(StageStyle.UNDECORATED);
        Scene scene = new Scene(root);
        stage.setMinWidth(1214);
        stage.setMinHeight(678);
        stage.setHeight(678);
        stage.setWidth(1214);
        stage.setMaxWidth(1214);
        stage.setMaxHeight(678);
        stage.setTitle("Classification Problem");
        stage.setScene(scene);
        stage.show();
       //hello
    }
    
}
