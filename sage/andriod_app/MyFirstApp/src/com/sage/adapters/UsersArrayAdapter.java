package com.sage.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.myfirstapp.R;
import com.sage.entities.RecipeBasicData;
import com.sage.entities.RecipeComment;
import com.sage.entities.RecipePublished;
import com.sage.entities.User;
import com.sage.listeners.ProfilePageClickListener;
import com.sage.listeners.RecipeDetailsClickListener;
import com.sage.utils.EntityUtils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class UsersArrayAdapter extends ArrayAdapter<User> {

	private final Context context;
	private List<User> users;
	private LayoutInflater inflater;

	public UsersArrayAdapter(Context context, List<User> recipes) {
		super(context, 0, recipes);
		this.context = context;
		this.users = new ArrayList<User>(recipes);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_layout_users, parent, false);

		User user = users.get(position);

		TextView recipeCommentOwner = (TextView) rowView.findViewById(R.id.user_display_name);
		recipeCommentOwner.setText(user.getUserDisplayName());

		recipeCommentOwner.setOnClickListener(new ProfilePageClickListener(this.getContext(), user.getUserDisplayName(),
				 user.getUsername(),user.get_id(), EntityUtils.isLoggedInUserRecipe(user.getUsername(), context)));

		return rowView;

	}

}
