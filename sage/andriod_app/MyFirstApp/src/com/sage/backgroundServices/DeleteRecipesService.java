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
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.DeleteRecipeService;

import java.util.HashSet;

/**
 * Created by tamar.twena on 4/20/2016.
 */
public class DeleteRecipesService extends IntentService {


    public DeleteRecipesService() {
        super("DeleteRecipesService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("deleteRecipes", "start delete recipes backgroundService");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
            if(!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userName)) {
                deleteRecipes(token, userName);
            }


        } catch (Exception e) {
            Log.e("failedDeleteRecipes", "failed delete recipes", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

    }

    private void deleteRecipes(String token, String userName) throws Exception {
        HashSet<RecipeDetails> recipesToDelete = RecipesToDeleteContainer.getInstance().getRecipesToDelete();
        HashSet<RecipeDetails> deletedRecipes = new HashSet<RecipeDetails>();
        if(recipesToDelete != null && recipesToDelete.size() > 0) {
            for(RecipeDetails recipeToDelete : recipesToDelete) {
                DeleteRecipeService service = new DeleteRecipeService(recipeToDelete, token, userName, null);
                JsonElement jsonElement = service.deleteRecipe();
                JsonObject resultJsonObject = jsonElement.getAsJsonObject();
                boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
                    if (requestSuccess) {
                        Log.i("deleteRecipes", "recipe deleted : " + recipeToDelete.get_id());
                        deletedRecipes.add(recipeToDelete);
                    }
                }
            for(RecipeDetails details  : deletedRecipes) {
                RecipesToDeleteContainer.getInstance().removeRecipeFromList(details);
            }
        }
    }
}
