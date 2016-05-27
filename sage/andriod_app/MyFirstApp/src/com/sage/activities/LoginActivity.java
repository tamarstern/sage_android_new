package com.sage.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.services.AuthenticateWithTokenService;
import com.sage.services.LoginService;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.LoginUtility;

;

public class LoginActivity extends Activity {

	private CallbackManager callbackManager;

	private EditText usernameEditText;
	private EditText passwordEditText;
	private boolean loginSuccess = false;
	private String token;
	private String userDisplayName;
	private String userObjectId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		FacebookSdk.sdkInitialize(getApplicationContext());
		callbackManager = CallbackManager.Factory.create();
		setContentView(R.layout.activity_login);


		String token = getDirectAuthenticationToken();
		if (!TextUtils.isEmpty(token)) {
			initLoginFormVisibility(View.GONE);
			AnalyticsUtils.sendAnalyticsTrackingEvent(this, AnalyticsUtils.ENTER_APP_WHEN_ALREADY_LOGGED_IN);
			new AuthenticateWithTokenTask(this).execute(token);


		} else {
			initLoginFormUi();

		}
	}

	private void initLoginFormUi() {

		initLoginFormVisibility(View.VISIBLE);

		usernameEditText = (EditText) findViewById(R.id.email);
		passwordEditText = (EditText) findViewById(R.id.password);

		initFacebookLoginButton();

		initDirectAuthenticationLoginButton();

		initRegisterLoginButton();

		initForgotPasswordLink();
	}



	private String getDirectAuthenticationToken() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		return sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
	}

	private void initFacebookLoginButton() {
		
		//TextView orLabel = (TextView)findViewById(R.id.label_or);
		//orLabel.setVisibility(View.GONE);
		
		
		LoginButton facebookLoginButton = (LoginButton) findViewById(R.id.facebook_login_button);
		facebookLoginButton.setReadPermissions("user_friends");
		facebookLoginButton.setVisibility(View.GONE);
		facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				continueToNextActivity(false);
			}

			@Override
			public void onCancel() {
				Toast.makeText(getApplicationContext(), "Login Canceled!", Toast.LENGTH_LONG).show();

			}

			@Override
			public void onError(FacebookException exception) {
				Toast.makeText(getApplicationContext(), "Login Failed with exception!", Toast.LENGTH_LONG).show();
			}
		});
	}

	private void openNewsfeedIfAlreadyLoggedInToFacebook() {
		AccessToken fb_token = AccessToken.getCurrentAccessToken();
		if (fb_token != null) {
			boolean termsSigned = EntityUtils.signedTerms(this, null);
			continueToNextActivity(termsSigned);
		}
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_HOME);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		callbackManager.onActivityResult(requestCode, resultCode, data);
	}

	private void initRegisterLoginButton() {
		Button button = (Button) findViewById(R.id.register_now_button);
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), RegisterNewUserActivity.class);
				startActivity(intent);

			}
		});

	}

	private void initForgotPasswordLink() {
		TextView forgotPassword = (TextView) findViewById(R.id.label_forgot_password);
		forgotPassword.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getApplicationContext(), ForgotPasswordActivity.class);
				startActivity(intent);

			}
		});

	}

	private void initDirectAuthenticationLoginButton() {
		Button button = (Button) findViewById(R.id.email_sign_in_button);

		final Activity activity = this;
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LoginValidator validator = new LoginValidator(getApplicationContext());
				boolean isValidLogin = validator.validateUserNamePassword(usernameEditText, passwordEditText);
				if (isValidLogin) {
					AnalyticsUtils.sendAnalyticsTrackingEvent(activity, AnalyticsUtils.PRESS_LOGIN_BUTTON);
					new LoginTask(activity, usernameEditText.getEditableText().toString(), passwordEditText.getEditableText().toString()).execute();

				}
			}
		});
	}

	private void continueToNextActivity(boolean signedTerms) {

		if(signedTerms) {
			Intent intent = new Intent(getApplicationContext(), NewsfeedActivity.class);
			intent.putExtra(EntityDataTransferConstants.AFTER_LOGIN, true);
			startActivity(intent);
		} else {
			Intent intent = new Intent(getApplicationContext(), TermsActivity.class);
			startActivity(intent);
		}
	}

	private class LoginTask extends AsyncTask<Void, Void, JsonElement> {

		private Activity activity;
		
		private ProgressDialogContainer container;

		private String username;
		private String password;

		public LoginTask(Activity activity, String username, String password) {
			this.activity = activity;
			this.username = username;
			this.password = password;
			container = new ProgressDialogContainer(activity);
		}
		

		@Override
		protected void onPreExecute() {
			container.showProgress();
		}


		@Override
		protected JsonElement doInBackground(Void... params) {

			try {
				LoginService service = new LoginService(activity);
				return service.login(username, password);
			} catch (Exception e) {
				ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
				final String message = getResources().getString(R.string.login_failed_no_connection);
				activity.runOnUiThread(new Runnable() {
					public void run() {
						Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();

					}
				});
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

			loginSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

			if (loginSuccess) {
				token = resultJsonObject.get(ActivityConstants.TOKEN_ELEMENT_NAME).getAsString();
				userDisplayName = resultJsonObject.get(ActivityConstants.USER_DISPLAY_ELEMENT_NAME).getAsString();
				userObjectId = resultJsonObject.get(ActivityConstants.USER_OBJECT_ID).getAsString();
				JsonElement termsJsonElement = resultJsonObject.get(ActivityConstants.TERMS_SIGNED_SERVER_INDICATION);
				boolean signedTerms = false;
				if(termsJsonElement != null) {
					signedTerms = termsJsonElement.getAsBoolean();
				}
				if(signedTerms) {
					LoginUtility.signTermsAndConditions(getApplicationContext(), username);
					LoginUtility.signatureSentToServer(getApplicationContext());
				}
				saveAuthDetails();
				continueToNextActivity(signedTerms);
			} else {
				Toast.makeText(getApplicationContext(), "Login failed. Incorrect username or password",
						Toast.LENGTH_LONG).show();
			}
		}

		private void saveAuthDetails() {
			LoginUtility.saveAuthDetails(getApplicationContext(), token, userDisplayName,
					usernameEditText.getText().toString(), passwordEditText.getText().toString(), userObjectId);
		}

	}

	private class AuthenticateWithTokenTask extends AsyncTask<String, Void, JsonElement> {

		private Activity activity;

		private ProgressDialogContainer container;

		public AuthenticateWithTokenTask(Activity activity) {

			this.activity = activity;
			container = new ProgressDialogContainer(activity);
		}

		@Override
		protected void onPreExecute() {
			container.showProgress();
		}

		@Override
		protected JsonElement doInBackground(String... token) {

			try {
				AuthenticateWithTokenService service = new AuthenticateWithTokenService(activity);
				String currentToken = token[0];
				return service.authenticateWithToke(currentToken);
			} catch (Exception e) {
				ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
				return null;
			}
		}

		@Override
		protected void onPostExecute(JsonElement result) {
			container.dismissProgress();
			if (result == null) {
				initLoginFormVisibility(View.VISIBLE);
				initLoginFormUi();
				return;
			}

			JsonObject resultJsonObject = result.getAsJsonObject();

			loginSuccess = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

			if (loginSuccess) {
				SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(activity);
				String username = preferences.getString(ActivityConstants.USER_NAME, null);

				boolean signedTerms = EntityUtils.signedTerms(getApplicationContext(),username);
				continueToNextActivity(signedTerms);
			} else {
				Toast.makeText(getApplicationContext(), "Login failed. Incorrect username or password",
						Toast.LENGTH_LONG).show();
			}
		}

	}

	private void initLoginFormVisibility(int visible) {
		ScrollView loginView = (ScrollView) findViewById(R.id.login_form);
		if(loginView != null) {
			loginView.setVisibility(visible);
		}

	}

}
