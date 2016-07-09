package com.sage.backgroundServices;

import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.sage.application.RecipeUrlDataContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.services.GetNewsFeedRecipesForUser;
import com.sage.services.GetPublishedRecipesForUser;
import com.sage.services.GetRecipeUrlDetailsService;
import com.sage.utils.RecipeOwnerContext;

import java.util.ArrayList;

/**
 * Created by tamar.twena on 4/23/2016.
 */
public class BackgroundServicesUtils {


    public static ArrayList<RecipeDetails> getNewsfeedRecipiesForPage(String token, String userName, int i) throws Exception {
        GetNewsFeedRecipesForUser service = new GetNewsFeedRecipesForUser(token, userName,
                i);
        JsonElement recipies = service.getRecipies();

        JsonObject recipiesAsJsonObject = recipies.getAsJsonObject();

        boolean recipiesFound = recipiesAsJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

        if (recipiesFound) {
            JsonElement dataElement = recipiesAsJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME);
            JsonArray resultDataObject = dataElement.getAsJsonArray();
            Gson gson = new GsonBuilder().create();
            ArrayList<RecipeDetails> details = gson.fromJson(resultDataObject, new TypeToken<ArrayList<RecipeDetails>>() {
            }.getType());
            for (RecipeDetails recipe : details) {
                if(recipe.getRecipeType().equals(RecipeType.LINK) && !TextUtils.isEmpty(recipe.getUrl())) {
                    initRecipeUrlData(token, userName, recipe);
                }
            }
            return details;
        }
        return new ArrayList<RecipeDetails>();
    }

    public static void initRecipeUrlData(String token, String userName, RecipeDetails recipe) {
        RecipeUrlDataContainer instance = RecipeUrlDataContainer.getInstance();
        boolean hasDataForRecipe = instance.hasDataForRecipe(recipe);
        if(recipe.isLinkDataInitialized()) {
            return;
        }
        if(hasDataForRecipe) {
            recipe.setLinkSiteName(instance.getSiteName(recipe));
            recipe.setLinkTitle(instance.getTitle(recipe));
            recipe.setLinkImageUrl(instance.getLinkImageUrl(recipe));
            recipe.setLinkDataInitialized(true);
        } else {
            BackgroundServicesUtils.GetRecipeLinkDetails(token, userName, recipe);
        }
    }

    public static ArrayList<RecipeDetails> getProfilePageRecipiesForPage(String token, String loggedInUserName, String userName, int i) throws Exception {
        GetPublishedRecipesForUser service = new GetPublishedRecipesForUser(token, loggedInUserName, userName,
                i);
        JsonElement recipies = service.getRecipies();
        JsonObject recipiesAsJsonObject = recipies.getAsJsonObject();
        boolean recipiesFound = recipiesAsJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

        if (recipiesFound) {
            JsonElement dataElement = recipiesAsJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME);
            JsonArray resultDataObject = dataElement.getAsJsonArray();
            Gson gson = new GsonBuilder().create();
            ArrayList<RecipeDetails> details = gson.fromJson(resultDataObject, new TypeToken<ArrayList<RecipeDetails>>() {
            }.getType());
            for (RecipeDetails recipe : details) {
                if (recipe.getRecipeType().equals(RecipeType.LINK) && !TextUtils.isEmpty(recipe.getUrl())) {
                    initRecipeUrlData(token, userName, recipe);
                }
            }
            return details;
        }
        return null;
    }


    public static void GetRecipeLinkDetails(String token, String userName, RecipeDetails recipe) {
        try {
            GetRecipeUrlDetailsService getUrlService = new GetRecipeUrlDetailsService(recipe.getUrl(), token, userName);
            JsonElement result = getUrlService.getUrlDetails();
            JsonObject linkResultJsonObject = result.getAsJsonObject();
            boolean saveSucceed = linkResultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
            if (saveSucceed) {
                JsonElement urlTitleJson = linkResultJsonObject.get(ActivityConstants.URL_TITLE_ELEMENT_NAME);
                RecipeUrlDataContainer instance = RecipeUrlDataContainer.getInstance();
                if (urlTitleJson != null) {
                    String urlTitle = urlTitleJson.getAsString();
                    recipe.setLinkTitle(urlTitle);
                    instance.setTtileForLinkDetails(recipe, urlTitle);
                }
                String urlImage;
                JsonElement urlImageJson = linkResultJsonObject.get(ActivityConstants.URL_IMAGE_ELEMENT_NAME);
                if (urlImageJson != null) {
                    urlImage = urlImageJson.getAsString();
                    recipe.setLinkImageUrl(urlImage);
                    instance.setLinkImageUrl(recipe, urlImage);
                }
                String siteName;
                JsonElement urlSiteJson = linkResultJsonObject.get(ActivityConstants.URL_SITE_ELEMENT_NAME);
                if (urlSiteJson != null) {
                    siteName = urlSiteJson.getAsString();
                    RecipeOwnerContext.setOwnerForUrl(recipe.getUrl(), siteName);
                    recipe.setLinkSiteName(siteName);
                    instance.setSiteNameForLinkDetails(recipe, siteName);
                }
                recipe.setLinkDataInitialized(true);

            }
        } catch (Exception e) {
            Log.e("failedFetchLink", "failed fetch link details", e);
        }
    }

}
