package com.sage.fragments;

import com.example.myfirstapp.R;
import com.sage.constants.ImageType;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeTextDetails;
import com.sage.utils.EntityUtils;
import com.sage.utils.ImageSelectorUtils;
import com.sage.utils.ImagesInitializer;
import com.sage.utils.RecipeDetailsBinder;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class RecipeDetailsFragment extends Fragment {

	private RecipeTextDetails recipeDetails;

	private EditText ingredients;

	private EditText prepartion;

	private EditText comments;

	private Button addImageButton;

	private ImageView mainPicture;

	private boolean cameraOpened = false;

	static final int REQUEST_IMAGE_CAPTURE = 1;
	static final int REQUEST_SELECT_FILE = 2;

	private static final int REQUEST_CAMERA = 1;

	private static final int SELECT_FILE = 2;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View recipeDetailsPanel = inflater.inflate(R.layout.recipe_details_fragment, container, false);

		final Activity activity = getActivity();
		Intent i = activity.getIntent();

		recipeDetails = (RecipeTextDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		addImageButton = (Button) recipeDetailsPanel.findViewById(R.id.add_picture);
		mainPicture = (ImageView) recipeDetailsPanel.findViewById(R.id.receipt_main_pic);

		initWithRecipeDetails(recipeDetailsPanel);

		initRecipeUi(recipeDetailsPanel, activity);

		addImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changePictureAction();

			}

		});
		mainPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), activity)) {
					changePictureAction();
				}

			}

		});

		return recipeDetailsPanel;
	}

	private void initRecipeUi(View recipeDetailsPanel, final Activity activity) {
		if (!EntityUtils.isNewRecipe(recipeDetails)) {
			initMainRecipePicture(recipeDetailsPanel);
			initExistingRecipeIngredients();
			initExistingRecipePreparation();
			initExistingRecipeComments();
			if (TextUtils.isEmpty(recipeDetails.getPictureId())) {
				if (!EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), activity)) {
					addImageButton.setVisibility(View.GONE);
					mainPicture.setVisibility(View.VISIBLE);
				} else {
					addImageButton.setVisibility(View.VISIBLE);
					mainPicture.setVisibility(View.GONE);
				}
			} else {
				addImageButton.setVisibility(View.GONE);
				mainPicture.setVisibility(View.VISIBLE);
			}
		} else {
			mainPicture.setVisibility(View.GONE);
		}

		LinearLayout commentsPanel = (LinearLayout) recipeDetailsPanel.findViewById(R.id.receipt_comments_panel);
		if (!EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), activity)) {
			ingredients.setEnabled(false);
			ingredients.setHint("");
			prepartion.setEnabled(false);
			prepartion.setHint("");
			commentsPanel.setVisibility(View.GONE);
		}
	}

	private void changePictureAction() {
		cameraOpened = true;
		ImageSelectorUtils.selectImage(getActivity());

	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (!cameraOpened) {
			return;
		}
		Bitmap thumbnail = ImageSelectorUtils.initializeImage(requestCode, resultCode, data, this.mainPicture, this.addImageButton,
				getActivity());
		
		this.recipeDetails.setImage(thumbnail);
		cameraOpened = false;
	}

	private void initMainRecipePicture(View recipeDetailsPanel) {
		ImageView mainPicture = (ImageView) recipeDetailsPanel.findViewById(R.id.receipt_main_pic);
		String pictureID = recipeDetails.getPictureId();

		ImagesInitializer.initialRecipeImage(this.getActivity(), pictureID, mainPicture,ImageType.RECIPE_PICTURE);

	}

	private void initWithRecipeDetails(View recipeDetailsPanel) {

		initIngredientsTextBox(recipeDetailsPanel);

		initPreparationTextBox(recipeDetailsPanel);

		initCommentsTextBox(recipeDetailsPanel);
	}

	private void initCommentsTextBox(View recipeDetailsPanel) {
		this.comments = (EditText) recipeDetailsPanel.findViewById(R.id.receipt_comments_content);
		RecipeDetailsBinder.bindTextBoxToComments(comments, recipeDetails);
	}

	private void initPreparationTextBox(View recipeDetailsPanel) {
		this.prepartion = (EditText) recipeDetailsPanel.findViewById(R.id.receipt_preparation_content);
		RecipeDetailsBinder.bindTextBoxToPreparation(prepartion, recipeDetails);
	}

	private void initIngredientsTextBox(View recipeDetailsPanel) {
		this.ingredients = (EditText) recipeDetailsPanel.findViewById(R.id.receipt_ingredients_content);
		RecipeDetailsBinder.bindTextBoxToIngredients(ingredients, recipeDetails);
	}

	private void initExistingRecipeComments() {
		String preparationComments = recipeDetails.getPreparationComments();
		if (!TextUtils.isEmpty(preparationComments)) {
			comments.setText(preparationComments);
			initTextProperties(comments);
			recipeDetails.setRecipeChanges(false);
		}

	}

	private void initTextProperties(EditText text) {
		text.setGravity(Gravity.LEFT);
		text.setSingleLine(false);
	}

	private void initExistingRecipeIngredients() {
		String ingredientsText = recipeDetails.getIngredients();
		if (!TextUtils.isEmpty(ingredientsText)) {
			ingredients.setText(ingredientsText);
			initTextProperties(ingredients);
			recipeDetails.setRecipeChanges(false);
		}

	}

	private void initExistingRecipePreparation() {
		String preparationDescription = recipeDetails.getPreparationDescription();
		if (!TextUtils.isEmpty(preparationDescription)) {
			prepartion.setText(preparationDescription);
			initTextProperties(prepartion);
			recipeDetails.setRecipeChanges(false);
		}

	}

}