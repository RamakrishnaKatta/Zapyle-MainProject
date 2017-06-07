package recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import activity.Branch_Redirect_Activity;

/**
 * Created by zapyle on 15/11/16.
 */

public class InstallReferrerReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        String referrer = intent.getStringExtra("referrer");
       Toast.makeText(context,"zz"+referrer.toString(),Toast.LENGTH_LONG).show();

        //Use the referrer
    }
}