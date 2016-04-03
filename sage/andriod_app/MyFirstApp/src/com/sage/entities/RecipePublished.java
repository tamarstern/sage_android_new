package com.sage.entities;

import java.io.Serializable;

public class RecipePublished implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7135642544852966586L;

	private String _id;
	
	private String header;
	
	private boolean published;

	public String getHeader() {
		return header;
	}

	public void setHeader(String recipeName) {
		this.header = recipeName;
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
		

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		SageEntity other = (SageEntity) obj;
		return this._id.equals(other._id);

	}

	@Override
	public int hashCode() {
		return (int) _id.hashCode();
	}

}
