package com.sage.adapters;

import java.text.MessageFormat;
import java.util.ArrayList;

import com.example.myfirstapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.activity.interfaces.IClosePopupCommentListener;
import com.sage.constants.ActivityConstants;
import com.sage.constants.ImageType;
import com.sage.entities.RecipeBasicData;
import com.sage.entities.RecipeLinkDetails;
import com.sage.entities.RecipeType;
import com.sage.entities.RecipeUserBasicData;
import com.sage.listeners.AddCommentsClickListener;
import com.sage.listeners.ProfilePageClickListener;
import com.sage.listeners.RecipeDetailsClickListener;
import com.sage.tasks.AddLikeTask;
import com.sage.tasks.CopyRecipeTask;
import com.sage.tasks.GetRecipeUrlDetailsTask;
import com.sage.tasks.RemoveLikeTask;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.ImagesInitializer;
import com.sage.utils.RecipeOwnerContext;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class NewsfeedArrayAdapter extends ArrayAdapter<RecipeUserBasicData> implements IClosePopupCommentListener {
	private final Activity context;
	private ArrayList<RecipeUserBasicData> details;
	private View rowView;
	private ImageButton addLike;
	private ImageButton featuredRecipe;
	private ImageButton unLike;
	private ImageView recipeMainPicture;
	private LinearLayout linkRecipePanel;
	private ProgressBar getLinkDetailsProgress;
	private ImageView linkImage;
	private TextView ownerTextView;

	public NewsfeedArrayAdapter(Activity context, ArrayList<RecipeUserBasicData> details) {
		super(context, 0, details);
		this.context = context;
		this.details = details;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(R.layout.row_layout_newsfeed, parent, false);

		final RecipeUserBasicData recipeUserBasicData = details.get(position);

		TextView recipeHeader = (TextView) rowView.findViewById(R.id.label);
		initHeaderTextBox(recipeUserBasicData, position, recipeHeader);

		TextView recipeUser = (TextView) rowView.findViewById(R.id.user_name);
		initUserNameTextView(recipeUser, recipeUserBasicData);

		ownerTextView = (TextView) rowView.findViewById(R.id.owner_name);

		initOwnerTextView(recipeUserBasicData);

		initLinkAndPictureComponents(recipeUserBasicData);

		initLinkAndMainPictureVisibility(position, recipeUserBasicData);

		initFeaturedRecipeButton(recipeUserBasicData);

		initCommentsAndLikesText(inflater, rowView, parent, position);

		ImageButton addComment = (ImageButton) rowView.findViewById(R.id.comments_button);

		initCommentButtonListener(parent, inflater, recipeUserBasicData, addComment);

		initLikeAndUnlikeButtons(position, recipeUserBasicData);

		initLikeButtonsVisibility(recipeUserBasicData.isUserLikeRecipe());

		ImageButton saveRecipe = (ImageButton) rowView.findViewById(R.id.save_recipe);
		saveRecipe.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Object[] params = ActivityUtils.generateServiceParamObjectWithUserId(context,
						recipeUserBasicData.get_id());
				new CopyRecipeTask(context).execute(params);
			}

		});

		if (EntityUtils.isLoggedInUserRecipe(recipeUserBasicData.getUserId(), context)) {
			saveRecipe.setVisibility(View.GONE);

		}

		return rowView;

	}

	private void initLinkAndMainPictureVisibility(final int position, final RecipeUserBasicData recipeUserBasicData) {
		if (!recipeUserBasicData.getRecipeType().equals(RecipeType.LINK)) {
			linkRecipePanel.setVisibility(View.GONE);
			initRecipeMainPicture(recipeUserBasicData);
		} else {
			recipeMainPicture.setVisibility(View.GONE);
			getLinkDetailsProgress.setVisibility(View.VISIBLE);
			// linkHeader.setVisibility(View.GONE);
			linkImage.setVisibility(View.GONE);
			if (recipeUserBasicData.isLinkDataInitialized()) {
				initLinkRecipeUi(recipeUserBasicData);
			} else {
				InitLinkRecipeData(recipeUserBasicData, position);
			}
		}
	}

	private void initCommentButtonListener(ViewGroup parent, LayoutInflater inflater,
			final RecipeUserBasicData recipeUserBasicData, ImageButton addComment) {
		AddCommentsClickListener commentsHandler = new AddCommentsClickListener(inflater, parent, rowView,
				recipeUserBasicData.get_id(), context);
		commentsHandler.registerListener(this);
		addComment.setOnClickListener(commentsHandler);
	}

	private void initFeaturedRecipeButton(final RecipeUserBasicData recipeUserBasicData) {

		featuredRecipe = (ImageButton) rowView.findViewById(R.id.featured_recipe);

		featuredRecipe.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context, context.getResources().getString(R.string.featured_recipe), Toast.LENGTH_SHORT)
						.show();
			}
		});

		if (!recipeUserBasicData.isFeaturedRecipe()) {
			featuredRecipe.setVisibility(View.GONE);
		}
	}

	private void initLinkAndPictureComponents(RecipeBasicData recipeUserBasicData) {
		recipeMainPicture = (ImageView) rowView.findViewById(R.id.recipe_main_picture);
		recipeMainPicture.setOnClickListener(new RecipeDetailsClickListener(context, recipeUserBasicData.get_id()));
		linkRecipePanel = (LinearLayout) rowView.findViewById(R.id.main_link_recipe_container_panel);
		linkRecipePanel.setOnClickListener(new RecipeDetailsClickListener(context, recipeUserBasicData.get_id()));
		getLinkDetailsProgress = (ProgressBar) rowView.findViewById(R.id.get_recipe_link_details_progress);
		linkImage = (ImageView) rowView.findViewById(R.id.recipe_link_main_picture);
	}

	private void InitLinkRecipeData(RecipeUserBasicData recipeUserBasicData, int position) {
		Object[] params = ActivityUtils.generateServiceParamObject(context, recipeUserBasicData.getUrl());

		new GetRecipeUrlDetails(recipeUserBasicData, position).execute(params);

	}

	private void initLikeAndUnlikeButtons(final int position, final RecipeUserBasicData recipeUserBasicData) {
		addLike = (ImageButton) rowView.findViewById(R.id.like_button);
		addLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.ADD_LIKE_NEWSFEED_PAGE);
				Object[] params = ActivityUtils.generateServiceParamObjectWithUserId(context,
						recipeUserBasicData.get_id());
				new AddLikeAdaptorTask(position, recipeUserBasicData.isFeaturedRecipe()).execute(params);
			}
		});

		unLike = (ImageButton) rowView.findViewById(R.id.unlike_button);

		unLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Object[] params = ActivityUtils.generateServiceParamObjectWithUserId(context,
						recipeUserBasicData.get_id());
				new RemoveAdaptorLikeTask(position, recipeUserBasicData.isFeaturedRecipe()).execute(params);
			}
		});
	}

	private void initRecipeMainPicture(final RecipeUserBasicData recipeUserBasicData) {

		ImagesInitializer.initialRecipeImage(getContext(), recipeUserBasicData.getPictureId(), recipeMainPicture,
				ImageType.IMAGE_RECIPE_PICTURE);

	}

	private void initHeaderTextBox(final RecipeUserBasicData recipeUserBasicData, int position, TextView recipeHeader) {

		String recipeHeaderValue = recipeUserBasicData.getHeader();
		recipeHeader.setText(recipeHeaderValue);
		recipeHeader.setOnClickListener(new RecipeDetailsClickListener(context, recipeUserBasicData.get_id()));
	}

	private void initUserNameTextView(TextView recipeUser, final RecipeUserBasicData recipeUserBasicData) {
		String recipeUserName = recipeUserBasicData.getUserDisplayName();
		if (TextUtils.isEmpty(recipeUserName)) {
			recipeUserName = recipeUserBasicData.getUserId();
		}
		recipeUser.setText(recipeUserName);
		boolean openUserProfile = EntityUtils.isLoggedInUserRecipe(recipeUserBasicData.getUserId(), context);
		recipeUser.setOnClickListener(
				new ProfilePageClickListener(this.getContext(), recipeUserBasicData.getUserDisplayName(),
						recipeUserBasicData.getUserId(), recipeUserBasicData.getUserObjectId(), openUserProfile));
	}

	private void initCommentsAndLikesText(LayoutInflater inflater, View convertView, ViewGroup parent, int position) {
		initCommentsLikeUi(position);
		TextView comments_number = (TextView) rowView.findViewById(R.id.comments_likes_text);
		RecipeUserBasicData recipeUserBasicData = details.get(position);
		AddCommentsClickListener commentsHandler = new AddCommentsClickListener(inflater, parent, convertView,
				recipeUserBasicData.get_id(), context);
		commentsHandler.registerListener(this);
		comments_number.setOnClickListener(commentsHandler);

	}

	public void initCommentsLikeUi(int position) {
		TextView comments_number = (TextView) rowView.findViewById(R.id.comments_likes_text);
		RecipeUserBasicData recipeUserBasicData = details.get(position);
		Integer numberOfComments = recipeUserBasicData.getComments_count();
		Integer numberOfLikes = recipeUserBasicData.getLikes_count();

		String likes_comments_text = context.getResources().getString(R.string.likes_comments_message);
		String likesCommentsTextAfterFormatting = MessageFormat.format(likes_comments_text, numberOfLikes,
				numberOfComments);

		comments_number.setText(likesCommentsTextAfterFormatting);
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

	private void initOwnerTextView(final RecipeUserBasicData recipeUserBasicData) {

		boolean isLinkRecipe = recipeUserBasicData.getRecipeType().equals(RecipeType.LINK);
		if (isLinkRecipe) {
			initOwnerTextViewForLinkRecipe(recipeUserBasicData);			
		} else {
			initOwnerTextViewForNonLinkRecipe(recipeUserBasicData);

		}

	}

	private void initOwnerTextViewForNonLinkRecipe(final RecipeUserBasicData recipeUserBasicData) {
		if (recipeUserBasicData.getUserObjectId().equals(recipeUserBasicData.getOwnerObjectId())) {
			String ownUserLbl = MessageFormat.format("{0}", context.getResources().getString(R.string.a_lbl));
			ownerTextView.setText(ownUserLbl);

		} else {
			String ownerDislayNameText = MessageFormat.format("{0}'\'s", recipeUserBasicData.getOwnerDisplayName());
			ownerTextView.setText(ownerDislayNameText);
			ownerTextView.setOnClickListener(
					new ProfilePageClickListener(this.getContext(), recipeUserBasicData.getOwnerDisplayName(),
							recipeUserBasicData.getOwnerUserName(), recipeUserBasicData.getOwnerObjectId(), false));
		}
	}

	private void initOwnerTextViewForLinkRecipe(final RecipeUserBasicData recipeUserBasicData) {
		String ownerName = RecipeOwnerContext.getOwner(recipeUserBasicData.getUrl());
		if (!TextUtils.isEmpty(ownerName)) {
			String ownerDislayNameText = MessageFormat.format("{0}'\'s", ownerName);
			ownerTextView.setText(ownerDislayNameText);
			ownerTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					RecipeLinkDetails linkDetails = createNewRecipeDetailsObject(recipeUserBasicData);
					ActivityUtils.openDisplayLinkActivity(context, linkDetails,
							EntityUtils.isLoggedInUserRecipe(recipeUserBasicData.getUserId(), context));
				}
			});
		} else {
			String ownUserLbl = MessageFormat.format("{0}", context.getResources().getString(R.string.a_lbl));
			ownerTextView.setText(ownUserLbl);
		}
		return;
	}

	private void initLinkRecipeUi(final RecipeUserBasicData recipeBasicData) {

		linkImage.setOnClickListener(new RecipeDetailsClickListener(context, recipeBasicData.get_id()));

		String linkTitle = recipeBasicData.getLinkTitle();

		String linkImageUrl = recipeBasicData.getLinkImageUrl();
		if (!TextUtils.isEmpty(linkTitle)) {
			ImagesInitializer.initImage(this.context, linkImage, linkImageUrl);
		}

		getLinkDetailsProgress.setVisibility(View.GONE);

		String ownerDislayNameText = MessageFormat.format("{0}'\'s", recipeBasicData.getLinkSiteName());
		ownerTextView.setText(ownerDislayNameText);
		ownerTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RecipeLinkDetails linkDetails = createNewRecipeDetailsObject(recipeBasicData);

				ActivityUtils.openDisplayLinkActivity(context, linkDetails,
						EntityUtils.isLoggedInUserRecipe(recipeBasicData.getUserId(), context));

			}

		});

		linkImage.setVisibility(View.VISIBLE);
	}

	private RecipeLinkDetails createNewRecipeDetailsObject(final RecipeUserBasicData recipeBasicData) {
		RecipeLinkDetails linkDetails = new RecipeLinkDetails();
		if (EntityUtils.isLoggedInUserRecipe(recipeBasicData.getUserId(), context)) {
			linkDetails.set_id(recipeBasicData.get_id());
		}

		linkDetails.setHeader(recipeBasicData.getHeader());

		linkDetails.setUrl(recipeBasicData.getUrl());
		return linkDetails;
	}

	@Override
	public void onClosePopupComment(int numOfComments, String recipeId) {
		for (RecipeUserBasicData recipeData : details) {
			if (recipeData.get_id().equals(recipeId)) {
				recipeData.setComments_count(numOfComments);
				int index = details.indexOf(recipeData);
				details.set(index, recipeData);
				notifyDataSetChanged();
				break;
			}

		}

	}

	private class AddLikeAdaptorTask extends AddLikeTask {

		private int position;

		public AddLikeAdaptorTask(int position, boolean featuredRecipe) {
			super(context, featuredRecipe);
			this.position = position;
		}

		@Override
		protected void handleSuccess(JsonObject resultJsonObject) {
			Gson gson = new Gson();

			JsonObject dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonObject();

			RecipeUserBasicData recipeData = gson.fromJson(dataElement, RecipeUserBasicData.class);

			details.set(position, recipeData);

			notifyDataSetChanged();

		}

	}

	private class RemoveAdaptorLikeTask extends RemoveLikeTask {

		private int position;

		public RemoveAdaptorLikeTask(int position, boolean featuredRecipe) {
			super(context, featuredRecipe);
			this.position = position;

		}

		@Override
		public void handleSuccess(JsonObject resultJsonObject) {
			Gson gson = new Gson();

			JsonObject dataElement = resultJsonObject.get(ActivityConstants.DATA_ELEMENT_NAME).getAsJsonObject();

			RecipeUserBasicData recipeData = gson.fromJson(dataElement, RecipeUserBasicData.class);

			details.set(position, recipeData);

			notifyDataSetChanged();

		}

	}

	private class GetRecipeUrlDetails extends GetRecipeUrlDetailsTask {

		private RecipeUserBasicData recipeBasicData;
		private int position;

		public GetRecipeUrlDetails(RecipeUserBasicData recipeBasicData, int position) {
			super(context);
			this.recipeBasicData = recipeBasicData;
			this.position = position;

		}

		@Override
		protected void handleSuccess(JsonObject resultJsonObject) {
			String urlTitle = null;
			JsonElement urlTitleJson = resultJsonObject.get(ActivityConstants.URL_TITLE_ELEMENT_NAME);
			if (urlTitleJson != null) {
				urlTitle = urlTitleJson.getAsString();
				recipeBasicData.setLinkTitle(urlTitle);
			}

			String urlImage = null;
			JsonElement urlImageJson = resultJsonObject.get(ActivityConstants.URL_IMAGE_ELEMENT_NAME);
			if (urlImageJson != null) {
				urlImage = urlImageJson.getAsString();
				recipeBasicData.setLinkImageUrl(urlImage);
			}

			String siteName = null;
			JsonElement urlSiteJson = resultJsonObject.get(ActivityConstants.URL_SITE_ELEMENT_NAME);
			if (urlSiteJson != null) {
				siteName = urlSiteJson.getAsString();
				RecipeOwnerContext.setOwnerForUrl(recipeBasicData.getUrl(), siteName);
				recipeBasicData.setLinkSiteName(siteName);
			}

			recipeBasicData.setLinkDataInitialized(true);
			details.set(position, recipeBasicData);

			notifyDataSetChanged();

		}
	}

}
