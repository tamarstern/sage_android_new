package com.sage.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myfirstapp.R;
import com.sage.entities.User;
import com.sage.listeners.ProfilePageClickListener;
import com.sage.utils.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class UsersArrayAdapter extends ArrayAdapter<User> {

	private final Activity context;
	private List<User> users;
	private LayoutInflater inflater;

	public UsersArrayAdapter(Activity context, List<User> recipes) {
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

		recipeCommentOwner.setOnClickListener(new ProfilePageClickListener(context, user.getUserDisplayName(),
				 user.getUsername(),user.get_id(), EntityUtils.isLoggedInUserRecipe(user.getUsername(), context)));

		return rowView;

	}

}
