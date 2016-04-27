package com.sage.application;

import android.graphics.Bitmap;

import com.sage.entities.RecipeDetails;
import com.sage.utils.CachedMap;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tamar.twena on 4/26/2016.
 */
public class RecipeImageContainer {

    private static volatile RecipeImageContainer instance;

    private static final Object LOCK = new Object();

    private ConcurrentHashMap<RecipeDetails, String> mainPictureMap = new ConcurrentHashMap<RecipeDetails, String>();

    private ConcurrentHashMap<RecipeDetails, String> recipeAsPictureMap = new ConcurrentHashMap<RecipeDetails, String>();

    private CachedMap<String, Bitmap> mainImageMap = new CachedMap<String, Bitmap>(10);

    private CachedMap<String, Bitmap> imageRecipeMap = new CachedMap<String, Bitmap>(10);


    private RecipeImageContainer() {

    }

    public static RecipeImageContainer getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new RecipeImageContainer();
                }
            }
        }
        return instance;
    }

    public void putRecipeMainPicture(RecipeDetails details, String imageId){
        mainPictureMap.put(details,imageId);
    }

    public String getRecipeMainPictureId(RecipeDetails details) {
        return mainPictureMap.get(details);
    }

    public void putRecipeAsPicture(RecipeDetails details, String imageId){
        recipeAsPictureMap.put(details,imageId);
    }

    public String getRecipeAsPictureId(RecipeDetails details) {
        return recipeAsPictureMap.get(details);
    }

    public void putMainImageForRecipe(String recipeId, Bitmap image) {
        this.mainImageMap.put(recipeId, image);
    }

    public Bitmap getMainImageForRecipe(String recipeId) {
        return this.mainImageMap.get(recipeId);
    }

    public void putRecipeImageForRecipe(String recipeId, Bitmap image) {
        this.imageRecipeMap.put(recipeId, image);
    }

    public Bitmap getRecipeImageForRecipe(String recipeId) {
        return this.imageRecipeMap.get(recipeId);
    }


}