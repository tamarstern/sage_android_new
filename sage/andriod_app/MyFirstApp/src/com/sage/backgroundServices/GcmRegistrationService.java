package com.sage.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.gcm.GcmPubSub;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.JsonElement;
import com.sage.activities.R;
import com.sage.constants.ActivityConstants;
import com.sage.constants.ServicesConstants;
import com.sage.services.UpdateUserService;

import java.io.IOException;

/**
 * Created by tamar.twena on 4/17/2016.
 */
public class GcmRegistrationService extends IntentService {

    private static final String[] TOPICS = {"publishRecipeByFollowing"};

    public GcmRegistrationService(String name) {
        super(name);
    }

    public GcmRegistrationService() {

        super("GcmRegistrationService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i("startGcmRegistration", "start GCM Registration service ");
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                    GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

            Log.i("successGcmRegistration", "GCM Registration Token: " + token);

            boolean registrationSuccess = sendRegistrationToServer(token);
            if(registrationSuccess) {
                subscribeTopics(token);
                sharedPreferences.edit().putBoolean(ServicesConstants.SENT_TOKEN_TO_SERVER, true).apply();
            }
        } catch (Exception e) {
            Log.e("failedInstanceId", "Insatnce Id Service Failed", e);
            sharedPreferences.edit().putBoolean(ServicesConstants.SENT_TOKEN_TO_SERVER, false).apply();
        }
        Intent registrationComplete = new Intent(ServicesConstants.REGISTRATION_COMPLETE);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void subscribeTopics(String token) throws IOException {
        GcmPubSub pubSub = GcmPubSub.getInstance(this);
        for (String topic : TOPICS) {
            pubSub.subscribe(token, "/topics/" + topic, null);
        }

    }

    private boolean sendRegistrationToServer(String token) throws Exception {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        String userName = sharedPref.getString(ActivityConstants.USER_NAME, null);

        if ( !TextUtils.isEmpty(userName)) {
            UpdateUserService updateUserService = new UpdateUserService(userName, null, null, token, null);
            JsonElement updateUserResult = updateUserService.updateUser();
            boolean updateSuccess = updateUserResult.getAsJsonObject().get(ActivityConstants.SUCCESS_ELEMENT_NAME).getAsBoolean();
            if (updateSuccess) {
                return true;
            }
        }
        return false;
    }


}
