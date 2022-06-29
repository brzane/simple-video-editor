/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.javacv.*;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Player {

    private final MediaPlayer Controller ;
    private final MediaView Video_MP4 ;
    private FFmpegFrameGrabber Grabber ;
    private File CropRecorder ;
    private final Pane ViedoWall ;

    public Player(File VideoFile , Scene S ) {
        Media media = new Media(VideoFile.toURI().toString());
        this.Controller = new MediaPlayer(media);
        this.Video_MP4 = new MediaView(this.Controller);
        this.Grabber = new FFmpegFrameGrabber(VideoFile.toString());
        this.CropRecorder = null ;
        this.ViedoWall = new Pane();

        this.Video_MP4.setPreserveRatio(false);
        this.Video_MP4.setFitWidth(S.getWidth()-100);
        this.Video_MP4.setFitHeight(S.getHeight()/2);
        S.widthProperty().addListener((observable, oldValue, newValue) -> Video_MP4.setFitWidth(newValue.intValue() - 100));
        S.heightProperty().addListener((observable, oldValue, newValue) -> Video_MP4.setFitHeight(newValue.doubleValue()/2));
    }

    public Pane ReturnScreen() {
        this.ViedoWall.getChildren().add(this.Video_MP4);
        return this.ViedoWall;
    }

    // True : Play | False : Pause
    public boolean Play_Pause() {

        MediaPlayer.Status Status = this.Controller.getStatus();

        if (Status == MediaPlayer.Status.PLAYING) {

            if (this.Controller.getCurrentTime().greaterThanOrEqualTo(this.Controller.getTotalDuration())) {
                this.Controller.seek(this.Controller.getStartTime()) ;
                this.Controller.play() ;
                return true ;
            }
            else {
                this.Controller.pause();
                return false;
            }
        }

        this.Controller.play();
        return true ;
    }

    public void SeekVideo(double Value) {
        this.Controller.seek(this.Controller.getMedia().
                getDuration().multiply(Value));
    }

    public void Volum_Setting(double Value) {
        this.Controller.setVolume(Value);
    }

    public void VideoTime(Slider S) {
        this.Controller.currentTimeProperty().addListener(ov -> Platform.runLater(() -> {
            S.setValue(Controller.getCurrentTime().toMillis()/
                    Controller.getTotalDuration()
                            .toMillis()* 100);
        }));
    }

    public ArrayList<Image> Video2Images() {

        ArrayList<Image> Images = new ArrayList<>();
        try {
            this.Grabber.start();
        } catch (FFmpegFrameGrabber.Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        int FramesRate = this.GetFrameRate() ;
        for (int i = 0 ; i <= this.RealFream() ; i++) {
            try {
                Frame VideoFrame = this.Grabber.grabImage();
                if(VideoFrame == null)
                    continue;
                Java2DFrameConverter FrameConverter = new Java2DFrameConverter();
                if(i % FramesRate == 0) {
                    Image Sample = SwingFXUtils.toFXImage(FrameConverter.getBufferedImage(VideoFrame), null);
                    Images.add(Sample);
//                    System.out.println(this.Grabber.getTimestamp());
                }
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            this.Grabber.stop();
        } catch (FFmpegFrameGrabber.Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Images ;
    }

    private int GetFrameRate() {

        int Temp ;

        if(this.Grabber.getFrameRate() == 0.0) {
            try {
                this.Grabber.start();
            } catch (FFmpegFrameGrabber.Exception e) {
                e.printStackTrace();
            }
            Temp = (int)Math.floor(this.Grabber.getFrameRate()) ;
            try {
                this.Grabber.close();
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        } else {
            Temp = (int)Math.floor(this.Grabber.getFrameRate()) ;
        }
        return Temp ;
    }

    private int RealFream() {
        int FR = this.GetFrameRate() ;
        int Temp = Math.floorDiv(this.Grabber.getLengthInFrames() , FR);
        return Temp * FR ;
    }

    public void CropVideo(int StartTime, int EndTime) {
        try {

            long Fix_StartTime = StartTime * 1000000L ;
            long Fix_EndTime = (EndTime + 1) * 1000000L ;

            this.Grabber.start();

            int width = this.Grabber.getImageWidth();
            int height = this.Grabber.getImageHeight();
            int channels = this.Grabber.getAudioChannels();

            File OutPut = UI.File_Generate.CreateFile("mp4");
            this.CropRecorder = UI.File_Generate.CreateFile("Temp_Vedio" , "mp4");
            FFmpegFrameRecorder FrameRecorder = this.AdjustRecord(OutPut , this.Grabber , width , height , channels);
            FFmpegFrameRecorder CropRecorder = this.AdjustRecord(this.CropRecorder , this.Grabber , width , height , channels) ;

            FrameRecorder.start();
            CropRecorder.start();

            Frame frame = null ;

            while ((frame = this.Grabber.grab()) != null) {
                long timestamp = this.Grabber.getTimestamp();
                if(timestamp >= Fix_StartTime && timestamp <= Fix_EndTime) {
                    CropRecorder.record(frame);
                    continue;
                }
                FrameRecorder.record(frame);
            }

            CropRecorder.stop();
            FrameRecorder.stop();
            this.Grabber.stop();
        } catch (Exception ex) {
            FFmpegLogCallback.set();
        }
    }

    public boolean Paste2Vedio(Integer StartTime) {
        if(this.CropRecorder == null)
            return false;
        try {

            File Input = UI.File_Generate.FileRemove(false);
            FFmpegFrameGrabber Choppy_Vedio = new FFmpegFrameGrabber(Input.toString());
            FFmpegFrameGrabber Crop_Vedio = new FFmpegFrameGrabber(this.CropRecorder.toString());
            File OutPut = UI.File_Generate.CreateFile("mp4");
            Long Fix_Time = (StartTime != null) ? StartTime * 1000000L : null;
            boolean IsPaste = false ;

            Choppy_Vedio.start();
            Crop_Vedio.start();

            int width = Choppy_Vedio.getImageWidth();
            int height = Choppy_Vedio.getImageHeight();
            int channels = Choppy_Vedio.getAudioChannels();
            FFmpegFrameRecorder FrameRecorder = this.AdjustRecord(OutPut , Choppy_Vedio , width , height , channels);


            FrameRecorder.start();
            Frame Frame_Choppy , Frame_Crop;

            while ((Frame_Choppy = Choppy_Vedio.grab()) != null) {
                if(Fix_Time != null)
                    if(!IsPaste && Choppy_Vedio.getTimestamp() >= Fix_Time) {
                        while ((Frame_Crop = Crop_Vedio.grab()) != null)
                            FrameRecorder.record(Frame_Crop);
                        IsPaste = true ;
                    }
                FrameRecorder.record(Frame_Choppy);
            }

            if(!IsPaste) {
                while ((Frame_Crop = Crop_Vedio.grab()) != null)
                    FrameRecorder.record(Frame_Crop);
//                IsPaste = true ;
            }

            Crop_Vedio.stop();
            FrameRecorder.stop();
            Choppy_Vedio.stop();
            Input.delete();
            this.CropRecorder.delete();
            this.CropRecorder = null ;
            return true;
        } catch (FFmpegFrameGrabber.Exception | FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
            return false ;
        }
    }

    public static ArrayList<Image> GetImage(File Input) {
        ArrayList<Image> Images = new ArrayList<>();
        FFmpegFrameGrabber Grabber = new FFmpegFrameGrabber(Input.toString());
        try {
            Grabber.start();
        } catch (FFmpegFrameGrabber.Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        int FramesRate = (int)Math.floor(Grabber.getFrameRate()) ;
        int RealFream = Math.floorDiv(Grabber.getLengthInFrames() , FramesRate) * FramesRate ;
        for (int i = 0 ; i <= RealFream ; i++) {
            try {
                Frame VideoFrame = Grabber.grabImage();
                if(VideoFrame == null)
                    continue;
                Java2DFrameConverter FrameConverter = new Java2DFrameConverter();
                if(i % FramesRate == 0) {
                    Image Sample = SwingFXUtils.toFXImage(FrameConverter.getBufferedImage(VideoFrame), null);
                    Images.add(Sample);
//                    System.out.println(this.Grabber.getTimestamp());
                }
            } catch (FrameGrabber.Exception ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        try {
            Grabber.stop();
        } catch (FFmpegFrameGrabber.Exception ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Images ;
    }

    public void ChangeSpeed(float Speed) {
        try {

            this.Grabber.start();

            int width = this.Grabber.getImageWidth();
            int height = this.Grabber.getImageHeight();
            int channels = this.Grabber.getAudioChannels();
            File OutPut = UI.File_Generate.CreateFile("mp4") ;
            FFmpegFrameRecorder FrameRecorder = this.AdjustRecord(OutPut , this.Grabber , width , height , channels);

            Frame Frame_Pass;
            FrameRecorder.start();

            while((Frame_Pass = this.Grabber.grab()) != null) {
                if(Math.floor(Speed) > 1) {
                    for (int i = 0; i < Math.floor(Speed) - 1 ; i++)
                        this.Grabber.grab();
                    FrameRecorder.record(Frame_Pass);
                } else if(Speed < 1)
                    for (float i = 1 ; i*Speed <= 1; i++)
                        FrameRecorder.record(Frame_Pass);
            }

            FrameRecorder.stop();
            this.Grabber.stop();

        } catch (FFmpegFrameGrabber.Exception | FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    public void Clean() {
        this.Controller.pause();
        this.ViedoWall.getChildren().clear();
        this.Grabber = null ;
        if(this.CropRecorder != null && this.CropRecorder.exists()) {
            this.CropRecorder.delete();
            this.CropRecorder = null ;
        }

    }

    public void ResetCache() {
        if(this.CropRecorder == null)
            return;
        this.CropRecorder.delete();
        this.CropRecorder = null ;
    }

    public void TextWaterMark() {
        try {
            String Text = "BRZANE" ;
            int FontSize = 40;

            this.Grabber.start();

            int width = this.Grabber.getImageWidth();
            int height = this.Grabber.getImageHeight();
            int channels = this.Grabber.getAudioChannels();
            File OutPut = UI.File_Generate.CreateFile("mp4");
            FFmpegFrameRecorder FrameRecorder = this.AdjustRecord(OutPut, this.Grabber, width, height, channels);

            String TextWatermark = "drawtext=text='"+Text + "':fontfile=" + "C\\\\:/Multimedia/font.ttf" + ":fontcolor=white:fontsize=" + FontSize + ":box=1:boxcolor=black@0.5:boxborderw=5:x=(w-text_w)/2:y=(h-text_h)/2";
            FFmpegFrameFilter FrameFilter = new FFmpegFrameFilter(TextWatermark, width, height);

            FrameFilter.start();
            FrameRecorder.start();

            Frame Frame_Pass ;

            while ((Frame_Pass = this.Grabber.grab()) != null) {
                FrameFilter.push(Frame_Pass);
                Frame FilteredFrame = FrameFilter.pull();
                FrameRecorder.record(FilteredFrame);
            }

            FrameRecorder.stop();
            FrameFilter.stop();
            this.Grabber.stop();

            } catch (FFmpegFrameGrabber.Exception | FFmpegFrameFilter.Exception | FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
    
   

   public void ImageWaterMark() {
        try {
            this.Grabber.start();

            int width = this.Grabber.getImageWidth();
            int height = this.Grabber.getImageHeight();
            int channels = this.Grabber.getAudioChannels();
            File OutPut = UI.File_Generate.CreateFile("mp4");
            FFmpegFrameRecorder FrameRecorder = this.AdjustRecord(OutPut, this.Grabber, width, height, channels);

            String WatermarkName = "1.png";
            String WaterMarkImage = "movie=C\\\\:/Multimedia/"+WatermarkName+"[watermark];[in][watermark]overlay=W-w+"+0 +":"+ 0 +":format=auto[out]";
            FFmpegFrameFilter FrameFilter = new FFmpegFrameFilter(WaterMarkImage, width, height);

            FrameFilter.start();
            FrameRecorder.start();

            Frame Frame_Pass ;

            while ((Frame_Pass = this.Grabber.grab()) != null) {
                FrameFilter.push(Frame_Pass);
                Frame FilteredFrame = FrameFilter.pull();
                FrameRecorder.record(FilteredFrame);
            }

            FrameRecorder.stop();
            FrameFilter.stop();
            this.Grabber.stop();

        } catch (FFmpegFrameGrabber.Exception | FFmpegFrameFilter.Exception | FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }
    public static void Merge(File VideoOne , File VideoTwo) {
        try {
            FFmpegFrameGrabber Grabber_1 = new FFmpegFrameGrabber(VideoOne.toString()) ;
            FFmpegFrameGrabber Grabber_2 = new FFmpegFrameGrabber(VideoTwo.toString()) ;
            File OutPut = UI.File_Generate.CreateFile("mp4");
            Grabber_1.start();
            Grabber_2.start();

            int width = Math.min(Grabber_1.getImageWidth(), Grabber_2.getImageWidth());
            int height = Math.min(Grabber_1.getImageHeight(), Grabber_2.getImageHeight());
            int channels = Math.min(Grabber_1.getAudioChannels() , Grabber_2.getAudioChannels());
            FFmpegFrameRecorder frameRecorder = new FFmpegFrameRecorder(OutPut, width, height);
            frameRecorder.setAudioBitrate(Grabber_1.getAudioBitrate());
            frameRecorder.setVideoBitrate(Grabber_1.getVideoBitrate());
            frameRecorder.setVideoQuality(0);
            frameRecorder.setAudioChannels(channels);
            frameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);

            frameRecorder.start();
            Frame Frame_Pass ;

            while((Frame_Pass = Grabber_1.grab()) != null)
                frameRecorder.record(Frame_Pass);
            while((Frame_Pass = Grabber_2.grab()) != null)
                frameRecorder.record(Frame_Pass);

            frameRecorder.stop();
            Grabber_2.stop();
            Grabber_1.stop();
        } catch (FFmpegFrameGrabber.Exception | FFmpegFrameRecorder.Exception e) {
            e.printStackTrace();
        }
    }

    private FFmpegFrameRecorder AdjustRecord(File OutPut , FFmpegFrameGrabber G , int width , int height , int channels) {
        FFmpegFrameRecorder frameRecorder = new FFmpegFrameRecorder(OutPut, width, height);
        frameRecorder.setAudioBitrate(G.getAudioBitrate());
        frameRecorder.setVideoBitrate(G.getVideoBitrate());
        frameRecorder.setVideoQuality(0); // MAX Quality!
        frameRecorder.setAudioChannels(channels); // Check
        frameRecorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        return frameRecorder ;
    }

}
