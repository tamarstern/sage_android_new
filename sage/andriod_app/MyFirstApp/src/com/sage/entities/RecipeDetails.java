package com.sage.entities;

import java.io.Serializable;
import java.util.Random;

import android.graphics.Bitmap;

public class RecipeDetails implements Serializable {

	private static final long serialVersionUID = 672748343523798530L;

	private String _id;

	private String header;

	private String preparationComments;

	private int rating;

	private int likes_count;

	private int comments_count;

	private RecipeType recipeType;

	private boolean published;

	private String userId;
	
	private String userObjectId;
	
	private String ownerObjectId;
	
	private String ownerDisplayName;
	
	private String ownerUserName;
	
	private transient Bitmap image;
	
	private boolean userLikeRecipe;
	
	private boolean recipeChanges;	

	public RecipeDetails() {

	}

	public RecipeDetails(String title, String comments) {
		this.header = title;
		this.preparationComments = comments;
		Random random = new Random();
		rating = random.nextInt(5 - 0) + 0;
	}

	public String getHeader() {
		return header;
	}

	public void setHeader(String title) {
		this.header = title;
	}

	public String getPreparationComments() {
		return preparationComments;
	}

	public void setPreparationComments(String comments) {
		this.preparationComments = comments;
	}

	public int getRating() {
		return rating;
	}

	public void setRating(int rating) {
		this.rating = rating;
	}

	public RecipeType getRecipeType() {
		return recipeType;
	}

	public void setRecipeType(RecipeType type) {
		this.recipeType = type;
	}

	public boolean isPublished() {
		return published;
	}

	public void setPublished(boolean published) {
		this.published = published;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public int getLikes_count() {
		return likes_count;
	}

	public void setLikes_count(int likes_count) {
		this.likes_count = likes_count;
	}

	public int getComments_count() {
		return comments_count;
	}

	public void setComments_count(int comments_count) {
		this.comments_count = comments_count;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}		
		RecipeDetails other = (RecipeDetails) obj;
		return this._id.equals(other._id);
				

	}

	@Override
	public int hashCode() {
		  return (int) _id.hashCode();
	}

	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public boolean isUserLikeRecipe() {
		return userLikeRecipe;
	}

	public void setUserLikeRecipe(boolean userLikeRecipe) {
		this.userLikeRecipe = userLikeRecipe;
	}

	public boolean isRecipeChanges() {
		return recipeChanges;
	}

	public void setRecipeChanges(boolean recipeChanges) {
		this.recipeChanges = recipeChanges;
	}

	public String getOwnerObjectId() {
		return ownerObjectId;
	}

	public void setOwnerObjectId(String ownerObjectId) {
		this.ownerObjectId = ownerObjectId;
	}

	public String getOwnerDisplayName() {
		return ownerDisplayName;
	}

	public void setOwnerDisplayName(String ownerDisplayName) {
		this.ownerDisplayName = ownerDisplayName;
	}

	public String getOwnerUserName() {
		return ownerUserName;
	}

	public void setOwnerUserName(String ownerUserName) {
		this.ownerUserName = ownerUserName;
	}

	public String getUserObjectId() {
		return userObjectId;
	}

	public void setUserObjectId(String userObjectId) {
		this.userObjectId = userObjectId;
	}


}
