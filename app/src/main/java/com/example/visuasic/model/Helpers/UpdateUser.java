package com.example.visuasic.model.Helpers;

import android.util.Log;

import com.example.visuasic.model.DAOs.UserDAO;
import com.example.visuasic.model.Entities.User;

import java.util.concurrent.CountDownLatch;

public class UpdateUser extends Thread {
    private UserDAO userDAO;
    private User user;
    private CountDownLatch latch = null;

    public UpdateUser(UserDAO userDAO, User user) {
        super();
        this.userDAO = userDAO;
        this.user = user;
    }

    public UpdateUser(UserDAO userDAO, User user, CountDownLatch latch) {
        super();
        this.userDAO = userDAO;
        this.user = user;
        this.latch = latch;
    }

    @Override
    public void run() {
        userDAO.updateUser(user.getUid(), user.isRegistered(), user.isLoggedIn());
        if(latch != null) {
            latch.countDown();
        }

    }
}
