package com.sage.constants;

public enum ImageType {
	RECIPE_PICTURE("recipePicture"),
	IMAGE_RECIPE_PICTURE("imageTypeRecipe");

	private String value;
	ImageType(String value){
		this.value = value;
	}
	public Object getValue() {
		// TODO Auto-generated method stub
		return value;
	}

}
