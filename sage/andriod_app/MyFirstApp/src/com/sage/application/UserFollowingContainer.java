package com.sage.application;

import com.sage.entities.User;

import java.util.ArrayList;
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

    public static String USERS_TO_FOLLOW_KEY = "usersToFollowKey";

    public static String USERS_TO_UNFOLLOW_KEY = "usersToUnfollowKey";


    private UserFollowingContainer() {

    }

    public void clearAll() {
        usersMap.clear();
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

    public boolean isFollowing(User user) {
        ArrayList<User> users = getUsers();
        if(users == null) {
            return false;
        }
        return users.contains(user);
    }

    public void putUsers(ArrayList<User> users) {
        HashSet<User> categoriesSet = new HashSet<User>(users);
        this.usersMap.put(FOLLOWING_KEY, categoriesSet);
    }

    public ArrayList<User> getUsers() {
        HashSet<User> users = (HashSet<User>)this.usersMap.get(FOLLOWING_KEY);
        if(users == null) {
            users = new HashSet<User>();
        }
        return new ArrayList<User>(users);
    }

    public void follow(User user) {
        HashSet<User> users =  (HashSet<User>)this.usersMap.get(FOLLOWING_KEY);
        if(users == null) {
            users = new HashSet<User>();
        }
        if(!users.contains(user)) {
            users.add(user);
            this.usersMap.put(FOLLOWING_KEY, users);
        }
        HashSet<User> usersToFollow = getUsersToFollow();
        usersToFollow.add(user);
        putUsersToFollow(usersToFollow);
    }

    public void unFollow(User user) {
        HashSet<User> users =  (HashSet<User>)this.usersMap.get(FOLLOWING_KEY);
        if(users != null) {
            users.remove(user);
            this.usersMap.put(FOLLOWING_KEY, users);
        }
        HashSet<User> usersToUnfollow = getUsersToUnfollow();
        usersToUnfollow.add(user);
        putUsersToUnfollow(usersToUnfollow);
    }

    public boolean followingInitialized() {
        return this.usersMap.containsKey(FOLLOWING_KEY);
    }

    public HashSet<User> getUsersToFollow() {
        HashSet<User> usersToFollow = (HashSet<User>)this.usersMap.get(USERS_TO_FOLLOW_KEY);
        if(usersToFollow == null) {
            usersToFollow = new HashSet<User>();
            putUsersToFollow(usersToFollow);
        }
        return usersToFollow;
    }

    public void putUsersToFollow(HashSet<User> users) {
        usersMap.put(USERS_TO_FOLLOW_KEY, users);
    }


    public HashSet<User> getUsersToUnfollow() {
        HashSet<User> usersToUnFollow = (HashSet<User>)this.usersMap.get(USERS_TO_UNFOLLOW_KEY);
        if(usersToUnFollow == null) {
            usersToUnFollow = new HashSet<User>();
            putUsersToUnfollow(usersToUnFollow);
        }
        return usersToUnFollow;
    }


    public void putUsersToUnfollow(HashSet<User> users) {
        usersMap.put(USERS_TO_UNFOLLOW_KEY, users);
    }









}
