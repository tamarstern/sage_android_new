package com.sage.entities;

public class RecipeUserBasicData extends RecipeBasicData {
	
	private static final long serialVersionUID = -7044473004867911786L;
	private String userId;
	
	private String userObjectId;
	
	private String userDisplayName;
	
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getUserObjectId() {
		return userObjectId;
	}
	public void setUserObjectId(String userObjectId) {
		this.userObjectId = userObjectId;
	}
	public String getUserDisplayName() {
		return userDisplayName;
	}
	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}


}
