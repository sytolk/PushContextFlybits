package bosch.mx.lud1ga.pushcontextflybits270.services;

import android.content.Intent;
import android.util.Log;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.exceptions.FlybitsPushException;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.models.Push;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

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
                    Intent broadcastIntent = new Intent(MSG_RECEIVED);
                    broadcastIntent.putExtra(MSG_RECEIVED_EXTRA, push.alert);
                    String title    = (push.title != null)? push.title : "SOME_TITLE";
                    String message  = (push.alert != null)? push.alert: "SOME_ALERT";
                    //setNotification(getBaseContext(), title, message);
                    sendBroadcast(broadcastIntent);
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
}
