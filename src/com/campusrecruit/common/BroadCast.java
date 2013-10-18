package com.campusrecruit.common;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.UserMessage;
import com.pcncad.campusRecruit.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

public class BroadCast extends BroadcastReceiver {

	private final static int NOTIFICATION_ID = R.layout.frame_context_v;
	private Recruit _recruit;
	private CareerTalk _career;
	private UserMessage _message;
	private BBSReply _reply;
	
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String ACTION_NAME = intent.getAction();
		if ("com.campusRecruit.action.NOTICE".equals(ACTION_NAME)) {
			int carrerTalkCount = intent.getIntExtra("careerTalkCount", 0); // 宣讲会
			int recruitCount = intent.getIntExtra("recruitCount", 0);// 校园招聘
			int messageCount = intent.getIntExtra("messageCount", 0);// 留言
			int replyCount = intent.getIntExtra("replyCount", 0);

			Bundle bundle = intent.getExtras();
			// 宣讲会
			if (MainActivity.bvCareerTalk != null) {
				MainActivity.CarrerTalkCount += carrerTalkCount;
				if (MainActivity.CarrerTalkCount > 0) {
					MainActivity.bvCareerTalk.setText(MainActivity.CarrerTalkCount + "");
					MainActivity.bvCareerTalk.show();
				} else {
					MainActivity.bvCareerTalk.setText("");
					MainActivity.bvCareerTalk.hide();
				}
			}
			// 校园招聘
			if (MainActivity.bvRecruit != null) {
				MainActivity.RecruitCount += recruitCount;
				if (MainActivity.RecruitCount > 0) {
					MainActivity.bvRecruit.setText(MainActivity.RecruitCount + "");
					MainActivity.bvRecruit.show();
				} else {
					MainActivity.bvRecruit.setText("");
					MainActivity.bvRecruit.hide();
				}
			}
			
			// 校园招聘
			if (replyCount > 0) {
				MainActivity.ReplyCount += replyCount;
			}
			
			if (messageCount > 0) {
				MainActivity.MessageCount += messageCount;
			}


			// 通知栏显示
			this.notification(context, carrerTalkCount, recruitCount,
					messageCount, replyCount, bundle);
		}
	}

	private void notification(Context context, int carrerTalkCount,
			int recruitCount, int messageCount, int replyCount, Bundle bundle) {
		int noticeCount = carrerTalkCount + recruitCount + messageCount
				+ replyCount;
		// 创建 NotificationManager
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String contentTitle = "";
		String contentText = "";
		// 判断是否发出通知信息
		// 创建通知 Notification
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
				context);
		
		//清除过去提醒
		notificationManager.cancelAll();
		
		MainActivity.NotifyCount += noticeCount;
		if (MainActivity.NotifyCount == 1) {
			if (MainActivity.RecruitCount == 1) {
				Recruit recruit = (Recruit) bundle.getSerializable("recruit");
				contentTitle = "您有一条新的校园招聘信息";
				contentText = String.format("%s %s", recruit.getCompanyName(),
						recruit.getPosition());
			}
			if (MainActivity.CarrerTalkCount == 1) {
				CareerTalk careerTalk = (CareerTalk) bundle
						.getSerializable("careertalk");
				contentTitle = "您有一条新的宣讲会信息";
				contentText = String.format("%s %s %s",
						careerTalk.getCompanyName(),
						careerTalk.getSchoolName(), careerTalk.getDate());
			}
			if (MainActivity.MessageCount == 1) {
				UserMessage message = (UserMessage) bundle
						.getSerializable("message");
				contentTitle = message.getUserName() + " 给你发来了私信";
				if (message.getContent().length() < 10)
					contentText = message.getContent();
				else
					contentText = message.getContent().substring(0, 10);
			}
			if (MainActivity.ReplyCount == 1) {
				BBSReply reply = (BBSReply) bundle.getSerializable("reply");
				contentTitle = reply.getUserName() + "回复了你";
				contentText = reply.getContent();
			}
		} else {
			contentTitle = "您有 " + MainActivity.NotifyCount + " 条新信息";
			if (MainActivity.RecruitCount > 0) {
				contentText += String.format("%d条招聘信息 ", MainActivity.RecruitCount);
			}
			if (MainActivity.CarrerTalkCount > 0) {
				contentText += String.format("%d条宣讲会信息 ", MainActivity.CarrerTalkCount);
			}
			if (MainActivity.MessageCount > 0) {
				contentText += String.format("%d条私信 ", MainActivity.MessageCount);
			}
			if (MainActivity.ReplyCount > 0) {
				contentText += String.format("%d个人回复了你 ", MainActivity.ReplyCount);
			}
		}
		mNotifyBuilder.setContentTitle(contentTitle)
				.setContentText(contentText).setSmallIcon(R.drawable.icon);

		// 设置点击通知跳转
		Intent resultIntent = new Intent(context, MainActivity.class);
		resultIntent.putExtra("NOTICE", true);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
		stackBuilder.addParentStack(MainActivity.class);
		stackBuilder.addNextIntent(resultIntent);

		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
				PendingIntent.FLAG_UPDATE_CURRENT);
		mNotifyBuilder.setContentIntent(resultPendingIntent);
		// 设置点击清除通知
		mNotifyBuilder.setAutoCancel(true); 

		/*
		 * if(noticeCount > _lastNoticeCount) { //设置通知方式 notification.defaults
		 * |= Notification.DEFAULT_LIGHTS;
		 * 
		 * //设置通知音-根据app设置是否发出提示音
		 * if(((AppContext)context.getApplicationContext()).isAppSound())
		 * notification.sound = Uri.parse("android.resource://" +
		 * context.getPackageName() + "/" + R.raw.notificationsound);
		 * 
		 * //设置振动 <需要加上用户权限android.permission.VIBRATE> //notification.vibrate =
		 * new long[]{100, 250, 100, 500}; }
		 */

		// 发出通知
		Uri alarmSound = RingtoneManager
				.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
		if (alarmSound == null) {
			alarmSound = RingtoneManager
					.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alarmSound == null) {
				alarmSound = RingtoneManager
						.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			}
		}

		// Add as notification
		mNotifyBuilder.setSound(alarmSound);
		mNotifyBuilder.setVibrate(new long[]{100, 250, 100, 500});
		notificationManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());
	}

}
