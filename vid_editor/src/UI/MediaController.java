/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.paint.Color;

public class MediaController {

    Slider TimeSeek ;
    Slider Volum ;
    Player Player_Video;

    public MediaController(Player PV) {
        this.TimeSeek = new Slider();
        this.Volum = new Slider() ;
        this.Player_Video = PV ;
    }

    public HBox ReturnTools() {

        HBox.setHgrow(TimeSeek, Priority.ALWAYS);
        HBox Container = new HBox();
        Container.setAlignment(Pos.CENTER);
        Container.setPadding(new Insets(5, 10, 5, 10));
        Container.setStyle("-fx-background-color: #fff;");
        Container.setMaxHeight(40);


        Label Volum_Text = new Label("Volum ");
        Volum_Text.setTextFill(Color.BLACK);

        this.Volum.setPrefWidth(70);
        this.Volum.setMinWidth(30);
        this.Volum.setValue(100);


        Container.getChildren().add(TimeSeek);
        Container.getChildren().add(Volum_Text);
        Container.getChildren().add(this.Volum);

        this.TimeSeek.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov)
            {
                if (TimeSeek.isPressed())
                    Player_Video.SeekVideo(TimeSeek.getValue() / 100);
            }
        });

        this.Volum.valueProperty().addListener(new InvalidationListener() {
            public void invalidated(Observable ov)
            {
                if (Volum.isPressed())
                    Player_Video.Volum_Setting(Volum.getValue() / 100);
            }
        });

        Player_Video.VideoTime(this.TimeSeek);

        return Container;
    }


}
