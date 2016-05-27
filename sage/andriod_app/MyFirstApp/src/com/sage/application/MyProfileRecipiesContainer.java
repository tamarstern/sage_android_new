package com.sage.application;

import android.text.TextUtils;

import com.sage.entities.RecipeDetails;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tamar.twena on 4/20/2016.
 */
public class MyProfileRecipiesContainer {

    private ConcurrentHashMap<Integer, Object> profilePageMap = new ConcurrentHashMap<Integer, Object>();

    private ConcurrentHashMap<String, String> profilePageFollowedByMap = new ConcurrentHashMap<String, String>();

    private static volatile MyProfileRecipiesContainer instance;

    private boolean myProfileRecipesInitialized;

    private static final Object LOCK = new Object();

    private MyProfileRecipiesContainer() {

    }

    public void clearAll() {
        profilePageMap.clear();
        profilePageFollowedByMap.clear();
    }

    public static MyProfileRecipiesContainer getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new MyProfileRecipiesContainer();
                }
            }
        }
        return instance;
    }

    public void deleteRecipe(RecipeDetails details) {
        Set<Integer> keys = profilePageMap.keySet();
        for(Integer key : keys) {
            ArrayList<RecipeDetails> recipesByPage = getRecipesByPage(key);
            recipesByPage.remove(details);
            putRecipesForPage(key, recipesByPage);
        }
    }

    public void removeOldRecipies() {
        Set<Integer> keys = this.profilePageMap.keySet();
        for(Integer key : keys) {
            if(key > 4) {
                this.profilePageMap.remove(key);
            }
        }
    }


    public ArrayList<RecipeDetails> getRecipesByPage(Integer pageNumber) {
        ArrayList<RecipeDetails> recipes =  (ArrayList<RecipeDetails>)this.profilePageMap.get(pageNumber);
        if(recipes == null) {
            recipes = new ArrayList<RecipeDetails>();
            putRecipesForPage(pageNumber, recipes);
        }
        return recipes;
    }

    public void putRecipesForPage(Integer pageNumber, ArrayList<RecipeDetails> details) {
        this.profilePageMap.put(pageNumber, new ArrayList<RecipeDetails>(details));

    }

    public void updateRecipeInProfile(RecipeDetails recipeDetails) {
        if(!recipeDetails.isPublished()) {
            for(Integer key : profilePageMap.keySet()) {
                ArrayList<RecipeDetails> recipes = getRecipesByPage(key);
                recipes.remove(recipeDetails);
                putRecipesForPage(key, recipes);
            }
        } else {
            addRecipe(recipeDetails);
        }
    }


    public void addRecipe(RecipeDetails details) {
        if(details.isPublished()) {
            deleteRecipe(details);
            ArrayList<RecipeDetails> recipes = getRecipesByPage(0);
            recipes.add(0, details);
            putRecipesForPage(0, recipes);
        }
    }

    public boolean hasCountForUser(String userId) {
        return profilePageFollowedByMap.containsKey(userId);
    }

    public void decreaseFollowedByCount(String userId) {
        if(profilePageFollowedByMap.containsKey(userId)) {
            String followCountStr =  profilePageFollowedByMap.get(userId);
            Integer followCount = Integer.valueOf(followCountStr);
            if(followCount == 0) {
                return;
            }
            Integer newCount = followCount - 1;
            profilePageFollowedByMap.put(userId, Integer.toString(newCount));
        }
    }



    public void increaseFollowedByCount(String userId) {
        if(profilePageFollowedByMap.containsKey(userId)) {
            String followCountStr =  profilePageFollowedByMap.get(userId);
            if(!TextUtils.isEmpty(followCountStr)) {
                Integer followCount = Integer.valueOf(followCountStr) + 1;
                profilePageFollowedByMap.put(userId, Integer.toString(followCount));
            }
        }
    }

    public String getFollowedByCountForUser(String userId) {
        if(profilePageFollowedByMap.containsKey(userId)) {
            return profilePageFollowedByMap.get(userId);
        }
        return Integer.toString(0);
    }

    public void setFollowByCountForUser(String userId, String followedByCount) {
        profilePageFollowedByMap.put(userId, followedByCount);
    }


    private void addLike(RecipeDetails details, ArrayList<RecipeDetails> recipes) {
        if( recipes != null && recipes.contains(details)) {
            int index = recipes.indexOf(details);
            RecipeDetails currentDetails = recipes.get(index);
            if(!currentDetails.isUserLikeRecipe()) {
                currentDetails.setLikes_count(currentDetails.getLikes_count()+1);
                currentDetails.setUserLikeRecipe(true);
            }
        }
    }

    private void removeLike(RecipeDetails details, ArrayList<RecipeDetails> recipes) {
        if( recipes != null && recipes.contains(details)) {
            int index = recipes.indexOf(details);
            RecipeDetails currentDetails = recipes.get(index);
            if(currentDetails.isUserLikeRecipe()) {
                currentDetails.setLikes_count(currentDetails.getLikes_count()-1);
                currentDetails.setUserLikeRecipe(false);
            }
        }
    }


    public void addLike(RecipeDetails details) {
        for(Integer page : profilePageMap.keySet()) {
            ArrayList<RecipeDetails> recipesByPage = getRecipesByPage(page);
            addLike(details, recipesByPage);
        }
    }

    public void removeLike(RecipeDetails details) {
        for(Integer page : profilePageMap.keySet()) {
            ArrayList<RecipeDetails> recipesByPage = getRecipesByPage(page);
            removeLike(details, recipesByPage);
        }
    }

    public boolean isMyProfileRecipesInitialized() {
        return myProfileRecipesInitialized;
    }

    public void setMyProfileRecipesInitialized(boolean myProfileRecipesInitialized) {
        this.myProfileRecipesInitialized = myProfileRecipesInitialized;
    }
}
