package com.sage.application;

import android.text.TextUtils;

import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tamar.twena on 4/17/2016.
 */
public class UserCategoriesContainer {

    private ConcurrentHashMap<String, Object> categoriesMap = new ConcurrentHashMap<String, Object>();

    private static volatile UserCategoriesContainer instance;

    private static final Object LOCK = new Object();

    public static String CATEGORIES_KEY = "categoriesKey";

    public static String CATEGORY_ID = "categoryId_";

    private UserCategoriesContainer() {

    }

    public static UserCategoriesContainer getInstance() {
        if (instance == null) {
            synchronized (LOCK) {
                if (instance == null) {
                    instance = new UserCategoriesContainer();
                }
            }
        }
        return instance;
    }

    public void putCategories(HashSet<RecipeCategory> categories) {
        HashSet<RecipeCategory> categoriesSet = new HashSet<RecipeCategory>(categories);
        this.categoriesMap.put(CATEGORIES_KEY, categoriesSet);
    }

    public ArrayList<RecipeCategory> getCategories() {
        HashSet<RecipeCategory> categories = (HashSet<RecipeCategory>) this.categoriesMap.get(CATEGORIES_KEY);
        return new ArrayList<RecipeCategory>(categories);
    }

    public void putCategory(RecipeCategory category) {
        HashSet<RecipeCategory> categories = (HashSet<RecipeCategory>) this.categoriesMap.get(CATEGORIES_KEY);
        categories.remove(category);
        categories.add(category);
        putCategories(categories);
        putRecipesForCategory(category, new HashSet<RecipeDetails>());
    }

    public void deleteCategory(RecipeCategory category) {
        HashSet<RecipeCategory> categories = (HashSet<RecipeCategory>) this.categoriesMap.get(CATEGORIES_KEY);
        categories.remove(category);
        this.categoriesMap.put(CATEGORIES_KEY, categories);
    }

    public boolean categoriesInitialized() {
        return this.categoriesMap.containsKey(CATEGORIES_KEY);
    }

    public void putRecipesForCategory(RecipeCategory category, HashSet<RecipeDetails> recipesSet) {
        String key = generateKey(category.get_id());
        this.categoriesMap.put(key, recipesSet);
    }

    private String generateKey(String categoryId) {
        String key = CATEGORY_ID + categoryId;
        return key;
    }

    public boolean hasRecipesForCategory(RecipeCategory category) {
        String key = generateKey(category.get_id());
        return this.categoriesMap.containsKey(key);
    }

    public HashSet<RecipeDetails> getRecipesForCategory(RecipeCategory category) {
        String key = generateKey(category.get_id());
        HashSet<RecipeDetails> recipesSet = (HashSet<RecipeDetails>) this.categoriesMap.get(key);
        if (recipesSet == null) {
            recipesSet = new HashSet<RecipeDetails>();
            putRecipesForCategory(category, recipesSet);
        }
        return recipesSet;

    }

    public void deleteRecipe(RecipeDetails recipe) {
        String key = generateKey(recipe.getCategoryId());
        HashSet<RecipeDetails> recipeSet = (HashSet<RecipeDetails>) this.categoriesMap.get(key);
        recipeSet.remove(recipe);
    }


    public void updateRecipeForCategoryInCache(RecipeDetails recipe, String oldCategoryId, String newCategoryId) {
        recipe.setCategoryId(newCategoryId);
        if (!TextUtils.isEmpty(oldCategoryId)) {
            String oldKey = generateKey(oldCategoryId);
            HashSet<RecipeDetails> recipeSet = (HashSet<RecipeDetails>) this.categoriesMap.get(oldKey);
            if(recipeSet != null && recipeSet.contains(recipe)) {
                recipeSet.remove(recipe);
            }
        }
        if(!TextUtils.isEmpty(newCategoryId)) {
            String newKey = generateKey(newCategoryId);
            HashSet<RecipeDetails> recipeSet = (HashSet<RecipeDetails>) this.categoriesMap.get(newKey);
            if(recipeSet != null && !recipeSet.contains(recipe)) {
                recipeSet.add(recipe);
            }
        }
    }
}
