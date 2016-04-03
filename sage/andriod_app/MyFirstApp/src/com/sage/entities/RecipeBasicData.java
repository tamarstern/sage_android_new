package com.sage.entities;

import java.io.Serializable;

public class RecipeBasicData implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String _id;
	
	private String header;

	private String pictureId;

	private int likes_count;

	private int comments_count;
	
	private RecipeType recipeType;
	
	private boolean userLikeRecipe;
	
	private String url;
	
	private String linkTitle;
	
	private String linkImageUrl;
	
	private String linkSiteName;
	
	private boolean linkDataInitialized;
	
	private boolean featuredRecipe;
	
	private String ownerObjectId;
	
	private String ownerDisplayName;
	
	private String ownerUserName;

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}
	
	public String getPictureId() {
		return pictureId;
	}

	public void setPictureId(String pictureId) {
		this.pictureId = pictureId;
	}



	public int getComments_count() {
		return comments_count;
	}

	public void setComments_count(int comments_count) {
		this.comments_count = comments_count;
	}

	public int getLikes_count() {
		return likes_count;
	}

	public void setLikes_count(int likes_count) {
		this.likes_count = likes_count;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public RecipeType getRecipeType() {
		return recipeType;
	}

	public void setRecipeType(RecipeType recipeType) {
		this.recipeType = recipeType;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}		
		RecipeBasicData other = (RecipeBasicData) obj;
		return this._id.equals(other._id);
				

	}

	@Override
	public int hashCode() {
		  return (int) _id.hashCode();
	}

	public boolean isUserLikeRecipe() {
		return userLikeRecipe;
	}

	public void setUserLikeRecipe(boolean userLikeRecipe) {
		this.userLikeRecipe = userLikeRecipe;
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

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

	public String getLinkSiteName() {
		return linkSiteName;
	}

	public void setLinkSiteName(String linkSiteName) {
		this.linkSiteName = linkSiteName;
	}

}
