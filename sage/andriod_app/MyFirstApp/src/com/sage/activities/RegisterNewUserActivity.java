package com.sage.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.services.RegisterNewService;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.LoginUtility;

public class RegisterNewUserActivity extends Activity {
	private EditText usernameEditText;
	private EditText passwordEditText;
	private EditText userDisplayName;
	private boolean registrationSuccess;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register_new_user);
		usernameEditText = (EditText) findViewById(R.id.email_register);
		passwordEditText = (EditText) findViewById(R.id.password_register);
		userDisplayName = (EditText) findViewById(R.id.user_name_register);
		passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					performRegisterNewUser();
					return true;
				}
				return false;
			}
		});
		initDirectAuthenticationRegisterButton();
	}

	private void initDirectAuthenticationRegisterButton() {
		Button button = (Button) findViewById(R.id.action_register);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				performRegisterNewUser();
			}
		});
	}

	private void performRegisterNewUser() {
		LoginValidator validator = new LoginValidator(getApplicationContext());
		boolean isValidDisplayName = validator.validateDisplayName(userDisplayName);
		if (!isValidDisplayName) {
			return;
		}
		boolean isValidLogin = validator.validateUserNamePassword(usernameEditText, passwordEditText);
		if (isValidLogin) {
			AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.PRESS_REGISTER_USER_BUTTON);
			new RegisterNewUserTask(this, userDisplayName.getEditableText().toString(),
					usernameEditText.getEditableText().toString(),
					passwordEditText.getEditableText().toString()).execute();
		}
	}


	private void startLoginActivity() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
	}

	private class RegisterNewUserTask extends AsyncTask<Void, Void, JsonElement> {

		private Activity activity;
		private ProgressDialogContainer container;
		private String userDisplayName;
		private String userName;
		private String password;

		public RegisterNewUserTask(Activity activity, String userDisplayName, String userName, String password) {
			this.activity = activity;
			container = new ProgressDialogContainer(activity);
			this.userDisplayName = userDisplayName;
			this.userName = userName;
			this.password = password;
		}

		@Override
		protected void onPreExecute() {
			container.showProgress();
		}

		@Override
		protected JsonElement doInBackground(Void... params) {

			try {
				RegisterNewService service = new RegisterNewService(activity);
				return service.registerNewUser(userDisplayName,
						userName, password);
			} catch (Exception e) {
				container.dismissProgress();
				ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			container.dismissProgress();
			if (result == null) {
				displayFailedRegisterMessage();
				return;
			}
			JsonObject resultJsonObject = result.getAsJsonObject();
			registrationSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
			if (registrationSuccess) {
				//sendWelcomeEmail();
				LoginUtility.saveAuthDetails(getApplicationContext(), null, userDisplayName,
						usernameEditText.getText().toString(), passwordEditText.getText().toString(), null);
				startLoginActivity();
			} else {
				displayFailedRegisterMessage();
				return;
			}

		}

	}

	private void displayFailedRegisterMessage() {
		final String message = getResources().getString(R.string.failed_register_new_user);
		Toast.makeText(RegisterNewUserActivity.this, message, Toast.LENGTH_LONG).show();
		return;
	}

}
