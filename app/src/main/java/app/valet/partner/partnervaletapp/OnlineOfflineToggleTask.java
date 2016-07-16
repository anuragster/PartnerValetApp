package app.valet.partner.partnervaletapp;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import app.valet.partner.partnervaletapp.dao.Partner;
import app.valet.partner.partnervaletapp.util.JsonUtil;
import app.valet.partner.partnervaletapp.util.StringUtils;

/**
 * Created by ASAX on 26-05-2016.
 */
public class OnlineOfflineToggleTask extends AsyncTask<Void, Void, String> {
    private Activity mapActivity;
    private Button mOfflineOnlineButton;
    private String gcmToken;
    private boolean newOnlineValue;

    public OnlineOfflineToggleTask(Button offlineOnlineButtonContext, Activity mapActivity, boolean newOnlineValue){
        this.mapActivity = mapActivity;
        this.mOfflineOnlineButton = offlineOnlineButtonContext;
        //this.gcmToken = gcmToken;
        this.newOnlineValue = newOnlineValue;
    }

    private void initGCMToken(){
        boolean isGCMInitialized = false;
        while(!isGCMInitialized) {
            try {
                InstanceID instanceID = InstanceID.getInstance(this.mapActivity);
                this.gcmToken = instanceID.getToken(this.mapActivity.getString(R.string.gcm_defaultSenderId),
                       GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
                isGCMInitialized = true;
            } catch (Exception e) {
                Log.e("Exception", "e - " + e);
                try {
                    Thread.sleep(1000l);
                }catch(Exception ee){
                }
            }
        }
    }

    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        String response = null;

        try {
            initGCMToken();
            // Construct the URL for the OpenWeatherMap query
            // Possible parameters are available at OWM's forecast API page, at
            // http://openweathermap.org/API#forecast
            URL url = new URL("http://52.33.192.176:8080/partner/register/6780ccbf42e584eb3cbc848b/" + this.gcmToken + "/" + this.newOnlineValue);

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("POST");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                response = null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                response = null;
            }
            response = buffer.toString();
        } catch (IOException e) {
            Log.e("PlaceholderFragment", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
            response = null;
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e("PlaceholderFragment", "Error closing stream", e);
                }
            }
        }
        return response;
    }

    /**
     * on getting result
     */
    @Override
    protected void onPostExecute(String result) {
        // something with data retrieved from server in doInBackground
        Log.e("TAG", "Post status update result - " + result);

        if(StringUtils.isEmpty(result)){
            return;
        }
        Partner partner = JsonUtil.fromJsonToObj(result, Partner.class);
        if(partner != null){
            boolean isOnline = ("AVAILABLE").equals(partner.getStatus());
            this.mOfflineOnlineButton.setText(isOnline?"ONLINE":"OFFLINE");
            ((MapsActivity)this.mapActivity).setStatus(isOnline);
            if(isOnline){
                this.mOfflineOnlineButton.setTextColor(Color.WHITE);
                this.mOfflineOnlineButton.setBackgroundColor(Color.parseColor("#00b359"));
            }else{
                this.mOfflineOnlineButton.setTextColor(Color.BLACK);
                this.mOfflineOnlineButton.setBackgroundColor(Color.WHITE);
            }
        }
    }
}
