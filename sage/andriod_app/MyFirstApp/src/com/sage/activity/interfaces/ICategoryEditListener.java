package com.sage.activity.interfaces;

import com.sage.entities.RecipeCategory;

public interface ICategoryEditListener {

	void onSaveCategory(RecipeCategory category);
	
	void onDeleteCategory(RecipeCategory category);

}
