package com.sage.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.JsonElement;
import com.sage.constants.ActivityConstants;
import com.sage.services.UpdateUserService;
import com.sage.utils.EntityUtils;
import com.sage.utils.LoginUtility;

/**
 * Created by tamar.twena on 4/17/2016.
 */
public class SignTermsService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public SignTermsService(String name) {
        super(name);
    }

    public SignTermsService() {
        super("SignTermsService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("startSignTerms", "start startTermsBackground");
            if(EntityUtils.termsSignatureSentToServer(this)) {
                return;
            }
            if(!EntityUtils.signedTerms(this)) {
                return;
            }
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userName)) {
                signTermsInBackground(userName);
            }
        } catch (Exception e) {
            Log.e("failedFetchCategories", "failed to get categories", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }

    private void signTermsInBackground(String userName) throws Exception {
        UpdateUserService updateUserService = new UpdateUserService(userName, null, null, null,true, null);
        JsonElement updateUserResult = updateUserService.updateUser();
        boolean updateSuccess = updateUserResult.getAsJsonObject().get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
        if (updateSuccess) {
            LoginUtility.signatureSentToServer(getApplicationContext());
        }
    }



}
