package com.sage.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.application.RecipesToDeleteContainer;
import com.sage.application.RecipesToSaveContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.UpdateExistingRecipeService;
import com.sage.utils.CacheUtils;
import com.sage.utils.ServicesUtils;

import java.util.HashSet;

/**
 * Created by tamar.twena on 4/20/2016.
 */
public class SaveRecipesService extends IntentService {


    public SaveRecipesService() {
        super("SaveRecipesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("saveRecipes", "start save recipes backgroundService");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);
            if(!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userName)) {
                saveRecipes(token, userName);
            }


        } catch (Exception e) {
            Log.e("failedDeleteRecipes", "failed delete recipes", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

    }

    private void saveRecipes(String token, String userName) throws Exception {
        HashSet<RecipeDetails> recipesToSave = RecipesToSaveContainer.getInstance().getExistingRecipesToSave();
        HashSet<RecipeDetails> savedRecipes = new HashSet<RecipeDetails>();
        if(recipesToSave != null && recipesToSave.size() > 0) {
            for(RecipeDetails recipeToSave : recipesToSave) {
                UpdateExistingRecipeService service = new UpdateExistingRecipeService(null);
                JsonElement jsonElement = service.saveRecipe(recipeToSave, token, userName);
                JsonObject resultJsonObject = jsonElement.getAsJsonObject();
                boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
                    if (requestSuccess) {
                        Log.i("saveRecipes", "recipe saved : " + recipeToSave.get_id());
                        savedRecipes.add(recipeToSave);
                        CacheUtils.updateRecipeUserTouchUps(recipeToSave, getApplicationContext());
                        CacheUtils.updateCacheAfterSaveExistingRecipe(recipeToSave);
                        ServicesUtils.saveRecipeImage(recipeToSave, token,getApplicationContext());
                    }
                }
            for(RecipeDetails details  : savedRecipes) {
                RecipesToDeleteContainer.getInstance().removeRecipeFromList(details);
            }
        }
    }
}
