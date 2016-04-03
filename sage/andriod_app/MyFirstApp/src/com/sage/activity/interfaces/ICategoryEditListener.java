package com.sage.activity.interfaces;

import com.sage.entities.RecipeCategoryBase;

public interface ICategoryEditListener {

	void onSaveCategory(RecipeCategoryBase category);
	
	void onDeleteCategory(RecipeCategoryBase category);

}
