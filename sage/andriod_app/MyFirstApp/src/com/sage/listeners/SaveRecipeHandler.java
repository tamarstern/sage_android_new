package com.sage.listeners;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.myfirstapp.ProgressDialogContainer;
import com.example.myfirstapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.application.MyProfileRecipiesContainer;
import com.sage.application.NewsfeedContainer;
import com.sage.application.RecipesToSaveContainer;
import com.sage.application.UserCategoriesContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.services.BaseSaveRecipeService;
import com.sage.services.SaveNewRecipeService;
import com.sage.services.UpdateExistingRecipeService;
import com.sage.tasks.AddRecipeToCategoryTask;
import com.sage.tasks.CopyRecipeTask;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.CacheUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.NavigationUtils;
import com.sage.utils.ServicesUtils;

public class SaveRecipeHandler {

    private LayoutInflater inflater;
    private ViewGroup container;
    private View savePublishRecipe;
    private RecipeDetails recipeDetails;
    private final Activity context;
    private RecipeCategory category;

    public SaveRecipeHandler(LayoutInflater inflater, ViewGroup container, View savePublishRecipe,
                             RecipeDetails recipeDetails, Activity context, RecipeCategory subCategory) {
        this.inflater = inflater;
        this.container = container;
        this.savePublishRecipe = savePublishRecipe;
        this.recipeDetails = recipeDetails;
        this.context = context;
        this.category = subCategory;
    }

    public void HandleSaveRecipe() {
        if (TextUtils.isEmpty(recipeDetails.getHeader())) {
            createSaveNoNameErrorPopup();
        } else {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

            String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
            if (EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), context)) {

                boolean newRecipe = EntityUtils.isNewRecipe(recipeDetails);

                Object[] params = new Object[]{recipeDetails, token, userName, newRecipe};

                AnalyticsUtils.sendAnalyticsTrackingEvent(context,
                        AnalyticsUtils.SAVE_RECIPE + recipeDetails.getRecipeType());

                new SaveRecipeTask().execute(params);

            } else {
                Object[] params = new Object[]{recipeDetails.get_id(), token, userName};

                new CopyRecipeTask(context,recipeDetails ).execute(params);
            }

        }

    }

    private void createSaveNoNameErrorPopup() {
        View popupView = inflater.inflate(R.layout.save_recipe_without_name_popup, container, false);
        final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
                true);
        popupWindow.showAtLocation(savePublishRecipe, Gravity.CENTER, 0, 0);
        ActivityUtils.InitPopupWindowWithEventHandler(popupWindow, context);

        Button goBackToRecipe = (Button) popupView.findViewById(R.id.go_back_to_recipe);
        goBackToRecipe.setOnClickListener(new OnClickListener() {

            public void onClick(View popupView) {
                popupWindow.dismiss();
            }
        });

        Button exitRecipeWithoutSaving = (Button) popupView.findViewById(R.id.exit_recipe_without_saving);
        exitRecipeWithoutSaving.setOnClickListener(new OnClickListener() {

            public void onClick(View popupView) {
                NavigationUtils.openNewsfeed(context);
            }
        });
    }

    private void updateCacheAndOpenRecipeCategoriesPage() {
        UserCategoriesContainer.getInstance().
                updateRecipeForCategoryInCache(recipeDetails, recipeDetails.getCategoryId(), category.get_id());
        NavigationUtils.openRecipesPerCategoryPage(context, category);
    }


    private class SaveRecipeTask extends AsyncTask<Object, Void, JsonElement> {

        private boolean isNewRecipe;
        private String token;
        private String userName;
        private RecipeDetails recipeDetails;

        private ProgressDialogContainer container;

        public SaveRecipeTask() {

            container = new ProgressDialogContainer(context);
        }

        @Override
        protected JsonElement doInBackground(Object... params) {

            try {
                recipeDetails = (RecipeDetails) params[0];
                token = (String) params[1];
                userName = (String) params[2];
                boolean newRecipe = (boolean) params[3];
                isNewRecipe = newRecipe;
                BaseSaveRecipeService service = null;
                if (newRecipe) {
                    service = new SaveNewRecipeService(context);

                } else {
                    service = new UpdateExistingRecipeService(context);

                }
                return service.saveRecipe(recipeDetails, token, userName);

            } catch (Exception e) {
                ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            container.showProgress();
        }


        @Override
        protected void onPostExecute(JsonElement result) {
            container.dismissProgress();
            if(result == null) {
                handleSaveRecipeOnFailure();
                return;
            }
            JsonObject resultJsonObject = result.getAsJsonObject();
            boolean saveSucceed = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
            if (saveSucceed) {
                Gson gson = new Gson();
                JsonObject dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonObject();
                RecipeDetails recipeDetails = ServicesUtils.createRecipeDetailsFromResponse(gson, dataElement);
                CacheUtils.updateRecipeUserTouchUps(recipeDetails, context);
                if (isNewRecipe) {
                    MyProfileRecipiesContainer.getInstance().addRecipe(recipeDetails);
                    NewsfeedContainer.getInstance().addRecipe(recipeDetails);
                    if (category == null) {
                        ActivityUtils.openCategoriesPage(recipeDetails, context);
                    } else {
                        attachRecipeToCategory(recipeDetails);
                    }
                } else {
                    CacheUtils.updateCacheAfterSaveExistingRecipe(recipeDetails);
                    NavigationUtils.openNewsfeed(context);

                }
                ServicesUtils.saveRecipeImage(recipeDetails, token, context);
            }
        }

        private void handleSaveRecipeOnFailure() {
            recipeDetails.setExceptionOnSave(true);
            if(isNewRecipe) {
                if(category != null) {
                    RecipesToSaveContainer.getInstance().addNewRecipeToSave(recipeDetails, category);
                    updateCacheAndOpenRecipeCategoriesPage();
                } else {
                    ActivityUtils.openCategoriesPage(recipeDetails, context);
                }
            } else {
                RecipesToSaveContainer.getInstance().addExsitingRecipeToSave(this.recipeDetails);
                CacheUtils.updateRecipeUserTouchUps(recipeDetails, context);
                CacheUtils.updateCacheAfterSaveExistingRecipe(recipeDetails);
                NavigationUtils.openNewsfeed(context);
            }
        }


        private void attachRecipeToCategory(RecipeDetails recipeDetails) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String userObjectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
            Object params = new Object[]{category, token, userObjectId, recipeDetails};
            new AddToCategoryTask(category).execute(params);
        }


    }


    private class AddToCategoryTask extends AddRecipeToCategoryTask {

        private RecipeCategory categoryToSave;

        public AddToCategoryTask(RecipeCategory categoryToSave) {
            super(context);
            this.categoryToSave = categoryToSave;
        }

        @Override
        protected void doHandleFailure() {
            recipeDetails.setExceptionOnSave(true);
            RecipesToSaveContainer.getInstance().addNewRecipeToSave(recipeDetails, categoryToSave);
            updateCacheAndOpenRecipeCategoriesPage();
        }

        @Override
        protected void doHandleSuccess() {
            updateCacheAndOpenRecipeCategoriesPage();
        }

    }

}
