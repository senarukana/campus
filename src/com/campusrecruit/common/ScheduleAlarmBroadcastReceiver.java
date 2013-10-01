package com.campusrecruit.common;


import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.Schedules;
import com.krislq.sliding.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class ScheduleAlarmBroadcastReceiver extends BroadcastReceiver{

	private AppContext appContext = null;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("broad", "1");
		String actionNameString = intent.getAction();
		Log.i("broad", "1");
		if(actionNameString != "com.campusRecruit.action.SCHEDULE")
			return;
		Log.i("broad", "1");
		appContext = (AppContext)context.getApplicationContext();
		Bundle bundle = intent.getExtras();
		Schedules s = (Schedules) bundle.getSerializable("schedule");
		if(s == null){
			return ;
		}
		appContext.messageCountAdd();
//		appContext.scheduleDelete(s);
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(context)
		        .setSmallIcon(R.drawable.alarm)
		        .setContentTitle("宣讲会提醒")
		        .setContentText(s.getCompanyName() + " 地点：" + s.getPlace())
		        .setContentInfo(appContext.getMessageCount() + "")
		        .setAutoCancel(true);
		Intent resultIntent = new Intent(context, MainActivity.class);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(MainActivity.class);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent =
		        stackBuilder.getPendingIntent(
		            0,
		            PendingIntent.FLAG_UPDATE_CURRENT
		        );
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(1, mBuilder.build());
	}

}
