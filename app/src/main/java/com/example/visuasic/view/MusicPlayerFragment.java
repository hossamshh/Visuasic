package com.example.visuasic.view;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.visuasic.R;
import com.example.visuasic.databinding.MusicPlayerFragmentBinding;
import com.example.visuasic.model.SingletonPlayer;

public class MusicPlayerFragment extends Fragment {
    private static final String TAG = "dev";

    private MusicPlayerFragmentBinding binding;

    public static MusicPlayerFragment newInstance() {
        return new MusicPlayerFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.music_player_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        binding = MusicPlayerFragmentBinding.inflate(getLayoutInflater());

        binding.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playFile();
            }
        });
        binding.pauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SingletonPlayer.pauseFile();
            }
        });

        binding.linearLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                int l = (int)(Math.random()*100);
                int m = (int)(Math.random()*100);
                int h = (int)(Math.random()*100);
                setAmps(l, m, h);
                binding.linearLayout.postDelayed(this, 500);
            }
        }, 500);
    }

    private void setAmps(int low, int mid, int high) {
        int layoutWidth = binding.linearLayout.getWidth();
        int height = binding.lowsViewLayout.getHeight();

        int lowsWidth = layoutWidth*low/100;
        binding.lowsViewLayout.setLayoutParams(new LinearLayout.LayoutParams(lowsWidth, height));

        int midsWidth = layoutWidth*low/100;
        binding.midsViewLayout.setLayoutParams(new LinearLayout.LayoutParams(midsWidth, height));

        int highsWidth = layoutWidth*low/100;
        binding.highsViewLayout.setLayoutParams(new LinearLayout.LayoutParams(highsWidth, height));
    }

    private void playFile() {
        SingletonPlayer.playFile(getContext(), R.raw.everybody_knows);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SingletonPlayer.releasePlayer();
    }

}