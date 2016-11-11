package bosch.mx.lud1ga.pushcontextflybits;

import android.app.Application;
import android.content.pm.PackageManager;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.FlybitsOptions;
import com.flybits.core.api.context.ContextPriority;

import java.util.ArrayList;
import java.util.StringTokenizer;

/**
 * Created by LUD1GA on 17/08/2016.
 *
 */
public class PushContextApplication extends MultiDexApplication {

    public void onCreate(){
        super.onCreate();
        ArrayList<String> languages = new ArrayList<>();
        languages.add("en");
        languages.add("ja");
        languages.add("de");
        FlybitsOptions builder = new FlybitsOptions.Builder(this)
                //Additional Options Can Be Added Here
//                .enablePushNotifications(FlybitsOptions.GCMType.WITH_GOOGLE_SERVICES_JSON
//                        , getString(R.string.app_sender_id))
//                .enableContextUploading(1, ContextPriority.LOW)
//                .enableContextRules(1)
//                .setLocalization(languages)
//                .setDebug(true)
                .build();

        //Initialize the FlybitsOptions
        Flybits.include(this).initialize(builder);
    }
}
