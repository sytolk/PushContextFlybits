package bosch.mx.lud1ga.pushcontextflybits270;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.awareness.fence.FenceState;

/**
 * Created by LUD1GA on 05/04/2017.
 *
 */

// Handle the callback on the Intent.
public class MyFenceReceiver extends BroadcastReceiver {

    public String TAG = getClass().getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        FenceState fenceState = FenceState.extract(intent);

//        Toast.makeText(context, "FENCE:: "+fenceState.getFenceKey(), Toast.LENGTH_SHORT).show();
        Log.i(TAG,  "FENCE:: "+fenceState.getFenceKey());

        switch (fenceState.getFenceKey()){
            case "headphoneFence":
                switch(fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Toast.makeText(context,  "Headphones are plugged in.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Headphones are plugged in.");
                        break;
                    case FenceState.FALSE:
                        Toast.makeText(context,  "Headphones are NOT plugged in.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Headphones are NOT plugged in.");
                        break;
                    case FenceState.UNKNOWN:
                        Toast.makeText(context,  "The headphone fence is in an unknown state.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "The headphone fence is in an unknown state.");
                        break;
                }
                break;
            case "userDrivingActivityFence":
                switch(fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Toast.makeText(context,  "User is Driving.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "User is Driving.");
                        break;
                    case FenceState.FALSE:
                        Toast.makeText(context,  "User is NOT Driving.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "User is NOT Driving.");
                        break;
                    case FenceState.UNKNOWN:
                        Toast.makeText(context,  "Driving Fence is in an unknown state.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Driving Fence is in an unknown state.");
                        break;
                }
                break;
            case "userWalkingActivityFence":
                switch(fenceState.getCurrentState()) {
                    case FenceState.TRUE:
                        Toast.makeText(context,  "User is Walking.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "User is Walking.");
                        break;
                    case FenceState.FALSE:
                        Toast.makeText(context,  "User is NOT Walking.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "User is NOT Walking.");
                        break;
                    case FenceState.UNKNOWN:
                        Toast.makeText(context,  "Unknown if Walking or NOT.", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Unknown if Walking or NOT.");
                        break;
                }
                break;
        }
    }
}