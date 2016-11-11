package bosch.mx.lud1ga.pushcontextflybits.services;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.exceptions.FlybitsPushException;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestPaginationCallback;
import com.flybits.core.api.models.Pagination;
import com.flybits.core.api.models.Push;
import com.flybits.core.api.models.Rule;
import com.flybits.core.api.utils.filters.PushHistoryOptions;
import com.google.android.gms.drive.realtime.internal.event.ObjectChangedDetails;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonSyntaxException;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;

import bosch.mx.lud1ga.pushcontextflybits.R;

public class FlybitsGCMListener extends GcmListenerService {

    public final static String MSG_RECEIVED = "PUSH_RECEIVED";
    public final static String EXTRA_MSG    = "MSG_EXTRA";
    private final String TAG = getClass().getSimpleName();

    @Override
    public void onMessageReceived(String from, Bundle data) {
//        parseNotificationFlybits(data);
        parseNotificationCustom(data);
    }

    private void parseNotificationFlybits(Bundle data){
        try {
            Flybits.include(getBaseContext()).parseGCMPushNotification(data, null, new IRequestCallback<Push>() {

                @Override
                public void onSuccess(Push pushRule) {
                    Log.i(TAG, "inside "+pushRule);
                    if (pushRule != null && pushRule.body != null
//                            && pushRule.body instanceof Rule
                            ) {

                        Rule rule = new Rule();

                        Log.i(TAG, "converting body to rule");
                        try {
                            JSONObject jsonObject = new JSONObject(pushRule.bodyAsString);
                            String templateName = jsonObject.getString("templateName");
                            String id = jsonObject.getString("id");
                            String templateId = jsonObject.getString("templateId");

                            Log.i(TAG, templateName+id+templateId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Log.i(TAG, "converted body to rule");
                        Intent broadcastIntent = new Intent(MSG_RECEIVED);

                        broadcastIntent.putExtra(EXTRA_MSG, rule.dataAsString);
                        String title = "TITLE";
                        String message = "MSG";
                        Log.i(TAG, "before setNot");
                        setNotification(getApplicationContext(), 2, title, message);

                        Log.i(TAG, "sending broadcast");
                        sendBroadcast(broadcastIntent);
                    }
                }

                @Override
                public void onException(Exception e) {
                    Log.e(TAG, "onException", e);

                }

                @Override
                public void onFailed(String s) {
                    Log.e(TAG, "onFailed :: "+s);

                }

                @Override
                public void onCompleted() {
                    Log.i(TAG, "onComplete");
                }
            });
        } catch (Exception e){
            Log.e(TAG, "onMessageReceived", e);
        }
    }

    private void parseNotificationCustom(Bundle data){
        BigDecimal version = new BigDecimal(data.getString("timestamp"));
        long timestamp = version.longValue();
        long version1 = data.containsKey("version")?Long.parseLong(data.getString("version")):-1L;

        Push pushRule = new Push(data.getString("entity"), data.getString("action"), data.getString("category")
                , version1, timestamp, data.getString("body"));

        Log.i(TAG, "Received GCMBroadcast: " + pushRule.toString());
//        if (pushRule != null && pushRule.body != null) {

        Log.i(TAG, "entity::"+pushRule.entity);
        if(pushRule.entity.equals(Push.Entity.MOMENT_INSTANCE)){
            handleMoment(pushRule);
        }else if(pushRule.entity.equals(Push.Entity.RULE)){
            handleRule(pushRule);
        }
    }

    private void handleMoment(Push pushRule) {
//        try {
//            JSONObject jsonObject = new JSONObject(pushRule.bodyAsString);
////            String templateName = jsonObject.getString("templateName");
////            String id = jsonObject.getString("id");
////            String templateId = jsonObject.getString("templateID");
//
//            rule.name = ;
//            rule.id = id;
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e(TAG, "Error "+e.getCause(), e);
//            rule.name = "Name";
//            rule.id = "id";
//        }

//        Intent broadcastIntent = new Intent(MSG_RECEIVED);

//        broadcastIntent.putExtra(EXTRA_MSG, rule.dataAsString);

        PushHistoryOptions options = new PushHistoryOptions.Builder()
                .addPaging(1, 0) //10 -> limit (number of response object requested), 0 -> offset
                // last first
                .setSortOrder(PushHistoryOptions.SortOrder.DESCENDING) //Set the sort order type can be ASCENDING/DESCENDING
                .build();

        Flybits.include(getApplicationContext()).getPushHistory(options,
                new IRequestPaginationCallback<ArrayList<Push>>(){
                    @Override
                    public void onSuccess(ArrayList<Push> pushes, Pagination pagination) {
                            if(pushes != null){
                                Log.i(TAG, "FROM HISTORY :: "+pushes.get(0).toString());
                                String title = pushes.get(0).title;
                                String message = pushes.get(0).alert;
                                Log.i(TAG, "before setNot");
                                setNotification(getApplicationContext(), 1, title, message);

                            }
                    }

                    @Override
                    public void onException(Exception e) {}

                    @Override
                    public void onFailed(String s) {}

                    @Override
                    public void onCompleted() {}
                });

//        Log.i(TAG, "sending broadcast");
//        sendBroadcast(broadcastIntent);

    }

    private void handleRule(Push pushRule) {
        Rule rule = new Rule();

        try {
            JSONObject jsonObject = new JSONObject(pushRule.bodyAsString);
            String templateName = jsonObject.getString("templateName");
            String id = jsonObject.getString("id");
            String templateId = jsonObject.getString("templateID");

            rule.templateID = templateId;
            rule.name = templateName;
            rule.id = id;

            Log.i(TAG, templateName+id+templateId);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.e(TAG, "Error "+e.getCause(), e);
            rule.name = "Name";
            rule.id = "id";
        }

        Log.i(TAG, "converted body to rule");
//        Intent broadcastIntent = new Intent(MSG_RECEIVED);

//        broadcastIntent.putExtra(EXTRA_MSG, rule.dataAsString);
        String title = rule.name;
        String message = rule.id;
        Log.i(TAG, "before setNot");
        setNotification(getApplicationContext(), 0, title, message);

//        Log.i(TAG, "sending broadcast");
//        sendBroadcast(broadcastIntent);

    }

    private void setNotification(Context context, int id, String heading, String message) {

        NotificationManager mNotificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        int iconDrawable = useWhiteIcon ? R.mipmap.ic_launcher : R.mipmap.ic_launcher;

        //Incase null to avoid crash
        if (message == null)
            message = "";

        long[] vibration = new long[1];
        vibration[0] = 100L;

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(context)
                        .setContentTitle(heading)
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(message))
                        .setSmallIcon(iconDrawable)
                        .setVibrate(vibration)
                        .setAutoCancel(true);


        mNotificationManager.notify(id, mBuilder.build());
    }

}