package com.sage.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.myfirstapp.R;
import com.sage.constants.ActivityConstants;
import com.sage.constants.ImageType;
import com.sage.constants.ServicesConstants;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.MessageFormat;

public class ImagesInitializer {

	private static CachedMap<String, Drawable> drawablesMap = new CachedMap<String, Drawable>(35);

	public static void initialRecipeImage(final Context context, String pictureID, ImageView imageView, ImageType imageType) {
		if (pictureID == null) {
			imageView.setImageResource(R.drawable.default_recipe_image);
			imageView.setVisibility(View.VISIBLE);
			// use default picture
		} else {
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
			String url = MessageFormat.format(ServicesConstants.PICTURE_URL, pictureID,imageType, token);
			initImage(context, imageView, url);
			imageView.setVisibility(View.VISIBLE);

		}

	}

	public static void initImage(final Context context, final ImageView imageView, final String url) {
		if (drawablesMap.containsKey(url)) {
			Drawable drawable = drawablesMap.get(url);
			if (drawable != null) {
				Drawable clone = drawable.getConstantState().newDrawable();
				imageView.setImageDrawable(clone);
				return;
			}
		}


		Picasso.Builder builder = new Picasso.Builder(context);

		builder.listener(new Picasso.Listener() {
			@Override
			public void onImageLoadFailed(Picasso arg0, Uri arg1, Exception arg2) {
				String e = arg2.getMessage();
				Log.e("failed to load image", e);
			}

		});
		RequestCreator load = builder.build().with(context).load(url).error(R.drawable.default_recipe_image);
		load.into(imageView, new com.squareup.picasso.Callback() {
					@Override
					public void onSuccess() {
						Drawable drawable = imageView.getDrawable();
						drawablesMap.put(url, drawable);
					}

					@Override
					public void onError() {
						Log.e("failed to load image", "failed to load image");
					}
				});
	}

	
}
