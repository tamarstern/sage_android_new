package com.sage.application;

import com.sage.entities.RecipeCategory;

import java.util.ArrayList;
import java.util.Collections;
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

    private UserCategoriesContainer() {

    }

    public static UserCategoriesContainer getInstance() {
        if(instance == null) {
            synchronized(LOCK) {
                if(instance == null) {
                    instance = new UserCategoriesContainer();
                }
            }
        }
        return instance;
    }

      public void putCategories(ArrayList<RecipeCategory> categories) {
          HashSet<RecipeCategory> categoriesSet = new HashSet<RecipeCategory>(categories);
          this.categoriesMap.put(CATEGORIES_KEY, categoriesSet);
      }

    public ArrayList<RecipeCategory> getCategories() {
        HashSet<RecipeCategory> categories = (HashSet<RecipeCategory>)this.categoriesMap.get(CATEGORIES_KEY);
        return new ArrayList<RecipeCategory>(categories);
    }

    public void putCategory(RecipeCategory category) {
        ArrayList<RecipeCategory> categories = getCategories();
        categories.add(category);
        Collections.sort(categories);
        putCategories(categories);
    }

    public void deleteCategory(RecipeCategory category) {
        HashSet<RecipeCategory> categories =  (HashSet<RecipeCategory>)this.categoriesMap.get(CATEGORIES_KEY);
        categories.remove(category);
        this.categoriesMap.put(CATEGORIES_KEY, categories);
    }

    public boolean categoriesInitialized() {
        return this.categoriesMap.containsKey(CATEGORIES_KEY);
    }
}
