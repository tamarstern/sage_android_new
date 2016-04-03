package com.example.myfirstapp;

import android.content.Context;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

public class LoginValidator {

	/**
	 * 
	 */
	private final Context context;

	public LoginValidator(Context context) {
		this.context = context;

	}

	public boolean validateUserNamePassword(EditText userName, EditText password) {

		boolean usernameValid = validateUsername(userName);		
		if(!usernameValid) {
			return false;
		}

		boolean validatePassword = validatePassword(password);
		if(!validatePassword) {
			return false;
		}

		return true;

	}
	
	public boolean comparePassword(EditText password, EditText retypePassword) {
		String passwordStr = password.getText().toString();
		String retypePasswordStr = retypePassword.getText().toString();
		if(!passwordStr.equals(retypePasswordStr)) {
			Toast.makeText(context, "Password not equal to retyped password", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
		
	}

	public boolean validatePassword(EditText password) {
		String passwordStr = password.getText().toString();
		if (TextUtils.isEmpty(passwordStr)) {
			Toast.makeText(context, "You did not enter a password", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}
	
	public boolean validateDisplayName(EditText displayName) {
		String displayNameStr = displayName.getText().toString();
		if (TextUtils.isEmpty(displayNameStr)) {
			Toast.makeText(context, "You did not enter a display name", Toast.LENGTH_SHORT).show();
			return false;
		}
		
		return true;
	}

	public boolean validateUsername(EditText userName) {
		String sUsername = userName.getText().toString();
		if (TextUtils.isEmpty(sUsername)) {
			Toast.makeText(context, "You did not enter a username", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (!android.util.Patterns.EMAIL_ADDRESS.matcher(sUsername).matches()) {
			Toast.makeText(context, "You did not enter a valid email", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

}