package com.campusrecruit.common;

import com.campusrecruit.activity.MainActivity;
import com.campusrecruit.bean.BBSReply;
import com.campusrecruit.bean.BBSTopic;
import com.campusrecruit.bean.CareerTalk;
import com.campusrecruit.bean.Recruit;
import com.campusrecruit.bean.UserMessage;
import com.krislq.sliding.R;

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

	private static int lastNoticeCount;
	private static int lastCareerCount;
	private static int lastRecruitCount;
	private static int lastMessageCount;
	private static int lastReplyCount;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("notice", "recevei!!!!!");
		String ACTION_NAME = intent.getAction();
		Log.i("notice", ACTION_NAME);
		if ("com.campusRecruit.action.NOTICE".equals(ACTION_NAME)) {
			int carrerTalkCount = intent.getIntExtra("careerTalkCount", 0); // 宣讲会
			int recruitCount = intent.getIntExtra("recruitCount", 0);// 校园招聘
			int messageCount = intent.getIntExtra("messageCount", 0);// 留言
			int replyCount = intent.getIntExtra("replyCount", 0);

			Log.i("notice",
					String.format(
							"carrerTalkCount %d, recruitCount %d, messageCount %d, replyCount %d",
							carrerTalkCount, recruitCount, messageCount,
							replyCount));

			Bundle bundle = intent.getExtras();

			// 宣讲会
			if (MainActivity.bvCareerTalk != null) {
				if (carrerTalkCount > 0) {
					MainActivity.bvCareerTalk.setText(carrerTalkCount + "");
					MainActivity.bvCareerTalk.show();
				} else {
					MainActivity.bvCareerTalk.setText("");
					MainActivity.bvCareerTalk.hide();
				}
				MainActivity.CarrerTalkCount = carrerTalkCount;
			}
			// 校园招聘
			if (MainActivity.bvRecruit != null) {
				if (recruitCount > 0) {
					Log.i("notice", "show bv recruit");
					MainActivity.bvRecruit.setText(recruitCount + "");
					MainActivity.bvRecruit.show();
				} else {
					MainActivity.bvRecruit.setText("");
					MainActivity.bvRecruit.hide();
				}
				MainActivity.RecruitCount = recruitCount;
			}
			
			// 校园招聘
			if (replyCount > 0) {
				Log.i("life","reply:"+replyCount);
				MainActivity.ReplyCount = replyCount;
			}
			
			if (messageCount > 0) {
				Log.i("life","message:"+messageCount);
				MainActivity.MessageCount = messageCount;
			}

			/*
			 * // 私信 if (MainActivity.bvRecruit != null) { if (recruitCount > 0)
			 * { Log.i("notice", "show bv recruit");
			 * MainActivity.bvRecruit.setText(recruitCount + "");
			 * MainActivity.bvRecruit.show(); } else {
			 * MainActivity.bvRecruit.setText("");
			 * MainActivity.bvRecruit.hide(); } MainActivity.RecruitCount =
			 * recruitCount; }
			 * 
			 * // 其他人回复我 if (MainActivity.bvRecruit != null) { if (recruitCount
			 * > 0) { Log.i("notice", "show bv recruit");
			 * MainActivity.bvRecruit.setText(recruitCount + "");
			 * MainActivity.bvRecruit.show(); } else {
			 * MainActivity.bvRecruit.setText("");
			 * MainActivity.bvRecruit.hide(); } MainActivity.RecruitCount =
			 * recruitCount; }
			 */

			// 私信
			/*
			 * if (MainActivity.bvBBSSection != null) { if (discussCount > 0) {
			 * MainActivity.bvBBSSection.setText(discussCount + "");
			 * MainActivity.bvBBSSection.show(); } else {
			 * MainActivity.bvBBSSection.setText("");
			 * MainActivity.bvBBSSection.hide(); } MainActivity.DiscussCount =
			 * discussCount; }
			 */

			// 通知栏显示
			this.notification(context, carrerTalkCount, recruitCount,
					messageCount, replyCount, bundle);
		}
	}

	private void notification(Context context, int carrerTalkCount,
			int recruitCount, int messageCount, int replyCount, Bundle bundle) {
		int noticeCount = carrerTalkCount + recruitCount + messageCount
				+ replyCount;
		Log.i("notice", String.format("%d %d %d %d", carrerTalkCount,
				recruitCount, messageCount, replyCount));
		// 创建 NotificationManager
		NotificationManager notificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		String contentTitle = "";
		String contentText = "";

		int _lastNoticeCount, _lastMessageCount, _lastReplyCount, _lastRecuitCount, _lastCareerCount;

		// 判断是否发出通知信息
		if (noticeCount == 0) {
			notificationManager.cancelAll();
			lastNoticeCount = 0;
			return;
		} else if (noticeCount == lastNoticeCount) {
			return;
		} else {
			_lastNoticeCount = lastNoticeCount;
			_lastMessageCount = lastMessageCount;
			_lastReplyCount = lastReplyCount;
			_lastRecuitCount = lastRecruitCount;
			_lastCareerCount = lastCareerCount;
			lastNoticeCount = noticeCount;
			lastMessageCount = messageCount;
			lastReplyCount = replyCount;
			lastRecruitCount = recruitCount;
			lastCareerCount = carrerTalkCount;
		}
		Log.i("notice", "notify");
		// 创建通知 Notification
		NotificationCompat.Builder mNotifyBuilder = new NotificationCompat.Builder(
				context);
		if (_lastNoticeCount == 0 && noticeCount == 1) {
			if (recruitCount > _lastRecuitCount) {
				Recruit recruit = (Recruit) bundle.getSerializable("recruit");
				contentTitle = "您有一条新的校园招聘信息";
				Log.i("notice", "new recruit");
				contentText = String.format("%s %s", recruit.getCompanyName(),
						recruit.getPosition());
				Log.i("notice", "new recruit content is");
			}
			if (carrerTalkCount > _lastCareerCount) {
				CareerTalk careerTalk = (CareerTalk) bundle
						.getSerializable("careertalk");
				if (careerTalk == null) {
					Log.i("oops", "!!!!!!!!!!!!!!!!!!!");
				}
				contentTitle = "您有一条新的宣讲会信息";
				contentText = String.format("%s %s %s",
						careerTalk.getCompanyName(),
						careerTalk.getSchoolName(), careerTalk.getDate());
			}
			if (messageCount > _lastMessageCount) {
				UserMessage message = (UserMessage) bundle
						.getSerializable("message");
				contentTitle = message.getUserName() + " 给你发来了私信";
				if (message.getContent().length() < 10)
					contentText = message.getContent();
				else
					contentText = message.getContent().substring(0, 10);
			}
			if (lastReplyCount > _lastReplyCount) {
				BBSReply reply = (BBSReply) bundle.getSerializable("reply");
				if (reply == null) {
					Log.i("oops", "!!!!!!!!!reply!!!!!!!!!!");
					return;
				}
				contentTitle = reply.getUserName() + "回复了你";
				contentText = reply.getContent();
			}
		} else {
			contentTitle = "您有 " + noticeCount + " 条新信息";
			if (recruitCount > 0) {
				contentText += String.format("%d条招聘信息 ", recruitCount);
			}
			if (carrerTalkCount > 0) {
				contentText += String.format("%d条宣讲会信息 ", carrerTalkCount);
			}
			if (messageCount > 0) {
				contentText += String.format("%d条私信 ", messageCount);
			}
			if (replyCount > 0) {
				contentText += String.format("%d个人回复了你 ", replyCount);
			}
		}
		Log.i("notice", "set content");
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
		/* mNotifyBuilder.setAutoCancel(true); */

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
		Log.i("notice", "notice content is " + contentText);
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
		notificationManager.notify(NOTIFICATION_ID, mNotifyBuilder.build());
	}

}
