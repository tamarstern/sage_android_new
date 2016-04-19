package com.sage.entities;

import java.util.ArrayList;
import java.util.List;

public class RecipeCategory extends RecipeCategoryBase implements Comparable<RecipeCategory> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4174511217569167918L;
	private List<RecipeSubCategory> subCategories = new ArrayList<RecipeSubCategory>();
	private List<RecipeDetails> recipes;

	public List<RecipeSubCategory> getSubCategories() {
		return subCategories;
	}

	public void setSubCategories(List<RecipeSubCategory> subCategories) {
		this.subCategories = subCategories;
	}

	public List<RecipeDetails> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<RecipeDetails> recipes) {
		this.recipes = recipes;
	}


	@Override
	public int compareTo(RecipeCategory another) {
		return this.getHeader().compareTo(another.getHeader());
	}
}
