package com.sage.backgroundServices;

import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.GetRecipeUrlDetailsService;
import com.sage.utils.RecipeOwnerContext;

/**
 * Created by tamar.twena on 4/23/2016.
 */
public class BackgroundServicesUtils {


    public static void GetRecipeLinkDetails(String token, String userName, RecipeDetails recipe) {
        try {
            GetRecipeUrlDetailsService getUrlService = new GetRecipeUrlDetailsService(recipe.getUrl(), token, userName);
            JsonElement result = getUrlService.getUrlDetails();
            JsonObject linkResultJsonObject = result.getAsJsonObject();
            boolean saveSucceed = linkResultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
            if (saveSucceed) {
                JsonElement urlTitleJson = linkResultJsonObject.get(ActivityConstants.URL_TITLE_ELEMENT_NAME);
                if (urlTitleJson != null) {
                    String urlTitle = urlTitleJson.getAsString();
                    recipe.setLinkTitle(urlTitle);
                }
                String urlImage;
                JsonElement urlImageJson = linkResultJsonObject.get(ActivityConstants.URL_IMAGE_ELEMENT_NAME);
                if (urlImageJson != null) {
                    urlImage = urlImageJson.getAsString();
                    recipe.setLinkImageUrl(urlImage);
                }
                String siteName;
                JsonElement urlSiteJson = linkResultJsonObject.get(ActivityConstants.URL_SITE_ELEMENT_NAME);
                if (urlSiteJson != null) {
                    siteName = urlSiteJson.getAsString();
                    RecipeOwnerContext.setOwnerForUrl(recipe.getUrl(), siteName);
                    recipe.setLinkSiteName(siteName);
                }
                recipe.setLinkDataInitialized(true);

            }
        } catch (Exception e) {
            Log.e("failedFetchLink", "failed fetch link details", e);
        }
    }

}
