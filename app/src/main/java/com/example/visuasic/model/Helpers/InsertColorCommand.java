package com.example.visuasic.model.Helpers;

import com.example.visuasic.model.DAOs.ColorsDAO;
import com.example.visuasic.model.Entities.ColorCommand;


public class InsertColorCommand extends Thread {
    private ColorsDAO colorsDAO;
    private ColorCommand colorCommand;

    public InsertColorCommand(ColorsDAO colorsDAO, ColorCommand colorCommand) {
        super();
        this.colorsDAO = colorsDAO;
        this.colorCommand = colorCommand;
    }

    @Override
    public void run() {
        colorsDAO.insertColor(colorCommand);
    }
}