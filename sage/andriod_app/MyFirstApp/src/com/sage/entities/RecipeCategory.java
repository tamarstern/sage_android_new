package com.sage.entities;

import java.util.ArrayList;
import java.util.List;

public class RecipeCategory extends RecipeCategoryBase {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4174511217569167918L;
	private List<RecipeSubCategory> subCategories = new ArrayList<RecipeSubCategory>();
	private List<RecipePublished> recipes;	

	public List<RecipeSubCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<RecipeSubCategory> subCategories) {
		this.subCategories = subCategories;
	}

	public List<RecipePublished> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<RecipePublished> recipes) {
		this.recipes = recipes;
	}

}
