package com.sage.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.application.RecipeCategoryContainer;
import com.sage.application.RecipesToSaveContainer;
import com.sage.application.UserCategoriesContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.AddRecipeToCategoryService;
import com.sage.services.BaseSaveRecipeService;
import com.sage.services.SaveNewRecipeService;
import com.sage.services.UpdateExistingRecipeService;
import com.sage.utils.CacheUtils;
import com.sage.utils.EntityUtils;
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
            String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
            if(!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userName)) {
                saveExistingRecipes(token, userName);
                saveNewRecipes(token, userName);
            }
        } catch (Exception e) {
            Log.e("failedSaveRecipes", "failed save recipes", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void saveExistingRecipes(String token, String userName) throws Exception {
        HashSet<RecipeDetails> recipesToSave = RecipesToSaveContainer.getInstance().getExistingRecipesToSave();
        HashSet<RecipeDetails> savedRecipes = new HashSet<RecipeDetails>();
        if(recipesToSave != null && recipesToSave.size() > 0) {
            for(RecipeDetails recipeToSave : recipesToSave) {
                UpdateExistingRecipeService service = new UpdateExistingRecipeService(null);
                JsonElement jsonElement = service.saveRecipe(recipeToSave, token, userName);
                JsonObject resultJsonObject = jsonElement.getAsJsonObject();
                boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
                    if (requestSuccess) {
                        RecipeDetails recipeDetails = createRecipeDetailsFromResponse(resultJsonObject);
                        Log.i("saveRecipes", "recipe saved : " + recipeDetails.get_id());
                        CacheUtils.updateCacheAfterSaveExistingRecipe(recipeDetails);
                        savedRecipes.add(recipeToSave);
                        ServicesUtils.saveRecipeImage(recipeToSave, token,getApplicationContext());
                    }
                }
            for(RecipeDetails details  : savedRecipes) {
                RecipesToSaveContainer.getInstance().removeRecipeFromList(details);
            }
        }
    }


    private void saveNewRecipes(String token, String userName) throws Exception {
        HashSet<RecipeCategoryContainer> recipesToSave = RecipesToSaveContainer.getInstance().getNewRecipesToSave();
        HashSet<RecipeCategoryContainer> savedRecipes = new HashSet<RecipeCategoryContainer>();
        if(recipesToSave != null && recipesToSave.size() > 0) {
            for(RecipeCategoryContainer recipeToSave : recipesToSave) {
                RecipeDetails details = recipeToSave.getDetails();
                JsonElement jsonElement = saveRecipe(token, userName, details);
                JsonObject resultJsonObject = jsonElement.getAsJsonObject();
                boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
                if (requestSuccess) {
                    Log.i("saveRecipes", "recipe saved : " + details);

                    RecipeDetails recipeDetails = createRecipeDetailsFromResponse(resultJsonObject);
                    recipeToSave.setDetails(recipeDetails);
                    //TODO - handle attach recipe to new category
                    boolean saveSucceed = executeSaveCategoryService(token, userName, recipeToSave, recipeDetails);
                    if (saveSucceed) {
                        savedRecipes.add(recipeToSave);
                        String newCategoryId = recipeToSave.getCategory().get_id();
                        details.setCategoryId(newCategoryId);
                        recipeDetails.setCategoryId(newCategoryId);
                        UserCategoriesContainer.getInstance().deleteRecipe(details);
                        UserCategoriesContainer.getInstance().updateRecipeForCategoryInCache(recipeDetails,
                                recipeDetails.getCategoryId(), newCategoryId);
                        ServicesUtils.saveRecipeImage(details, token, getApplicationContext());
                    }
                }
            }
            for(RecipeCategoryContainer details  : savedRecipes) {
                RecipesToSaveContainer.getInstance().removeNewRecipeFromTheList(details);
            }
        }
    }

    private boolean executeSaveCategoryService(String token, String userName, RecipeCategoryContainer recipeToSave, RecipeDetails recipeDetails) throws Exception {
        AddRecipeToCategoryService addToCategoryService = new AddRecipeToCategoryService(recipeDetails,
                recipeToSave.getCategory(), token, userName, null);
        JsonElement categoryServiceResult = addToCategoryService.addRecipeToSubCategory();
        JsonObject categoryResultJsonObject = categoryServiceResult.getAsJsonObject();
        return categoryResultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
    }

    private RecipeDetails createRecipeDetailsFromResponse(JsonObject resultJsonObject) {
        Gson gson = new Gson();
        JsonObject dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonObject();
        return ServicesUtils.createRecipeDetailsFromResponse(gson, dataElement);
    }

    private JsonElement saveRecipe(String token, String userName, RecipeDetails details) throws Exception {
        BaseSaveRecipeService service = null;
        service = createService(details);
        return service.saveRecipe(details, token, userName);
    }

    @NonNull
    private BaseSaveRecipeService createService(RecipeDetails details) {
        BaseSaveRecipeService service;
        if (EntityUtils.isNewRecipe(details)) {
            service = new SaveNewRecipeService(null);

        } else {
            service = new UpdateExistingRecipeService(null);

        }
        return service;
    }
}
