/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

/**
 *
 * @author JEREMY
 */
import org.bytedeco.ffmpeg.global.avcodec;
import org.bytedeco.ffmpeg.global.avutil;
import org.bytedeco.javacv.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import static org.bytedeco.opencv.helper.opencv_imgcodecs.cvLoadImage;
public class Image2Mp4 {
    public static void main(String[] args) throws Exception {
        String mp4SavePath = "F:\\extracted.mp4";
        String audioPath="F:\\test.mp3";
        String Output="F:\\yesss.mp4";
        //image folder
        String img = "F:\\images";
        int width = 1600;
        int height = 900;
        //loop all picture
        File file = new File(img);
        File[] files = file.listFiles();
        Arrays.sort(files, new Comparator<File>(){
                @Override
                public int compare(File f1, File f2) {
                    String s1 = f1.getName().substring(0, f1.getName().indexOf("."));
                    String s2 = f2.getName().substring(0, f2.getName().indexOf("."));
                    return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));  
                }
        });
      // Arrays.sort(files);
        Map<Integer, File> imgMap = new HashMap<Integer, File>();
        int num = 0;
        for (File imgFile : files) {
            //System.out.println(imgFile);
            imgMap.put(num, imgFile);
            
            num++;
        }
        createMp4(mp4SavePath, imgMap, width, height);
       // mergeAudioAndVideo(mp4SavePath, audioPath, Output);
    }

    private static void createMp4(String mp4SavePath, Map<Integer, File> imgMap, int width, int height) throws FrameRecorder.Exception {
        FFmpegFrameRecorder recorder = new FFmpegFrameRecorder(mp4SavePath, width, height);
        //set codec
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264);
        //set frame rate
        recorder.setFrameRate(30);
        recorder.setPixelFormat(avutil.AV_PIX_FMT_YUV420P);
        recorder.setFormat("mp4");
        try {
            recorder.start();
            Java2DFrameConverter converter = new Java2DFrameConverter();
            //duraiton is 22 seconds
            for (int i = 0; i < imgMap.size(); i++) {
                BufferedImage read = ImageIO.read(imgMap.get(i));
              
                    recorder.record(converter.getFrame(read));
                
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            recorder.stop();
            recorder.release();
        }
    }
    public static boolean mergeAudioAndVideo(String videoPath, String audioPath, String outPut) throws Exception {
        boolean isCreated = true;
        File file = new File(videoPath);
        if (!file.exists()) {
            return false;
        }
        FrameRecorder recorder = null;
        FrameGrabber grabber1 = null;
        FrameGrabber grabber2 = null;
        try {
            //load video
            grabber1 = new FFmpegFrameGrabber(videoPath);
            //load audio
            grabber2 = new FFmpegFrameGrabber(audioPath);
            grabber1.start();
            grabber2.start();
            //create record
            recorder = new FFmpegFrameRecorder(outPut,
                    grabber1.getImageWidth(), grabber1.getImageHeight(),
                    grabber2.getAudioChannels());
            recorder.setFormat("mp4");
            recorder.setFrameRate(grabber1.getFrameRate());
            recorder.setSampleRate(grabber2.getSampleRate());
            recorder.start();
            Frame frame1;
            Frame frame2 ;
            //record video frame
            while ((frame1 = grabber1.grabFrame()) != null ){
                recorder.record(frame1);
            }
            //record sample frame
            while ((frame2 = grabber2.grabFrame()) != null) {
                recorder.record(frame2);
            }
            grabber1.stop();
            grabber2.stop();
            recorder.stop();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (recorder != null) {
                    recorder.release();
                }
                if (grabber1 != null) {
                    grabber1.release();
                }
                if (grabber2 != null) {
                    grabber2.release();
                }
            } catch (FrameRecorder.Exception e) {
                e.printStackTrace();
            }
        }
        return isCreated;
    }
}