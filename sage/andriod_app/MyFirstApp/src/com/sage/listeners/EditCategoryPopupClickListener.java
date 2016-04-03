package com.sage.listeners;

import com.sage.activity.interfaces.ICategoryEditListener;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeCategoryBase;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class EditCategoryPopupClickListener implements OnClickListener {

	private EditCategoryPopupHandler popupHandler;

	public EditCategoryPopupClickListener(LayoutInflater inflater, ViewGroup container, RecipeCategory category,
			Activity context) {
		popupHandler = new EditCategoryPopupHandler(inflater, container, category, context);

	}

	@Override
	public void onClick(View v) {
		popupHandler.handleEditCategory();
	}

	public void notifySaveCategory(RecipeCategoryBase category) {
		this.popupHandler.notifySaveCategory(category);
	}

	public void registerListener(ICategoryEditListener listener) {
		this.popupHandler.registerListener(listener);

	}

	public void unRegisterListener(ICategoryEditListener listener) {
		this.popupHandler.unRegisterListener(listener);

	}

}
