package com.example.trails.model;
import java.util.ArrayList;

import static com.example.trails.MainActivity.globalUser;

public class SingletonCurrentUser {

    private static User currentUserInstance = new User();

    public SingletonCurrentUser(){}

    public static void setCurrentUserInstance(User user){
        currentUserInstance = user;
    }

    public static User getCurrentUserInstance(){
        if (currentUserInstance.getIdUser() == null)
            globalUser();
        if(currentUserInstance.getFavoriteTrails() == null){
            currentUserInstance.setFavoriteTrails(new ArrayList<String>());
        }

        if(currentUserInstance.getMadeTrails() == null){
            currentUserInstance.setMadeTrails(new ArrayList<String>());
        }
        return currentUserInstance;
    }
}
