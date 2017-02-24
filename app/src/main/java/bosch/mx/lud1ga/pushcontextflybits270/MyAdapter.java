package bosch.mx.lud1ga.pushcontextflybits270;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.flybits.core.api.Flybits;
import com.flybits.core.api.interfaces.IRequestCallback;
import com.flybits.core.api.models.JWTToken;
import com.flybits.core.api.models.Zone;
import com.flybits.core.api.models.ZoneMoment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * Created by LUD1GA on 11/08/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = getClass().getSimpleName();
    private ArrayList<ZoneMoment> mZoneMoments = new ArrayList<>();
    private ArrayList<Zone> mZones = new ArrayList<>();
    private Context mContext;

    public void setZoneMoments(ArrayList<ZoneMoment> zoneMoments){
        if(zoneMoments == null){
//            Log.i(TAG, "zoneMoments null");
            mZoneMoments  = new ArrayList<>();
        }else{
//            Log.i(TAG, "zoneMoments NOT null");
            mZoneMoments = zoneMoments;
//            setUpTokens();
        }
        notifyDataSetChanged();
    }

    public void setZones(ArrayList<Zone> zones){
        if(zones == null){
            mZones  = new ArrayList<>();
        }else{
            mZones = zones;
        }
    }

    private String getZoneName(ZoneMoment zoneMoment){
        for(Zone zone : mZones){
            if(zoneMoment.zoneID.equals(zone.id)){
                return zone.getName();
            }
        }
        return "N/A";
    }

    private String getZoneIcon(ZoneMoment zoneMoment){
        for(Zone zone : mZones){
            if(zoneMoment.zoneID.equals(zone.id)){
                return zone.getIcon();
            }
        }
        return "N/A";
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView titleView;
        public TextView subtitleView;
        public TextView body;
        public ImageView image;
        public ImageView backgroundImage;
        public TextView zoneName;
        public CardView cardView;

        public MyViewHolder(View v) {
            super(v);

            titleView = (TextView) v.findViewById(R.id.card_title);
            subtitleView = (TextView) v.findViewById(R.id.card_subtitle);
            body = (TextView) v.findViewById(R.id.card_body);
            image = (ImageView) v.findViewById(R.id.card_image);
            zoneName = (TextView) v.findViewById(R.id.card_zone);
            cardView = (CardView) v.findViewById(R.id.cardview_generic);
            backgroundImage = (ImageView) v.findViewById(R.id.card_background);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context) {
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                      int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_generic, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final MyViewHolder myViewHolder = (MyViewHolder) holder;
        ZoneMoment zoneMoment = mZoneMoments.get(holder.getAdapterPosition());

        Log.i(TAG, "loading background cardview "+getZoneIcon(zoneMoment));
        Glide.with(mContext)
                .load(getZoneIcon(zoneMoment))
                .asBitmap()
                .centerCrop()
                .into(myViewHolder.backgroundImage);

        if(zoneMoment.metadataAsString != null) {
            try {
                JSONObject metaData = new JSONObject(zoneMoment.metadataAsString);

                JSONObject data = metaData.getJSONObject("data");
                Log.i(TAG, data.has("templateID")+"--"+metaData.get("data").toString());
                if(data.has("templateID")){
                    aobVH(myViewHolder, zoneMoment, data);
                }else{
                    genericMomentVH(myViewHolder, zoneMoment
                            , data.getJSONObject("locales").getJSONObject("en"));
                }
            } catch (JSONException e) {
                defaultVH(myViewHolder, zoneMoment);
            }
        }else{
            defaultVH(myViewHolder, zoneMoment);
        }
    }

    /**
     * AdvancedObjectBuilder ViewHolder
     */
    private void aobVH(MyViewHolder myViewHolder, ZoneMoment zoneMoment, JSONObject data)
            throws JSONException{
        String templateId = data.getString("templateID");
        switch (templateId) {

            case "card_battery_low": {

                JSONObject enData = data
                        .getJSONObject("localizedKeyValuePairs").getJSONObject("en")
                        .getJSONObject("root");
                String subtitle = enData
                        .getString("msg");
                String body = enData
                        .getString("body");

                myViewHolder.titleView.setText(zoneMoment.getName());
                myViewHolder.body.setText(body);
                myViewHolder.subtitleView.setText(subtitle);
                myViewHolder.zoneName.setText(getZoneName(zoneMoment));
                Glide.with(mContext)
                        .load(enData.getString("img"))
                        .placeholder(R.mipmap.ic_launcher)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .into(myViewHolder.image);
            }
            break;
//                    case "card_type": {
//
//                        JSONObject enData = data
//                                .getJSONObject("localizedKeyValuePairs")
//                                .getJSONObject("en").getJSONObject("root");
//
//                        String cardType = enData.getString("card-type");
//
//                        switch (cardType){
//
//                        }
//                    }
//                    break;
            case "cafe-location-info": {
                // address
                // menu-reference
                // title
                // image-reference
                //hours
                //gps-coordinates{}
                JSONObject enData = data
                        .getJSONObject("localizedKeyValuePairs")
                        .getJSONObject("en").getJSONObject("root");

                String title = enData.getString("title");
                String subTitle = enData.getString("address");
                String body = "Hours";
                JSONArray hours = enData.getJSONArray("hours");
                for(int i = 0; i < hours.length(); i++){
                    body += "\n"+hours.getString(i);
                }

                String img = enData.getString("image-reference");

                myViewHolder.titleView.setText(title);
                myViewHolder.subtitleView.setText(subTitle);
                myViewHolder.body.setText(body);

                Glide.with(mContext)
                        .load(img)
                        .centerCrop()
                        .into(myViewHolder.image);
            }
            break;

            default:
                genericMomentVH(myViewHolder, zoneMoment, data);
                break;
        }
    }

    public void genericMomentVH(MyViewHolder myViewHolder, ZoneMoment zoneMoment
            , JSONObject data) throws JSONException {

//        Log.i(TAG, "genericMomentVH");
        if(data.has("websites")){
            Log.i(TAG, "websites");
            JSONObject jWebsite = data.getJSONArray("websites").getJSONObject(0);
            myViewHolder.titleView.setText(zoneMoment.getName());
            myViewHolder.subtitleView.setText(jWebsite.getString("url"));
            myViewHolder.body.setText(jWebsite.getString("description"));
            myViewHolder.zoneName.setText(getZoneName(zoneMoment));
        }else if(data.has("youtubeVideos")){
//            Log.i(TAG, "youtubeVideos");
            JSONObject jYoutubeVideo = data.getJSONArray("youtubeVideos").getJSONObject(0);

            myViewHolder.titleView.setText(zoneMoment.getName());
            myViewHolder.subtitleView.setText(jYoutubeVideo.getString("videoUrl"));// chanel name
            myViewHolder.body.setText(
                    jYoutubeVideo.getJSONObject("video").getJSONObject("snippet")
                            .getString("channelTitle"));// url
            myViewHolder.zoneName.setText(getZoneName(zoneMoment));
        }else {
//            Log.i(TAG, "default");
            myViewHolder.titleView.setText(zoneMoment.getName());
            myViewHolder.subtitleView.setText(zoneMoment.getName());
            myViewHolder.body.setText("Lorem Ipsum Lorum ");
            myViewHolder.zoneName.setText(getZoneName(zoneMoment));
        }

        Glide.with(mContext)
                .load(zoneMoment.getIcon())
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .skipMemoryCache(true)
                .into(myViewHolder.image);
    }

    public void defaultVH(final MyViewHolder myViewHolder, final ZoneMoment zoneMoment){

        Log.i(TAG, "defaultVH :: "+zoneMoment.getName());
        myViewHolder.titleView.setText(zoneMoment.getName());
        myViewHolder.subtitleView.setText(zoneMoment.getName());
//        myViewHolder.body.setText("Lorem Ipsum Lorum ");
        myViewHolder.zoneName.setText(getZoneName(zoneMoment));
        Glide.with(mContext)
                .load(zoneMoment.getIcon())
                .placeholder(R.mipmap.ic_launcher)
                .centerCrop()
                .skipMemoryCache(true)
                .into(myViewHolder.image);

        if(zoneMoment.packageName.contains("com.bosch")){
            Log.i(TAG, "ZMI name "+zoneMoment.getName());
            Glide.with(mContext)
                    .load(zoneMoment.getIcon())
                    .placeholder(R.mipmap.ic_launcher)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .into(myViewHolder.backgroundImage);
            getToken(zoneMoment, myViewHolder);
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mZoneMoments.size();
    }

    private void getToken(final ZoneMoment zoneMoment, final MyViewHolder myViewHolder){
//        for (int i = 0; i < mZoneMoments.size(); i++) {
        Log.i(TAG, "ZMI loading token "+zoneMoment.getName());
        
        Flybits.include(mContext).getZoneMomentJWTToken(
                zoneMoment,
                new IRequestCallback<JWTToken>() {
                    @Override
                    public void onSuccess(JWTToken jwtToken) {
                        Log.i(TAG, zoneMoment.getName()+" ZMI TOKEN on success " + jwtToken.token);
//                                mZoneMoments.get(finalI).metadataAsString = jwtToken.token;

                        String url = zoneMoment.launchURL
                                +"?payload="+jwtToken.token;
                        Log.i(TAG, "loading custom moment from Moment Server:: "+url);
                        switch (zoneMoment.packageName){
                            case "com.bosch.bnext.moments.helloworld":
                                getHelloWorldCMInfo(url, myViewHolder);
                                break;
                            case "com.bosch.bnext.moments.smartparking":
                                getSmartParkingCMInfo(url, myViewHolder);
                                break;
                            case "com.bosch.bulletinboard":
                                getBulletingBoardCMInfo(url, myViewHolder);
                                break;
                            default:
                                Log.i(TAG, "ZMI package name not found "+zoneMoment.packageName);
                        }
                    }

                    @Override
                    public void onException(Exception e) {
                        Log.e(TAG, "ZMI TOKEN on ERROR ", e);

                    }

                    @Override
                    public void onFailed(String s) {
                        Log.e(TAG, "ZMI TOKEN on ERROR "+ s);
                    }

                    @Override
                    public void onCompleted() {
                        Log.e(TAG, "on completed");

                    }
                });
//        }

    }

    private void getHelloWorldCMInfo(final String url, final MyViewHolder myHolder){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method. GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i(TAG, jsonObject.toString());
                            String body = jsonObject.getString("message")+"\n";

                            try {
                                body += "Instance building:: " + jsonObject.getString("building") + "\n";
                                body += "Instance floor:: " + jsonObject.getString("defaultFloor") + "\n";
                            }catch (JSONException e){};
                            Log.i(TAG, body);

                            myHolder.body.setText(body);
//                            Glide.with(mContext)
//                                    .load(jsonObject.getString("file"))
//                                    .placeholder(R.mipmap.ic_launcher)
//                                    .centerCrop()
//                                    .skipMemoryCache(true)
//                                    .into(myHolder.image);
                            myHolder.image.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            myHolder.body.setText("error parsing json from custom moment");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                myHolder.body.setText("That didn't work!");
                Log.e(TAG, "Error "+url , error);
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getSmartParkingCMInfo(final String url, final MyViewHolder myHolder){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method. GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i(TAG, jsonObject.toString());
                            String body = "";
                            try {
                                body += "Instance Location:: " + jsonObject.getString("location") + "\n";
                                body += "Instance building:: " + jsonObject.getString("building") + "\n";
                            }catch (JSONException e){
                                Log.e(TAG, "Error retrieving JSON", e);
                            }
                            myHolder.body.setText(body);
                            myHolder.image.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            myHolder.body.setText("error parsing json from custom moment");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                myHolder.body.setText("That didn't work!");
                Log.e(TAG, "Error "+url , error);
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    private void getBulletingBoardCMInfo(final String url, final MyViewHolder myHolder){

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(mContext);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method. GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Log.i(TAG, jsonArray.toString());
                            String body = jsonArray.toString();
//                            try {
//                                body += "Instance Location:: " + jsonObject.getString("location") + "\n";
//                                body += "Instance building:: " + jsonObject.getString("building") + "\n";
//                            }catch (JSONException e){
//                                Log.e(TAG, "Error retrieving JSON", e);
//                            }
                            myHolder.body.setText(body);
                            myHolder.image.setVisibility(View.GONE);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            myHolder.body.setText("error parsing json from custom moment");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                myHolder.body.setText("That didn't work!");
                Log.e(TAG, "Error "+url , error);
            }
        });
// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
}