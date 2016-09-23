package com.sage.listeners;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.sage.activity.interfaces.ICategoryEditListener;
import com.sage.entities.RecipeCategory;

public class EditCategoryPopupClickListener implements OnClickListener {

	private EditCategoryPopupHandler popupHandler;
	private RecipeCategory category;
	public EditCategoryPopupClickListener(LayoutInflater inflater, ViewGroup container, RecipeCategory category,
			Activity context) {
		this.category = category;
		popupHandler = new EditCategoryPopupHandler(inflater, container, context);

	}

	@Override
	public void onClick(View v) {
		popupHandler.handleEditCategory(category);
	}

	public void notifySaveCategory(RecipeCategory category) {
		this.popupHandler.notifySaveCategory(category);
	}

	public void registerListener(ICategoryEditListener listener) {
		this.popupHandler.registerListener(listener);

	}

	public void unRegisterListener(ICategoryEditListener listener) {
		this.popupHandler.unRegisterListener(listener);

	}

}
