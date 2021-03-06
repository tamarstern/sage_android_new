package com.sage.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.PopupWindow;

import com.sage.activities.ActivityCategoriesPage;
import com.sage.activities.DisplayLinkWebPageActivity;
import com.sage.activities.LinkRecipePageActivity;
import com.sage.activities.PictureRecipePageActivity;
import com.sage.activities.ResetPasswordActivity;
import com.sage.activities.TextReciptPageActivity;
import com.sage.activity.interfaces.IExitWithoutSaveListener;
import com.sage.application.UserFollowingContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.listeners.ExitRecipeWithoutSavingPopupHandler;
import com.sage.tasks.FollowingListBackgroundInitializer;

public class ActivityUtils {

    public static void forceInitFollowingList(Activity activity) {
        if(!UserFollowingContainer.getInstance().followingInitialized()) {
            FollowingListBackgroundInitializer initializer = new FollowingListBackgroundInitializer();
            initializer.fetchUsers(activity);
        }

    }

    public static void openCategoriesPage(RecipeDetails recipeDetails, Context context) {
        Intent intent = new Intent(context, ActivityCategoriesPage.class)
                .putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, recipeDetails);
        context.startActivity(intent);
    }

    public static void startResetPasswordActivity(Activity context, String username) {

        Intent intent = new Intent(context, ResetPasswordActivity.class).putExtra(ActivityConstants.USER_NAME,
                username);
        context.startActivity(intent);
    }


    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    public static void openDisplayLinkActivity(Activity activity, RecipeDetails recipeDetails,
                                               boolean isLoggedInUserRecipe) {
        Intent intent = new Intent(activity, DisplayLinkWebPageActivity.class)
                .putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, recipeDetails);

        intent.putExtra(EntityDataTransferConstants.LOGGED_IN_USER_RECIPE, isLoggedInUserRecipe);

        activity.startActivity(intent);
    }

    public static void openRecipeActivity(RecipeDetails details, Activity context) {
        Intent intent = null;
        intent = getRecipeIntent(details, context);
        context.startActivity(intent);
    }


    public static Intent getRecipeIntent(RecipeDetails details, Context context) {
        Intent intent;
        switch (details.getRecipeType()) {
            case PICTURE:
                intent = new Intent(context, PictureRecipePageActivity.class)
                        .putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, details);

                if (EntityUtils.isLoggedInUserRecipe(details.getUserId(), context)) {
                    intent.putExtra(EntityDataTransferConstants.IN_MY_RECIPE_PAGE, true);
                }
                break;
            case LINK:
                intent = new Intent(context, LinkRecipePageActivity.class)
                        .putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, details);

                if (EntityUtils.isLoggedInUserRecipe(details.getUserId(), context)) {
                    intent.putExtra(EntityDataTransferConstants.IN_MY_RECIPE_PAGE, true);
                }
                break;
            default:
                intent = new Intent(context, TextReciptPageActivity.class)
                        .putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, details);
                if (EntityUtils.isLoggedInUserRecipe(details.getUserId(), context)) {
                    intent.putExtra(EntityDataTransferConstants.IN_MY_RECIPE_PAGE, true);
                }
                break;
        }
        return intent;
    }

    public static void InitPopupWindowWithEventHandler(final PopupWindow popupWindow, Context context) {
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        popupWindow.getContentView().setFocusableInTouchMode(true);
        View contentView = popupWindow.getContentView().getRootView();

        contentView.setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    popupWindow.dismiss();
                    return true;
                } else {
                    return false;
                }

            }
        });

        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams p = (WindowManager.LayoutParams) contentView.getLayoutParams();
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        p.dimAmount = 0.3f;
        wm.updateViewLayout(contentView, p);

    }

    public static void HandleConnectionUnsuccessfullToServer(Exception excption) {

        Log.e(Context.CONNECTIVITY_SERVICE, "Unable connect to server", excption);
    }

    public static Object[] generateServiceParamObject(Context context, String firstParam) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

        String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);

        Object[] params = new Object[]{firstParam, token, userName};
        return params;
    }

    public static Object[] generateServiceParamObjectWithUserId(Context context, String firstParam) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);

        String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

        Object[] params = new Object[]{firstParam, token, userName};
        return params;
    }




    public static void handleExitWithoutSavingPopup(Activity context) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ViewGroup viewGroup = (ViewGroup) context.getWindow().getDecorView();
        ExitRecipeWithoutSavingPopupHandler handler = new ExitRecipeWithoutSavingPopupHandler(inflater, viewGroup,
                viewGroup, context);
        if (context instanceof IExitWithoutSaveListener) {
            IExitWithoutSaveListener listener = (IExitWithoutSaveListener) context;
            handler.registerListener(listener);
        }
        handler.handleExitWithoutSave();
    }



}
