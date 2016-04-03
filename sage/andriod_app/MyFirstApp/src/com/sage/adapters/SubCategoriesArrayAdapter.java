package com.sage.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.myfirstapp.R;
import com.sage.entities.RecipeBasicData;
import com.sage.entities.RecipePublished;
import com.sage.listeners.RecipeDetailsClickListener;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SubCategoriesArrayAdapter extends ArrayAdapter<String> {

	private final Activity context;
	private List<RecipePublished> recipes;
	private LayoutInflater inflater;

	public SubCategoriesArrayAdapter(Activity context, Collection<RecipePublished> recipes, String[] headers) {
		super(context, -1, headers);
		this.context = context;
		this.recipes = new ArrayList<RecipePublished>(recipes);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_layout_sub_categories, parent, false);

		RecipePublished recipePublished = recipes.get(position);

		String recipeNameValue = recipePublished.getHeader();

		TextView categoryName = (TextView) rowView.findViewById(R.id.recipe_name);
		categoryName.setText(recipeNameValue);
		
		RelativeLayout recipeLine = (RelativeLayout)rowView.findViewById(R.id.recipe_line);
		recipeLine.setOnClickListener(new RecipeDetailsClickListener(context, recipePublished.get_id()));

		ImageView recipePublishedIcon = (ImageView) rowView.findViewById(R.id.recipe_published);

		if (!recipePublished.isPublished()) {
			recipePublishedIcon.setVisibility(View.GONE);
		}

		return rowView;

	}

}
