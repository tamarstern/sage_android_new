package com.sage.backgroundServices;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.sage.application.GoogleAnalyticsApplication;

/**
 * Created by tamar.twena on 7/16/2016.
 */
public class BackgroundServicesScheduler {

    public static void scheduleAlarmBackgroundServices(Activity activity) {

        GoogleAnalyticsApplication application = (GoogleAnalyticsApplication) activity.getApplication();
        if(application.isBackgroundServicesScheduled()) {
            return;
        }

        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);

        Intent followingIntent = new Intent(activity.getApplicationContext(), GetFollowingReceiver.class);
        final PendingIntent followingPIntent = PendingIntent.getBroadcast(activity, GetFollowingReceiver.REQUEST_CODE,
                followingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                3*60*1000, followingPIntent);
        Intent categoriesIntent = new Intent(activity.getApplicationContext(), CategoriesReceiver.class);
        final PendingIntent categoriesPIntent = PendingIntent.getBroadcast(activity, CategoriesReceiver.REQUEST_CODE,
                categoriesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                8*60*1000, categoriesPIntent);

        Intent profileRecipiesIntent = new Intent(activity.getApplicationContext(), GetProfileRecipiesReceiver.class);
        final PendingIntent profileRecipiesPIntent = PendingIntent.getBroadcast(activity, GetProfileRecipiesReceiver.REQUEST_CODE,
                profileRecipiesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                10*60*1000, profileRecipiesPIntent);

        Intent newsfeedRecipiesIntent = new Intent(activity.getApplicationContext(), GetNewsfeedRecipiesReceiver.class);
        final PendingIntent newsfeedRecipiesPIntent = PendingIntent.getBroadcast(activity, GetNewsfeedRecipiesReceiver.REQUEST_CODE,
                newsfeedRecipiesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                10*60*1000, newsfeedRecipiesPIntent);

        Intent profileRecipiesForFollowingIntent = new Intent(activity.getApplicationContext(), GetProfilePageRecipiesForFollowingReceiver.class);
        final PendingIntent profileRecipiesForFollowingPIntent = PendingIntent.getBroadcast(activity, GetProfilePageRecipiesForFollowingReceiver.REQUEST_CODE,
                profileRecipiesForFollowingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                10*60*1000, profileRecipiesForFollowingPIntent);

        Intent syncFollowingIntent = new Intent(activity.getApplicationContext(), SyncFollowUsersReceiver.class);
        final PendingIntent syncFollowingPIntent = PendingIntent.getBroadcast(activity, SyncFollowUsersReceiver.REQUEST_CODE,
                syncFollowingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                1*60*1000, syncFollowingPIntent);

        Intent deleteRecipesIntent = new Intent(activity.getApplicationContext(), DeleteRecipesReceiver.class);
        final PendingIntent deleteRecipesPIntent = PendingIntent.getBroadcast(activity, SyncFollowUsersReceiver.REQUEST_CODE,
                deleteRecipesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                1*60*1000, deleteRecipesPIntent);


        Intent saveRecipesIntent = new Intent(activity.getApplicationContext(), SaveRecipesReceiver.class);
        final PendingIntent saveRecipesPIntent = PendingIntent.getBroadcast(activity, SyncFollowUsersReceiver.REQUEST_CODE,
                saveRecipesIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                1*60*1000, saveRecipesPIntent);

		/*Intent recipesForCategoryIntent = new Intent(getApplicationContext(), RecipesForCategoryReceiver.class);
		final PendingIntent recipesForCategoryPIntent = PendingIntent.getBroadcast(this, RecipesForCategoryReceiver.REQUEST_CODE,
				recipesForCategoryIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
				5*60*1000, recipesForCategoryPIntent);*/


        Intent followedByIntent = new Intent(activity.getApplicationContext(), GetFollowedByReceiver.class);
        final PendingIntent followedByPIntent = PendingIntent.getBroadcast(activity, GetFollowedByReceiver.REQUEST_CODE,
                followedByIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                5*60*1000, followedByPIntent);

        Intent signTermsIntent = new Intent(activity.getApplicationContext(), SignTermsReceiver.class);
        final PendingIntent signTermsPIntent = PendingIntent.getBroadcast(activity, SignTermsReceiver.REQUEST_CODE,
                signTermsIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                1*60*1000, signTermsPIntent);

        application.setBackgroundServicesScheduled(true);

    }

}
