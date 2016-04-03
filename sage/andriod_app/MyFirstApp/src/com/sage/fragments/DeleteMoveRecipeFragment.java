package com.sage.fragments;

import com.example.myfirstapp.ActivityCategoriesPage;
import com.example.myfirstapp.R;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.listeners.DeletePopupClickListener;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.EntityUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class DeleteMoveRecipeFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View savePublishRecipe = inflater.inflate(R.layout.delete_move_recipe_fragment, container, false);

		final Activity activity = getActivity();
		Intent i = activity.getIntent();

		final RecipeDetails recipeDetails = (RecipeDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		View deleteButton = savePublishRecipe.findViewById(R.id.delete_recipe);

		View moveButton = savePublishRecipe.findViewById(R.id.move_recipe);

		deleteButton.setOnClickListener(
				new DeletePopupClickListener(inflater, container, savePublishRecipe, recipeDetails, activity));

		moveButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				AnalyticsUtils.sendAnalyticsTrackingEvent(activity, AnalyticsUtils.PRESS_MOVE_RECIPE);
				
				Intent intent = new Intent(activity.getApplicationContext(), ActivityCategoriesPage.class)
						.putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, recipeDetails);
				startActivity(intent);

			}
		});

		if (EntityUtils.isNewRecipe(recipeDetails)
				|| !EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), activity)) {

			deleteButton.setVisibility(View.GONE);

			moveButton.setVisibility(View.GONE);

		}
		return savePublishRecipe;
	}

}
