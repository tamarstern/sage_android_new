package com.sage.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import com.sage.application.UserCategoriesContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.services.GetRecipiesForCategoryService;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by tamar.twena on 4/20/2016.
 */
public class GetRecipesForCategoriesService extends IntentService {

    public GetRecipesForCategoriesService() {
        super("GetRecipesForCategoriesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("startRecForCat", "start recipes for category backgroundService");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userName)) {
                ArrayList<RecipeCategory> categories = UserCategoriesContainer.getInstance().getCategories();
                if (categories == null || categories.size() == 0) {
                    return;
                }
                for (RecipeCategory category : categories) {
                    initRecipesPerCategory(token, userName, category);
                }
            }
        } catch (Exception e) {
            Log.e("failedGetCRecipes", "failed get category recipes", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

    }

    private void initRecipesPerCategory(String token, String userName, RecipeCategory category) throws Exception {
        GetRecipiesForCategoryService service = new GetRecipiesForCategoryService(token, userName,
                category);

        JsonElement recipes = service.getRecipes();

        if (recipes != null) {
            JsonArray resultJsonObject = recipes.getAsJsonArray();

            Gson gson = new GsonBuilder().create();

            ArrayList<RecipeDetails> recipesToSave = gson.fromJson(resultJsonObject,
                    new TypeToken<ArrayList<RecipeDetails>>() {
                    }.getType());

            HashSet<RecipeDetails> recipesSetToSave = new HashSet<RecipeDetails>(recipesToSave);
            for (RecipeDetails recipe : recipesSetToSave) {
                if(recipe.getRecipeType().equals(RecipeType.LINK) && !TextUtils.isEmpty(recipe.getUrl())) {
                    BackgroundServicesUtils.GetRecipeLinkDetails(token, userName, recipe);
                }
            }
            UserCategoriesContainer.getInstance().putRecipesForCategory(category, recipesSetToSave);
        }
    }
}
