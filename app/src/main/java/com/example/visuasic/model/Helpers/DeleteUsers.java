package com.example.visuasic.model.Helpers;


import com.example.visuasic.model.DAOs.UserDAO;

public class DeleteUsers extends Thread {
    private UserDAO userDAO;

    public DeleteUsers(UserDAO userDAO) {
        super();
        this.userDAO = userDAO;
    }

    @Override
    public void run() {
        userDAO.deleteUsers();
    }
}
