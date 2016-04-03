package com.sage.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.example.myfirstapp.ActivityCategoriesPage;
import com.example.myfirstapp.NewsfeedActivity;
import com.example.myfirstapp.R;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.listeners.AddRecipePopupHandler;
import com.sage.listeners.ProfilePageHandler;
import com.sage.listeners.SettingsPopupHandler;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.EntityUtils;

public class ToolbarFragment extends Fragment {

	private AddRecipePopupHandler handler; 
	
	private SettingsPopupHandler settingsHandler;
	
	private PagePressed pagePressed;

	public enum PagePressed {
		HOME, CATEGORIES, PROFILE, ADD_NEW,SETTINGS
	}

	@Override
	public View onCreateView( LayoutInflater inflater,  ViewGroup container, Bundle savedInstanceState) {

		 View toolbar = inflater.inflate(R.layout.buttom_toolbar_fragment, container, false);

		ImageButton settingsButton = (ImageButton) toolbar.findViewById(R.id.settings_buttom_toolbar);

		ImageButton homeButton = (ImageButton) toolbar.findViewById(R.id.home_buttom_toolbar);

		final Activity activity = getActivity();
		Intent intent = activity.getIntent();

		final boolean inRecipePage = intent.getSerializableExtra(EntityDataTransferConstants.IN_MY_RECIPE_PAGE) != null;

		final RecipeDetails details = (RecipeDetails) intent
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		homeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (shouldHandleExistWithoutSaving(inRecipePage, details, activity)) {
					setPagePressed(PagePressed.HOME);
					ActivityUtils.handleExitWithoutSavingPopup(activity);

				} else {
					openNewsfeed(activity);
				}
			}

		});
		settingsHandler = new SettingsPopupHandler(inflater,container, toolbar, activity);

		settingsButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (shouldHandleExistWithoutSaving(inRecipePage, details, activity)) {
					setPagePressed(PagePressed.SETTINGS);
					ActivityUtils.handleExitWithoutSavingPopup(activity);
				} else {
					settingsHandler.handle();
				}
			}

		});


		ImageButton newReceipeButton = (ImageButton) toolbar.findViewById(R.id.new_receipt_buttom_toolbar);
		handler = new AddRecipePopupHandler(inflater, toolbar, container, activity,
				null);
		
		newReceipeButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AnalyticsUtils.sendAnalyticsTrackingEvent(activity, AnalyticsUtils.CLICK_ADD_NEW_RECIPE);
				if (shouldHandleExistWithoutSaving(inRecipePage, details, activity)) {
					setPagePressed(PagePressed.ADD_NEW);
					ActivityUtils.handleExitWithoutSavingPopup(activity);
				} else {				
					handler.handleAddRecipe();
				}
			}
		});

		ImageButton recipeBoxButton = (ImageButton) toolbar.findViewById(R.id.receipt_box_buttom_toolbar);

		recipeBoxButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (shouldHandleExistWithoutSaving(inRecipePage, details, activity)) {
					setPagePressed(PagePressed.CATEGORIES);
					ActivityUtils.handleExitWithoutSavingPopup(activity);
				} else {
					openCategoriesPage(activity);
				}

			}

		});

		initUserProfileButton(toolbar, inRecipePage, activity, details);
		return toolbar;
	}

	private boolean shouldHandleExistWithoutSaving(final boolean inRecipePage, final RecipeDetails details, Context context) {
		return inRecipePage && (EntityUtils.isRecipeChanges(context, details));
	}

	private void openCategoriesPage(final Context applicationContext) {
		Intent intent = new Intent(applicationContext, ActivityCategoriesPage.class);
		startActivity(intent);
	}

	private void openProfilePage(final Activity applicationContext) {
		ProfilePageHandler handler = new ProfilePageHandler(applicationContext,null, null, null, true );
		handler.HandleOpenProfilePage();
	}

	private void openNewsfeed(final Context applicationContext) {
		Intent intent = new Intent(applicationContext, NewsfeedActivity.class);
		startActivity(intent);
	}

	private void initUserProfileButton(View toolbar, final boolean inRecipePage, final Activity activity,
			final RecipeDetails details) {
		ImageButton userProfileButton = (ImageButton) toolbar.findViewById(R.id.my_profile_buttom_toolbar);

		userProfileButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (shouldHandleExistWithoutSaving(inRecipePage, details, activity)) {
					setPagePressed(PagePressed.PROFILE);
					ActivityUtils.handleExitWithoutSavingPopup(activity);
				} else {
					openProfilePage(activity);
				}

			}

		});
	}

	public PagePressed getPagePressed() {
		return pagePressed;
	}

	private void setPagePressed(PagePressed pagePressed) {
		this.pagePressed = pagePressed;
	}

	public void handlePagePressed() {
		if (pagePressed != null) {
			switch (pagePressed) {
			case CATEGORIES:
				openCategoriesPage(getActivity());
				break;
			case PROFILE:
				openProfilePage(getActivity());
				break;
			case HOME:
				openNewsfeed(getActivity());
				break;
			case ADD_NEW:
				handler.handleAddRecipe();
				break;
			case SETTINGS:
				settingsHandler.handle();
				break;
			default:
				openNewsfeed(getActivity());
				break;
			}
			pagePressed = null;
		}
	}

}
