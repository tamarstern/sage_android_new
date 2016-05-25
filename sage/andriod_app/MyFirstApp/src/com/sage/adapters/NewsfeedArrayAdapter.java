package com.sage.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
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
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonObject;
import com.sage.activities.R;
import com.sage.activity.interfaces.IClosePopupCommentListener;
import com.sage.application.RecipeUrlDataContainer;
import com.sage.entities.RecipeDetails;
import com.sage.entities.RecipeType;
import com.sage.listeners.AddCommentsClickListener;
import com.sage.listeners.ProfilePageClickListener;
import com.sage.listeners.RecipeDetailsClickListener;
import com.sage.tasks.AddLikeTask;
import com.sage.tasks.CopyRecipeTask;
import com.sage.tasks.GetRecipeUrlDetailsTask;
import com.sage.tasks.RemoveLikeTask;
import com.sage.utils.ActivityUtils;
import com.sage.utils.AnalyticsUtils;
import com.sage.utils.CacheUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.ImagesInitializer;
import com.sage.utils.RecipeOwnerContext;

import java.text.MessageFormat;
import java.util.ArrayList;

public class NewsfeedArrayAdapter extends ArrayAdapter<RecipeDetails> implements IClosePopupCommentListener {
	private final Activity context;
	private ArrayList<RecipeDetails> details;
	private View rowView;
	private ViewGroup parent;
	private ImageButton addLike;
	private ImageButton unLike;
	private ImageView recipeMainPicture;
	private ProgressBar getLinkDetailsProgress;
	private TextView ownerTextView;
	private LayoutInflater inflater;

	public NewsfeedArrayAdapter(Activity context, ArrayList<RecipeDetails> details) {
		super(context, 0, details);
		this.context = context;
		this.details = details;

	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		rowView = inflater.inflate(R.layout.row_layout_newsfeed, parent, false);
		this.parent = parent;
		final RecipeDetails recipeUserBasicData = details.get(position);

		TextView recipeHeader = (TextView) rowView.findViewById(R.id.label);
		initHeaderTextBox(recipeUserBasicData, recipeHeader);

		TextView recipeUser = (TextView) rowView.findViewById(R.id.user_name);
		initUserNameTextView(recipeUser, recipeUserBasicData);

		ownerTextView = (TextView) rowView.findViewById(R.id.owner_name);

		initOwnerTextView(recipeUserBasicData);

		initLinkAndPictureComponents(recipeUserBasicData);

		initLinkAndMainPictureVisibility(position, recipeUserBasicData);

		initFeaturedRecipeButton(recipeUserBasicData);

		initCommentsAndLikesText( position);

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
				new CopyRecipeTask(context, recipeUserBasicData).execute(params);
			}

		});

		if (EntityUtils.isLoggedInUserRecipe(recipeUserBasicData.getUserId(), context)) {
			saveRecipe.setVisibility(View.GONE);

		}

		return rowView;

	}

	private void initLinkAndMainPictureVisibility(final int position, final RecipeDetails recipeUserBasicData) {
		if (!recipeUserBasicData.getRecipeType().equals(RecipeType.LINK)) {
				initRecipeMainPicture(recipeUserBasicData);
		} else {
			if (recipeUserBasicData.isLinkDataInitialized()) {
				initLinkRecipeUi(recipeUserBasicData);
			} else {
				InitLinkRecipeData(recipeUserBasicData, position);
			}
		}
	}

	private void initCommentButtonListener(ViewGroup parent, LayoutInflater inflater,
			final RecipeDetails recipeUserBasicData, ImageButton addComment) {
		AddCommentsClickListener commentsHandler = new AddCommentsClickListener(inflater, parent, rowView,
				recipeUserBasicData, context);
		commentsHandler.registerListener(this);
		addComment.setOnClickListener(commentsHandler);
	}

	private void initFeaturedRecipeButton(final RecipeDetails recipeUserBasicData) {

		ImageButton featuredRecipe = (ImageButton) rowView.findViewById(R.id.featured_recipe);

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

	private void initLinkAndPictureComponents(RecipeDetails recipeUserBasicData) {
		recipeMainPicture = (ImageView) rowView.findViewById(R.id.recipe_main_picture);
		recipeMainPicture.setOnClickListener(new RecipeDetailsClickListener(context, recipeUserBasicData));
		getLinkDetailsProgress = (ProgressBar) rowView.findViewById(R.id.get_recipe_link_details_progress);
	}

	private void InitLinkRecipeData(RecipeDetails recipeUserBasicData, int position) {

		RecipeUrlDataContainer instance = RecipeUrlDataContainer.getInstance();
		boolean hasDataForRecipe = instance.hasDataForRecipe(recipeUserBasicData);
		if(hasDataForRecipe) {
			recipeUserBasicData.setLinkSiteName(instance.getSiteName(recipeUserBasicData));
			recipeUserBasicData.setLinkTitle(instance.getTitle(recipeUserBasicData));
			recipeUserBasicData.setLinkImageUrl(instance.getLinkImageUrl(recipeUserBasicData));
			recipeUserBasicData.setLinkDataInitialized(true);
			notifyDataSetChanged();
		} else {

			Object[] params = ActivityUtils.generateServiceParamObject(context, recipeUserBasicData.getUrl());

			new GetRecipeUrlDetails(recipeUserBasicData, position).execute(params);
		}

	}

	private void initLikeAndUnlikeButtons(final int position, final RecipeDetails recipeUserBasicData) {
		addLike = (ImageButton) rowView.findViewById(R.id.like_button);
		addLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				AnalyticsUtils.sendAnalyticsTrackingEvent(context, AnalyticsUtils.ADD_LIKE_NEWSFEED_PAGE);

				recipeUserBasicData.setLikes_count(recipeUserBasicData.getLikes_count() + 1);
				recipeUserBasicData.setUserLikeRecipe(true);
				details.set(position, recipeUserBasicData);
				notifyDataSetChanged();
				CacheUtils.addLike(recipeUserBasicData);

				Object[] params = ActivityUtils.generateServiceParamObjectWithUserId(context,
						recipeUserBasicData.get_id());
				new AddLikeAdaptorTask(recipeUserBasicData.isFeaturedRecipe()).execute(params);
			}
		});

		unLike = (ImageButton) rowView.findViewById(R.id.unlike_button);

		unLike.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				recipeUserBasicData.setLikes_count(recipeUserBasicData.getLikes_count() - 1);
				recipeUserBasicData.setUserLikeRecipe(false);
				details.set(position, recipeUserBasicData);
				notifyDataSetChanged();

				CacheUtils.removeLike(recipeUserBasicData);
				Object[] params = ActivityUtils.generateServiceParamObjectWithUserId(context,
						recipeUserBasicData.get_id());
				new RemoveAdaptorLikeTask(recipeUserBasicData.isFeaturedRecipe()).execute(params);
			}
		});
	}

	private void initRecipeMainPicture(final RecipeDetails recipeUserBasicData) {


		recipeMainPicture.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.MATCH_PARENT));

	//	String id = CacheUtils.getRecipeMainPictureId(recipeUserBasicData);
	//	String url = ImagesInitializer.getUrl(context, id, ImageType.RECIPE_PICTURE);
	//	TCImageLoader loader = ((GoogleAnalyticsApplication) context.getApplication()).getLoader();
	//	loader.display(url, recipeMainPicture,getLinkDetailsProgress, R.drawable.default_recipe_image);


		ImagesInitializer.initRecipeMainPicture(recipeMainPicture, getLinkDetailsProgress, recipeUserBasicData, context);


	}

	private void initHeaderTextBox(final RecipeDetails recipeUserBasicData, TextView recipeHeader) {

		String recipeHeaderValue = recipeUserBasicData.getHeader();
		recipeHeader.setText(recipeHeaderValue);
		recipeHeader.setOnClickListener(new RecipeDetailsClickListener(context, recipeUserBasicData));
	}

	private void initUserNameTextView(TextView recipeUser, final RecipeDetails recipeUserBasicData) {
		String recipeUserName = recipeUserBasicData.getUserDisplayName();
		if (TextUtils.isEmpty(recipeUserName)) {
			recipeUserName = recipeUserBasicData.getUserId();
		}
		recipeUser.setText(recipeUserName);
		boolean openUserProfile = EntityUtils.isLoggedInUserRecipe(recipeUserBasicData.getUserId(), context);
		recipeUser.setOnClickListener(
				new ProfilePageClickListener(context, recipeUserBasicData.getUserDisplayName(),
						recipeUserBasicData.getUserId(), recipeUserBasicData.getUserObjectId(), openUserProfile));
	}

	private void initCommentsAndLikesText( int position) {
		initCommentsLikeUi(position);
		TextView comments_number = (TextView) rowView.findViewById(R.id.comments_likes_text);
		RecipeDetails recipeUserBasicData = details.get(position);
		AddCommentsClickListener commentsHandler = new AddCommentsClickListener(inflater, parent, rowView,
				recipeUserBasicData, context);
		commentsHandler.registerListener(this);
		comments_number.setOnClickListener(commentsHandler);

	}

	public void initCommentsLikeUi(int position) {
		TextView comments_number = (TextView) rowView.findViewById(R.id.comments_likes_text);
		RecipeDetails recipeUserBasicData = details.get(position);
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

	private void initOwnerTextView(final RecipeDetails recipeUserBasicData) {

		boolean isLinkRecipe = recipeUserBasicData.getRecipeType().equals(RecipeType.LINK);
		if (isLinkRecipe) {
			initOwnerTextViewForLinkRecipe(recipeUserBasicData);			
		} else {
			initOwnerTextViewForNonLinkRecipe(recipeUserBasicData);

		}

	}

	private void initOwnerTextViewForNonLinkRecipe(final RecipeDetails recipeUserBasicData) {
		if (recipeUserBasicData.getUserObjectId().equals(recipeUserBasicData.getOwnerObjectId())) {
			String ownUserLbl = MessageFormat.format("{0}", context.getResources().getString(R.string.a_lbl));
			ownerTextView.setText(ownUserLbl);

		} else {
			String ownerDislayNameText = MessageFormat.format("{0}'\'s", recipeUserBasicData.getOwnerDisplayName());
			ownerTextView.setText(ownerDislayNameText);
			ownerTextView.setTypeface(null, Typeface.BOLD);
			ownerTextView.setOnClickListener(
					new ProfilePageClickListener(context, recipeUserBasicData.getOwnerDisplayName(),
							recipeUserBasicData.getOwnerUserName(), recipeUserBasicData.getOwnerObjectId(), false));
		}
	}

	private void initOwnerTextViewForLinkRecipe(final RecipeDetails recipeUserBasicData) {
		String ownerName = RecipeOwnerContext.getOwner(recipeUserBasicData.getUrl());
		if (!TextUtils.isEmpty(ownerName)) {
			String ownerDislayNameText = MessageFormat.format("{0}'\'s", ownerName);
			ownerTextView.setText(ownerDislayNameText);
			ownerTextView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					RecipeDetails linkDetails = createNewRecipeDetailsObject(recipeUserBasicData);
					ActivityUtils.openDisplayLinkActivity(context, linkDetails,
							EntityUtils.isLoggedInUserRecipe(recipeUserBasicData.getUserId(), context));
				}
			});
			ownerTextView.setTypeface(null, Typeface.BOLD);
		} else {
			String ownUserLbl = MessageFormat.format("{0}", context.getResources().getString(R.string.a_lbl));
			ownerTextView.setText(ownUserLbl);
		}
		return;
	}

	private void initLinkRecipeUi(final RecipeDetails recipeBasicData) {

		recipeMainPicture.setOnClickListener(new RecipeDetailsClickListener(context, recipeBasicData));

		String linkImageUrl = recipeBasicData.getLinkImageUrl();
		if (!TextUtils.isEmpty(linkImageUrl)) {
			ImagesInitializer.initImage(this.context, recipeMainPicture,getLinkDetailsProgress, linkImageUrl);
		} else {
			getLinkDetailsProgress.setVisibility(View.GONE);
			recipeMainPicture.setVisibility(View.VISIBLE);
			Drawable defaultDrawable = context.getDrawable(R.drawable.default_recipe_image);
			recipeMainPicture.setImageDrawable(defaultDrawable);
		}
		initLinkOwnerName(recipeBasicData);

		recipeBasicData.setLinkUiInitialized(true);
	}

	private void initLinkOwnerName(final RecipeDetails recipeBasicData) {
		String ownerDislayNameText =TextUtils.isEmpty(recipeBasicData.getLinkSiteName()) ?
				context.getResources().getString(R.string.a_lbl)  : MessageFormat.format("{0}'\'s", recipeBasicData.getLinkSiteName());
		ownerTextView.setText(ownerDislayNameText);
		ownerTextView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				RecipeDetails linkDetails = createNewRecipeDetailsObject(recipeBasicData);

				ActivityUtils.openDisplayLinkActivity(context, linkDetails,
						EntityUtils.isLoggedInUserRecipe(recipeBasicData.getUserId(), context));

			}

		});
		if(!TextUtils.isEmpty(recipeBasicData.getLinkSiteName())) {
			ownerTextView.setTypeface(null, Typeface.BOLD);
		}
	}

	private RecipeDetails createNewRecipeDetailsObject(final RecipeDetails recipeBasicData) {
		RecipeDetails linkDetails = new RecipeDetails();
		linkDetails.setRecipeType(RecipeType.LINK);
		if (EntityUtils.isLoggedInUserRecipe(recipeBasicData.getUserId(), context)) {
			linkDetails.set_id(recipeBasicData.get_id());
		}

		linkDetails.setHeader(recipeBasicData.getHeader());

		linkDetails.setUrl(recipeBasicData.getUrl());
		return linkDetails;
	}

	@Override
	public void onClosePopupComment(int numOfComments, String recipeId) {
		for (RecipeDetails recipeData : details) {
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



		public AddLikeAdaptorTask( boolean featuredRecipe) {
			super(context, featuredRecipe);

		}

		@Override
		protected void handleSuccess(JsonObject resultJsonObject) {

		}

	}

	private class RemoveAdaptorLikeTask extends RemoveLikeTask {


		public RemoveAdaptorLikeTask(boolean featuredRecipe) {
			super(context, featuredRecipe);

		}

		@Override
		public void handleSuccess(JsonObject resultJsonObject) {
		}

	}

	private class GetRecipeUrlDetails extends GetRecipeUrlDetailsTask {

		private RecipeDetails recipeBasicData;
		private int position;

		public GetRecipeUrlDetails(RecipeDetails recipeBasicData, int position) {
			super(context);
			this.recipeBasicData = recipeBasicData;
			this.position = position;

		}

		@Override
		protected void handleSuccess(JsonObject resultJsonObject) {
			initLinkDataFromResponse(resultJsonObject, recipeBasicData);
			details.set(position, recipeBasicData);
			notifyDataSetChanged();

		}

		@Override
		protected void handleFailure() {
			recipeBasicData.setLinkDataInitialized(true);
			details.set(position, recipeBasicData);
			notifyDataSetChanged();

		}
	}

}
