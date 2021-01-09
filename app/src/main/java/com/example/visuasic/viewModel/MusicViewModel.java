package com.example.visuasic.viewModel;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;

import com.example.visuasic.model.Repository;

public class MusicViewModel extends AndroidViewModel {
    private Repository repository;

    public MusicViewModel(Application application){
        super(application);
        repository = Repository.getRepo(application);
    }

    public void setCurrentColor(int r, int g, int b) {
        repository.setCurrentColor(r, g, b);
    }
}
