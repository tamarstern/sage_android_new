package com.sage.entities;

import android.graphics.Bitmap;

import java.io.Serializable;

public class RecipeDetails implements Serializable, Comparable<RecipeDetails> {

	private static final long serialVersionUID = 672748343523798530L;

	private String _id;

	private String header;

	private String preparationComments;

	private int likes_count;

	private int comments_count;

	private RecipeType recipeType;

	private boolean published;

	private String userId;
	
	private String userObjectId;

	private String categoryId;

	private String userDisplayName;
	
	private String ownerObjectId;
	
	private String ownerDisplayName;
	
	private String ownerUserName;
	
	private transient Bitmap image;
	
	private boolean userLikeRecipe;
	
	private boolean recipeChanges;

	private String ingredients;

	private String preparationDescription;

	private String pictureId;

	private boolean linkDataInitialized;

	private boolean featuredRecipe;

	private String imageRecipe_pictureId;

	private transient Bitmap recipeAsPictureImage;

	private String linkTitle;

	private String linkImageUrl;

	private String linkSiteName;

	private String url;

	private boolean linkUiInitialized;


	public RecipeDetails() {

	}

	public RecipeDetails(String title, String comments) {
		this.header = title;
		this.preparationComments = comments;
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
		  return _id.hashCode();
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


	public String getIngredients() {
		return ingredients;
	}

	public void setIngredients(String ingredients) {
		this.ingredients = ingredients;
	}

	public String getPreparationDescription() {
		return preparationDescription;
	}

	public void setPreparationDescription(String preparationDescription) {
		this.preparationDescription = preparationDescription;
	}

	public String getPictureId() {
		return pictureId;
	}

	public void setPictureId(String pictureId) {
		this.pictureId = pictureId;
	}

	public boolean isLinkDataInitialized() {
		return linkDataInitialized;
	}

	public void setLinkDataInitialized(boolean linkDataInitialized) {
		this.linkDataInitialized = linkDataInitialized;
	}

	public boolean isFeaturedRecipe() {
		return featuredRecipe;
	}

	public void setFeaturedRecipe(boolean featuredRecipe) {
		this.featuredRecipe = featuredRecipe;
	}

	public String getImageRecipe_pictureId() {
		return imageRecipe_pictureId;
	}

	public void setImageRecipe_pictureId(String imageRecipe_pictureId) {
		this.imageRecipe_pictureId = imageRecipe_pictureId;
	}

	public Bitmap getRecipeAsPictureImage() {
		return recipeAsPictureImage;
	}

	public void setRecipeAsPictureImage(Bitmap recipeAsPictureImage) {
		this.recipeAsPictureImage = recipeAsPictureImage;
	}

	public String getLinkTitle() {
		return linkTitle;
	}

	public void setLinkTitle(String linkTitle) {
		this.linkTitle = linkTitle;
	}

	public String getLinkImageUrl() {
		return linkImageUrl;
	}

	public void setLinkImageUrl(String linkImageUrl) {
		this.linkImageUrl = linkImageUrl;
	}

	public String getLinkSiteName() {
		return linkSiteName;
	}

	public void setLinkSiteName(String linkSiteName) {
		this.linkSiteName = linkSiteName;
	}

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isLinkUiInitialized() {
		return linkUiInitialized;
	}

	public void setLinkUiInitialized(boolean linkUiInitialized) {
		this.linkUiInitialized = linkUiInitialized;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	@Override
	public int compareTo(RecipeDetails another) {
		return this.getHeader().compareTo(another.getHeader());
	}
}
