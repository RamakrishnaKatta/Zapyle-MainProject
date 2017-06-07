package services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.ResultReceiver;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

/**
 * Created by zapyle on 31/8/16.
 */
public class GcmRegService extends IntentService {
   // public ResultReceiver receiver;
    GoogleCloudMessaging gcm;
    public static String regId, csrf_token;
    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public GcmRegService() {
        super(FeedService.class.getName());
    }


    @Override
    protected void onHandleIntent(Intent intent) {
      //  receiver = intent.getParcelableExtra("receiver");
        System.out.println("insidegcmserver123");

        registerInBackground();
        this.stopSelf();

    }
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                    }
                    regId = gcm.register("1038239410729");

                    Log.d("RegisterActivity", "registerInBackground - regId: "
                            + regId);
                    msg = "Device registered, registration ID=" + regId;

                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("RegisterActivity", "Error: " + msg);
                }
                Log.d("RegisterActivity", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                System.out.println("Registered with GCM Server.--service" + msg);
//
                SharedPreferences settings = getSharedPreferences("LoginSession",
                        Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.putBoolean("GcmCheck", true);
                editor.putString("GcMToken", regId);
                editor.apply();



            }
        }.execute(null, null, null);
    }
}
