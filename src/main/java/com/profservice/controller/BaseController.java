package com.profservice.controller;

import com.profservice.model.User;

public abstract class BaseController {
    protected User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
        initializeData();
    }

    protected abstract void initializeData();
}