package com.sage.application;

import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;

import java.util.HashSet;

/**
 * Created by tamar.twena on 5/2/2016.
 */
public class RecipesToSaveContainer {


    private HashSet<RecipeDetails> existingRecipesToSave = new HashSet<RecipeDetails>();

    private HashSet<RecipeCategoryContainer> newRecipesToSave = new HashSet<RecipeCategoryContainer>();


    private static volatile RecipesToSaveContainer instance;

    private static final Object LOCK = new Object();

    private RecipesToSaveContainer() {

    }

    public static RecipesToSaveContainer getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new RecipesToSaveContainer();
                }
            }
        }
        return instance;
    }


    public void addExsitingRecipeToSave(RecipeDetails recipe) {
        if(existingRecipesToSave.contains(recipe)) {
            existingRecipesToSave.remove(recipe);
        }
        existingRecipesToSave.add(recipe);
    }


    public HashSet<RecipeDetails> getExistingRecipesToSave() {
        return new HashSet<RecipeDetails>(this.existingRecipesToSave);
    }

    public void removeRecipeFromList(RecipeDetails details) {
        existingRecipesToSave.remove(details);
    }

    public HashSet<RecipeCategoryContainer> getNewRecipesToSave() {
        return this.newRecipesToSave;
    }

    public void removeNewRecipeFromTheList(RecipeCategoryContainer container) {
        this.newRecipesToSave.remove(container);
    }

    public void addNewRecipeToSave(RecipeDetails details, RecipeCategory category) {
        RecipeCategoryContainer container = new RecipeCategoryContainer();
        container.setCategory(category);
        container.setDetails(details);
        this.newRecipesToSave.add(container);
    }


}
