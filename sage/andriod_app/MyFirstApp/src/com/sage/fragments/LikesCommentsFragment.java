package com.sage.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sage.activities.R;
import com.sage.activity.interfaces.IClosePopupCommentListener;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.listeners.AddCommentsClickListener;
import com.sage.tasks.AddLikeTask;
import com.sage.tasks.RemoveLikeTask;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.CacheUtils;
import com.sage.utils.EntityUtils;

import java.text.MessageFormat;

public class LikesCommentsFragment extends Fragment implements IClosePopupCommentListener {

	private ImageButton addLike;
	private ImageButton unLike;
	private ImageButton addComment;
	private RecipeDetails receiptDetails;

	private View likesCommentsPanel;

	private LayoutInflater inflater;

	private ViewGroup container;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		this.inflater = inflater;
		final LayoutInflater currentInflater = inflater;

		this.container = container;
		final ViewGroup currentContainer = container;

		likesCommentsPanel = inflater.inflate(R.layout.likes_comments_fragment, container, false);

		final Activity activity = getActivity();
		Intent i = activity.getIntent();

		receiptDetails = (RecipeDetails) i
				.getSerializableExtra(EntityDataTransferConstants.RECIPE_DETAILS_DATA_TRANSFER);

		if (EntityUtils.isNewRecipe(receiptDetails)) {
			View socialPanel = likesCommentsPanel.findViewById(R.id.comments_likes_panel_recipe_page);
			socialPanel.setVisibility(View.GONE);

		} else {
			initCommentsLikesPanel(inflater, container, likesCommentsPanel, receiptDetails);
		}

		addComment = (ImageButton) likesCommentsPanel.findViewById(R.id.comments_button_fragment);
		addComment.setOnClickListener(new AddCommentsClickListener(inflater, container, likesCommentsPanel,
				receiptDetails, activity));

		addLike = (ImageButton) likesCommentsPanel.findViewById(R.id.like_button_fragment);
		addLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				receiptDetails.setLikes_count(receiptDetails.getLikes_count()+1);
				receiptDetails.setUserLikeRecipe(true);
				initLikeButtonsVisibility(receiptDetails.isUserLikeRecipe());
				initCommentsLikesPanel(currentInflater, currentContainer, likesCommentsPanel, receiptDetails);
				CacheUtils.addLike(receiptDetails);

				AnalyticsUtils.sendAnalyticsTrackingEvent(activity, AnalyticsUtils.ADD_LIKE_RECIPE_PAGE);
				
				Object[] params = ActivityUtils.generateServiceParamObjectWithUserId(activity, receiptDetails.get_id());
				new AddLikeFragmentTask(activity, false).execute(params);
			}
		});

		unLike = (ImageButton) likesCommentsPanel.findViewById(R.id.unlike_button_fragment);

		unLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				receiptDetails.setLikes_count(receiptDetails.getLikes_count()-1);
				receiptDetails.setUserLikeRecipe(false);

				initLikeButtonsVisibility(receiptDetails.isUserLikeRecipe());
				initCommentsLikesPanel(currentInflater, currentContainer, likesCommentsPanel, receiptDetails);

				CacheUtils.removeLike(receiptDetails);

				Object[] params = ActivityUtils.generateServiceParamObjectWithUserId(activity, receiptDetails.get_id());
				new RemoveLikeFragmentTask(activity, false).execute(params);
			}
		});

		initLikeButtonsVisibility(receiptDetails.isUserLikeRecipe());

		return likesCommentsPanel;
	}

	private void initLikeButtonsVisibility(boolean isUserLikeRecipe) {
		if (isUserLikeRecipe) {
			unLike.setVisibility(View.VISIBLE);
			addLike.setVisibility(View.GONE);
		} else {
			unLike.setVisibility(View.GONE);
			addLike.setVisibility(View.VISIBLE);
		}
	}

	private void initCommentsLikesPanel(LayoutInflater inflater, ViewGroup container, View likesComments,
			RecipeDetails receiptDetails) {
		TextView likeCommentsTextView = (TextView) likesComments.findViewById(R.id.likes_comments_text);
		int numberOfLikes = receiptDetails.getLikes_count();
		int numberOfComments = receiptDetails.getComments_count();

		String likes_comments_text = getActivity().getResources().getString(R.string.likes_comments_message);
		String likesCommentsTextAfterFormatting = MessageFormat.format(likes_comments_text, numberOfLikes,
				numberOfComments);
		likeCommentsTextView.setText(likesCommentsTextAfterFormatting);

		AddCommentsClickListener addComments = new AddCommentsClickListener(inflater, container, likesComments,
				receiptDetails, getActivity());
		likeCommentsTextView.setOnClickListener(addComments);
		addComments.registerListener(this);

	}


	@Override
	public void onClosePopupComment(int numOfComments, String recipeId) {
		receiptDetails.setComments_count(numOfComments);
		initCommentsLikesPanel(inflater, container, likesCommentsPanel, receiptDetails);
	}

	private class AddLikeFragmentTask extends AddLikeTask {

		public AddLikeFragmentTask(Activity activity, boolean featuredRecipe) {
			super(activity, featuredRecipe);

		}

		@Override
		protected void handleSuccess(JsonObject resultJsonObject) {

		}

	}

	private class RemoveLikeFragmentTask extends RemoveLikeTask {

		public RemoveLikeFragmentTask(Activity activity, boolean featuredRecipe) {
			super(activity, featuredRecipe);

		}

		@Override
		public void handleSuccess(JsonObject resultJsonObject) {

		}

	}

}
