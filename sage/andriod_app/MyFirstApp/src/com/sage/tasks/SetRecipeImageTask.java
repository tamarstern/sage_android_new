package com.sage.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.application.RecipeImageContainer;
import com.sage.constants.ActivityConstants;
import com.sage.constants.ImageType;
import com.sage.entities.RecipeDetails;
import com.sage.services.PostRecipeImage;

/**
 * Created by tamar.twena on 5/2/2016.
 */
public class SetRecipeImageTask extends AsyncTask<Object, Void, JsonElement> {

    private ImageType type;
    private RecipeDetails details;
    private Bitmap bitmap;

    public SetRecipeImageTask(ImageType type, RecipeDetails details, Bitmap bitmap) {
        this.type = type;
        this.details = details;
        this.bitmap = bitmap;
    }


    @Override
    protected JsonElement doInBackground(Object... params) {
        try {
            String token = (String) params[0];
            String path = (String) params[1];
            PostRecipeImage service = new PostRecipeImage(bitmap, token, details.get_id(), path, this.type);
            return service.sendImage();
        } catch (Exception e) {
            Log.e("failedSavePicture", "fail to save picture", e);
            return null;
        }

    }

    @Override
    protected void onPostExecute(JsonElement result) {
        if (result == null) {
            return;
        }
        JsonObject resultJsonObject = result.getAsJsonObject();
        boolean saveSucceed = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
        if (saveSucceed) {
            String recipeImageId = resultJsonObject.get(ActivityConstants.MESSAGE_ELEMENT_NAME).getAsString();
            if (!TextUtils.isEmpty(recipeImageId)) {
                if (type.equals(ImageType.RECIPE_PICTURE)) {
                    RecipeImageContainer.getInstance().putRecipeMainPicture(this.details, recipeImageId);
                } else if (type.equals(ImageType.IMAGE_RECIPE_PICTURE)) {
                    RecipeImageContainer.getInstance().putRecipeAsPicture(this.details, recipeImageId);
                }
            }
        }
    }
}
