package com.example.myfirstapp;

import java.io.IOException;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.services.RegisterNewService;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
		initDirectAuthenticationRegisterButton();
	}

	private void initDirectAuthenticationRegisterButton() {
		Button button = (Button) findViewById(R.id.action_register);

		final Activity activity = this;
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LoginValidator validator = new LoginValidator(getApplicationContext());
				boolean isValidDisplayName = validator.validateDisplayName(userDisplayName);
				if (!isValidDisplayName) {
					return;
				}
				boolean isValidLogin = validator.validateUserNamePassword(usernameEditText, passwordEditText);
				if (isValidLogin) {
					AnalyticsUtils.sendAnalyticsTrackingEvent(activity, AnalyticsUtils.PRESS_REGISTER_USER_BUTTON);
					new RegisterNewUserTask(activity).execute();
				}
			}
		});
	}

	private void startLoginActivity() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
	}

	private void sendWelcomeEmail() {
		Intent i = new Intent(Intent.ACTION_SEND);
		i.setType("message/rfc822");
		i.putExtra(Intent.EXTRA_EMAIL, new String[] { usernameEditText.getText().toString() });
		i.putExtra(Intent.EXTRA_SUBJECT, "Welcome to sage!");
		i.putExtra(Intent.EXTRA_TEXT, "Welcome to sage!");
		try {
			startActivity(Intent.createChooser(i, "Send mail..."));
		} catch (android.content.ActivityNotFoundException ex) {
			Toast.makeText(RegisterNewUserActivity.this, "There are no email clients installed.", Toast.LENGTH_SHORT)
					.show();
		}
	}

	private class RegisterNewUserTask extends AsyncTask<Void, Void, JsonElement> {

		private Activity activity;
		private ProgressDialogContainer container;

		public RegisterNewUserTask(Activity activity) {
			this.activity = activity;
			container = new ProgressDialogContainer(activity);
		}

		@Override
		protected void onPreExecute() {
			container.showProgress();
		}

		@Override
		protected JsonElement doInBackground(Void... params) {

			try {
				RegisterNewService service = new RegisterNewService(activity);
				return service.registerNewUser(userDisplayName.getText().toString(),
						usernameEditText.getText().toString(), passwordEditText.getText().toString());
			} catch (Exception e) {
				container.dismissProgress();
				ActivityUtils.HandleConnectionUnsuccessfullToServer(activity);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			container.dismissProgress();
			if (result == null) {
				return;
			}
			JsonObject resultJsonObject = result.getAsJsonObject();
			registrationSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
			if (registrationSuccess) {
				sendWelcomeEmail();
				startLoginActivity();
			}

		}

	}

}
