package com.sage.entities;

import android.util.Log;

public class User implements Comparable<User> , Cloneable{
	
	private String userDisplayName;
	
	private String _id;
	
	private String username;

	public String getUserDisplayName() {
		return userDisplayName;
	}

	public void setUserDisplayName(String userDisplayName) {
		this.userDisplayName = userDisplayName;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String _id) {
		this._id = _id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Override
	public int compareTo(User another) {
		return getUserDisplayName().compareTo(another.getUserDisplayName());
	}

	@Override
	public User clone()  {
		try {
			User details =  (User)super.clone();
			details.userDisplayName = this.userDisplayName;
			details.username = this.username;
			details._id = this._id;
			return details;
		} catch (CloneNotSupportedException e) {
			Log.e("failCloneDetails", "failed to clone details", e);
			return null;
		}

	}


	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		User other = (User) obj;
		return this._id.equals(other._id);

	}

	@Override
	public int hashCode() {
		return (int) _id.hashCode();
	}

}
