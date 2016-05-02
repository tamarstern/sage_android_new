package com.sage.application;

import com.sage.entities.RecipeDetails;

import java.util.HashSet;

/**
 * Created by tamar.twena on 5/1/2016.
 */
public class RecipesToDeleteContainer {

    private HashSet<RecipeDetails> recipesToDelete = new HashSet<RecipeDetails>();

    private static volatile RecipesToDeleteContainer instance;

    private static final Object LOCK = new Object();

    private RecipesToDeleteContainer() {

    }


    public static RecipesToDeleteContainer getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new RecipesToDeleteContainer();
                }
            }
        }
        return instance;
    }

    public synchronized void addRecipeToDelete(RecipeDetails recipe) {
        this.getRecipesToDelete().add(recipe);
    }


    public synchronized HashSet<RecipeDetails> getRecipesToDelete() {

        return new HashSet<RecipeDetails>(recipesToDelete);
    }

    public synchronized void removeRecipeFromList(RecipeDetails recipe) {
        recipesToDelete.remove(recipe);
    }
}
