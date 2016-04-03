package com.sage.fragments;

import java.io.IOException;
import java.text.MessageFormat;

import com.example.myfirstapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.activity.interfaces.IClosePopupCommentListener;
import com.sage.constants.ActivityConstants;
import com.sage.entities.EntityDataTransferConstants;
import com.sage.entities.RecipeDetails;
import com.sage.listeners.AddCommentsClickListener;
import com.sage.services.RemoveLikeService;
import com.sage.services.SaveNewLikeService;
import com.sage.tasks.AddLikeTask;
import com.sage.tasks.RemoveLikeTask;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.EntityUtils;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

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

		this.container = container;

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
				receiptDetails.get_id(), activity));

		addLike = (ImageButton) likesCommentsPanel.findViewById(R.id.like_button_fragment);
		addLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				AnalyticsUtils.sendAnalyticsTrackingEvent(activity, AnalyticsUtils.ADD_LIKE_RECIPE_PAGE);
				
				Object[] params = ActivityUtils.generateServiceParamObjectWithUserId(activity, receiptDetails.get_id());
				new AddLikeFragmentTask(activity, false).execute(params);
			}
		});

		unLike = (ImageButton) likesCommentsPanel.findViewById(R.id.unlike_button_fragment);

		unLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
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
				receiptDetails.get_id(), getActivity());
		likeCommentsTextView.setOnClickListener(addComments);
		addComments.registerListener(this);

	}

	private void updateExistingRecipeDetailsWithLikesData(JsonObject resultJsonObject) {
		Gson gson = new Gson();

		JsonObject dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonObject();

		RecipeDetails currentDetails = gson.fromJson(dataElement, RecipeDetails.class);

		receiptDetails.setLikes_count(currentDetails.getLikes_count());

		receiptDetails.setUserLikeRecipe(currentDetails.isUserLikeRecipe());
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
			updateExistingRecipeDetailsWithLikesData(resultJsonObject);

			initLikeButtonsVisibility(receiptDetails.isUserLikeRecipe());

			initCommentsLikesPanel(inflater, container, likesCommentsPanel, receiptDetails);

		}

	}

	private class RemoveLikeFragmentTask extends RemoveLikeTask {

		public RemoveLikeFragmentTask(Activity activity, boolean featuredRecipe) {
			super(activity, featuredRecipe);

		}

		@Override
		public void handleSuccess(JsonObject resultJsonObject) {
			updateExistingRecipeDetailsWithLikesData(resultJsonObject);

			initLikeButtonsVisibility(receiptDetails.isUserLikeRecipe());

			initCommentsLikesPanel(inflater, container, likesCommentsPanel, receiptDetails);

		}

	}

}
