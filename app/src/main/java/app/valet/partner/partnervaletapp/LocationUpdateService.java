package app.valet.partner.partnervaletapp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by ASAX on 30-06-2016.
 */
public class LocationUpdateService extends IntentService {
    public LocationUpdateService(){
        super("name");
    }
    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();
        Log.e("TAG", "Work in progress");
        // Do work here, based on the contents of dataString

    }
}
