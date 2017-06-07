package utils;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import activity.SplashScreen;
import activity.Upload1;
import services.UploadService;

/**
 * Created by haseeb on 1/3/16.
 */
public class ServiceHandle {

    public static String datapath;
    static int AlbumCount = 0;
    static int ProductId;
    static int CurrentCount = 0;
    public  static Boolean Status = false;
    static Boolean PTA = false;
    static Boolean Add = false;
    static Boolean BackgroundUploadStatus = false;

    public static void StartService(Context context){

        SharedPreferences settings = context.getSharedPreferences("UploadServiceSession",
                Context.MODE_PRIVATE);

        BackgroundUploadStatus = settings.getBoolean("BackgroundUploadStatus", false);
        datapath = settings.getString("datapath", "");
        AlbumCount = settings.getInt("AlbumCount", 0);
        ProductId = settings.getInt("ProductId", 0);
        CurrentCount = settings.getInt("CurrentCount", 0);
        PTA = settings.getBoolean("PTA", false);
        Add = settings.getBoolean("Add", false);
        ////System.out.println("outside brodcast receiver startservice__" + datapath + "__" + AlbumCount + "__" + ProductId + "__" + CurrentCount);

        ////System.out.println("Status___" + Status);

        if (!BackgroundUploadStatus && CurrentCount < AlbumCount) {
            if (!Status) {
                Status = true;
                ////System.out.println("inside brodcast receiver__" + datapath + "__" + AlbumCount + "__" + ProductId + "__" + CurrentCount);
                Intent intent_service = new Intent(context, UploadService.class);
                if (Upload1.mReceiver != null) {
                    intent_service.putExtra("receiver", Upload1.mReceiver);
                }
//                else {
//                    intent_service.putExtra("receiver", SplashScreen.mReceiver);
//                }
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
