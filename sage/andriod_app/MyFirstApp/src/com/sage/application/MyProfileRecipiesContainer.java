package com.sage.application;

import com.sage.entities.RecipeDetails;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tamar.twena on 4/20/2016.
 */
public class MyProfileRecipiesContainer {

    private ConcurrentHashMap<Integer, Object> profilePageMap = new ConcurrentHashMap<Integer, Object>();

    private static volatile MyProfileRecipiesContainer instance;

    private static final Object LOCK = new Object();

    private MyProfileRecipiesContainer() {

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

    public void addRecipe(RecipeDetails details) {
        if(details.isPublished()) {
            ArrayList<RecipeDetails> recipes = getRecipesByPage(0);
            recipes.add(0, details);
            putRecipesForPage(0, recipes);
        }
    }

}
