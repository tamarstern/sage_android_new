package com.sage.listeners;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.myfirstapp.R;
import com.sage.activities.NewsfeedActivity;
import com.sage.application.MyProfileRecipiesContainer;
import com.sage.application.NewsfeedContainer;
import com.sage.application.RecipesToDeleteContainer;
import com.sage.application.UserCategoriesContainer;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;

public class DeletePopupClickListener implements OnClickListener {

	private LayoutInflater inflater;
	private ViewGroup container;
	private View savePublishRecipe;
	private RecipeDetails recipeDetails;
	private Activity context;

	public DeletePopupClickListener(LayoutInflater inflater, ViewGroup container, View savePublishRecipe,
			RecipeDetails recipeDetails, Activity context) {
		this.inflater = inflater;
		this.container = container;
		this.savePublishRecipe = savePublishRecipe;
		this.recipeDetails = recipeDetails;
		this.context = context;
	}

	@Override
	public void onClick(View v) {
		View popupView = inflater.inflate(R.layout.delete_recipe_popup, container, false);
		Rect displayRectangle = new Rect();
		Window window = context.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		final PopupWindow popupWindow = new PopupWindow(popupView, (int) (displayRectangle.width() * 0.6f),
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		popupWindow.showAtLocation(savePublishRecipe, Gravity.CENTER, 0, 0);
		ActivityUtils.InitPopupWindowWithEventHandler(popupWindow, context);

		Button confirmDelete = (Button) popupView.findViewById(R.id.confirm_delete_recipe);
		confirmDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				UserCategoriesContainer.getInstance().deleteRecipe(recipeDetails);
				MyProfileRecipiesContainer.getInstance().deleteRecipe(recipeDetails);
				NewsfeedContainer.getInstance().deleteRecipe(recipeDetails);
				RecipesToDeleteContainer.getInstance().addRecipeToDelete(recipeDetails);
				Intent intent = new Intent(context, NewsfeedActivity.class)
						.putExtra(EntityDataTransferConstants.RECIPE_DELETED, true);
				context.startActivity(intent);
				AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.PRESS_DELETE_RECIPE);
			}
		});

		Button cancelDelete = (Button) popupView.findViewById(R.id.cancel_delete_recipe);
		cancelDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				popupWindow.dismiss();
			}
		});

	}
}
