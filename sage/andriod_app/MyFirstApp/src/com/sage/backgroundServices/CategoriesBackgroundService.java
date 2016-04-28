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
import com.sage.services.GetCategoriesForUserService;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by tamar.twena on 4/17/2016.
 */
public class CategoriesBackgroundService extends IntentService {

    public static String CATEGORIES_KEY = "categoriesKey";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public CategoriesBackgroundService(String name) {
        super(name);
    }

    public CategoriesBackgroundService() {
        super("CategoriesBackgroundService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("startCategoryBackground", "start categorybackgroundService");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userName)) {
                fetchCategoriesInBackground(token, userName);
            }
        } catch (Exception e) {
            Log.e("failedFetchCategories", "failed to get categories", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }


    }

    private void fetchCategoriesInBackground(String token, String userName) throws Exception {
        GetCategoriesForUserService service = new GetCategoriesForUserService(token, userName);
        JsonElement categories;
        categories = service.getCategories();
        if (categories != null) {
            JsonArray resultJsonObject = categories.getAsJsonArray();
            Gson gson = new GsonBuilder().create();
            ArrayList<RecipeCategory> categoriesToSave = gson.fromJson(resultJsonObject, new TypeToken<ArrayList<RecipeCategory>>() {
            }.getType());

            if (categoriesToSave != null) {
                UserCategoriesContainer.getInstance().putCategories(new HashSet<RecipeCategory>(categoriesToSave));
                UserCategoriesContainer.getInstance().setCategoriesInitialized(true);
                initRecipesForCategories();
            }
        }
    }

    private void initRecipesForCategories() {
        Intent fetchCategoriesRecipes = new Intent(this, GetRecipesForCategoriesService.class);
        startService(fetchCategoriesRecipes);
    }
}
