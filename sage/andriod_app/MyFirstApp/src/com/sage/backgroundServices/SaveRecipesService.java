package com.sage.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
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
                saveAttachToCategory(token, userName);
            }
        } catch (Exception e) {
            Log.e("failedSaveRecipes", "failed save recipes", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }


    private void saveAttachToCategory(String token, String userName) throws Exception {
        HashSet<RecipeCategoryContainer> attachToCategoryContainer = RecipesToSaveContainer.getInstance().getAttachCategoryToSave();
        HashSet<RecipeCategoryContainer> processedContainers = new HashSet<RecipeCategoryContainer>();
        if(attachToCategoryContainer != null && attachToCategoryContainer.size() > 0) {
            for(RecipeCategoryContainer recipeToSave : attachToCategoryContainer) {
                RecipeDetails recipeDetails = recipeToSave.getDetails();
                boolean saveSucceed = executeSaveCategoryService(token, userName, recipeToSave, recipeDetails);
                if (saveSucceed) {
                    Log.i("saveRecipes", "recipe was attached to category : " + recipeDetails.get_id());
                    recipeDetails.setExceptionOnSave(false);
                    CacheUtils.updateCacheAfterSaveExistingRecipe(recipeDetails);
                    processedContainers.add(recipeToSave);
                }
            }
            for(RecipeCategoryContainer details  : processedContainers) {
                RecipesToSaveContainer.getInstance().removeAttachCategoryFromTheList(details);
            }
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
                        saveRecipeImage(token, recipeToSave.getImage(), recipeToSave.getRecipeAsPictureImage(), recipeToSave);
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
            for(RecipeCategoryContainer containerToSave : recipesToSave) {
                RecipeDetails existingMemoryDetails = containerToSave.getDetails();
                String oldId = existingMemoryDetails.get_id();
                existingMemoryDetails.set_id(null);
                JsonElement jsonElement = saveRecipe(token, userName, existingMemoryDetails);
                JsonObject resultJsonObject = jsonElement.getAsJsonObject();
                boolean requestSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
                if (requestSuccess) {
                    Log.i("saveRecipes", "recipe saved : " + existingMemoryDetails);
                    RecipeDetails detailsFromResponse = createRecipeDetailsFromResponse(resultJsonObject);
                    saveRecipeImage(token, containerToSave.getMainRecipePicture(), containerToSave.getRecipeImagePicture(), detailsFromResponse);
                    //TODO - handle attach recipe to new category
                    boolean saveSucceed = executeSaveCategoryService(token, userName, containerToSave, detailsFromResponse);
                    if (saveSucceed) {
                        savedRecipes.add(containerToSave);
                        String newCategoryId = containerToSave.getCategory().get_id();
                        syncServerAndMemoryDetails(existingMemoryDetails, oldId, detailsFromResponse, newCategoryId);
                        updateCache(existingMemoryDetails, detailsFromResponse, newCategoryId);

                    }
                }
            }
            for(RecipeCategoryContainer details  : savedRecipes) {
                RecipesToSaveContainer.getInstance().removeNewRecipeFromTheList(details);
            }
        }
    }

    private void saveRecipeImage(String token,Bitmap mainPicture, Bitmap recipeImagePicture, RecipeDetails detailsFromResponse) {
        ServicesUtils.saveRecipeMainPicture(detailsFromResponse.get_id(),
                mainPicture, getApplicationContext(), token);
        ServicesUtils.saveRecipeImagePicture(detailsFromResponse.get_id(),
                recipeImagePicture, getApplicationContext(), token);
    }


    private void updateCache(RecipeDetails existingDetails, RecipeDetails detailsFromResponse, String newCategoryId) {
        UserCategoriesContainer.getInstance().deleteRecipe(existingDetails);
        UserCategoriesContainer.getInstance().updateRecipeForCategoryInCache(detailsFromResponse,
                detailsFromResponse.getCategoryId(), newCategoryId);
    }

    private void syncServerAndMemoryDetails(RecipeDetails existingDetails, String oldId, RecipeDetails detailsFromResponse, String newCategoryId) {
        existingDetails.setCategoryId(newCategoryId);
        existingDetails.set_id(oldId);
        detailsFromResponse.setCategoryId(newCategoryId);
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
        BaseSaveRecipeService service = new SaveNewRecipeService(null);
        return service.saveRecipe(details, token, userName);
    }

}
