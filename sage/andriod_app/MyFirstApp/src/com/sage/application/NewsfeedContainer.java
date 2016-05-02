package com.sage.application;

import com.sage.entities.RecipeDetails;

import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tamar.twena on 4/21/2016.
 */
public class NewsfeedContainer {

    private ConcurrentHashMap<Integer, Object> newsfeedMap = new ConcurrentHashMap<Integer, Object>();

    private ConcurrentHashMap<String, Object> profileUsersMap = new ConcurrentHashMap<String, Object>();

    private static volatile NewsfeedContainer instance;

    private static final Object LOCK = new Object();

    private NewsfeedContainer() {

    }

    public void clearAll() {
        newsfeedMap.clear();
        profileUsersMap.clear();
    }

    public static NewsfeedContainer getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new NewsfeedContainer();
                }
            }
        }
        return instance;
    }

    public void deleteRecipe(RecipeDetails details) {
        Set<Integer> keys = newsfeedMap.keySet();
        for(Integer key : keys) {
            ArrayList<RecipeDetails> recipesByPage = getRecipesByPage(key);
            recipesByPage.remove(details);
            putRecipesForPage(key, recipesByPage);
        }
    }



    public ArrayList<RecipeDetails> getRecipesByPage(Integer pageNumber) {
        ArrayList<RecipeDetails> recipes =  (ArrayList<RecipeDetails>)this.newsfeedMap.get(pageNumber);
        if(recipes == null) {
            recipes = new ArrayList<RecipeDetails>();
            putRecipesForPage(pageNumber, recipes);
        }
        return recipes;
    }

    public void putRecipesForPage(Integer pageNumber, ArrayList<RecipeDetails> details) {
        this.newsfeedMap.put(pageNumber, new ArrayList<RecipeDetails>(details));
    }

    public void removeOldRecipies() {
        Set<Integer> keys = this.newsfeedMap.keySet();
        for(Integer key : keys) {
            if(key > 4) {
                this.newsfeedMap.remove(key);
            }
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

    public void addProfileRecipesForUser(String userId, ArrayList<RecipeDetails> newsfeedRecipes) {
        this.profileUsersMap.put(userId, newsfeedRecipes);
    }

    public ArrayList<RecipeDetails> getProfileForUser(String userId) {
        ArrayList<RecipeDetails> details = (ArrayList<RecipeDetails>) profileUsersMap.get(userId);
        return details;
    }

    public void clearProfileRecipes() {
        profileUsersMap.clear();
    }

    public void updateRecipeInNewsfeed(RecipeDetails recipeDetails) {
        if(!recipeDetails.isPublished()) {
            for(Integer key : newsfeedMap.keySet()) {
                ArrayList<RecipeDetails> recipes = getRecipesByPage(key);
                recipes.remove(recipeDetails);
                putRecipesForPage(key, recipes);
            }
        } else {
            addRecipe(recipeDetails);
        }
    }


}
