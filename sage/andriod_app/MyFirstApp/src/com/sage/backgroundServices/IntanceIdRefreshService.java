package com.sage.backgroundServices;

import android.content.Intent;
import android.util.Log;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by tamar.twena on 4/17/2016.
 */
public class IntanceIdRefreshService extends InstanceIDListenerService {



    @Override
    public void onTokenRefresh() {
        Log.i("startRefreshGcmToken", "start refresh gcm token");
        Intent intent = new Intent(this, GcmRegistrationService.class);
        startService(intent);
    }


}
