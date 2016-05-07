package com.sage.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myfirstapp.R;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.listeners.RecipeDetailsClickListener;
import com.sage.tasks.GetRecipeUrlDetailsTask;
import com.sage.utils.ActivityUtils;
import com.sage.utils.RecipeOwnerContext;

import java.util.ArrayList;
import java.util.List;

public class RecipesForCategoriesArrayAdapter extends ArrayAdapter<RecipeDetails> {

	private final Activity context;
	private List<RecipeDetails> recipes;
	private LayoutInflater inflater;

	public RecipesForCategoriesArrayAdapter(Activity context, ArrayList<RecipeDetails> recipes) {
		super(context, 0, recipes);
		this.context = context;
		this.recipes = new ArrayList<RecipeDetails>(recipes);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_layout_sub_categories, parent, false);

		RecipeDetails recipePublished = recipes.get(position);

		String recipeNameValue = recipePublished.getHeader();

		TextView categoryName = (TextView) rowView.findViewById(R.id.recipe_name);
		categoryName.setText(recipeNameValue);
		
		RelativeLayout recipeLine = (RelativeLayout)rowView.findViewById(R.id.recipe_line);
		if(!recipePublished.isExceptionOnSave()) {
			recipeLine.setOnClickListener(new RecipeDetailsClickListener(context, recipePublished));
		} else {
			recipeLine.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					String message = context.getResources().getString(R.string.recipe_being_saved);
					Toast.makeText(context, message, Toast.LENGTH_LONG).show();
				}
			});
		}


		ImageView recipePublishedIcon = (ImageView) rowView.findViewById(R.id.recipe_published);

		if (!recipePublished.isPublished()) {
			recipePublishedIcon.setVisibility(View.GONE);
		}

		if(recipePublished.getRecipeType().equals(RecipeType.LINK)) {
			Object[] params = ActivityUtils.generateServiceParamObject(context, recipePublished.getUrl());

			new GetRecipeUrlDetails(recipePublished, position).execute(params);

		}

		return rowView;

	}


	private class GetRecipeUrlDetails extends GetRecipeUrlDetailsTask {

		private RecipeDetails recipeBasicData;
		private int position;

		public GetRecipeUrlDetails(RecipeDetails recipeBasicData, int position) {
			super(context);
			this.recipeBasicData = recipeBasicData;
			this.position = position;

		}

		@Override
		protected void handleSuccess(JsonObject resultJsonObject) {
			String urlTitle;
			JsonElement urlTitleJson = resultJsonObject.get(ActivityConstants.URL_TITLE_ELEMENT_NAME);
			if (urlTitleJson != null) {
				urlTitle = urlTitleJson.getAsString();
				recipeBasicData.setLinkTitle(urlTitle);
			}

			String urlImage;
			JsonElement urlImageJson = resultJsonObject.get(ActivityConstants.URL_IMAGE_ELEMENT_NAME);
			if (urlImageJson != null) {
				urlImage = urlImageJson.getAsString();
				recipeBasicData.setLinkImageUrl(urlImage);
			}

			String siteName;
			JsonElement urlSiteJson = resultJsonObject.get(ActivityConstants.URL_SITE_ELEMENT_NAME);
			if (urlSiteJson != null) {
				siteName = urlSiteJson.getAsString();
				RecipeOwnerContext.setOwnerForUrl(recipeBasicData.getUrl(), siteName);
				recipeBasicData.setLinkSiteName(siteName);
			}

			recipeBasicData.setLinkDataInitialized(true);

		}
	}

}
