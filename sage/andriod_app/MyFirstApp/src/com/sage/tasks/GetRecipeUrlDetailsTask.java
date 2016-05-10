package com.sage.tasks;

import android.app.Activity;
import android.os.AsyncTask;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sage.application.RecipeUrlDataContainer;
import com.sage.constants.ActivityConstants;
import com.sage.entities.RecipeDetails;
import com.sage.services.GetRecipeUrlDetailsService;
import com.sage.utils.ActivityUtils;
import com.sage.utils.EntityUtils;
import com.sage.utils.RecipeOwnerContext;

public abstract class GetRecipeUrlDetailsTask extends AsyncTask<Object, Void, JsonElement> {

	private String token;
	private String userName;
	private String url;
	
	private Activity context;
	
	public GetRecipeUrlDetailsTask(Activity context) {
		this.context = context;	

	}

	@Override
	protected JsonElement doInBackground(Object... params) {

		try {
			url = (String) params[0];
			token = (String) params[1];
			userName = (String) params[2];
			GetRecipeUrlDetailsService service = new GetRecipeUrlDetailsService(url, token, userName);
			return service.getUrlDetails();
		} catch (Exception e) {
			ActivityUtils.HandleConnectionUnsuccessfullToServer(e);
			return null;
		}
	}

	@Override
	protected void onPostExecute(JsonElement result) {
		if (result == null) {
			handleFailure();
			return;
		}
		JsonObject resultJsonObject = result.getAsJsonObject();
		boolean saveSucceed = resultJsonObject.get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
		if (saveSucceed) {
			handleSuccess(resultJsonObject);
		} else {
			handleFailure();
		}

	}

	protected void initLinkDataFromResponse(JsonObject resultJsonObject, RecipeDetails linkDetails ) {
		RecipeUrlDataContainer instance = RecipeUrlDataContainer.getInstance();
		JsonElement urlTitleElement = resultJsonObject.get(ActivityConstants.URL_TITLE_ELEMENT_NAME);
		if (urlTitleElement != null) {
			String urlTitle = urlTitleElement.getAsString();
			linkDetails.setLinkTitle(urlTitle);
			if(!EntityUtils.isNewRecipe(linkDetails)) {
				instance.setTtileForLinkDetails(linkDetails, urlTitle);
			}
		}

		JsonElement urlImageElement = resultJsonObject.get(ActivityConstants.URL_IMAGE_ELEMENT_NAME);
		if (urlImageElement != null) {
			String urlImage = urlImageElement.getAsString();
			linkDetails.setLinkImageUrl(urlImage);
			if(!EntityUtils.isNewRecipe(linkDetails)) {
				instance.setLinkImageUrl(linkDetails, urlImage);
			}
		}
		JsonElement urlSiteJson = resultJsonObject.get(ActivityConstants.URL_SITE_ELEMENT_NAME);
		if (urlSiteJson != null) {
			String siteName = urlSiteJson.getAsString();
			RecipeOwnerContext.setOwnerForUrl(linkDetails.getUrl(), siteName);
			linkDetails.setLinkSiteName(siteName);
			if(!EntityUtils.isNewRecipe(linkDetails)) {
				instance.setSiteNameForLinkDetails(linkDetails, siteName);
			}
		}
		linkDetails.setLinkDataInitialized(true);
	}


	protected abstract void handleSuccess(JsonObject resultJsonObject);


	protected void handleFailure() {

	}

}
