package recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import activity.upload;
import services.UploadService;

/**
 * Created by haseeb on 29/2/16.
 */
public class ConnectivityDetector_Receiver extends BroadcastReceiver{

    String datapath;
    int AlbumCount = 0;
    int ProductId;
    int CurrentCount = 0;
    public  static Boolean Status = false;
    Boolean PTA = false;
    Boolean Add = false;
    Boolean BackgroundUploadStatus = false;

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null) {
            if (info.isConnected()) {
                // you got a connection! tell your user!
                Log.i("Post", "Connected");
                SharedPreferences settings = context.getSharedPreferences("UploadServiceSession",
                        Context.MODE_PRIVATE);

                datapath = settings.getString("datapath", "");
                AlbumCount = settings.getInt("AlbumCount", 0);
                ProductId = settings.getInt("ProductId", 0);
                CurrentCount = settings.getInt("CurrentCount", 0);
                PTA = settings.getBoolean("PTA", false);
                Add = settings.getBoolean("Add", false);

                //////System.out.println("Status___" + Status);

                if (!BackgroundUploadStatus && CurrentCount < AlbumCount) {
                    if (!Status) {
                        Status = true;

                        Intent intent_service = new Intent(context, UploadService.class);
                        if (upload.mReceiver != null) {
                            intent_service.putExtra("receiver", upload.mReceiver);
                         }
                        intent_service.putExtra("Filepath", datapath);
                        intent_service.putExtra("ProductId", ProductId);
                        intent_service.putExtra("FromActivity", false);
                        intent_service.putExtra("PTA", PTA);
                        intent_service.putExtra("CurrentCount", CurrentCount);
                        intent_service.putExtra("Add", Add);
                        intent_service.putExtra("NumberOfProducts", AlbumCount);
                        context.startService(intent_service);


                    }

                }
                else {
                    Status = false;
                }
            }

        }
    }

}