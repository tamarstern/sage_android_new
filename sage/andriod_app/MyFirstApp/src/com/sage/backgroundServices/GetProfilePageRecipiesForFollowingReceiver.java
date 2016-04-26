package com.sage.backgroundServices;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by tamar.twena on 4/17/2016.
 */
public class GetProfilePageRecipiesForFollowingReceiver extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, GetProfilePageRecipesForFollowing.class);
        context.startService(i);
    }
}
