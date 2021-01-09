package com.example.visuasic.view;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.visuasic.R;
import com.example.visuasic.model.SingletonPlayer;
import com.example.visuasic.viewModel.ControlViewModel;
import com.example.visuasic.viewModel.MusicViewModel;

public class MusicPlayerFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "dev";

    private MusicViewModel musicViewModel;

    private ImageView imageView;
    private SeekBar musicSeekBar;
    private TextView musicName;
    private ImageButton playButton, pauseButton, forwardButton, backwardButton;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.music_player_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        imageView = getView().findViewById(R.id.musicImage);
        musicSeekBar = getView().findViewById(R.id.musicSeekBar);
        musicName = getView().findViewById(R.id.musicName);
        playButton = getView().findViewById(R.id.playButton);
        pauseButton = getView().findViewById(R.id.pauseButton);
        forwardButton = getView().findViewById(R.id.forwardButton);
        backwardButton = getView().findViewById(R.id.backwardButton);

        musicSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SingletonPlayer.setPosition(seekBar.getProgress());

            }
        });

        playButton.setOnClickListener(this);
        pauseButton.setOnClickListener(this);
        forwardButton.setOnClickListener(this);
        backwardButton.setOnClickListener(this);

        musicViewModel = new ViewModelProvider(this).get(MusicViewModel.class);

        SingletonPlayer.initPlayer(getContext(), R.raw.song_of_myself);
        requestAudioPermission();

        setUIParams();
    }

    private void setUIParams() {
        SingletonPlayer.initPlayer(getContext(), R.raw.song_of_myself);

        requestAudioPermission();

        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        AssetFileDescriptor afd = getResources().openRawResourceFd(R.raw.song_of_myself);
        mmr.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
        byte [] data = mmr.getEmbeddedPicture();
        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
        imageView.setImageBitmap(bitmap);

        musicName.setText(getResources().getResourceEntryName(R.raw.song_of_myself));

        musicSeekBar.setMax(SingletonPlayer.getDuration());
    }


    private final Runnable seekBarRunnable = new Runnable() {
        @Override
        public void run() {
            if(SingletonPlayer.isPlaying()) {
                musicSeekBar.setProgress(SingletonPlayer.getPosition());
                musicSeekBar.postDelayed(this, 1000);
            }
        }
    };

    private void requestAudioPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                SingletonPlayer.initVisualizer(musicViewModel);
            }
            else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                    Toast.makeText(getContext(), "Application uses audio recording to control your LED Strip based on" +
                            " playing music", Toast.LENGTH_LONG).show();
                }
                requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 0);
            }

        } else {
            SingletonPlayer.initVisualizer(musicViewModel);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getContext(),
                        "Application can not perform music visualization feature", Toast.LENGTH_LONG).show();
            }
            else {
                SingletonPlayer.initVisualizer(musicViewModel);
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playButton:
                SingletonPlayer.playPauseFile();
                musicSeekBar.post(seekBarRunnable);
                playButton.setVisibility(View.GONE);
                pauseButton.setVisibility(View.VISIBLE);
                break;

            case R.id.pauseButton:
                SingletonPlayer.playPauseFile();
                playButton.setVisibility(View.VISIBLE);
                pauseButton.setVisibility(View.GONE);
                break;

            case R.id.forwardButton:
                SingletonPlayer.playForward();
                break;

            case R.id.backwardButton:
                SingletonPlayer.playBackward();
                break;

            default:
                break;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SingletonPlayer.releasePlayer();
    }
}