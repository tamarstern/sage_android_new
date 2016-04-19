package com.sage.application;

import com.sage.entities.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tamar.twena on 4/19/2016.
 */
public class UserFollowingContainer {

    private ConcurrentHashMap<String, Object> usersMap = new ConcurrentHashMap<String, Object>();

    private static volatile UserFollowingContainer instance;

    private static final Object LOCK = new Object();

    public static String FOLLOWING_KEY = "followingKey";

    private UserFollowingContainer() {

    }

    public static UserFollowingContainer getInstance() {
        if(instance == null) {
            synchronized(LOCK) {
                if(instance == null) {
                    instance = new UserFollowingContainer();
                }
            }
        }
        return instance;
    }

    public void putUsers(ArrayList<User> users) {
        Collections.sort(users);
        HashSet<User> categoriesSet = new HashSet<User>(users);
        this.usersMap.put(FOLLOWING_KEY, categoriesSet);
    }

    public ArrayList<User> getUsers() {
        HashSet<User> users = (HashSet<User>)this.usersMap.get(FOLLOWING_KEY);
        return new ArrayList<User>(users);
    }

    public void follow(User user) {
        HashSet<User> users =  (HashSet<User>)this.usersMap.get(FOLLOWING_KEY);
        if(!users.contains(user)) {
            users.add(user);
        }
        ArrayList<User> usersArray = new ArrayList<User>(users);
        putUsers(usersArray);
    }

    public void unFollow(User user) {
        HashSet<User> users =  (HashSet<User>)this.usersMap.get(FOLLOWING_KEY);
        users.remove(user);
        this.usersMap.put(FOLLOWING_KEY, users);
    }

    public boolean followingInitialized() {
        return this.usersMap.containsKey(FOLLOWING_KEY);
    }











}
