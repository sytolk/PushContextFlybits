package bosch.mx.lud1ga.pushcontextflybits270;

import android.support.multidex.MultiDexApplication;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.FlybitsOptions;
import com.flybits.core.api.context.v2.ContextPriority;

/**
 * Created by LUD1GA on 17/08/2016.
 *
 */
public class PushContextApplication extends MultiDexApplication {

    public void onCreate(){
        super.onCreate();
//        ArrayList<String> languages = new ArrayList<>();
//        languages.add("en");
//        languages.add("ja");
//        languages.add("de");
        FlybitsOptions builder = new FlybitsOptions.Builder(this)
                //Additional Options Can Be Added Here
//                .enablePushNotifications(FlybitsOptions.GCMType.WITH_GOOGLE_SERVICES_JSON
//                        , getString(R.string.app_sender_id), true)
                .enableContextUploading(1, ContextPriority.HIGH)
//                .enableContextRules(1)
                .enablePushNotifications(FlybitsOptions.GCMType.NO_GCM, null)
//                .setLocalization(languages)
                .setDebug(true)
                .build();

        //Initialize the FlybitsOptions
        Flybits.include(this).initialize(builder);
    }
}
