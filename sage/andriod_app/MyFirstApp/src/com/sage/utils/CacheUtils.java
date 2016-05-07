package com.sage.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.sage.application.MyProfileRecipiesContainer;
import com.sage.application.NewsfeedContainer;
import com.sage.application.RecipeImageContainer;
import com.sage.application.UserCategoriesContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.entities.User;

/**
 * Created by tamar.twena on 5/2/2016.
 */
public class CacheUtils {

    public static void updateCacheAfterSaveExistingRecipe(RecipeDetails recipeDetails) {
        UserCategoriesContainer.getInstance().
                updateRecipeForCategoryInCache(recipeDetails, null, recipeDetails.getCategoryId());
        NewsfeedContainer.getInstance().updateRecipeInNewsfeed(recipeDetails);
        MyProfileRecipiesContainer.getInstance().updateRecipeInProfile(recipeDetails);
    }

    public static String getRecipeImagePictureId(RecipeDetails recipeDetails) {
        String recipeAsPictureId = RecipeImageContainer.getInstance().getRecipeAsPictureId(recipeDetails.get_id());
        if (!TextUtils.isEmpty(recipeAsPictureId)) {
            return recipeAsPictureId;
        }
        return recipeDetails.getImageRecipe_pictureId();
    }

    public static void updateRecipeUserTouchUps(RecipeDetails details, Context context) {
        updateRecipeUserTouchUps(details, context, null);
    }


    public static void updateRecipeUserTouchUps(RecipeDetails details, Context context, User user) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String userDisplayName = sharedPref.getString(ActivityConstants.USER_DISPLAY_NAME, null);
        details.setUserDisplayName(userDisplayName);

        String userObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
        details.setUserObjectId(userObjectId);

        String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);
        details.setUserId(userName);


        if (!details.getRecipeType().equals(RecipeType.LINK) && TextUtils.isEmpty(details.getOwnerObjectId())) {
            if (user == null) {
                if (TextUtils.isEmpty(details.getOwnerDisplayName())) {
                    details.setOwnerDisplayName(userDisplayName);
                }
                if (TextUtils.isEmpty(details.getOwnerObjectId())) {
                    details.setOwnerObjectId(userObjectId);
                }
                if (TextUtils.isEmpty(details.getOwnerUserName())) {
                    details.setOwnerUserName(userName);
                }
            } else {
                if (TextUtils.isEmpty(details.getOwnerDisplayName())) {
                    details.setOwnerDisplayName(user.getUserDisplayName());
                }
                if (TextUtils.isEmpty(details.getOwnerObjectId())) {
                    details.setOwnerObjectId(user.get_id());
                }
                if (TextUtils.isEmpty(details.getOwnerUserName())) {
                    details.setOwnerUserName(user.getUserDisplayName());
                }
            }
        }
    }

    public static String getRecipeMainPictureId(RecipeDetails recipeDetails) {
        String recipePictureId = RecipeImageContainer.getInstance().getRecipeMainPictureId(recipeDetails.get_id());
        if (!TextUtils.isEmpty(recipePictureId)) {
            return recipePictureId;
        }
        return recipeDetails.getPictureId();
    }

}
