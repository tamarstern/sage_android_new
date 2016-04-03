package com.example.myfirstapp;

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

import android.app.Activity;
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
import android.widget.Toast;

public class PictureRecipePageActivity extends AppCompatActivity implements IExitWithoutSaveListener {

	private RecipePictureDetails recipeDetails;
	private RecipeCategory category;

	private Button addImageButton;

	private ImageView recipeAsPicture;

	static final int IMAGE_SELECTOR = 1;

	private boolean cameraOpened;

	private MenuItem publishMenuItem;

	private MenuItem unpublishMenuItem;

	static final int REQUEST_IMAGE_CAPTURE = 1;

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

	private void initPublishAndUnpublishMenuItems(Menu menu) {
		publishMenuItem = menu.findItem(R.id.action_publish);
		unpublishMenuItem = menu.findItem(R.id.action_unpublish);
		if (recipeDetails.isPublished()) {
			publishMenuItem.setVisible(false);
			unpublishMenuItem.setVisible(true);
		} else {
			publishMenuItem.setVisible(true);
			unpublishMenuItem.setVisible(false);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		if (EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), this)) {
			getMenuInflater().inflate(R.menu.recipe_page_menu_for_logged_in_user, menu);
			initPublishAndUnpublishMenuItems(menu);
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
		} else if (item.getItemId() == R.id.action_publish) {
			Toast toast = Toast.makeText(this, R.string.publish_recipe, Toast.LENGTH_SHORT);
			toast.show();
			recipeDetails.setPublished(true);
			recipeDetails.setRecipeChanges(true);
			publishMenuItem.setVisible(false);
			unpublishMenuItem.setVisible(true);
			AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.PUBLISH_RECIPE);
			return true;
		} else if (item.getItemId() == R.id.action_unpublish) {
			Toast toast = Toast.makeText(this, R.string.unpublish_recipe, Toast.LENGTH_SHORT);
			toast.show();
			recipeDetails.setPublished(false);
			recipeDetails.setRecipeChanges(true);
			publishMenuItem.setVisible(true);
			unpublishMenuItem.setVisible(false);
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

	private void initPictureAndPictureButton() {

		if (!EntityUtils.isNewRecipe(recipeDetails)) {
			initMainRecipePicture(this.recipeAsPicture, ImageType.IMAGE_RECIPE_PICTURE);
			if (TextUtils.isEmpty(recipeDetails.getImageRecipe_pictureId())) {
				if (!EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), this)) {
					addImageButton.setVisibility(View.GONE);
					recipeAsPicture.setVisibility(View.VISIBLE);
				} else {
					addImageButton.setVisibility(View.VISIBLE);
					recipeAsPicture.setVisibility(View.GONE);
				}
			} else {
				addImageButton.setVisibility(View.GONE);
				recipeAsPicture.setVisibility(View.VISIBLE);
			}
		} else {
			recipeAsPicture.setVisibility(View.GONE);
		}

		addImageButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				changePictureOnClick();

			}

		});
		final Activity activity = this;
		recipeAsPicture.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (EntityUtils.isLoggedInUserRecipe(recipeDetails.getUserId(), activity)) {
					changePictureOnClick();
				}

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

	}

}
