package com.sage.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.sage.activities.R;
import com.sage.constants.ServicesConstants;
import com.sage.entities.RecipeDetails;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.RequestCreator;

import java.text.MessageFormat;

public class ImagesInitializer {

	private static CachedMap<String, Drawable> drawablesMap = new CachedMap<String, Drawable>(50);

	public static void initialRecipeImage(final Context context, String pictureID, ImageView imageView, ProgressBar progressBar) {
		if (pictureID == null) {
			imageView.setImageResource(R.drawable.default_recipe_image);
			imageView.setVisibility(View.VISIBLE);
			progressBar.setVisibility(View.GONE);
		} else {
			String url = getUrl( pictureID);
			initImage(context, imageView, progressBar,url);
			imageView.setVisibility(View.VISIBLE);

		}

	}

	@NonNull
	public static String getUrl(String pictureID) {
		String url =  MessageFormat.format(ServicesConstants.PICTURE_URL_GET, pictureID);

		return url;
	}

	public static void initImage(final Context context, final ImageView imageView,final ProgressBar progressBar, final String url) {
		if (drawablesMap.containsKey(url)) {
			Drawable drawable = drawablesMap.get(url);
			if (drawable != null) {
				Drawable clone = drawable.getConstantState().newDrawable();
				imageView.setImageDrawable(clone);
				progressBar.setVisibility(View.GONE);
				return;
			}
		}

		progressBar.setVisibility(View.VISIBLE);
		imageView.setVisibility(View.GONE);


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

						progressBar.setVisibility(View.GONE);
						imageView.setVisibility(View.VISIBLE);

						Drawable drawable = imageView.getDrawable();
						drawablesMap.put(url, drawable);
					}

					@Override
					public void onError() {

						progressBar.setVisibility(View.GONE);
						imageView.setVisibility(View.VISIBLE);
						Log.e("failed to load image", "failed to load image");
					}
				});
	}

	public static void initRecipeMainPicture(ImageView image,ProgressBar progressBar, RecipeDetails details, Activity context) {
		String id = CacheUtils.getRecipeMainPictureId(details);
		ImagesInitializer.initialRecipeImage(context, id, image, progressBar);
	}





}
