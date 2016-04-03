package com.example.myfirstapp;

import com.sage.constants.ActivityConstants;
import com.sage.tasks.UpdateUserTask;
import com.sage.utils.LoginUtility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChangeUserDisplayNameActivity extends Activity {

	private EditText newDisplayNameEditText;

	private Button changeDisplayNameButton;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_user_display_name);

		final Activity activity = this;

		newDisplayNameEditText = (EditText) findViewById(R.id.enter_new_name);

		changeDisplayNameButton = (Button) findViewById(R.id.change_display_name_button);

		changeDisplayNameButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				LoginValidator validator = new LoginValidator(getApplicationContext());
				boolean isValidDisplayName = validator.validateDisplayName(newDisplayNameEditText);
				if (isValidDisplayName) {
					String displayName = newDisplayNameEditText.getEditableText().toString();
					SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(activity);

					String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);

					String[] params = new String[] { userName, null, displayName };
					new UpdateUserDisplayNameTask(activity).execute(params);

				}
			}
		});

	}

	private void openNewsfeed(final Context applicationContext) {
		Intent intent = new Intent(applicationContext, NewsfeedActivity.class);
		startActivity(intent);
	}

	private class UpdateUserDisplayNameTask extends UpdateUserTask {

		private Activity activity;

		public UpdateUserDisplayNameTask(Activity activity) {
			super(activity);
			this.activity = activity;
		}

		@Override
		public void handleSuccess() {
			LoginUtility.saveUserDisplayName(getApplicationContext(),
					newDisplayNameEditText.getEditableText().toString());
			openNewsfeed(activity);

		}
	}

}
