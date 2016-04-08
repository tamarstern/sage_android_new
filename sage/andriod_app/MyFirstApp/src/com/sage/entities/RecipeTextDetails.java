package com.sage.entities;

public class RecipeTextDetails extends RecipeDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String ingredients;

	private String preparationDescription;

	private String pictureId;
	
	//private transient Bitmap image;
	
	
	
	public RecipeTextDetails() {
		super();
		this.setRecipeType(RecipeType.TEXT);
	}

	public RecipeTextDetails(String title, String ingredients, String preparation, String comments, String pictureId) {
		super(title, comments);
		this.ingredients = ingredients;
		this.preparationDescription = preparation;
		this.setPictureId(pictureId);
		this.setRecipeType(RecipeType.TEXT);
	}

	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public String getPreparationDescription() {
		return preparationDescription;
	}

	public void setPreparationDescription(String preparation) {
		this.preparationDescription = preparation;
	}

	public String getPictureId() {
		return pictureId;
	}

	public void setPictureId(String pictureId) {
		this.pictureId = pictureId;
	}

	}
