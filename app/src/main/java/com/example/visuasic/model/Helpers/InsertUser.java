package com.example.visuasic.model.Helpers;

import com.example.visuasic.model.DAOs.UserDAO;
import com.example.visuasic.model.Entities.User;


public class InsertUser extends Thread {
    private UserDAO userDAO;
    private User user;

    public InsertUser(UserDAO userDAO, User user) {
        super();
        this.userDAO = userDAO;
        this.user = user;
    }

    @Override
    public void run() {
        userDAO.insertUser(user);
    }
}