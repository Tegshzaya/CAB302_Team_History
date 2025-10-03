package com.learn.plan_b_302.DB;

import com.learn.plan_b_302.User;

// Class Responsibilities: Handles Users.
public class UserDataManager {
    private static final String DB_URL = "jdbc:sqlite:artgame.db";
    // TODO: Overall object is to Transfer every relevant user function from DatabaseManager. Ensuring OOP.

    // TODO: Create Authentication.
    public User authenticateUser(String username, String password) {return null;}
    // TODO: Create Signup/Create User.
    public boolean createUser(String username, String password){ return false;}
    // TODO: Create a way to update User Score.
    public void updateScore(String username, int highscore) {}

}
