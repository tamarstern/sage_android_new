package com.sage.activities;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myfirstapp.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.services.FindUserService;
import com.sage.utils.ActivityUtils;

public class ForgotPasswordActivity extends Activity {

	private EditText emailEditText;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_forgot_password);

		emailEditText = (EditText) findViewById(R.id.email);

		String username = emailEditText.getText().toString();
		if (!TextUtils.isEmpty(username)) {
			emailEditText.setText("");
		}

		Button continueButton = (Button) findViewById(R.id.continue_button);

		final Activity activity = this;
		continueButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				LoginValidator validator = new LoginValidator(activity);
				boolean isValidLogin = validator.validateUsername(emailEditText);
				if (isValidLogin) {
					String userName = emailEditText.getText().toString();
					new FindUserTask(activity).execute(userName);
				}
			}
		});
	}

	private void startResetPasswordActivity() {
		String userName = emailEditText.getText().toString();
		ActivityUtils.startResetPasswordActivity(this, userName);
	}

	private class FindUserTask extends AsyncTask<String, Void, JsonElement> {

		private Activity activity;
		private ProgressDialogContainer container;

		public FindUserTask(Activity activity) {
			this.activity = activity;
			container = new ProgressDialogContainer(activity);
		}
		
		@Override
		protected void onPreExecute() {
			container.showProgress();
		}
		
		@Override
		protected JsonElement doInBackground(String... params) {

			try {
				String username = params[0];
				FindUserService service = new FindUserService(username);
				return service.findUser();
			} catch (Exception e) {
				ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
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

			boolean userFound = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();

			if (userFound) {
				startResetPasswordActivity();
			} else {
				Toast.makeText(getApplicationContext(), "Inserted email does not registered in the system.",
						Toast.LENGTH_LONG).show();
			}
		}

	}
}
