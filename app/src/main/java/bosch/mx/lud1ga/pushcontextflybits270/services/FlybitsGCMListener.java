package bosch.mx.lud1ga.pushcontextflybits270.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.exceptions.FlybitsPushException;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.models.Push;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

import bosch.mx.lud1ga.pushcontextflybits270.R;

public class FlybitsGCMListener extends FirebaseMessagingService {


    private static final String MSG_RECEIVED = "flybits::Gcm::msg_received";
    private static final String MSG_RECEIVED_EXTRA = "flybits::Gcm::msg_received_extra";

    private final String TAG = getClass().getSimpleName();

    @Override
    public void onMessageReceived(RemoteMessage message){
        String from = message.getFrom();
        Map data = message.getData();

        Log.i(TAG, "onMessageReceived FIREBASE");

        try {
            Flybits.include(getBaseContext()).parseFCMPushNotification(data, null, new IRequestCallback<Push>() {
                @Override
                public void onSuccess(Push push) {
//                    Intent broadcastIntent = new Intent(MSG_RECEIVED);
//                    broadcastIntent.putExtra(MSG_RECEIVED_EXTRA, push.alert);
//                    String title    = (push.title != null)? push.title : "SOME_TITLE";
//                    String message  = (push.alert != null)? push.alert: "SOME_ALERT";
                    showBeaconRuleNotification(getBaseContext(), push);
//                    sendBroadcast(broadcastIntent);
                }

                @Override
                public void onException(Exception e) {}

                @Override
                public void onFailed(String s) {}

                @Override
                public void onCompleted() {}
            });
        }catch (FlybitsPushException e){
            //Your FCM Message is Not Flybits Compatible, write your own logic
        }
    }

    private void showBeaconRuleNotification(Context baseContext, Push push) {
        String msg = (!TextUtils.isEmpty(push.bodyAsString))? push.bodyAsString : "No custom fields... ";
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(push.title)
                        .setContentText(push.alert)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(push.alert+"\n"+msg));
        // Sets an ID for the notification
        int mNotificationId = (push.title.contains("ZoneCTXRule"))?222222:111111;
// Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
// Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId, mBuilder.build());
    }
}
