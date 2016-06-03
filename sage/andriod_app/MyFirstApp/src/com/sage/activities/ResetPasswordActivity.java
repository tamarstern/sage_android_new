package com.sage.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.sage.constants.ActivityConstants;
import com.sage.tasks.UpdateUserTask;
import com.sage.utils.LoginUtility;
import com.sage.activities.R;

public class ResetPasswordActivity extends Activity {

	private LoginValidator validator;

	private EditText password;

	private EditText retypePassword;

	private String username;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reset_password);

		username = getIntent().getSerializableExtra(ActivityConstants.USER_NAME).toString();

		password = (EditText) findViewById(R.id.type_password);

		retypePassword = (EditText) findViewById(R.id.retype_password);

		validator = new LoginValidator(this);

		final Activity activity = this;

		Button resetPasswordButton = (Button) findViewById(R.id.reset_password_button);

		resetPasswordButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				boolean isPasswordValid = validator.validatePassword(password);
				boolean isPasswordRetypeValid = validator.validatePassword(retypePassword);
				boolean passwordEqual = validator.comparePassword(password, retypePassword);
				if (isPasswordValid && isPasswordRetypeValid && passwordEqual) {
					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

					String objectId = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);

					String[] params = new String[] { username, password.getEditableText().toString(), null };
					new ResetPasswordTask(activity).execute(params);
				}
			}
		});

	}

	private void startLoginActivity() {
		Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
		startActivity(intent);
	}

	public class ResetPasswordTask extends UpdateUserTask {

		private Activity activity;

		public ResetPasswordTask(Activity activity) {
			super(activity);
			this.activity = activity;
		}

		@Override
		public void handleSuccess() {
			LoginUtility.savePassword(activity, password.getEditableText().toString());
			startLoginActivity();

		}

	}
}
