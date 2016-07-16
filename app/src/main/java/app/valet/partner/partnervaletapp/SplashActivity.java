package app.valet.partner.partnervaletapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import app.valet.partner.partnervaletapp.dao.Partner;
import app.valet.partner.partnervaletapp.util.HttpUtil;
import app.valet.partner.partnervaletapp.util.JsonUtil;
import app.valet.partner.partnervaletapp.util.StringUtils;

public class SplashActivity extends Activity {
    private static final String TAG = "TAG";
    /** Duration of wait **/
    private final long SPLASH_DISPLAY_LENGTH = 4000;
    private Activity splashActivity;

    /** Called when the activity is first created. */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        splashActivity = this;
        Log.e("TAG", "=================SPLASH SCREEN=====================");
        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/


        startHeavyProcessing();
        /*try {
            Thread.sleep(SPLASH_DISPLAY_LENGTH);
        }catch(Exception e){}*/
    }

    private void startHeavyProcessing(){
        new LongOperation().execute("");
    }

    private class LongOperation extends AsyncTask<String, Void, Partner> {

        @Override
        protected Partner doInBackground(String... params) {
            //some heavy processing resulting in a Data String
            /*for (int i = 0; i < 5; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.interrupted();
                }
            }*/
            //return "whatever result you have";
            Partner partner = null;
            try {
                while (!LocationCoordinator.getInstance(splashActivity).isLocationSet()) {
                    Log.e(TAG, "Location is not set!!");
                    try {
                        Thread.sleep(100);
                    } catch (Exception e) {

                    }
                }
                Log.e(TAG, "Location is set now!!");
                InstanceID instanceID = InstanceID.getInstance(getApplicationContext());
                String token = instanceID.getToken(getString(R.string.gcm_defaultSenderId),
                        GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                partner = getPartner(token);
                Log.e("splash", "partner - " + partner);
            }catch (Exception e){
                Log.e("Exception", "e - " + e);
            }

            return partner;
        }

        @Override
        protected void onPostExecute(Partner result) {
            Intent i = new Intent(splashActivity, MapsActivity.class);

            if(result!=null) {
                Log.e("splash", "result - " + result.getStatus());
                i.putExtra("online", "AVAILABLE".equals(result.getStatus()));
            }
            startActivity(i);
            finish();
        }

        @Override
        protected void onPreExecute() {
            LocationCoordinator.getInstance(splashActivity);
        }

        @Override
        protected void onProgressUpdate(Void... values) {}

        private Partner getPartner(String token) throws Exception{
            String response = HttpUtil.sendGet("http://52.33.192.176:8080/partner/register/" + token);
            if(StringUtils.isEmpty(response)){
                return null;
            }
            Log.e("splash", "response --> " + response);
            return JsonUtil.fromJsonToObj(response, Partner.class);
        }


    }
}
