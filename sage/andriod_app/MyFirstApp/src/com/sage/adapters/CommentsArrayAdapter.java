package com.sage.adapters;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.example.myfirstapp.R;
import com.sage.entities.RecipeBasicData;
import com.sage.entities.RecipeComment;
import com.sage.entities.RecipePublished;
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

public class CommentsArrayAdapter extends ArrayAdapter<String> {

	private final Context context;
	private List<RecipeComment> comments;
	private LayoutInflater inflater;

	public CommentsArrayAdapter(Context context, Collection<RecipeComment> recipes, String[] headers) {
		super(context, -1, headers);
		this.context = context;
		this.comments = new ArrayList<RecipeComment>(recipes);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View rowView = inflater.inflate(R.layout.row_layout_comments, parent, false);

		RecipeComment comment = comments.get(position);

		TextView recipeComment = (TextView) rowView.findViewById(R.id.comment);
		recipeComment.setText(comment.getText());

		TextView recipeCommentOwner = (TextView) rowView.findViewById(R.id.comment_owner);
		recipeCommentOwner.setText(comment.getUserDisplayName());

		String userName = comment.getUserId();
		recipeCommentOwner
				.setOnClickListener(new ProfilePageClickListener(this.getContext(), comment.getUserDisplayName(),
						userName, comment.getUserObjectId(), EntityUtils.isLoggedInUserRecipe(userName, context)));

		return rowView;

	}

}
