package com.sage.entities;

public class RecipeLinkDetails extends RecipeDetails {
	
	private static final long serialVersionUID = 5922272952878287635L;
	private String url;
	

	private String linkTitle;
	
	private String linkImageUrl;
	
	private String linkSiteName;

	
	public RecipeLinkDetails() {
		super();
		this.setRecipeType(RecipeType.LINK);
	}
	
	public RecipeLinkDetails(String title, String url, String comments) {
		super(title, comments);
		this.url = url;
		this.setRecipeType(RecipeType.LINK);
	}


	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
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

}
