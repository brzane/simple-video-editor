package UI;

import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Stack;

public class UI {

    private final Stage Main_Stage;
    private Player Player_Video ;
    private MediaController Controller ;
    private Scroll_Image Scroller ;
    private final ArrayList<Button> All_Button ;

    public UI(Stage Main) {
        this.Main_Stage = Main ;
        this.Player_Video = null ;
        this.Controller = null ;
        this.Scroller = null ;
        this.All_Button = new ArrayList<>();
    }

    public Scene GetUI() {

        BorderPane Root = new BorderPane();

        Scene S = new Scene(Root , 800 , 800 , Color.GRAY) ;

        // Open Video
        Button Open_Video = new Button("Open");
        Open_Video.setStyle("-fx-background-color: #80334d;");
        Open_Video.setMinWidth(90);
        Open_Video.setCursor(Cursor.HAND);
        Open_Video.setTextFill(Color.WHITE);
        this.All_Button.add(Open_Video); // 0

        Open_Video.setOnAction(e-> {
            FileChooser Window_Choose = new FileChooser();
            Window_Choose.getExtensionFilters().add(new FileChooser.ExtensionFilter
                    ("MP4" , "*.mp4"));
            Window_Choose.setTitle("Open File");
            File Video_File = Window_Choose.showOpenDialog(this.Main_Stage);
            if(Video_File != null) {
                File_Generate.Clear();
                this.Dis_AbleButton(1 , this.All_Button.size()-2 , true);
                this.Dis_AbleButton(new int[]{1 , 2 , 3 , 7 , 10 , 11} , false);
                File_Generate.AddFile(Video_File);
                this.ResetVedioTools(new Player(Video_File , S) , Root);
            }
        });
        // Save Video
        Button Save_Video = new Button("Save");
        Save_Video.setStyle("-fx-background-color: #80334d;");
        Save_Video.setMinWidth(90);
        Save_Video.setCursor(Cursor.HAND);
        Save_Video.setTextFill(Color.WHITE);
        this.All_Button.add(Save_Video); // 1

        Save_Video.setOnAction(e -> {
            FileChooser Window_Choose = new FileChooser();
            Window_Choose.getExtensionFilters().add(new FileChooser.ExtensionFilter
                    ("MP4" , "*.mp4" ));
            Window_Choose.setTitle("Save File");
            File Video_File = Window_Choose.showSaveDialog(this.Main_Stage);
            if (Video_File != null) {
                try {
                    FileInputStream Out = new FileInputStream(File_Generate.LastFile());
                    FileOutputStream In = new FileOutputStream(Video_File);
                    int Num ;
                    while((Num = Out.read()) != -1)
                        In.write(Num);
                    In.close();
                    Out.close();
                } catch (IOException fileNotFoundException) {
                    fileNotFoundException.printStackTrace();
                }
            }
        });
        // Video 2 Images
        Button Get_Images = new Button("Edit");
        Get_Images.setStyle("-fx-background-color: #000;");
        Get_Images.setMinWidth(90);
        Get_Images.setCursor(Cursor.HAND);
        Get_Images.setTextFill(Color.WHITE);
        this.All_Button.add(Get_Images); // 2

        Get_Images.setOnAction(event -> {
            if(this.Scroller != null)
                return;
            ArrayList<Image> VideoImages = this.Player_Video.Video2Images();
            this.Scroller = new Scroll_Image(VideoImages);
            this.Dis_AbleButton(new int[]{4} , false);
            Root.setBottom(this.Scroller.ReturnTools());
        });
        // Play & Pause Video
        Button Play_Video = new Button("Play");
        Play_Video.setStyle("-fx-background-color: #000;");
        Play_Video.setMinWidth(90);
        Play_Video.setCursor(Cursor.HAND);
        Play_Video.setTextFill(Color.WHITE);
        this.All_Button.add(Play_Video); // 3

        Play_Video.setOnAction(e-> {
            Play_Video.setText(this.Player_Video.Play_Pause() ? "Pause" : "Play");
        });
        // Crop Video
        Button Crop_Video = new Button("Crop");
        Crop_Video.setStyle("-fx-background-color: #000;");
        Crop_Video.setMinWidth(90);
        Crop_Video.setCursor(Cursor.HAND);
        Crop_Video.setTextFill(Color.WHITE);
        this.All_Button.add(Crop_Video); // 4

        Crop_Video.setOnAction(e -> {
            if(this.Scroller.GetFrom() == null || this.Scroller.GetTo() == null)
                return;
            this.Player_Video.CropVideo(this.Scroller.GetFrom() , this.Scroller.GetTo());
            this.Scroller.ResetObject(Player.GetImage(File_Generate.LastFile()));
            this.Dis_AbleButton( new int[]{4}, true);
            this.Dis_AbleButton( new int[]{5 , 6 , 9}, false);
            Root.setBottom(this.Scroller.ReturnTools());
        });
        // Paste Video
        Button Paste_Vedio = new Button("Paste");
        Paste_Vedio.setStyle("-fx-background-color: #000;");
        Paste_Vedio.setMinWidth(90);
        Paste_Vedio.setCursor(Cursor.HAND);
        Paste_Vedio.setTextFill(Color.WHITE);
        this.All_Button.add(Paste_Vedio); // 5

        Paste_Vedio.setOnAction(e -> {
            if(!this.Player_Video.Paste2Vedio(this.Scroller.GetFrom()))
                return;
            this.Dis_AbleButton(4 , 6 , true);
            this.Dis_AbleButton(new int[]{12} , false);
            this.ResetVedioTools(new Player(File_Generate.LastFile() , S) , Root);
        });
        // Delete Video
        Button Delete_Crop = new Button("Delete");
        Delete_Crop.setStyle("-fx-background-color: #000;");
        Delete_Crop.setMinWidth(90);
        Delete_Crop.setCursor(Cursor.HAND);
        Delete_Crop.setTextFill(Color.WHITE);
        this.All_Button.add(Delete_Crop); // 6

        Delete_Crop.setOnAction(e -> {
            this.Dis_AbleButton(4 , 6 , true);
            this.Dis_AbleButton(new int[]{12} , false);
            this.ResetVedioTools(new Player(File_Generate.LastFile() , S) , Root);
        });
        // Speed Video
        Button Speed_Video = new Button("Speed");
        Speed_Video.setStyle("-fx-background-color: #000;");
        Speed_Video.setMinWidth(90);
        Speed_Video.setCursor(Cursor.HAND);
        Speed_Video.setTextFill(Color.WHITE);
        this.All_Button.add(Speed_Video); // 7

        Speed_Video.setOnAction(e -> {
            if(Speed_Video.getText().equals("Speed")) {
                Speed_Video.setText("1X");
                return;
            }
            String Temp = Speed_Video.getText().split("X")[0];
            float Result = this.NextValue(Temp) ;
            Temp = (Result >= 1)? Integer.toString((int)Result) : Float.toString(Result);
            Speed_Video.setText(Temp+"X");
            this.Dis_AbleButton(new int[]{8} , Speed_Video.getText().equals("1X"));
        });
        // Speed Video
        Button ProcessSpeed = new Button("Apply Speed");
        ProcessSpeed.setStyle("-fx-background-color: #000;");
        ProcessSpeed.setMinWidth(90);
        ProcessSpeed.setCursor(Cursor.HAND);
        ProcessSpeed.setTextFill(Color.WHITE);
        this.All_Button.add(ProcessSpeed); // 8

        ProcessSpeed.setOnAction(e -> {
            String Temp = Speed_Video.getText().split("X")[0];
            float Result = Float.parseFloat(Temp) ;
            this.All_Button.get(7).setText("Speed");
            this.Dis_AbleButton(new int[]{8} , true);
            this.Player_Video.ChangeSpeed(Result);
            this.ResetVedioTools(new Player(File_Generate.LastFile() , S) , Root);
            this.Dis_AbleButton(new int[]{12} , false);
        });
        // Cancel Edit
        Button Cancel_Edit = new Button("Cancel");
        Cancel_Edit.setStyle("-fx-background-color: #000;");
        Cancel_Edit.setMinWidth(90);
        Cancel_Edit.setCursor(Cursor.HAND);
        Cancel_Edit.setTextFill(Color.WHITE);
        this.All_Button.add(Cancel_Edit); // 9

        Cancel_Edit.setOnAction(e -> {
            this.Dis_AbleButton(new int[]{5 , 6 , 9} , true);
            this.Dis_AbleButton(new int[]{4} , false);
            this.Scroller.ResetObject();
            File_Generate.FileRemove(true);
            this.Player_Video.ResetCache();
            Root.setBottom(this.Scroller.ReturnTools());
        });
        // WaterMarkText Edit
        Button WaterMarkText = new Button("WaterMark");
        WaterMarkText.setStyle("-fx-background-color: #000;");
        WaterMarkText.setMinWidth(90);
        WaterMarkText.setCursor(Cursor.HAND);
        WaterMarkText.setTextFill(Color.WHITE);
        this.All_Button.add(WaterMarkText); // 10

        WaterMarkText.setOnAction(e -> {
            this.Player_Video.TextWaterMark();
            this.ResetVedioTools(new Player(File_Generate.LastFile() , S) , Root);
            this.Dis_AbleButton(new int[]{12} , false);
        });
        // WaterMark Edit
        Button WaterMark_Image = new Button("WaterMarkImage");
        WaterMark_Image.setStyle("-fx-background-color: #000;");
        WaterMark_Image.setMinWidth(90);
        WaterMark_Image.setCursor(Cursor.HAND);
        WaterMark_Image.setTextFill(Color.WHITE);
        this.All_Button.add(WaterMark_Image); // 11

        WaterMark_Image.setOnAction(e -> {
            this.Player_Video.ImageWaterMark();
            this.ResetVedioTools(new Player(File_Generate.LastFile() , S) , Root);
            this.Dis_AbleButton(new int[]{12} , false);
        });
        // Undo
        Button Undo = new Button("Undo");
        Undo.setStyle("-fx-background-color: #000;");
        Undo.setMinWidth(90);
        Undo.setCursor(Cursor.HAND);
        Undo.setTextFill(Color.WHITE);
        this.All_Button.add(Undo); // 12

        Undo.setOnAction(e -> {
            File_Generate.FileRemove(true);
            File Befor_Last = File_Generate.LastFile();
            this.ResetVedioTools(new Player(Befor_Last , S) , Root);
            if(File_Generate.Files_Temp.size() <= 1)
                this.Dis_AbleButton(new int[]{12} , true);
        });
        // Merge
        Button Merge = new Button("Merge");
        Merge.setStyle("-fx-background-color: #80334d;");
        Merge.setMinWidth(90);
        Merge.setCursor(Cursor.HAND);
        Merge.setTextFill(Color.WHITE);
        this.All_Button.add(Merge); // 13

        Merge.setOnAction(e -> {
            FileChooser Window_Choose = new FileChooser();
            Window_Choose.getExtensionFilters().add(new FileChooser.ExtensionFilter
                    ("MP4" , "*.mp4"));
            Window_Choose.setTitle("Open File");
            File One_Video = Window_Choose.showOpenDialog(this.Main_Stage);
            File Two_Video = Window_Choose.showOpenDialog(this.Main_Stage);
            if((One_Video != null && One_Video.exists()) && (Two_Video != null && Two_Video.exists())) {
                Player.Merge(One_Video , Two_Video);
                this.ResetVedioTools(new Player(File_Generate.LastFile() , S) , Root);
                this.Dis_AbleButton(1 , this.All_Button.size()-2 , true);
                this.Dis_AbleButton(new int[]{1 , 2 , 3 , 7 , 10 , 11} , false);
            }
        });
        // Container All Buttons Control
        this.Dis_AbleButton(1 , this.All_Button.size()-2 , true);
        VBox All_Button_Menu = new VBox(10);
        All_Button_Menu.getChildren().addAll(this.All_Button) ;
        All_Button_Menu.setPadding(new Insets(5));
        All_Button_Menu.setStyle("-fx-background-color: #999");
        All_Button_Menu.setPrefWidth(100);
        Root.setLeft(All_Button_Menu);
        
        // Package in Group
        Root.setStyle("-fx-background: transparent;");

        return S;
    }

    private void ResetVedioTools(Player P , BorderPane Root) {
        Root.setCenter(null);
        Root.setBottom(null);

        VBox Vedio_Controller = new VBox();
        if(this.Player_Video != null) {
            this.Player_Video.Clean();
            if(this.All_Button.get(3).getText().equals("Pause"))
                this.All_Button.get(3).setText("Play");
            if(!this.All_Button.get(7).getText().equals("Speed"))
                this.All_Button.get(7).setText("Speed");
        }
        this.Player_Video = P ;
        this.Controller = new MediaController(P) ;

        Vedio_Controller.getChildren().add(this.Player_Video.ReturnScreen());
        Vedio_Controller.getChildren().add(this.Controller.ReturnTools());
        Root.setCenter(Vedio_Controller);

        this.Scroller = null ;
//        ArrayList<Image> VideoImages = this.Player_Video.Video2Images();
//        this.Scroller = new Scroll_Image(VideoImages);
//        Root.setBottom(this.Scroller.ReturnTools());
    }

    private float NextValue(String S) {
        float Result = 0 ;
        switch (S) {
            case "1" :
                Result = 2 ;
                break;
            case "2" :
                Result = 4 ;
                break;
            case "4" :
                Result = 0.25f ;
                break;
            case "0.25" :
                Result = 0.5f ;
                break;
            case "0.5" :
                Result = 1f ;
                break;
        }
        return Result ;
    }

    private void Dis_AbleButton(int[] Buttoms , boolean Disable) {
        for (int Temp : Buttoms) {
            this.All_Button.get(Temp).setStyle("-fx-background-color: #" + ((Disable)? "000" : "80334d") + ";");
            this.All_Button.get(Temp).setDisable(Disable);
        }
    }

    private void Dis_AbleButton(Integer From , Integer To , boolean Disable) {

        if(From == null || To == null) {
            int Temp = (From != null)? From : To ;
            this.All_Button.get(Temp).setDisable(Disable);
            this.All_Button.get(Temp).setStyle("-fx-background-color: #" + ((Disable)? "000" : "80334d") + ";");
            return;
        }

        for (int i = From ; i <= To ; i++)
            if(Disable) {
                this.All_Button.get(i).setStyle("-fx-background-color: #000;");
                this.All_Button.get(i).setDisable(true);
            } else {
                this.All_Button.get(i).setStyle("-fx-background-color: #80334d;");
                this.All_Button.get(i).setDisable(false);
            }
    }

    public static class File_Generate {

        private static final Stack<File> Files_Temp = new Stack<>();

        private static int ID_Register = 0 ;

        private static final File Directory = new File("src/test");

        public static File CreateFile(String Suffix) {
            if (!File_Generate.Directory.exists() || !File_Generate.Directory.isDirectory())
                File_Generate.Directory.mkdirs();
            File Temp = new File(File_Generate.Directory.getPath() + "/" + File_Generate.ID_Register + "." + Suffix);
            File_Generate.Files_Temp.push(Temp);
            File_Generate.ID_Register++ ;
            return Temp;
        }

        public static File CreateFile(String Name , String Suffix) {
            if (!File_Generate.Directory.exists() || !File_Generate.Directory.isDirectory())
                File_Generate.Directory.mkdirs();
            File Temp = new File(File_Generate.Directory.getPath() + "/" + Name + "." + Suffix);
            return Temp;
        }

        //Change Need
        public static void AddFile(File F) {
            File_Generate.Files_Temp.push(F);

        }

        public static File FileRemove(boolean Confirme) {
            if(Confirme) {
                File_Generate.Files_Temp.pop().delete();
                return null ;
            }
            File Output = File_Generate.Files_Temp.pop();
            return Output;
        }

        public static File LastFile() {
            return File_Generate.Files_Temp.peek();

        }

        public static void Clear() {
            if (File_Generate.Directory.exists() && File_Generate.Directory.isDirectory()) {
                for (File Temp: Objects.requireNonNull(File_Generate.Directory.listFiles()))
                    Temp.delete();
            }
            File_Generate.Files_Temp.clear();
            File_Generate.ID_Register = 0 ;
        }
    }
}
