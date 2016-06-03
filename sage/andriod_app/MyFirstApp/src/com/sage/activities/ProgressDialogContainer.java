package com.sage.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

public class ProgressDialogContainer {

	private ProgressDialog progressDialog;

	private Context context;

	public ProgressDialogContainer(Context context) {
		this.context = context;
	}

	public void showProgress() {
		if (progressDialog != null && progressDialog.isShowing())
			dismissProgress();

		progressDialog = new ProgressDialog(context, R.style.ProgressSpinner);
		progressDialog.setCancelable(false);
		progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
		progressDialog.show();
	}

	public void dismissProgress() {
		try {
			if (progressDialog != null && progressDialog.isShowing()) {
				progressDialog.dismiss();
				progressDialog = null;
			}
		} catch (IllegalArgumentException e) {
			Log.e("dismissDialog", "illegal argumant exception on dissmiss dialog", e);
		} finally {
			progressDialog = null;
		}

	}

}
