package com.sage.listeners;

import java.util.HashSet;
import java.util.Set;

import com.example.myfirstapp.NewsfeedActivity;
import com.example.myfirstapp.R;
import com.sage.activity.interfaces.IExitWithoutSaveListener;
import com.sage.utils.ActivityUtils;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupWindow;

public class ExitRecipeWithoutSavingPopupHandler {

	private LayoutInflater inflater;
	private ViewGroup container;
	private View savePublishRecipe;
	private Activity context;
	private Set<IExitWithoutSaveListener> listeners = new HashSet<IExitWithoutSaveListener>();

	public ExitRecipeWithoutSavingPopupHandler(LayoutInflater inflater, ViewGroup container, View savePublishRecipe,
			Activity context) {
		this.inflater = inflater;
		this.container = container;
		this.savePublishRecipe = savePublishRecipe;
		this.context = context;
	}

	public void handleExitWithoutSave() {
		View popupView = inflater.inflate(R.layout.exit_without_saving_recipe_popup, container, false);
		final PopupWindow popupWindow = new PopupWindow(popupView, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				true);
		popupWindow.showAtLocation(savePublishRecipe, Gravity.CENTER, 0, 0);
		ActivityUtils.InitPopupWindowWithEventHandler(popupWindow, context);

		Button goBackToRecipe = (Button) popupView.findViewById(R.id.go_back_to_recipe);
		goBackToRecipe.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				popupWindow.dismiss();
			}
		});

		Button cancelDelete = (Button) popupView.findViewById(R.id.exit_without_saving);
		cancelDelete.setOnClickListener(new OnClickListener() {

			public void onClick(View popupView) {
				popupWindow.dismiss();
				for (IExitWithoutSaveListener listener : listeners) {
					listener.onExistWithoutSave();
				}
			}
		});

	}

	public void registerListener(IExitWithoutSaveListener listener) {
		listeners.add(listener);
	}

	public void unRegisterListener(IExitWithoutSaveListener listener) {
		listeners.remove(listener);
	}

}
