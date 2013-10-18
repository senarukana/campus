package com.campusrecruit.common;

import java.util.Calendar;

import com.campusrecruit.app.AppContext;
import com.campusrecruit.bean.Schedules;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class AlarmSetter extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		AppContext appContext = (AppContext)context.getApplicationContext();
		for (Schedules schedule : appContext.scheduleGetAll()) {
			appContext.setAlarm(appContext, schedule);
		}
	}
}
