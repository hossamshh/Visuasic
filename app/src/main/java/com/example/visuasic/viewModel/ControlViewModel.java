package com.example.visuasic.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.visuasic.model.Entities.ColorCommand;
import com.example.visuasic.model.Repository;

import java.util.List;

public class ControlViewModel extends AndroidViewModel {
    private Repository repository;

    private LiveData<List<ColorCommand>> colorCommands;

    public ControlViewModel(Application application){
        super(application);
        repository = Repository.getRepo(application);
        colorCommands = repository.getColorCommands();
    }

    public LiveData<List<ColorCommand>> getColorCommands() {
        return colorCommands;
    }

    public void setCurrentColor(int r, int g, int b) {
        repository.setCurrentColor(r, g, b);
    }

    public void addColorCommand(ColorCommand colorCommand) {
        repository.addColorCommand(colorCommand);
    }

    public void deleteColorCommand(ColorCommand colorCommand) {
        repository.deleteColor(colorCommand);
    }

}