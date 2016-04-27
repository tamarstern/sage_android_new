package com.sage.fragments;

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
import android.widget.RelativeLayout;

import com.example.myfirstapp.DisplayImageActivity;
import com.example.myfirstapp.R;
import com.sage.activity.interfaces.IOnWindowFocusChanged;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.utils.ActivityUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.ImageSelectorUtils;
import com.sage.utils.ImagesInitializer;
import com.sage.utils.RecipeDetailsBinder;

public class RecipeDetailsFragment extends Fragment implements IOnWindowFocusChanged {

	private RecipeDetails recipeDetails;

	private EditText ingredients;

	private EditText prepartion;

	private EditText comments;

	private Button addImageButton;

	private Button editImageButton;

	private ImageView mainPicture;

	private boolean cameraOpened = false;
	private View recipeDetailsPanel;

	private boolean isImageFitToScreen = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		recipeDetailsPanel = inflater.inflate(R.layout.recipe_details_fragment, container, false);

		final Activity activity = getActivity();
		Intent i = activity.getIntent();

		recipeDetails = (RecipeDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		addImageButton = (Button) recipeDetailsPanel.findViewById(R.id.add_picture);
		addImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changePictureAction();

			}

		});
		mainPicture = (ImageView) recipeDetailsPanel.findViewById(R.id.receipt_main_pic);
		mainPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(recipeDetails.getPictureId())) {

					Intent intent = new Intent(activity, DisplayImageActivity.class).
							putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER,recipeDetails).
							putExtra(EntityDataTransferConstants.RECIPE_IMAGE_TYPE, RecipeType.TEXT);
					activity.startActivity(intent);

				}
			}
		});

		editImageButton = (Button) recipeDetailsPanel.findViewById(R.id.edit_main_picture_button);

		editImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changePictureAction();

			}

		});

		initWithRecipeDetails();

		initRecipeUi(activity);
		return recipeDetailsPanel;
	}

	private void initRecipeUi( final Activity activity) {
		if (!EntityUtils.isNewRecipe(recipeDetails)) {
			initRecipeContentForNonNewRecipes();
			String recipeMainPictureId = ActivityUtils.getRecipeMainPictureId(recipeDetails);
			if (TextUtils.isEmpty(recipeMainPictureId)) {
				if (!EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), activity)) {
					addImageButton.setVisibility(View.GONE);
					mainPicture.setVisibility(View.VISIBLE);
					editImageButton.setVisibility(View.GONE);
				} else {
					addImageButton.setVisibility(View.VISIBLE);
					makePictureEditPanleInvisible();
				}
			} else {
				addImageButton.setVisibility(View.GONE);
				mainPicture.setVisibility(View.VISIBLE);
				if(EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), activity)) {
					editImageButton.setVisibility(View.VISIBLE);
				} else {
					editImageButton.setVisibility(View.GONE);
				}
			}
		} else {
			makePictureEditPanleInvisible();
		}
		updateUIForNonLoggedInUserRecipe(activity);
	}

	private void initRecipeContentForNonNewRecipes() {
		initExistingRecipeIngredients();
		initExistingRecipePreparation();
		initExistingRecipeComments();
	}

	private void makePictureEditPanleInvisible() {
		RelativeLayout mainPictureEditPanel = (RelativeLayout)recipeDetailsPanel.findViewById(R.id.main_picture_edit_panel);
		mainPictureEditPanel.setVisibility(View.GONE);
	}

	private void updateUIForNonLoggedInUserRecipe( Activity activity) {
		if (!EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), activity)) {
			ingredients.setEnabled(false);
			ingredients.setHint("");
			prepartion.setEnabled(false);
			prepartion.setHint("");
			LinearLayout commentsPanel = (LinearLayout) recipeDetailsPanel.findViewById(R.id.receipt_comments_panel);
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

		RelativeLayout mainPictureEditPanel = (RelativeLayout)recipeDetailsPanel.findViewById(R.id.main_picture_edit_panel);
		mainPictureEditPanel.setVisibility(View.VISIBLE);

		this.recipeDetails.setRecipeChanges(true);
	}

	private void initMainRecipePicture() {
		if(!EntityUtils.isNewRecipe(recipeDetails)) {
			ImageView mainPicture = (ImageView) recipeDetailsPanel.findViewById(R.id.receipt_main_pic);
			ImagesInitializer.initRecipeMainPicture(mainPicture, recipeDetails, getActivity());
		}

	}

	private void initWithRecipeDetails() {

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

	@Override
	public void onFocusChanged() {
		initMainRecipePicture();
	}
}