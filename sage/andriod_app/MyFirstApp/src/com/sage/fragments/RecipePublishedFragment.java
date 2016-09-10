package com.sage.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.sage.activities.R;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.utils.EntityUtils;

public class RecipePublishedFragment extends Fragment {

	private RecipeDetails receiptDetails;

	private SwitchCompat publishedSwitch;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View recipeDetailsPanel = inflater.inflate(R.layout.recipe_published_fragment, container, false);

		Intent i = getActivity().getIntent();

		receiptDetails = (RecipeDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		if (!EntityUtils.isLoggedInUserRecipe(receiptDetails.getUserId(), getActivity())) {
			RelativeLayout panel = (RelativeLayout)recipeDetailsPanel.findViewById(R.id.recipe_published_panel);
			panel.setVisibility(View.GONE);
			return recipeDetailsPanel;
		}

		publishedSwitch = (SwitchCompat)recipeDetailsPanel.findViewById(R.id.publish_switch);
		publishedSwitch.setChecked(receiptDetails.isPublished());
		publishedSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
										 boolean isChecked) {

				if (isChecked) {
					receiptDetails.setPublished(true);
					receiptDetails.setRecipeChanges(true);
				} else {
					receiptDetails.setPublished(false);
					receiptDetails.setRecipeChanges(true);
				}

			}
		});

		return recipeDetailsPanel;
	}
}