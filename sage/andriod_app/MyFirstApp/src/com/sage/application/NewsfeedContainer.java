package com.sage.application;

import com.sage.entities.RecipeDetails;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tamar.twena on 4/21/2016.
 */
public class NewsfeedContainer {

    private ConcurrentHashMap<Integer, Object> newsfeedMap = new ConcurrentHashMap<Integer, Object>();

    private ConcurrentHashMap<String, Object> newsfeedUsersMap = new ConcurrentHashMap<String, Object>();

    private static volatile NewsfeedContainer instance;

    private static final Object LOCK = new Object();

    private NewsfeedContainer() {

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

    public void addRecipe(RecipeDetails details) {
        if(details.isPublished()) {
            ArrayList<RecipeDetails> recipes = getRecipesByPage(0);
            if(recipes.contains(details)) {
                recipes.remove(details);
            }
            recipes.add(0, details);
            putRecipesForPage(0, recipes);
        }
    }

    public void addNewsfeedRecipesForUser(String userId, ArrayList<RecipeDetails> newsfeedRecipes) {
        this.newsfeedUsersMap.put(userId, newsfeedRecipes);
    }

    public ArrayList<RecipeDetails> getRecipesForUser(String userId) {
        ArrayList<RecipeDetails> details = (ArrayList<RecipeDetails>)newsfeedUsersMap.get(userId);
        return details;
    }

}
