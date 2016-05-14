package com.sage.backgroundServices;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.sage.constants.ActivityConstants;

/**
 * Created by tamar.twena on 4/17/2016.
 */
public class GcmListenerService extends IntentService {

    public static String CATEGORIES_KEY = "GcmListenerService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GcmListenerService(String name) {
        super(name);
    }

    public GcmListenerService() {
        super("GcmListenerService");
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Log.i("startGcmListenerService", "start GcmListenerService");
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPref.getString(ActivityConstants.AUTH_TOKEN, null);
            String userName = sharedPref.getString(ActivityConstants.USER_OBJECT_ID, null);
            if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(userName)) {

            }
        } catch (Exception e) {
            Log.e("failedGcmListene", "Gcm Listener Service Failed", e);
        } finally {
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }
    }


}
