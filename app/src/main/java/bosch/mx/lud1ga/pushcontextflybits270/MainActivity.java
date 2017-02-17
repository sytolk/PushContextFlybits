package bosch.mx.lud1ga.pushcontextflybits270;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.MainThread;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.flybits.core.api.Flybits;
import com.flybits.core.api.context.v2.AvailablePlugins;
import com.flybits.core.api.context.v2.BasicData;
import com.flybits.core.api.context.v2.ContextManager;
import com.flybits.core.api.context.v2.ContextPriority;
import com.flybits.core.api.context.v2.plugins.FlybitsContextPlugin;
import com.flybits.core.api.context.v2.plugins.battery.BatteryData;
import com.flybits.core.api.context.v2.plugins.beacon.BeaconDataList;
import com.flybits.core.api.context.v2.plugins.beacon.BeaconMonitored;
import com.flybits.core.api.context.v2.plugins.location.LocationData;
import com.flybits.core.api.context.v2.plugins.network.NetworkData;
import com.flybits.core.api.events.EventZoneAdd;
import com.flybits.core.api.events.EventZoneEntered;
import com.flybits.core.api.events.EventZoneExited;
import com.flybits.core.api.events.EventZoneModified;
import com.flybits.core.api.events.EventZoneMomentAdd;
import com.flybits.core.api.events.EventZoneMomentModified;
import com.flybits.core.api.events.EventZoneMomentRemoved;
import com.flybits.core.api.events.EventZoneRemoved;
import com.flybits.core.api.events.context.EventContextRuleAdded;
import com.flybits.core.api.events.context.EventContextRuleRemoved;
import com.flybits.core.api.events.context.EventContextRuleStatusUpdated;
import com.flybits.core.api.events.context.EventContextSensorValuesUpdated;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.interfaces.IRequestLoggedIn;
import com.flybits.core.api.interfaces.IRequestPaginationCallback;
import com.flybits.core.api.models.Pagination;
import com.flybits.core.api.models.Push;
import com.flybits.core.api.models.Rule;
import com.flybits.core.api.models.User;
import com.flybits.core.api.models.Zone;
import com.flybits.core.api.models.ZoneMoment;
import com.flybits.core.api.utils.filters.LoginOptions;
import com.flybits.core.api.utils.filters.ZoneMomentOptions;
import com.flybits.core.api.utils.filters.ZoneOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.NoSubscriberEvent;

public class MainActivity extends AppCompatActivity {

    private boolean isLoggedIn = false;
    private String TAG = getClass().getSimpleName();

    private final int INTERVAL_SECS = 30;
    private final int FLEX_TIME_SECS = 5;

    private MyAdapter mAdapter;
    private ArrayList<Zone> mZones = new ArrayList();
    private RecyclerView mRecycler;
    private ProgressBar mProgressBar;

//    Location mOutsideBoschGDLGPS;
//    Location mInsideBoschGDLGPS;
//    Location mOutsideBoschGDLNetwork;
//    Location mInsideBoschGDLNetwork;
//    LocationManager mLocationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mProgressBar = (ProgressBar)findViewById(R.id.main_progressbar);

        checkPermissions();

        mAdapter = new MyAdapter(getApplicationContext());

        mRecycler = (RecyclerView) findViewById(R.id.main_recyclerview);
        mRecycler.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        mRecycler.setAdapter(mAdapter);

//        setMockLocation();

    }

    private void checkPermissions(){

        // Assume thisActivity is the current activity
        int permissionCheck = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck != PackageManager.PERMISSION_GRANTED){

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.ACCESS_COARSE_LOCATION},
                    1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1000);
        }
    }

    private void setMockLocation()
    {
//        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
////        mLocationManager.removeTestProvider(LocationManager.GPS_PROVIDER);
//        mLocationManager.addTestProvider(
//                LocationManager.GPS_PROVIDER,
//                "requiresNetwork" == "",
//                "requiresSatellite" == "",
//                "requiresCell" == "",
//                "hasMonetaryCost" == "",
//                "supportsAltitude" == "",
//                "supportsSpeed" == "",
//                "supportsBearing" == "",
//
//                android.location.Criteria.POWER_LOW,
//                android.location.Criteria.ACCURACY_FINE
//        );
//        mLocationManager.addTestProvider(
//                LocationManager.NETWORK_PROVIDER,
//                "requiresNetwork" == "",
//                "requiresSatellite" == "",
//                "requiresCell" == "",
//                "hasMonetaryCost" == "",
//                "supportsAltitude" == "",
//                "supportsSpeed" == "",
//                "supportsBearing" == "",
//
//                android.location.Criteria.POWER_LOW,
//                android.location.Criteria.ACCURACY_FINE
//        );
//
//        mLocationManager.setTestProviderEnabled(LocationManager.GPS_PROVIDER,true);
//        mLocationManager.setTestProviderEnabled(LocationManager.NETWORK_PROVIDER,true);
//
//        mLocationManager.setTestProviderStatus(LocationManager.GPS_PROVIDER,
//                LocationProvider.AVAILABLE, null,
//                System.currentTimeMillis());
//        mLocationManager.setTestProviderStatus(LocationManager.NETWORK_PROVIDER,
//                LocationProvider.AVAILABLE, null,
//                System.currentTimeMillis());
//
//        mInsideBoschGDLGPS = new Location(LocationManager.GPS_PROVIDER);
//        mInsideBoschGDLGPS.setLatitude(20.678837);
//        mInsideBoschGDLGPS.setLongitude(-103.340120);
//        mInsideBoschGDLGPS.setAccuracy(1);
//        mInsideBoschGDLGPS.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
//        mInsideBoschGDLGPS.setTime(System.currentTimeMillis());
//        mInsideBoschGDLNetwork = new Location(LocationManager.GPS_PROVIDER);
//        mInsideBoschGDLNetwork.setLatitude(20.678837);
//        mInsideBoschGDLNetwork.setLongitude(-103.340120);
//        mInsideBoschGDLNetwork.setAccuracy(1);
//        mInsideBoschGDLNetwork.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
//        mInsideBoschGDLNetwork.setTime(System.currentTimeMillis());
//
//        mOutsideBoschGDLGPS = new Location(LocationManager.GPS_PROVIDER);
//        mOutsideBoschGDLGPS.setLatitude (20.674443);
//        mOutsideBoschGDLGPS.setLongitude(-103.387257);
//        mOutsideBoschGDLGPS.setAccuracy(1);
//        mOutsideBoschGDLGPS.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
//        mOutsideBoschGDLGPS.setTime(System.currentTimeMillis());
//        mOutsideBoschGDLNetwork = new Location(LocationManager.GPS_PROVIDER);
//        mOutsideBoschGDLNetwork.setLatitude (20.674443);
//        mOutsideBoschGDLNetwork.setLongitude(-103.387257);
//        mOutsideBoschGDLNetwork.setAccuracy(1);
//        mOutsideBoschGDLNetwork.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
//        mOutsideBoschGDLNetwork.setTime(System.currentTimeMillis());
//
//        mLocationManager.setTestProviderLocation
//                (LocationManager.GPS_PROVIDER,
//                        mInsideBoschGDLGPS);
//
//        mLocationManager.setTestProviderLocation
//                (LocationManager.NETWORK_PROVIDER,
//                        mInsideBoschGDLNetwork);
    }

    private void loginFlybits() {

        mProgressBar.setVisibility(View.VISIBLE);
        Flybits.include(MainActivity.this).isUserLoggedIn(true, new IRequestLoggedIn() {
            @Override
            public void onLoggedIn(User user) {
                Log.i(TAG, "onLoggedIn");

                isLoggedIn = true;
                registerContextUpdates();

                requestZones(false);
            }

            @Override
            public void onNotLoggedIn() {
                LoginOptions options = new LoginOptions.Builder(MainActivity.this)
                        .loginAnonymously()
                        .setDeviceOSVersion()
                        .setRememberMeToken()
                        .build();

                Flybits.include(MainActivity.this).login(options, new IRequestCallback<User>() {
                    @Override
                    public void onSuccess(User user) {
                        isLoggedIn = true;
                        Log.i(TAG, "onLoggedIn onSuccess");

//                        requestMoments(false);
                        requestZones(false);
                        registerContextUpdates();
                    }

                    @Override
                    public void onException(Exception e) {
                        Log.e(TAG, "onLoggedIn Exception", e);

                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailed(String s) {
                        Log.i(TAG, "onLoggedIn onFailed "+s);
                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted");
                        mProgressBar.setVisibility(View.INVISIBLE);

                    }
                });
            }

            @Override
            public void onException(Exception e) {
                Log.e("onException", "LoginFlybits", e);
            }
        });
    }

    private void requestZones(final boolean subscribed){
        mProgressBar.setVisibility(View.VISIBLE);

        ZoneOptions options = new ZoneOptions.Builder()
                .addZoneId("E094A8B2-D992-4938-A8A3-A51C558C7F26")//daniel testing zone
                .build();
        Flybits.include(MainActivity.this).getZones(options,
                new IRequestPaginationCallback<ArrayList<Zone>>() {
                    @Override
                    public void onSuccess(ArrayList<Zone> zones, Pagination pagination) {

                        mAdapter.setZones(zones);

                        if(!subscribed) {
                            for (int i = 0; i < zones.size(); i++) {
                                mZones.add(zones.get(i));
                                Flybits.include(MainActivity.this).subscribe(Push.Entity.ZONE, zones.get(i).id);
                                Log.i(TAG, "subscribing for zone :: " +
                                        "\n name: " + zones.get(i).getName()
                                        +"\n description: " + zones.get(i).getDescription()
//                                        +"\n lang codes "+ zones.get(i).getSupportedLanguageCodes().size()
//                                        + " :" + zones.get(i).getSupportedLanguageCodes()
//                                        +"\n metadata: " + zones.get(i).metadataAsString
//                                        +"\n timezone: " + zones.get(i).timezone
//                                        +"\n color: " + zones.get(i).color
//                                        +"\n #moments: " + zones.get(i).countZoneMoments
                                        +"\n #desc: " + zones.get(i).getDescription("de")
                                );
                            }
                        }

                        requestMoments(subscribed);
                    }

                    @Override
                    public void onException(Exception e) {
                        Log.e(TAG, "onException getting Zones", e);

                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onFailed(String s) {
                        Log.i(TAG, "onFailed "+s);

                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCompleted() {
                    }
                });
    }

    private void requestMoments(final boolean subscribed){
        ZoneMomentOptions filter = new ZoneMomentOptions.Builder()
                .addZoneId("E094A8B2-D992-4938-A8A3-A51C558C7F26")
//                .addZoneId("11A98B2C-F8BF-4E21-85BC-099055EA55C6")
                .build();

        Flybits.include(MainActivity.this).getZoneMoments(filter
                , new IRequestPaginationCallback<ArrayList<ZoneMoment>>() {
                    @Override
                    public void onSuccess(ArrayList<ZoneMoment> zoneMoments, Pagination pagination) {
                        mAdapter.setZoneMoments(zoneMoments);
                        Log.i(TAG, "onSuccess -- getZoneMoments -- size:"+ zoneMoments.size());

                        if(!subscribed) {
                            for (int i = 0; i < zoneMoments.size(); i++) {
//                                Flybits.include(MainActivity.this).subscribe(Push.Entity.ZONE_MOMENT, zoneMoments.get(i).id);

                                Log.i(TAG, "subscribing for zone_moment :: " +
                                        "\n name: " + zoneMoments.get(i).getName()
                                        +"\n lang codes "+ zoneMoments.get(i).getSupportedLanguageCodes().size()
                                        + " :" + zoneMoments.get(i).getSupportedLanguageCodes()
                                        +"\n metadata: " + zoneMoments.get(i).metadataAsString
                                        +"\n launchURL: " + zoneMoments.get(i).launchURL
                                        +"\n packageName: " + zoneMoments.get(i).packageName
                                );
                            }
                        }
                        mProgressBar.setVisibility(View.INVISIBLE);

                        requestRules();
                        getMonitoredBeacons();
                    }

                    @Override
                    public void onException(Exception e) {
                        Log.e(TAG, "onException getting ZoneMoments", e);
                        mProgressBar.setVisibility(View.INVISIBLE);

                    }

                    @Override
                    public void onFailed(String s) {
                        Log.i(TAG, "onFailed "+s);

                        mProgressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onCompleted() {
                        Log.i(TAG, "onCompleted -- getZoneMoments");
                        mProgressBar.setVisibility(View.INVISIBLE);

                    }
                });
    }

    ArrayList<Rule> mRules = new ArrayList<>();
    private void requestRules(){

        Flybits.include(MainActivity.this).getRules(new IRequestCallback<ArrayList<Rule>>() {
            @Override
            public void onSuccess(ArrayList<Rule> rules) {
                Log.i(TAG, "getRules :: onSuccess :: "+rules.size());
                mRules = rules;
                for(Rule rule : rules){
                    Log.i(TAG, "rule :: "+rule.dataAsString);
//                    Flybits.include(MainActivity.this).subscribe(Push.Entity.RULE, rule.id);
                }
//                Flybits.include(MainActivity.this).subscribe(Push.Entity.RULE);
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "getRules :: ",e);

            }

            @Override
            public void onFailed(String s) {
                Log.i(TAG, "onFailed :: get rules :: "+s);

            }

            @Override
            public void onCompleted() {
                Log.i(TAG, "onCompleted :: get rules");

            }
        });
    }

//    ArrayList<BeaconMonitored> mBeacons = new ArrayList<>();

    ArrayList<BeaconMonitored> mBeacons = new ArrayList<>();

    private void getMonitoredBeacons(){
        Flybits.include(MainActivity.this).getMonitoredBeacons(new IRequestCallback<List<BeaconMonitored>>() {
            @Override
            public void onSuccess(List<BeaconMonitored> beaconMonitoreds) {
                mBeacons = (ArrayList<BeaconMonitored>) beaconMonitoreds;
            }

            @Override
            public void onException(Exception e) {
                Log.e(TAG, "getMonitoredBeacons :: ",e);

            }

            @Override
            public void onFailed(String s) {
                Log.e(TAG, "getMonitoredBeacons :: "+s);

            }

            @Override
            public void onCompleted() {

            }
        });
    }

    FlybitsContextPlugin mPluginBattery;
    FlybitsContextPlugin mPluginLocation;
    FlybitsContextPlugin mPluginNetwork;
    FlybitsContextPlugin mBeaconPlugin;

    @Override
    public void onStart() {
        super.onStart();

//        ContextManager.include(MainActivity.this)
//                .flushContext(getApplicationContext(), ContextPriority.LOW);

        loginFlybits();
    }

    @Override
    public void onResume(){
        super.onResume();
    }

    private void registerContextUpdates(){
        EventBus.getDefault().register(MainActivity.this);


        Log.i(TAG, "registering everything");

//        ContextManager.include(MainActivity.this)
//                .registerUploadingContext(ContextPriority.HIGH, INTERVAL_SECS, FLEX_TIME_SECS);
        ContextManager.include(MainActivity.this)
                .registerForRules(INTERVAL_SECS, FLEX_TIME_SECS);
        ContextManager.include(MainActivity.this)
                .registerUploadingContext(ContextPriority.HIGH, INTERVAL_SECS, FLEX_TIME_SECS);

        /**
         * REGISTER FOR CONTEXT CHANGES
         */
        registerReceiver(mReceiver, new IntentFilter("CONTEXT_UPDATED"));

        //Register of Context Updates
        mPluginBattery = new FlybitsContextPlugin.Builder()
                .setPluginIdentifier(AvailablePlugins.BATTERY)
                .setRefreshTime(INTERVAL_SECS,FLEX_TIME_SECS
                        , TimeUnit.SECONDS
                )
                .build();
        mPluginLocation = new FlybitsContextPlugin.Builder()
                .setPluginIdentifier(AvailablePlugins.LOCATION)
                .setRefreshTime(INTERVAL_SECS, FLEX_TIME_SECS
                        , TimeUnit.SECONDS
                )
                .build();
        mPluginNetwork = new FlybitsContextPlugin.Builder()
                .setPluginIdentifier(AvailablePlugins.NETWORK_CONNECTIVITY)
                .setRefreshTime(INTERVAL_SECS, FLEX_TIME_SECS
                        , TimeUnit.SECONDS
                )
                .build();
        mBeaconPlugin = new FlybitsContextPlugin.Builder()
                .setPluginIdentifier(AvailablePlugins.BEACON)
                .setRefreshTime(INTERVAL_SECS, FLEX_TIME_SECS
                        , TimeUnit.SECONDS
                )
                .build();

        ContextManager.include(MainActivity.this).register(mPluginNetwork);
        ContextManager.include(MainActivity.this).register(mPluginBattery);
        ContextManager.include(MainActivity.this).register(mPluginLocation);
        ContextManager.include(MainActivity.this).register(mBeaconPlugin);
//        Flybits.include(MainActivity.this).activateContextRules(10);
    }

    public void onPause(){
        super.onPause();
    }

    public void onStop(){
        Log.i(TAG, "Un-registering everything");
        try{
            for(Zone zone : mZones) {
                Flybits.include(MainActivity.this).unsubscribe(Push.Entity.ZONE, zone.id);
            }
            Flybits.include(MainActivity.this).unsubscribe(Push.Entity.ZONE_MOMENT
                    , "3D3E1BA6-E345-4371-A74A-7A8C3DFE30DD");
        }catch (Exception e){
            Log.e(TAG, "unsubscribe", e);
        }

        ContextManager.include(MainActivity.this).unregisterUploadingContext();

        try{
            ContextManager.include(MainActivity.this).unregister(mBeaconPlugin);
        }catch(Exception e){}
        try{
            ContextManager.include(MainActivity.this).unregister(mPluginLocation);
        }catch(Exception e){}
        try{
            ContextManager.include(MainActivity.this).unregister(mPluginNetwork);
        }catch(Exception e){}
        try{
            ContextManager.include(MainActivity.this).unregister(mPluginBattery);
        }catch(Exception e){}

        try {
            unregisterReceiver(mReceiver);
        }catch(RuntimeException e){}
        try {
            ContextManager.include(MainActivity.this).unregisterFromRuleCollection();

        }catch(RuntimeException e){}

        EventBus.getDefault().unregister(this);

        super.onStop();
    }

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.i(TAG, "onReceive");
            Bundle bundle = intent.getExtras();

            final String CONTEXT_TYPE_KEY = "CONTEXT_TYPE";
            final String CONTEXT_OBJ_KEY = "CONTEXT_OBJ";
            try {

                if (bundle.containsKey(CONTEXT_TYPE_KEY)) {
                    String contextType = bundle.getString(CONTEXT_TYPE_KEY);

                    if (contextType.equals(AvailablePlugins.BATTERY.getKey())) {
                        BatteryData data = bundle.getParcelable(CONTEXT_OBJ_KEY);
                        Toast.makeText(MainActivity.this,
                                "Battery Plugin :: " + data.percentage
                                        + " .. charging :: " + data.isCharging
                                , Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Battery Plugin :: " + data.toJson());
                        ContextManager.include(MainActivity.this).refresh(mPluginBattery);

                    }else if (contextType.equals(AvailablePlugins.LOCATION.getKey())) {

                        LocationData data = bundle.getParcelable(CONTEXT_OBJ_KEY);
                        Toast.makeText(MainActivity.this
                                , "Location Plugin :: " + data.toString()
                                , Toast.LENGTH_SHORT).show();
//                        Log.i(TAG, "Location Plugin :: " + data.toJson());
                        ContextManager.include(MainActivity.this).refresh(mPluginLocation);
//                        switchLocations(data);

                    }else if (contextType.equals(AvailablePlugins.NETWORK_CONNECTIVITY.getKey())) {

                        NetworkData data = bundle.getParcelable(CONTEXT_OBJ_KEY);
                        Toast.makeText(MainActivity.this
                                , "Network Plugin :: " + data.ssid
                                , Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Network Plugin :: " + data.toJson());
                        ContextManager.include(MainActivity.this).refresh(mPluginNetwork);

                    }else if(contextType.equals(AvailablePlugins.BEACON.getKey())){

                        BeaconDataList beaconDataList = bundle.getParcelable(CONTEXT_OBJ_KEY);
                        Toast.makeText(MainActivity.this
                                , "Beacon Plugin :: " + beaconDataList.toString()
                                , Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Beacon Plugin :: " + beaconDataList.toJson());

                    }

                    Log.i(TAG, bundle.toString());
                }
            }catch(RuntimeException e){
                Log.e(TAG, "onReceive", e);
            }
        }
    };

//    private void switchLocations(LocationData locationData){
//        if(locationData.lat == mInsideBoschGDLGPS.getLatitude()){
//            //inside... lets go out
//            mLocationManager.setTestProviderLocation
//                    (LocationManager.GPS_PROVIDER,
//                            mOutsideBoschGDLGPS);
//
//            mLocationManager.setTestProviderLocation
//                    (LocationManager.NETWORK_PROVIDER,
//                            mOutsideBoschGDLNetwork);
////            setMockLocation(mOutsideBoschGDL);
//            Log.i(TAG, "changing from INISDE to OUTSIDE");
//
//            Toast.makeText(MainActivity.this, "changing from INISDE to OUTSIDE"
//                    , Toast.LENGTH_SHORT).show();
//        }else{
//            mLocationManager.setTestProviderLocation
//                    (LocationManager.GPS_PROVIDER,
//                            mInsideBoschGDLGPS);
//
//            mLocationManager.setTestProviderLocation
//                    (LocationManager.NETWORK_PROVIDER,
//                            mInsideBoschGDLGPS);
////            setMockLocation(mInsideBoschGDL);
//            Log.i(TAG, "changing from OUTSIDE to INSIDE");
//            Toast.makeText(getApplicationContext(), "changing from OUTSIDE to INSIDE"
//                    , Toast.LENGTH_SHORT).show();
//        }
//    }

    public void onEventMainThread(EventContextSensorValuesUpdated event){
        Toast.makeText(MainActivity.this, "EventContextSensorValuesUpdated  "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());

    }

    public void onEventMainThread(EventContextRuleAdded event){
        Toast.makeText(MainActivity.this, "EventContextRuleAdded "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());

        ContextManager.include(MainActivity.this).refresh(mPluginBattery);
        ContextManager.include(MainActivity.this).refresh(mPluginLocation);
        ContextManager.include(MainActivity.this).refresh(mPluginNetwork);

        requestZones(true);
    }

    public void onEventMainThread(EventContextRuleRemoved event){
        Toast.makeText(MainActivity.this, "EventContextRuleRemoved "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());

        ContextManager.include(MainActivity.this).refresh(mPluginBattery);
        ContextManager.include(MainActivity.this).refresh(mPluginLocation);
        ContextManager.include(MainActivity.this).refresh(mPluginNetwork);

        requestZones(true);
    }

    public void onEventMainThread(EventContextRuleStatusUpdated event){
        Toast.makeText(MainActivity.this, "EventContextRuleStatusUpdated "
                +event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, "EventContextRuleStatusUpdated" + event.contextRule.toString());
        Log.i(TAG, "EventContextRuleStatusUpdated" + event.toString());

//        ContextManager.include(MainActivity.this).refresh(mPluginBattery);
//        ContextManager.include(MainActivity.this).refresh(mPluginLocation);
//        ContextManager.include(MainActivity.this).refresh(mPluginNetwork);

        BasicData beaconData = Flybits.include(MainActivity.this).getDataForContext(AvailablePlugins.BEACON);
        Log.i(TAG, "BEACON ::: "+beaconData.valueAsString);

        Type listType = new TypeToken<List<FlybitsBeacon>>(){}.getType();
        List<FlybitsBeacon> flybitsBeacons = new Gson().fromJson(beaconData.valueAsString, listType);

        for (FlybitsBeacon beacon : flybitsBeacons){
            Log.i(TAG, "FlybitsBeacon:: "+ beacon.getUuid()+" .... nearby? "+beacon.inRange);
        }

        /*
         [{"majorID":"90","minorID":"80","uuid":"00000009-9999-8888-7777-666666666666","type":"iBeacon","inRange":false}
         ,{"majorID":"100","minorID":"50","uuid":"11111111-2222-3333-4444-555555555555","type":"iBeacon","inRange":false}]
         */
        requestZones(true);
    }

    public void onEventMainThread(EventZoneEntered event){
        Toast.makeText(MainActivity.this, "EventZoneEntered "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());
        requestZones(true);
    }

    public void onEventMainThread(EventZoneModified event){
        Toast.makeText(MainActivity.this, "EventZoneModified "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());
        requestZones(true);
    }

    public void onEventMainThread(EventZoneAdd event){
        Toast.makeText(MainActivity.this, "EventZoneAdd "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());
        requestZones(true);
    }

    public void onEventMainThread(EventZoneExited event){
        Toast.makeText(MainActivity.this, "EventZoneExited "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());
        requestZones(true);
    }

    public void onEventMainThread(EventZoneRemoved event){
        Toast.makeText(MainActivity.this, "EventZoneRemoved "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());
        requestZones(true);
    }

    public void onEventMainThread(EventZoneMomentModified event){
        Toast.makeText(MainActivity.this, "EventZoneMomentModified "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());
        requestZones(true);
    }

    public void onEventMainThread(EventZoneMomentRemoved event){
        Toast.makeText(MainActivity.this, "EventZoneMomentRemoved "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());
        requestZones(true);
    }

    public void onEventMainThread(EventZoneMomentAdd event){
        Toast.makeText(MainActivity.this, "EventZoneMomentAdd "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());
        requestZones(true);
    }

    public void onEventMainThread(NoSubscriberEvent event){
        Toast.makeText(MainActivity.this, "NoSubscriberEvent "+event.toString(), Toast.LENGTH_SHORT).show();
        Log.i(TAG, event.toString());
        requestZones(true);
    }
}

