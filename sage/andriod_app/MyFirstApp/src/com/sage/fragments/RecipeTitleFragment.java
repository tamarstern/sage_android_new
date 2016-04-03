package com.sage.fragments;

import com.example.myfirstapp.R;
import com.sage.activity.interfaces.IInitLinkDetailsListener;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeLinkDetails;
import com.sage.entities.RecipeType;
import com.sage.utils.EntityUtils;
import com.sage.utils.RecipeDetailsBinder;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

public class RecipeTitleFragment extends Fragment implements IInitLinkDetailsListener {

	private RecipeDetails receiptDetails;

	private EditText title;

	static final int REQUEST_IMAGE_CAPTURE = 1;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View recipeDetailsPanel = inflater.inflate(R.layout.recipe_title_fragment, container, false);

		Intent i = getActivity().getIntent();

		receiptDetails = (RecipeDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		initWithRecipe(recipeDetailsPanel);
		if (!EntityUtils.isNewRecipe(receiptDetails)) {
			initExistingRecipeTitle();

		}
		if (!EntityUtils.isLoggedInUserRecipe(receiptDetails.getUserId(), getActivity())) {
			title.setEnabled(false);
		}
		return recipeDetailsPanel;
	}

	private void initWithRecipe(View recipeDetailsPanel) {

		this.title = (EditText) recipeDetailsPanel.findViewById(R.id.recipe_title_edit_text);
		RecipeDetailsBinder.bindTextBoxToTitle(title, receiptDetails, getActivity());
		initTextProperties(title);

	}

	private void initExistingRecipeTitle() {
		String recipeTitle = receiptDetails.getHeader();
		if (!TextUtils.isEmpty(recipeTitle)) {
			this.title.setText(recipeTitle);
			receiptDetails.setRecipeChanges(false);
		}
	}

	private void initTextProperties(EditText text) {
		text.setSingleLine(false);
	}

	@Override
	public void onInitLinkDetails() {
		if (EntityUtils.isNewRecipe(receiptDetails) && 
				this.receiptDetails.getRecipeType().equals(RecipeType.LINK)) {
			RecipeLinkDetails linkDetails = (RecipeLinkDetails)receiptDetails;
			String linkTitle = linkDetails.getLinkTitle();
			if(!TextUtils.isEmpty(linkTitle)) {
				this.title.setText(linkTitle);
			}			
		}

	}

}