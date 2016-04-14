package com.example.myfirstapp;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.sage.activity.interfaces.IExitWithoutSaveListener;
import com.sage.constants.ImageType;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipePictureDetails;
import com.sage.fragments.ToolbarFragment;
import com.sage.listeners.SaveRecipeHandler;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.ImageSelectorUtils;
import com.sage.utils.ImagesInitializer;

public class PictureRecipePageActivity extends AppCompatActivity implements IExitWithoutSaveListener {

	private RecipePictureDetails recipeDetails;
	private RecipeCategory category;

	private Button addImageButton;

	private ImageView recipeAsPicture;

	private Button editRecipePictureButton;

	private boolean cameraOpened;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_recipe_page);
		Intent i = getIntent();

		recipeDetails = (RecipePictureDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		category = (RecipeCategory) i
				.getSerializableExtra(EntityDataTransferConstants.CATEGORY_DATA_TRANSFER);

		addImageButton = (Button) findViewById(R.id.add_recipe_as_picture);
		recipeAsPicture = (ImageView) findViewById(R.id.recipe_as_picture_receipt_image);
		editRecipePictureButton = (Button)findViewById(R.id.edit_recipe_picture_button);

		initSupportActionBar();

		initPictureAndPictureButton();
		disableLockScreenAndTimeout();

	}

	private void disableLockScreenAndTimeout() {
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

	}

	private void initSupportActionBar() {
		Toolbar myToolbar = (Toolbar) findViewById(R.id.title_save_toolbar);
		setSupportActionBar(myToolbar);
		if (!EntityUtils.isNewRecipe(recipeDetails)) {
			String recipeTitle = recipeDetails.getHeader();
			getSupportActionBar().setTitle(recipeTitle);
		} else {
			getSupportActionBar().setTitle(getResources().getString(R.string.add_recipe_title_toolbar));
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), this)) {
			getMenuInflater().inflate(R.menu.recipe_page_menu_for_logged_in_user, menu);
		} else {
			getMenuInflater().inflate(R.menu.recipe_page_menu_for_not_logged_in_user, menu);
		}

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.action_save) {
			LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			ViewGroup viewGroup = (ViewGroup) getWindow().getDecorView();
			SaveRecipeHandler handler = new SaveRecipeHandler(inflater, viewGroup, viewGroup, recipeDetails, this,
					category);
			handler.HandleSaveRecipe();
			return true;
		} else {
			return super.onOptionsItemSelected(item);

		}
	}

	@Override
	public void onBackPressed() {
		if (EntityUtils.isRecipeChanges(this, recipeDetails)) {
			ActivityUtils.handleExitWithoutSavingPopup(this);
		} else {
			super.onBackPressed();
		}
	}

	private void initMainRecipePicture(ImageView imageView, ImageType imageType) {
		String pictureID = this.recipeDetails.getImageRecipe_pictureId();
		if (!TextUtils.isEmpty(pictureID)) {
			ImagesInitializer.initialRecipeImage(this, pictureID, imageView, imageType);
		}

	}

	private void makePictureEditPanleInvisible() {
		RelativeLayout mainPictureEditPanel = (RelativeLayout)findViewById(R.id.recipe_picture_edit_panel);
		mainPictureEditPanel.setVisibility(View.GONE);
	}


	private void initPictureAndPictureButton() {

		if (!EntityUtils.isNewRecipe(recipeDetails)) {
			initMainRecipePicture(this.recipeAsPicture, ImageType.IMAGE_RECIPE_PICTURE);
			if (TextUtils.isEmpty(recipeDetails.getImageRecipe_pictureId())) {
				if (!EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), this)) {
					addImageButton.setVisibility(View.GONE);
					recipeAsPicture.setVisibility(View.VISIBLE);
					editRecipePictureButton.setVisibility(View.GONE);
				} else {
					addImageButton.setVisibility(View.VISIBLE);
					makePictureEditPanleInvisible();
				}
			} else {
				addImageButton.setVisibility(View.GONE);
				recipeAsPicture.setVisibility(View.VISIBLE);
				if(EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), this)) {
					editRecipePictureButton.setVisibility(View.VISIBLE);
				} else {
					editRecipePictureButton.setVisibility(View.GONE);
				}
			}
		} else {
			makePictureEditPanleInvisible();
		}

		addImageButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changePictureOnClick();
			}
		});
		editRecipePictureButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				changePictureOnClick();
			}
		});
	}

	private void changePictureOnClick() {
		AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.CLICK_ADD_PICTURE_TO_PICTURE_RECIPE);
		cameraOpened = true;
		ImageSelectorUtils.selectImage(this);
	}

	@Override
	public void onExistWithoutSave() {
		ToolbarFragment toolbarFragment = (ToolbarFragment) getFragmentManager()
				.findFragmentById(R.id.buttom_toolbar_fragment);
		if (toolbarFragment.getPagePressed() != null) {
			toolbarFragment.handlePagePressed();
		} else {
			Intent intent = new Intent(this, NewsfeedActivity.class);
			startActivity(intent);
		}

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		Fragment fragment = getFragmentManager().findFragmentById(R.id.recipe_picture_details_panel);
		fragment.onActivityResult(requestCode, resultCode, data);
		if (!cameraOpened) {
			return;
		}
		Bitmap thumbnail = ImageSelectorUtils.initializeImage(requestCode, resultCode, data, this.recipeAsPicture,
				this.addImageButton, this);
		this.recipeDetails.setRecipeAsPictureImage(thumbnail);
		cameraOpened = false;

		RelativeLayout mainPictureEditPanel = (RelativeLayout) findViewById(R.id.recipe_picture_edit_panel);
		mainPictureEditPanel.setVisibility(View.VISIBLE);

		this.recipeDetails.setRecipeChanges(true);

	}

}
