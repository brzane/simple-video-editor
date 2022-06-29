/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage PrimaryStage) {
        UI UserInterFace = new UI(PrimaryStage);
        PrimaryStage.setScene(UserInterFace.GetUI());
        PrimaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
