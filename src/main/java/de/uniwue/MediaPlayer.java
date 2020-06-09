package de.uniwue;

import com.mpatric.mp3agic.Mp3File;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.AudioDevice;
import javazoom.jl.player.FactoryRegistry;
import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class MediaPlayer implements Runnable{

    private AdvancedPlayer player;
    private AudioDevice device;

    private Thread thread = null;

    int start;
    int end;
    String filepath;
    Mp3File mp3;

    public MediaPlayer(String filepath,int start, int end) {
        this.filepath = filepath;
        this.start = start;
        this.end = end;

        this.filepath = this.filepath.replace(".cha",".mp3");
        System.out.println(filepath);
        filepath = filepath.substring(0,filepath.length()-4) + ".mp3";

        try {
            System.out.println(filepath);
            mp3 = new Mp3File(filepath);
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void playMedia() throws UnsupportedAudioFileException,
            IOException, LineUnavailableException, JavaLayerException {

        try {


            int bitrate = mp3.getBitrate();
            System.out.println("bitrate: " + bitrate);
            int samplerate = mp3.getSampleRate();
            System.out.println("sampleRate: " + samplerate);
            int frameCount = mp3.getFrameCount();
            System.out.println("frameCount" + frameCount);
            long totalTime = mp3.getLengthInMilliseconds();
            System.out.println("totalTime: " + totalTime);
            float frameTime = totalTime/frameCount;
            System.out.println("frameTime:" + frameTime);

            start = calculateFrame(start,(int) frameTime);
            end = calculateFrame(end,(int) frameTime);
            Thread thread = new Thread(this);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void run() {
        try {
            InputStream is = new FileInputStream(filepath);

            FactoryRegistry r = FactoryRegistry.systemRegistry();
            device = r.createAudioDevice();
            AdvancedPlayer player = new AdvancedPlayer(is,device);
            player.setPlayBackListener(new PlaybackListener() {

                @Override
                public void playbackStarted(PlaybackEvent playbackEvent) {
                    System.out.println("started..");
                    //		   thread.resume();
                }

                public void playbackFinished(PlaybackEvent playbackEvent) {
                    System.out.println("finished..");
                }
            });
            System.out.println("Play : " + start);
            player.play(start,end);
            //System.out.println("Playback finished..");
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public int calculateFrame( int time,int frameTime) {

        return time/frameTime;
    }
}
