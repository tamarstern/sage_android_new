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

import com.example.myfirstapp.R;
import com.sage.entities.RecipeDetails;
import com.sage.listeners.RecipeDetailsClickListener;

import java.util.ArrayList;
import java.util.List;

public class SubCategoriesArrayAdapter extends ArrayAdapter<RecipeDetails> {

	private final Activity context;
	private List<RecipeDetails> recipes;
	private LayoutInflater inflater;

	public SubCategoriesArrayAdapter(Activity context, ArrayList<RecipeDetails> recipes) {
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
		recipeLine.setOnClickListener(new RecipeDetailsClickListener(context, recipePublished));

		ImageView recipePublishedIcon = (ImageView) rowView.findViewById(R.id.recipe_published);

		if (!recipePublished.isPublished()) {
			recipePublishedIcon.setVisibility(View.GONE);
		}

		return rowView;

	}

}
