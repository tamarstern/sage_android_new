package com.sage.application;

import android.support.annotation.NonNull;
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

    private ConcurrentHashMap<String, Boolean> hasRecipesForCategory = new ConcurrentHashMap<String, Boolean>();

    private static volatile UserCategoriesContainer instance;

    private static final Object LOCK = new Object();

    public static String CATEGORIES_KEY = "categoriesKey";

    public static String CATEGORY_ID = "categoryId_";

    private boolean categoriesInitialized;

    private UserCategoriesContainer() {

    }

    public void clearAll() {
        categoriesMap.clear();
        categoriesInitialized = false;
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


    public void setCategoriesInitialized(boolean categoriesInitialized) {
        this.categoriesInitialized = categoriesInitialized;
    }

    public void putCategories(HashSet<RecipeCategory> categories) {
        HashSet<RecipeCategory> categoriesSet = new HashSet<RecipeCategory>(categories);
        this.categoriesMap.put(CATEGORIES_KEY, categoriesSet);
    }

    public ArrayList<RecipeCategory> getCategories() {
        HashSet<RecipeCategory> categories = getRecipeCategoriesAndInit();
        return new ArrayList<RecipeCategory>(categories);
    }

    @NonNull
    private HashSet<RecipeCategory> getRecipeCategoriesAndInit() {
        HashSet<RecipeCategory> categories = (HashSet<RecipeCategory>) this.categoriesMap.get(CATEGORIES_KEY);
        if (categories == null) {
            categories = new HashSet<RecipeCategory>();
            putCategories(categories);
        }
        return categories;
    }

    public void putCategory(RecipeCategory category) {
        HashSet<RecipeCategory> categories = getRecipeCategoriesAndInit();
        boolean categoryExist = categories.contains(category);
        if (categoryExist) {
            categories.remove(category);
        }
        categories.add(category);
        putCategories(categories);
        if (!categoryExist) {
            putRecipesForCategory(category, new HashSet<RecipeDetails>());
        }
    }

    public void deleteCategory(RecipeCategory category) {
        HashSet<RecipeCategory> categories = (HashSet<RecipeCategory>) this.categoriesMap.get(CATEGORIES_KEY);
        categories.remove(category);
        this.categoriesMap.put(CATEGORIES_KEY, categories);
    }

    public boolean categoriesInitialized() {
        return categoriesInitialized;
    }

    public void putRecipesForCategory(RecipeCategory category, HashSet<RecipeDetails> recipesSet) {
        String key = generateKey(category.get_id());
        this.categoriesMap.put(key, recipesSet);
        this.hasRecipesForCategory.put(key, true);
    }

    private String generateKey(String categoryId) {
        return CATEGORY_ID + categoryId;
    }

    public boolean hasRecipesForCategory(RecipeCategory category) {
        String key = generateKey(category.get_id());
        Boolean hasRecipes = this.hasRecipesForCategory.containsKey(key);
        if(hasRecipes == null || !hasRecipes) {
            return false;
        }
        return true;
    }

    public HashSet<RecipeDetails> getRecipesForCategory(RecipeCategory category) {
        String key = generateKey(category.get_id());
        HashSet<RecipeDetails> recipesSet = (HashSet<RecipeDetails>) this.categoriesMap.get(key);
        if (recipesSet == null) {
            recipesSet = new HashSet<RecipeDetails>();
            this.categoriesMap.put(key, recipesSet);
        }
        return recipesSet;

    }

    public void deleteRecipe(RecipeDetails recipe) {
        String key = generateKey(recipe.getCategoryId());
        HashSet<RecipeDetails> recipeSet = (HashSet<RecipeDetails>) this.categoriesMap.get(key);
        if (recipeSet != null) {
            recipeSet.remove(recipe);
        }
    }


    public void updateRecipeForCategoryInCache(RecipeDetails recipe, String oldCategoryId, String newCategoryId) {
        recipe.setCategoryId(newCategoryId);
        if (!TextUtils.isEmpty(oldCategoryId)) {
            String oldKey = generateKey(oldCategoryId);
            HashSet<RecipeDetails> recipeSet = (HashSet<RecipeDetails>) this.categoriesMap.get(oldKey);
            if (recipeSet != null && recipeSet.contains(recipe)) {
                recipeSet.remove(recipe);
            }
        }
        if (!TextUtils.isEmpty(newCategoryId)) {
            String newKey = generateKey(newCategoryId);
            HashSet<RecipeDetails> recipeSet = (HashSet<RecipeDetails>) this.categoriesMap.get(newKey);
            if(recipeSet == null) {
                recipeSet = new HashSet<RecipeDetails>();
                this.categoriesMap.put(newKey, recipeSet);
            }
            if (recipeSet != null) {
                if (recipeSet.contains(recipe)) {
                    recipeSet.remove(recipe);
                }
                recipeSet.add(recipe);
            }
        }
    }
}

