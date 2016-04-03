package com.sage.listeners;

import com.example.myfirstapp.AddRecipeAsLinkActivity;
import com.example.myfirstapp.PictureRecipePageActivity;
import com.example.myfirstapp.R;
import com.example.myfirstapp.TextReciptPageActivity;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeLinkDetails;
import com.sage.entities.RecipePictureDetails;
import com.sage.entities.RecipeSubCategory;
import com.sage.entities.RecipeTextDetails;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.PopupWindow;

public class AddRecipePopupHandler {

	private LayoutInflater inflater;
	private ViewGroup container;
	private View view;
	private final Activity context;
	private RecipeCategory category;

	public AddRecipePopupHandler(LayoutInflater inflater, View view, ViewGroup container, Activity context,
			RecipeCategory category) {
		this.inflater = inflater;
		this.view = view;
		this.container = container;
		this.context = context;
		this.category = category;
	}

	public void handleAddRecipe() {
		View popupView = inflater.inflate(R.layout.add_recipe_popup, container, false);

		Rect displayRectangle = new Rect();
		Window window = context.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		final PopupWindow popupWindow = new PopupWindow(popupView, (int) (displayRectangle.width() * 0.7f),
				(int) (displayRectangle.height() * 0.55f), true);

		popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
		ActivityUtils.InitPopupWindowWithEventHandler(popupWindow, context);

		Button linkLayout = (Button) popupView.findViewById(R.id.add_link_panel);

		linkLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.CLICK_ADD_LINK_RECIPE);
				Intent intent = new Intent(context, AddRecipeAsLinkActivity.class)
						.putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, new RecipeLinkDetails())
						.putExtra(EntityDataTransferConstants.CATEGORY_DATA_TRANSFER, category)
						.putExtra(EntityDataTransferConstants.IN_MY_RECIPE_PAGE, true);
				context.startActivity(intent);
				
			}
		});

		Button textLayout = (Button) popupView.findViewById(R.id.add_text_panel);

		textLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RecipeTextDetails textRecipe = new RecipeTextDetails();	
				AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.CLICK_ADD_TEXT_RECIPE);
				Intent intent = new Intent(context, TextReciptPageActivity.class)
						.putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, textRecipe)
						.putExtra(EntityDataTransferConstants.CATEGORY_DATA_TRANSFER, category)
						.putExtra(EntityDataTransferConstants.IN_MY_RECIPE_PAGE, true);
				context.startActivity(intent);

			}
		});

		Button pictureLayout = (Button) popupView.findViewById(R.id.add_picture_panel);

		pictureLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.CLICK_ADD_PICTURE_RECIPE);
				RecipePictureDetails pictureRecipe = new RecipePictureDetails();		
				Intent intent = new Intent(context, PictureRecipePageActivity.class)
						.putExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER, pictureRecipe)
						.putExtra(EntityDataTransferConstants.CATEGORY_DATA_TRANSFER, category)
						.putExtra(EntityDataTransferConstants.IN_MY_RECIPE_PAGE, true);
				context.startActivity(intent);
			}
		});

	}
}
