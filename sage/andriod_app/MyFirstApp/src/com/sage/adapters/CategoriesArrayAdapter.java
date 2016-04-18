package com.sage.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.myfirstapp.R;
import com.sage.activity.interfaces.ICategoryEditListener;
import com.sage.entities.RecipeCategory;
import com.sage.entities.RecipeDetails;
import com.sage.listeners.ChooseCategoryClickListener;
import com.sage.listeners.EditCategoryPopupClickListener;

import java.util.ArrayList;
import java.util.List;

public class CategoriesArrayAdapter extends ArrayAdapter<RecipeCategory> {

	private static final String BLACK_TEXT_COLOR = "#000000";
	private final Activity context;
	private List<RecipeCategory> categories;
	private LayoutInflater inflater;
	private RecipeDetails details;

	public CategoriesArrayAdapter(Activity context, ArrayList<RecipeCategory> categories,
			RecipeDetails details) {
		super(context,0, categories);
		this.context = context;
		this.categories = new ArrayList<RecipeCategory>(categories);
		this.details = details;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_layout_categories, parent, false);

		RecipeCategory recipeCategory = categories.get(position);

		String categoryNameValue = recipeCategory.getHeader();

		TextView categoryName = (TextView) rowView.findViewById(R.id.recipe_category_name);
		categoryName.setText(categoryNameValue);
		categoryName.setOnClickListener(new ChooseCategoryClickListener(context, details, recipeCategory));

		initEditCategoryButton(parent, rowView, recipeCategory);

		RelativeLayout categoriesPanel = (RelativeLayout) rowView.findViewById(R.id.category_line);
		categoriesPanel.setOnClickListener(new ChooseCategoryClickListener(context, details, recipeCategory));

		return rowView;

	}

	private void initEditCategoryButton(ViewGroup parent, View rowView, RecipeCategory recipeCategory) {
		ImageButton editIcon = (ImageButton) rowView.findViewById(R.id.edit_category_icon);
		EditCategoryPopupClickListener editClickListener = new EditCategoryPopupClickListener(inflater, parent,
				recipeCategory, context);
		editClickListener.registerListener((ICategoryEditListener) context);
		editIcon.setOnClickListener(editClickListener);
	}

}
