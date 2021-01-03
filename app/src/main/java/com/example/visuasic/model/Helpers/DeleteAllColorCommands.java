package com.example.visuasic.model.Helpers;

import com.example.visuasic.model.DAOs.ColorsDAO;
import com.example.visuasic.model.Entities.ColorCommand;


public class DeleteAllColorCommands extends Thread {
    private ColorsDAO colorsDAO;

    public DeleteAllColorCommands(ColorsDAO colorsDAO) {
        super();
        this.colorsDAO = colorsDAO;
    }

    @Override
    public void run() {
        colorsDAO.deleteColors();
    }
}