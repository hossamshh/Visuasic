package com.example.visuasic.model;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.audiofx.Visualizer;
import android.util.Log;

public class SingletonPlayer  {
    private static final String TAG = "dev";

    private static MediaPlayer mediaPlayer = null;
    private static Visualizer visualizer = null;

    private static int nSamples = 0;
    private static float avgLows = 0, avgMids = 0, avgHighs = 0;

    public static void playFile(Context context, int file) {
        if(mediaPlayer == null){
            mediaPlayer = MediaPlayer.create(context, file);
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

                        Log.d(TAG, "onFftDataCapture: " + bands[0] + ", " + bands[1] + ", " + bands[2]);
                    }
                }, 10000, false, true);
                visualizer.setEnabled(true);
            }
            catch (Exception e) {
                Log.d(TAG, "playFile: " + e);
            }



            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    releasePlayer();
                }
            });

        }
    }

    private static void getBandsTiers(float[] bands) {
        nSamples++;
        avgLows = (avgLows*(nSamples-1) + bands[0])/nSamples;
        avgMids = (avgMids*(nSamples-1) + bands[1])/nSamples;
        avgHighs = (avgHighs*(nSamples-1) + bands[2])/nSamples;

        float lowsTier0 = avgLows - avgLows/3;
        float lowsTier1 = avgLows + avgLows/3;

        float midsTier0 = avgMids - avgMids/3;
        float midsTier1 = avgMids + avgMids/3;

        float highsTier0 = avgHighs - avgHighs/3;
        float highsTier1 = avgHighs + avgHighs/3;

        if(bands[0] < lowsTier0) bands[0] = 0;
        else if(bands[0] < lowsTier1) bands[0] = 1;
        else bands[0] = 2;

        if(bands[1] < midsTier0) bands[1] = 0;
        else if(bands[1] < midsTier1) bands[1] = 1;
        else bands[1] = 2;

        if(bands[2] < highsTier0) bands[2] = 0;
        else if(bands[2] < highsTier1) bands[2] = 1;
        else bands[2] = 2;
    }

    public static boolean isPlaying() {
        if(mediaPlayer != null)
            return mediaPlayer.isPlaying();
        return false;
    }

    public static void pauseFile() {
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

    public static void releasePlayer() {
        if(mediaPlayer != null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if(visualizer != null) {
            visualizer.release();
            visualizer = null;
        }
    }

}
