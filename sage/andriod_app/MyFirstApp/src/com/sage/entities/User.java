package com.sage.entities;

public class User implements Comparable<User> {
	
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
