package com.example.visuasic.view;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;

import com.example.visuasic.R;
import com.example.visuasic.model.Entities.ColorCommand;
import com.example.visuasic.viewModel.ControlViewModel;

import java.util.List;

public class ControlFragment extends Fragment implements SeekBar.OnSeekBarChangeListener, AddColorDialog.AddDialogInterface {
    private ControlViewModel controlViewModel;

    private SeekBar redSeekBar, greenSeekBar, blueSeekBar;
    private View colorPreview;
    private ImageButton addButton;

    private RecyclerView recyclerView;
    private ColorCommandsAdapter colorCommandsAdapter;

    private int currentColor = Color.rgb(0, 0, 0);

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.control_fragment, container, false);

        redSeekBar = rootView.findViewById(R.id.redSeekbar);
        greenSeekBar = rootView.findViewById(R.id.greenSeekbar);
        blueSeekBar = rootView.findViewById(R.id.blueSeekbar);
        colorPreview = rootView.findViewById(R.id.colorPreview);
        addButton = rootView.findViewById(R.id.buttonAdd);
        recyclerView = rootView.findViewById(R.id.recyclerView);


        redSeekBar.setMax(3);
        greenSeekBar.setMax(3);
        blueSeekBar.setMax(3);

        redSeekBar.setOnSeekBarChangeListener(this);
        greenSeekBar.setOnSeekBarChangeListener(this);
        blueSeekBar.setOnSeekBarChangeListener(this);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openAddDialog();
            }
        });

        colorCommandsAdapter = new ColorCommandsAdapter(rootView.getContext());
        recyclerView.setAdapter(colorCommandsAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(rootView.getContext()));

        return  rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        controlViewModel =  new ViewModelProvider(this).get(ControlViewModel.class);
        controlViewModel.getColorCommands().observe(getViewLifecycleOwner(), new Observer<List<ColorCommand>>() {
            @Override
            public void onChanged(List<ColorCommand> colorCommands) {
                colorCommandsAdapter.setObjects(colorCommands, controlViewModel, redSeekBar, greenSeekBar, blueSeekBar);
            }
        });
    }

    private void openAddDialog() {
        AddColorDialog dialog = new AddColorDialog();
        dialog.setTargetFragment(this, 0);
        dialog.show(getParentFragmentManager(), "add color dialog");
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean bool) {
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        int r = redSeekBar.getProgress();
        int g = greenSeekBar.getProgress();
        int b = blueSeekBar.getProgress();

        currentColor = Color.rgb(r*85, g*85, b*85);
        colorPreview.setBackgroundColor(currentColor);

        controlViewModel.setCurrentColor(r, g, b);
    }

    @Override
    public void getColorPhrase(String colorPhrase) {
        ColorCommand colorCommand = new ColorCommand(currentColor, colorPhrase);
        controlViewModel.addColorCommand(colorCommand);
    }
}