package com.sage.entities;

import java.io.Serializable;

public class RecipeCategoryBase implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4099435042944360226L;
	private String header;

	private String _id;
	private String userId;

	public String getHeader() {
		return header;
	}

	public void setHeader(String categoryName) {
		this.header = categoryName;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
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
		RecipeCategoryBase other = (RecipeCategoryBase) obj;
		return this._id.equals(other._id);

	}

	@Override
	public int hashCode() {
		return (int) _id.hashCode();
	}

}
