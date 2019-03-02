package com.example.myfirebasecloudmessaging;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
	private static String TAG = "MyFirebaseMessagingService";
	
	@Override
	public void onNewToken(String s) {
		super.onNewToken(s);
		Log.d(TAG, "Refreshed token: " + s);
	}
	
	// Method ini ditriggered pas message diterima dari Firebase console
	@Override
	public void onMessageReceived(RemoteMessage remoteMessage) {
		super.onMessageReceived(remoteMessage);
		if(remoteMessage.getNotification() != null){
			sendNotification(remoteMessage.getNotification().getBody()); // Call sendNotification method utk menampilkan notif
		}
	}
	
	private void sendNotification(String messageBody) {
		String channelId = getString(R.string.default_notification_channel_id);
		String channelName = getString(R.string.default_notification_channel_name);
		
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); // Add flag for features applying to object. In this case, when the activity running the task, the activity moved to top of the stack and the other activity in the stack will be destroyed
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT); // Set pending intent based on the above mentioned intent
		
		Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION); // Create uri object for ringtone
		// Create blueprint for Notification object
		NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
			.setSmallIcon(R.mipmap.ic_launcher)
			.setContentText(messageBody)
			.setAutoCancel(true)
			.setSound(defaultSoundUri) // Set sound based on URI object
			.setContentIntent(pendingIntent);
		
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		
		// Check if the build version in device is on Oreo Android OS or newer
		if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
			NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT);
			
			notificationBuilder.setChannelId(channelId);
			
			if(mNotificationManager != null){
				mNotificationManager.createNotificationChannel(channel); // Set channel for notification manager to give notification to user (for Oreo Android OS or newer)
			}
		}
		
		// Build a notification object
		Notification notification = notificationBuilder.build();
		
		// Give notification to user (for OS that is older than Oreo)
		if(mNotificationManager != null){
			mNotificationManager.notify(0, notification);
		}
	}
}
