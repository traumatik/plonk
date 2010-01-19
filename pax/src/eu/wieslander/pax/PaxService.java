package eu.wieslander.pax;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.acl.NotOwnerException;
import java.util.Enumeration;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.widget.RemoteViews;

public class PaxService extends Service{

	private static final int CUSTOM_VIEW_ID = 0;
	NanoHTTPd p;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		  super.onCreate();

          try {
				p = new NanoHTTPd(8080);
				//tv.setAutoLinkMask(Linkify.WEB_URLS);
				//tv.setText("Ready on ip: " + p.getLocalIpAddress() + ":8080");
		        //webview.loadUrl("http://" + p.getLocalIpAddress() + ":8080");
		        //running = true;
		       
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//tv.setText(e.toString());
				e.printStackTrace();
				//running = false;
			}
		//Notification Stuff
			StringBuilder sb = new StringBuilder();
			sb.append(getString(R.string.notificationTextStart));
			sb.append(getLocalIpAddress());
			CharSequence notificationText = sb.toString();
			
			String ns = Context.NOTIFICATION_SERVICE;
			NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
			Notification notification = new Notification(R.drawable.icon, "Pax Service Started", System.currentTimeMillis());
			notification.flags = Notification.FLAG_NO_CLEAR;
			notification.flags = Notification.FLAG_ONGOING_EVENT;
			//RemoteViews contentView = new RemoteViews(getPackageName(), R.layout.notification);
			//contentView.setImageViewResource(R.id.notificationImage, R.drawable.icon);
			//contentView.
			
			//contentView.setTextViewText(R.id.notificationText, notificationText);
			//PendingIntent pendingIntent = PendingIntent.
			//startService(new Intent(PaxToast.this, PaxService.class));
			
			//contentView.setOnClickPendingIntent(R.id.NotificationButton, pendingIntent);
			//notification.contentView = contentView;
			Context context = getApplicationContext();
			CharSequence contentTitle = "Pax Service";
			
			Intent notificationIntent = new Intent(this, PaxAlert.class);
			PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);
			notification.contentIntent = contentIntent;
			notification.setLatestEventInfo(context, contentTitle, notificationText, contentIntent);
			
			mNotificationManager.notify(CUSTOM_VIEW_ID, notification);
		
		}
	
	@Override
	public void onDestroy() {
		  super.onDestroy();
		  String ns = Context.NOTIFICATION_SERVICE;
	      NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		  mNotificationManager.cancel(CUSTOM_VIEW_ID);
		  if (p != null){
			 p.stop(); 
		  }
		  
		}

	public String getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						Log.i("LOG_pawn", inetAddress.getHostAddress().toString());
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Log.e("LOG_pawn", ex.toString());
		}
		return null;
	}
}
