package com.sage.backgroundServices;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

/**
 * Created by tamar.twena on 4/17/2016.
 */
public class IntanceIdRefreshService extends InstanceIDListenerService {



    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, GcmRegistrationService.class);
        startService(intent);
    }


}
