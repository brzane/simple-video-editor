/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.scene.control.ScrollPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import java.util.ArrayList;

public class Scroll_Image {

    private final ArrayList<ImageView> ImagesV ;
    private ArrayList<ImageView> ImageCache ;
    private Integer From , To ;

    public Scroll_Image(ArrayList<Image> ImageList) {
        this.ImagesV = new ArrayList<>();
        this.ImageCache = null ;
        this.SettingImage(ImageList);
        From = To = null ;
    }

    private void SettingImage(ArrayList<Image> ImageList) {

        int Count = 0 ;
        for (Image Sample: ImageList) {
            ImageView ImgView = new ImageView(Sample);
            ImgView.setId(String.valueOf(Count));
            ImgView.setX(5);
            ImgView.setY(0);
            ImgView.setFitWidth(150);
            ImgView.setPreserveRatio(true);
            ImgView.setEffect(null);

            ImgView.addEventHandler(MouseEvent.MOUSE_PRESSED , e -> {
                // ID number * 1000000L => 1 seconde
                int Temp = Integer.parseInt(ImgView.getId());

                if(ImgView.getEffect() == null)
                    ImgView.setEffect(new DropShadow(20 , Color.AQUA));
                else
                    ImgView.setEffect(null);

                if(this.From == null)
                    this.From = Temp ;
                else if(this.To == null) {
                    this.To = Temp ;
                    this.SelectImagesGroup(this.From , this.To , true);
                } else {
                    this.SelectImagesGroup(this.From , this.To , false);
                    if(Temp >= this.From && Temp <= this.To)
                        ImgView.setEffect(new DropShadow(20 , Color.AQUA));
                    this.From = Temp ;
                    this.To = null ;
                }
            });

            Count++;
            this.ImagesV.add(ImgView);
        }
    }

    public ScrollPane ReturnTools() {

        HBox ContainerImage = new HBox(15);

        ContainerImage.getChildren().addAll(this.ImagesV);

        ScrollPane SP = new ScrollPane();

        SP.setContent(ContainerImage) ;

        SP.hbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);
        SP.vbarPolicyProperty().setValue(ScrollPane.ScrollBarPolicy.NEVER);

        SP.setStyle("-fx-background: BLUEVIOLET ; -fx-background-color: transparent;");

        return SP ;
    }

    public Integer GetFrom() {
        return this.From;
    }

    public Integer GetTo() {
        return this.To;
    }

    public void ResetObject() {
        if(this.ImageCache == null)
            return;
        this.ImagesV.clear();
        this.From = this.To = null ;
        ArrayList<Image> Temp = new ArrayList<>();
        for (ImageView T : this.ImageCache)
            Temp.add(T.getImage());
        this.SettingImage(Temp);
        this.ImageCache = null ;
    }

    public void ResetObject(ArrayList<Image> ImageList) {
        this.ImageCache = new ArrayList<>(this.ImagesV);
        this.ImagesV.clear();
        this.From = this.To = null ;
        this.SettingImage(ImageList);
    }

    // True : Select Group | False : Unselect Group
    private void SelectImagesGroup (int Point_A , int Point_B , boolean Process) {

        if(Point_A > Point_B) {
            this.From = Point_B ;
            this.To = Point_A ;
        } else if(Point_A < Point_B) {
            this.From = Point_A ;
            this.To = Point_B ;
        } else
            return;

        if(Process)
            for (int i = this.From; i < this.To; i++)
                this.ImagesV.get(i).setEffect(new DropShadow(20 , Color.AQUA));
        else
            for (int i = this.From ; i <= this.To; i++)
                this.ImagesV.get(i).setEffect(null);
    }
}
