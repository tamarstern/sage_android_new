package com.sage.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.sage.activities.R;
import com.sage.application.UserFollowingContainer;
import com.sage.entities.User;
import com.sage.listeners.ProfilePageClickListener;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.EntityUtils;

import java.util.ArrayList;
import java.util.List;

public class UsersArrayAdapter extends ArrayAdapter<User> {

	private final Activity context;
	private List<User> users;
	private LayoutInflater inflater;

	Button followUser;
	Button unfollowUser;

	public UsersArrayAdapter(Activity context, List<User> recipes) {
		super(context, 0, recipes);
		this.context = context;
		this.users = new ArrayList<User>(recipes);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_layout_users, parent, false);

		followUser = (Button) rowView.findViewById(R.id.follow_user);
		unfollowUser = (Button) rowView.findViewById(R.id.unfollow_user);

		final User user = users.get(position);

		TextView userDisplayName = (TextView) rowView.findViewById(R.id.user_display_name);
		userDisplayName.setText(user.getUserDisplayName());

		userDisplayName.setOnClickListener(new ProfilePageClickListener(context, user.getUserDisplayName(),
				user.getUsername(), user.get_id(), EntityUtils.isLoggedInUserRecipe(user.getUsername(), context)));

		initFollowAndUnfollowButtonsVisibility(user);

		followUser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				followUser(user);
			}
		});

		unfollowUser.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				unfollowUser(user);
			}
		});

		return rowView;
	}

	private void followUser(User userToFollow) {
		User user = userToFollow.clone();
		UserFollowingContainer.getInstance().follow(user);
		followUser.setVisibility(View.GONE);
		unfollowUser.setVisibility(View.VISIBLE);
		AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.PRESS_FOLLOW_USER);
		notifyDataSetChanged();

	}


	private void unfollowUser(User userToUnfollow) {
		User user = userToUnfollow.clone();
		UserFollowingContainer.getInstance().unFollow(user);
		followUser.setVisibility(View.VISIBLE);
		unfollowUser.setVisibility(View.GONE);
		AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.PRESS_UNFOLLOW_USER);
		notifyDataSetChanged();
	}

	private void initFollowAndUnfollowButtonsVisibility(User user) {
		if(EntityUtils.isLoggedInUser(user.getUsername(), this.context)) {
			followUser.setVisibility(View.GONE);
			unfollowUser.setVisibility(View.GONE);
		} else {
			if(UserFollowingContainer.getInstance().isFollowing(user)) {
				followUser.setVisibility(View.GONE);
				unfollowUser.setVisibility(View.VISIBLE);
			} else {
				followUser.setVisibility(View.VISIBLE);
				unfollowUser.setVisibility(View.GONE);
			}
		}
	}

}
