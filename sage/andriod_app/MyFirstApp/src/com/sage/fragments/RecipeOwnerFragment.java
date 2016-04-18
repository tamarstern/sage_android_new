package com.sage.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myfirstapp.R;
import com.sage.activity.interfaces.IInitLinkDetailsListener;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.listeners.ProfilePageClickListener;
import com.sage.utils.ActivityUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.RecipeOwnerContext;

public class RecipeOwnerFragment extends Fragment implements IInitLinkDetailsListener {

	private RecipeDetails receiptDetails;

	private TextView label;

	private TextView ownerDisplayName;

	static final int REQUEST_IMAGE_CAPTURE = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View recipeDetailsPanel = inflater.inflate(R.layout.recipe_owner_fragment, container, false);

		Intent i = getActivity().getIntent();

		receiptDetails = (RecipeDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		label = (TextView) recipeDetailsPanel.findViewById(R.id.recipe_owner_fragment_lbl);

		ownerDisplayName = (TextView) recipeDetailsPanel.findViewById(R.id.recipe_owner_display_name);

		LinearLayout panel = (LinearLayout) recipeDetailsPanel.findViewById(R.id.receipt_owner_panel);

		if (EntityUtils.isNewRecipe(receiptDetails) && !receiptDetails.getRecipeType().equals(RecipeType.LINK)) {
			panel.setVisibility(View.GONE);
			return recipeDetailsPanel;
		}

		final Activity activity = getActivity();

		boolean isLinkRecipe = receiptDetails.getRecipeType().equals(RecipeType.LINK);
		if (isLinkRecipe) {
			initLinkOwnerName(activity);
		} else {
			String ownerObjectId = receiptDetails.getOwnerObjectId();
			if (!TextUtils.isEmpty(ownerObjectId)) {
				if (ownerObjectId.equals(receiptDetails.getUserObjectId())
						&& EntityUtils.isLoggedInUserRecipe(receiptDetails.getUserId(), this.getActivity())) {
					panel.setVisibility(View.GONE);
					return recipeDetailsPanel;
				}
			}
			String displayName = receiptDetails.getOwnerDisplayName();
			if (!TextUtils.isEmpty(displayName)) {
				ownerDisplayName.setText(displayName);
				ownerDisplayName.setOnClickListener(new ProfilePageClickListener(getActivity(),
						receiptDetails.getOwnerDisplayName(), receiptDetails.getOwnerUserName(), ownerObjectId, false));
			}
		}
		return recipeDetailsPanel;
	}

	private void initLinkOwnerName(final Activity activity) {
		final RecipeDetails linkDetails = (RecipeDetails) receiptDetails;
		String ownerName = RecipeOwnerContext.getOwner(linkDetails.getUrl());
		if (!TextUtils.isEmpty(ownerName)) {
            ownerDisplayName.setText(ownerName);
            ownerDisplayName.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {

                    ActivityUtils.openDisplayLinkActivity(activity, linkDetails,
							EntityUtils.isLoggedInUserRecipe(receiptDetails.getUserId(), activity));
                }
            });
        }
	}

	@Override
	public void onInitLinkDetails() {
		final Activity activity = getActivity();
		String linkSiteName = receiptDetails.getLinkSiteName();
		if (!TextUtils.isEmpty(linkSiteName)) {
			ownerDisplayName.setText(linkSiteName);
			ownerDisplayName.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {

					ActivityUtils.openDisplayLinkActivity(getActivity(), receiptDetails,
							EntityUtils.isLoggedInUserRecipe(receiptDetails.getUserId(), activity));

				}
			});

		}

	}

}