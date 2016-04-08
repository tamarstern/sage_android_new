package com.sage.entities;

import android.graphics.Bitmap;

public class RecipePictureDetails extends RecipeTextDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 371706336666368798L;

	private String imageRecipe_pictureId;
	
	private transient Bitmap recipeAsPictureImage;

	public RecipePictureDetails() {

		super();
		this.setRecipeType(RecipeType.PICTURE);
	}

	
	public Bitmap getRecipeAsPictureImage() {
		return recipeAsPictureImage;
	}

	public void setRecipeAsPictureImage(Bitmap recipeAsPictureImage) {
		this.recipeAsPictureImage = recipeAsPictureImage;
	}


	public String getImageRecipe_pictureId() {
		return imageRecipe_pictureId;
	}


	public void setImageRecipe_pictureId(String imageRecipe_pictureId) {
		this.imageRecipe_pictureId = imageRecipe_pictureId;
	}

}
