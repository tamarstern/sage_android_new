package com.sage.entities;

import java.util.List;

public class RecipeSubCategory extends RecipeCategoryBase {

	private static final long serialVersionUID = -6010571716067304516L;
	
	private String categoryId;
	
	private List<RecipeDetails> recipes;

	public List<RecipeDetails> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<RecipeDetails> recipeNames) {
		this.recipes = recipeNames;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

}
