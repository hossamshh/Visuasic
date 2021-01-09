package com.example.visuasic.model;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;

import com.example.visuasic.viewModel.MusicViewModel;

public class SingletonPlayer  {
    private static MediaPlayer mediaPlayer = null;
    private static Visualizer visualizer = null;

    private static int nSamples = 0;
    private static float avgLows = 0, avgMids = 0, avgHighs = 0;
    private static float maxLow, maxMid, maxHigh;

    public static void initPlayer(Context context, int file) {
        if(mediaPlayer == null) {
            mediaPlayer = MediaPlayer.create(context, file);
            mediaPlayer.setLooping(true);
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    releasePlayer();
                }
            });
        }
    }

    public static void initVisualizer(final MusicViewModel musicViewModel) {
        try {
            visualizer = new Visualizer(mediaPlayer.getAudioSessionId());
            visualizer.setMeasurementMode(Visualizer.MEASUREMENT_MODE_NONE);
            visualizer.setScalingMode(Visualizer.SCALING_MODE_NORMALIZED);
            visualizer.setCaptureSize(128);
            visualizer.setDataCaptureListener(new Visualizer.OnDataCaptureListener() {
                @Override
                public void onWaveFormDataCapture(Visualizer visualizer, byte[] bytes, int i) {
                }

                @Override
                public void onFftDataCapture(Visualizer visualizer, byte[] fft, int rate) {
                    if(isPlaying()) {
                        int n = fft.length;
                        float[] magnitudes = new float[n / 2 + 1];
                        float[] bands = new float[3];
                        for (int k = 1; k < n / 2; k++) {
                            int i = k * 2;
                            magnitudes[k] = (float)Math.hypot(fft[i], fft[i + 1]);
                            if(k < 16) bands[0] += magnitudes[k];
                            else if(k < 32) bands[1] += magnitudes[k];
                            else bands[2] += magnitudes[k];
                        }
                        bands[0] = bands[0]/15;
                        bands[1] = bands[1]/16;
                        bands[2] = bands[2]/32;

                        getBandsTiers(bands);

                        musicViewModel.setCurrentColor((int)bands[0], (int)bands[1], (int)bands[2]);
                    }
                }
            }, 5000, false, true);
            visualizer.setEnabled(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void getBandsTiers(float[] bands) {
        if(maxLow < bands[0]) maxLow = bands[0];
        bands[0] = bands[0]/maxLow * 51;

        if(maxMid < bands[1]) maxMid = bands[1];
        bands[1] = bands[1]/maxMid * 51;

        if(maxHigh < bands[2]) maxHigh = bands[2];
        bands[2] = bands[2]/maxHigh * 51;

//        nSamples++;
//        avgLows = (avgLows*(nSamples-1) + bands[0])/nSamples;
//        avgMids = (avgMids*(nSamples-1) + bands[1])/nSamples;
//        avgHighs = (avgHighs*(nSamples-1) + bands[2])/nSamples;
//
//        float rFraction = bands[0]/avgLows;
//        float gFraction = bands[1]/avgMids;
//        float bFraction = bands[2]/avgLows;
//
//        bands[0] = rFraction*25;
//        bands[0] = bands[0] > 51? 51: bands[0];
//
//        bands[1] = gFraction*25;
//        bands[1] = bands[1] > 51? 51: bands[1];
//
//        bands[2] = bFraction*25;
//        bands[2] = bands[2] > 51? 51: bands[2];

//        float lowsTier0 = avgLows - avgLows/3;
//        float lowsTier1 = avgLows + avgLows/3;
//
//        float midsTier0 = avgMids - avgMids/3;
//        float midsTier1 = avgMids + avgMids/3;
//
//        float highsTier0 = avgHighs - avgHighs/3;
//        float highsTier1 = avgHighs + avgHighs/3;
//
//        if(bands[0] < lowsTier0) bands[0] = 18;
//        else if(bands[0] < lowsTier1) bands[0] = 35;
//        else bands[0] = 51;
//
//        if(bands[1] < midsTier0) bands[1] = 10;
//        else if(bands[1] < midsTier1) bands[1] = 18;
//        else bands[1] = 25;
//
//        if(bands[2] < highsTier0) bands[2] = 18;
//        else if(bands[2] < highsTier1) bands[2] = 35;
//        else bands[2] = 51;
    }

    public static boolean isPlaying() {
        if(mediaPlayer != null)
            return mediaPlayer.isPlaying();
        return false;
    }

    public static void playPauseFile() {
        if(mediaPlayer != null){
            if(mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                if(visualizer != null)
                    visualizer.setEnabled(false);
            }
            else {
                mediaPlayer.start();
                if(visualizer != null)
                    visualizer.setEnabled(true);
            }
        }
    }

    public static int getPosition() {
        if(mediaPlayer != null) {
            return mediaPlayer.getCurrentPosition()/1000;
        }
        return 0;
    }

    public static int getDuration() {
        if(mediaPlayer != null) {
            return mediaPlayer.getDuration()/1000;
        }
        return 1;
    }

    public static void setPosition(int pos) {
        if(mediaPlayer != null) {
            mediaPlayer.seekTo(pos*1000);
        }
    }

    public static void playForward() {
        if(mediaPlayer != null) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
        }
    }

    public static void playBackward() {
        if(mediaPlayer != null) {
            mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() - 10000);
        }
    }

    public static void releasePlayer() {
        if(visualizer != null) {
            visualizer.release();
            visualizer = null;
        }
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

}
