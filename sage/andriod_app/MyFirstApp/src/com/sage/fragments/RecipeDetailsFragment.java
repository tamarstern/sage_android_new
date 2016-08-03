package com.sage.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sage.activities.DisplayImageActivity;
import com.sage.activities.R;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.utils.CacheUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.ImageSelectorUtils;
import com.sage.utils.ImagesInitializer;
import com.sage.utils.RecipeDetailsBinder;

import java.util.HashMap;
import java.util.Map;

public class RecipeDetailsFragment extends Fragment {

	private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
	final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;

	private RecipeDetails recipeDetails;

	private EditText ingredients;

	private EditText prepartion;

	private EditText comments;

	private Button addImageButton;

	private Button editImageButton;

	private ImageView mainPicture;

	private ProgressBar progressBar;

	private boolean cameraOpened = false;
	private View recipeDetailsPanel;



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		recipeDetailsPanel = inflater.inflate(R.layout.recipe_details_fragment, container, false);

		final Activity activity = getActivity();
		Intent i = activity.getIntent();

		recipeDetails = (RecipeDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);
		progressBar = (ProgressBar) recipeDetailsPanel.findViewById(R.id.get_main_picture_progress);
		progressBar.setVisibility(View.GONE);
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
		initMainRecipePicture();
		return recipeDetailsPanel;
	}

	private void initRecipeUi( final Activity activity) {
		if (!EntityUtils.isNewRecipe(recipeDetails)) {
			initRecipeContentForNonNewRecipes();
			String recipeMainPictureId = CacheUtils.getRecipeMainPictureId(recipeDetails);
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

		int permissionCheckWriteExternal = ContextCompat.checkSelfPermission(getActivity(),
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int permissionCheckReadExternal = ContextCompat.checkSelfPermission(getActivity(),
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int permissionCheckCamera = ContextCompat.checkSelfPermission(getActivity(),
				Manifest.permission.CAMERA);

		if((permissionCheckWriteExternal != PackageManager.PERMISSION_GRANTED)
				|| (permissionCheckReadExternal != PackageManager.PERMISSION_GRANTED)
				|| (permissionCheckCamera != PackageManager.PERMISSION_GRANTED)) {
			ActivityCompat.requestPermissions(getActivity(),
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.READ_EXTERNAL_STORAGE,
							Manifest.permission.CAMERA},
					REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
			return;
		}

		cameraOpened = true;
		ImageSelectorUtils.selectImage(getActivity());

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		switch (requestCode) {
			case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
			{
				Map<String, Integer> perms = new HashMap<String, Integer>();
				// Initial
				perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
				perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
				// Fill with results
				for (int i = 0; i < permissions.length; i++)
					perms.put(permissions[i], grantResults[i]);
				// Check for ACCESS_FINE_LOCATION
				if (perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
						&& perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
					// All Permissions Granted
					cameraOpened = true;
					ImageSelectorUtils.selectImage(getActivity());
				} else {
					String message = getResources().getString(R.string.need_permissions_to_insert_picture);
					Toast.makeText(getActivity(),message , Toast.LENGTH_SHORT)
							.show();
				}
			}
			break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
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
			ProgressBar progressBar = (ProgressBar) recipeDetailsPanel.findViewById(R.id.get_main_picture_progress);
			String id = CacheUtils.getRecipeMainPictureId(recipeDetails);
			ImagesInitializer.initialRecipeImage(getActivity(), id, mainPicture, progressBar);
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
	//	ingredients.setHorizontallyScrolling(false);
	//	ingredients.setMaxLines(Integer.MAX_VALUE);
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