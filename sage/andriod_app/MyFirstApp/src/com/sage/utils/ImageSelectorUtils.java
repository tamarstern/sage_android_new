package com.sage.utils;

import android.app.Activity;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.myfirstapp.R;

public class ImageSelectorUtils {

	private static final int REQUEST_CAMERA = 1;

	private static final int SELECT_FILE = 2;

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int REQUEST_SELECT_FILE = 2;

	public static void selectImage(final Activity context) {
		final CharSequence[] items = {context.getString(R.string.take_photo)
				,context.getString(R.string.choose_from_library), "Cancel" };
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("Add Photo!");
		builder.setItems(items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int item) {
				if (items[item].equals(context.getString(R.string.take_photo))) {
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
					context.startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
				} else if (items[item].equals(context.getString(R.string.choose_from_library))) {
					Intent intent = new Intent(Intent.ACTION_PICK,
							android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
					intent.setType("image/*");
					context.startActivityForResult(Intent.createChooser(intent, context.getString(R.string.select_file)), REQUEST_SELECT_FILE);
				} else if (items[item].equals("Cancel")) {
					dialog.dismiss();
				}
			}
		});
		builder.show();
	}


	public static Bitmap initializeImage(int requestCode, int resultCode, Intent data, ImageView mainPicture,
			Button addImageButton, Activity activity) {
		if (resultCode == Activity.RESULT_OK) {
			mainPicture.setVisibility(View.VISIBLE);
			addImageButton.setVisibility(View.GONE);
			if (requestCode == REQUEST_CAMERA) {
				Uri selectedImageUri = data.getData();
				return setBitmapFromFile(mainPicture, activity, selectedImageUri);
			} else if (requestCode == SELECT_FILE) {
				Uri selectedImageUri = data.getData();
				return setBitmapFromFile(mainPicture, activity, selectedImageUri);
			}
		}
		return null;
	}

	private static Bitmap setBitmapFromFile(ImageView mainPicture, Activity activity, Uri selectedImageUri) {
		String[] projection = { MediaColumns.DATA };
		CursorLoader cursorLoader = new CursorLoader(activity, selectedImageUri, projection, null, null, null);
		Cursor cursor = cursorLoader.loadInBackground();
		int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
		cursor.moveToFirst();
		String selectedImagePath = cursor.getString(column_index);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;
		Bitmap bm = BitmapFactory.decodeFile(selectedImagePath, options);
		Bitmap rotated = ImageResizeUtils.rotateBitmap(selectedImagePath, bm);
		mainPicture.setImageBitmap(rotated);
		return rotated;
	}



}
