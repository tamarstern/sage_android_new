package com.sage.activities;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sage.activity.interfaces.IExitWithoutSaveListener;
import com.sage.application.GoogleAnalyticsApplication;
import com.sage.application.TCImageLoader;
import com.sage.backgroundServices.BackgroundServicesScheduler;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.fragments.ToolbarFragment;
import com.sage.listeners.SaveRecipeHandler;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.CacheUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.ImageSelectorUtils;
import com.sage.utils.ImagesInitializer;

import java.util.HashMap;
import java.util.Map;

public class PictureRecipePageActivity extends AppCompatActivity implements IExitWithoutSaveListener {

	private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;
	final private int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 124;
	private RecipeDetails recipeDetails;
	private RecipeCategory category;

	private Button addImageButton;

	private ImageView recipeAsPicture;

	private Button editRecipePictureButton;

	private boolean cameraOpened;

	private ProgressBar progressBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture_recipe_page);
		BackgroundServicesScheduler.scheduleAlarmBackgroundServices(this);
		ActivityUtils.forceInitFollowingList(this);

		Intent i = getIntent();

		recipeDetails = (RecipeDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		category = (RecipeCategory) i
				.getSerializableExtra(EntityDataTransferConstants.CATEGORY_DATA_TRANSFER);
		progressBar = (ProgressBar)findViewById(R.id.get_recipe_picture_progress);
		progressBar.setVisibility(View.GONE);
		addImageButton = (Button) findViewById(R.id.add_recipe_as_picture);
		recipeAsPicture = (ImageView) findViewById(R.id.recipe_as_picture_receipt_image);
		editRecipePictureButton = (Button)findViewById(R.id.edit_recipe_picture_button);

		final Activity activity = this;

		recipeAsPicture.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!TextUtils.isEmpty(recipeDetails.getImageRecipe_pictureId())) {

					Intent intent = new Intent(activity, DisplayImageActivity.class).
							putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER,recipeDetails).
							putExtra(EntityDataTransferConstants.RECIPE_IMAGE_TYPE, RecipeType.PICTURE);
					activity.startActivity(intent);

				}
			}
		});


		initSupportActionBar();

		initPictureAndPictureButton();
		initMainRecipePicture();
		disableLockScreenAndTimeout();

	}


	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {
		super.onRestoreInstanceState(savedInstanceState, persistentState);
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


	private void initMainRecipePicture() {
		if(EntityUtils.isNewRecipe(recipeDetails)) {
			return;
		}
		String id = CacheUtils.getRecipeImagePictureId(recipeDetails);
		if(!TextUtils.isEmpty(id)) {

			String url = ImagesInitializer.getUrl( id);
			TCImageLoader loader = ((GoogleAnalyticsApplication) this.getApplication()).getLoader();
			loader.display(url, this.recipeAsPicture,this.progressBar, R.drawable.default_recipe_image);

		}
	}


	private void makePictureEditPanleInvisible() {
		RelativeLayout mainPictureEditPanel = (RelativeLayout)findViewById(R.id.recipe_picture_edit_panel);
		mainPictureEditPanel.setVisibility(View.GONE);
	}


	private void initPictureAndPictureButton() {

		if (!EntityUtils.isNewRecipe(recipeDetails)) {
			String recipeImagePictureId = CacheUtils.getRecipeImagePictureId(recipeDetails);
			if (TextUtils.isEmpty(recipeImagePictureId)) {
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

		int permissionCheckWriteExternal = ContextCompat.checkSelfPermission(this,
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
		int permissionCheckReadExternal = ContextCompat.checkSelfPermission(this,
				android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

		int permissionCheckCamera = ContextCompat.checkSelfPermission(this,
				Manifest.permission.CAMERA);


		if((permissionCheckWriteExternal != PackageManager.PERMISSION_GRANTED)
				|| (permissionCheckReadExternal != PackageManager.PERMISSION_GRANTED)
				|| (permissionCheckCamera != PackageManager.PERMISSION_GRANTED)) {
			ActivityCompat.requestPermissions(this,
					new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
							Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
					REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
			return;
		}

		openChoosePicturePopup();
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
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
					openChoosePicturePopup();
				} else {
					String message = getResources().getString(R.string.need_permissions_to_insert_picture);
					Toast.makeText(PictureRecipePageActivity.this,message , Toast.LENGTH_SHORT)
							.show();
				}
			}
			break;
			default:
				super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		}
	}

	private void openChoosePicturePopup() {
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
			intent.putExtra(EntityDataTransferConstants.NEW_RECIPE_UNSAVED, true);
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
