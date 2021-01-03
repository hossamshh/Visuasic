package com.example.visuasic.model.Helpers;

import com.example.visuasic.model.DAOs.ColorsDAO;
import com.example.visuasic.model.Entities.ColorCommand;


public class DeleteColorCommand extends Thread {
    private ColorsDAO colorsDAO;
    private ColorCommand colorCommand;

    public DeleteColorCommand(ColorsDAO colorsDAO, ColorCommand colorCommand) {
        super();
        this.colorsDAO = colorsDAO;
        this.colorCommand = colorCommand;
    }

    @Override
    public void run() {
        colorsDAO.deleteColor(colorCommand);
    }
}