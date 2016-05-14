package com.sage.listeners;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;

import com.example.myfirstapp.R;
import com.sage.activities.ChangeUserDisplayNameActivity;
import com.sage.activities.LoginActivity;
import com.sage.application.GoogleAnalyticsApplication;
import com.sage.constants.ActivityConstants;
import com.sage.utils.ActivityUtils;
import com.sage.utils.LoginUtility;

public class SettingsPopupHandler {

	private LayoutInflater inflater;
	private ViewGroup container;
	private View savePublishRecipe;
	private Activity context;

	public SettingsPopupHandler(LayoutInflater inflater, ViewGroup container, View savePublishRecipe,
			Activity context) {
		this.inflater = inflater;
		this.container = container;
		this.savePublishRecipe = savePublishRecipe;
		this.context = context;
	}

	public void handle() {
		View popupView = inflater.inflate(R.layout.settings_popup, container, false);
		Rect displayRectangle = new Rect();
		Window window = context.getWindow();
		window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
		final PopupWindow popupWindow = new PopupWindow(popupView, (int) (displayRectangle.width() * 0.6f),
				WindowManager.LayoutParams.WRAP_CONTENT, true);
		popupWindow.showAtLocation(savePublishRecipe, Gravity.CENTER, 0, 0);
		ActivityUtils.InitPopupWindowWithEventHandler(popupWindow, context);

		Button logoutButton = (Button) popupView.findViewById(R.id.logout_panel);
		logoutButton.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				LoginUtility.cleanAuthenticationDetails(context);
				GoogleAnalyticsApplication application = ((GoogleAnalyticsApplication)context.getApplication());
				application.clearAllCaches();
				Intent intent = new Intent(context, LoginActivity.class);
				context.startActivity(intent);
			}
		});

		Button changePasswordButton = (Button) popupView.findViewById(R.id.change_password_panel);
		changePasswordButton.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

				String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);

				ActivityUtils.startResetPasswordActivity(context, userName);
			}
		});

		Button changeDisplayNameButton = (Button) popupView.findViewById(R.id.change_display_name_panel);
		changeDisplayNameButton.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				Intent intent = new Intent(context, ChangeUserDisplayNameActivity.class);
				context.startActivity(intent);
			}
		});

	}

}
