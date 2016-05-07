package com.sage.application;

import android.graphics.Bitmap;

import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;

import java.util.HashSet;

/**
 * Created by tamar.twena on 5/2/2016.
 */
public class RecipesToSaveContainer {


    private HashSet<RecipeDetails> existingRecipesToSave = new HashSet<RecipeDetails>();

    private HashSet<RecipeCategoryContainer> newRecipesToSave = new HashSet<RecipeCategoryContainer>();

    private HashSet<RecipeCategoryContainer> categoryAttachFailureRecipes = new HashSet<RecipeCategoryContainer>();

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

    public void addNewRecipeToSave(RecipeDetails details, RecipeCategory category, Bitmap mainPicture, Bitmap recipeImagePicture) {
        RecipeCategoryContainer container = new RecipeCategoryContainer();
        container.setCategory(category);
        container.setDetails(details);
        container.setMainRecipePicture(mainPicture);
        container.setRecipeImagePicture(recipeImagePicture);
        this.newRecipesToSave.add(container);
    }

    public void updateNewRecipeToSaveWithCategory(RecipeDetails details, RecipeCategory category) {
        for(RecipeCategoryContainer container :  this.newRecipesToSave) {
            if(container.getDetails().equals(details)) {
                container.setCategory(category);
                break;
            }
        }
    }


    public void addAttachCategoryToSave(RecipeDetails details, RecipeCategory category) {
        RecipeCategoryContainer container = new RecipeCategoryContainer();
        container.setCategory(category);
        container.setDetails(details);
        this.categoryAttachFailureRecipes.add(container);
    }

    public HashSet<RecipeCategoryContainer> getAttachCategoryToSave() {
        return this.categoryAttachFailureRecipes;
    }

    public void removeAttachCategoryFromTheList(RecipeCategoryContainer container) {
        this.categoryAttachFailureRecipes.remove(container);
    }



}
