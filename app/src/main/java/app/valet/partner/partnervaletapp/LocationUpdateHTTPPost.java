package app.valet.partner.partnervaletapp;

import android.location.Location;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ASAX on 01-07-2016.
 */
public class LocationUpdateHTTPPost extends AsyncTask<Void, Void, String> {
    public static String deviceLocationId = null;
    private Location location;



    public void setLocation(Location location){
        this.location = location;
    }
    @Override
    protected String doInBackground(Void... params) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        Log.e("NodeJS", "Updating location!!");

        DataOutputStream dos = null;
        try {
            URL url = new URL("http://52.33.192.176:8080/partner/location/"+deviceLocationId);


            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", "application/json");
            urlConnection.setRequestMethod("POST");
            urlConnection.setUseCaches(false);
            urlConnection.connect();

            dos = new DataOutputStream(urlConnection.getOutputStream());


            JSONObject object = new JSONObject();

            object.put("name", "Anurag");
            object.put("lat", ""+this.location.getLatitude());
            object.put("lng", ""+this.location.getLongitude());
            object.put("accuracy" , "" + this.location.getAccuracy());
            dos.write(object.toString().getBytes("UTF-8"));
            dos.flush();
            int responseCode = urlConnection.getResponseCode();

            if(200 == responseCode){
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(urlConnection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                if(response.length()>0) {
                    JSONObject jsonObject = new JSONObject(response.toString());
                    String token = jsonObject.getString("_id");
                    if(token!=null && token.trim().length()>0){
                        deviceLocationId = token;
                    }
                }
                in.close();
            }
        } catch (Exception e) {
            Log.e("TAG", "Error " + e);
        } finally{
            if(dos != null){
                try{
                    dos.close();
                }catch(final IOException e){
                    System.out.println("Error closing stream" + e);
                }
            }

            if (urlConnection != null) {
                urlConnection.disconnect();
            }

            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    System.out.println("Error closing stream" + e);
                }
            }
        }
        return "empty";
    }

    /**
     * on getting result
     */
    @Override
    protected void onPostExecute(String result) {
        // something with data retrieved from server in doInBackground
    }
}
