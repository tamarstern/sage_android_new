package com.sage.application;

import com.sage.entities.RecipeDetails;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tamar.twena on 4/26/2016.
 */
public class RecipeImageContainer {

    private static volatile RecipeImageContainer instance;

    private static final Object LOCK = new Object();

    private ConcurrentHashMap<String, String> mainPictureMap = new ConcurrentHashMap<String, String>();

    private ConcurrentHashMap<String, String> recipeAsPictureMap = new ConcurrentHashMap<String, String>();

    private RecipeImageContainer() {

    }

    public void clearAll() {
        mainPictureMap.clear();
        recipeAsPictureMap.clear();
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

    public void putRecipeMainPicture(String detailsId, String imageId){
        mainPictureMap.put(detailsId,imageId);
    }

    public String getRecipeMainPictureId(String detailsId) {
        return mainPictureMap.get(detailsId);
    }

    public void putRecipeAsPicture(String detailsId, String imageId){
        recipeAsPictureMap.put(detailsId,imageId);
    }

    public String getRecipeAsPictureId(String detailsId) {
        return recipeAsPictureMap.get(detailsId);
    }



}
