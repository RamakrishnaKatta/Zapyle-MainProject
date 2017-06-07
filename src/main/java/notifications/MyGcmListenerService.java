package notifications;

/**
 * Created by zapyle on 17/10/16.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.DisplayMetrics;
import android.util.Log;

import com.android.volley.VolleyError;
import com.freshdesk.hotline.Hotline;
import com.freshdesk.hotline.HotlineNotificationConfig;
import com.google.android.gms.gcm.GcmListenerService;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import DataBase.DatabaseDB;
import activity.BuyPage;
import activity.EarnCash;
import activity.FilteredFeed;
import activity.HomePageNew;
import activity.Notifications;
import activity.ProductPage;
import activity.ProfilePage;
import activity.SplashScreen;
import activity.Upload1;
import network.ApiCommunication;
import network.ApiService;
import utils.ExternalFunctions;
import utils.GetSharedValues;

public class MyGcmListenerService extends GcmListenerService implements ApiCommunication {
    public static final String API_URL = EnvConstants.APP_BASE_URL;
    public String message;

    Intent following_intent;
    Context context;
    String title, sent_time, opened_time;
    int notif_id = 0;
    Bitmap remote_picture = null;
    Boolean notifImageStatus = false;
    DatabaseDB db;
    String TAG="GCM";
    //    SharedPreferences notif_set
    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, "From: " + from + "---" + data);

        Hotline instance = Hotline.getInstance(this);
        if (Hotline.isHotlineNotification(data)) {
            HotlineNotificationConfig notificationConfig = new HotlineNotificationConfig()
                    .setNotificationSoundEnabled(true)
                    .launchDeepLinkTargetOnNotificationClick(true)
                    .launchActivityOnFinish(HomePageNew.class.getName())
                    .setPriority(NotificationCompat.PRIORITY_HIGH);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                notificationConfig.setSmallIcon(R.drawable.iconsmall);
            } else {
                notificationConfig.setSmallIcon(R.drawable.icon);

            }


            Hotline.getInstance(getApplicationContext()).setNotificationConfig(notificationConfig);
            instance.handleGcmMessage(data);
            return;
        } else {

            sendNotification(data);

            ////System.out.println("bundle:" + data);
//        message = data.getString("message");
            //Log.d(TAG, "From: " + from + "---" + data.get("body"));
        }
    }

    private void sendNotification(Bundle data) {
//        System.out.println("DATAAA:" + data.getString("notif_id") + "__" + data.getString("sent_time") + "__" + data.getString("action"));
        System.out.println("body1:" + data);
        Object body = data.get("body");
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(body.toString());



        } catch (Exception e) {

        }
        Boolean async = false;
//        Marketing = data.getString("marketing");
//        try {
//        Spanned msgToShow = Html.fromHtml(message);
        String msgToShow = null;
        try {
            msgToShow = jsonObject.getString("message");
            Log.d(TAG, "From: " + "message" + "---" + jsonObject.getString("message"));
        } catch (JSONException e) {


        }


        if (data.containsKey("title")) {
            title = data.getString("title");
            Log.d(TAG, "From: " + "title" + "---" + data.getString("title"));
        } else {
            title = "Zapyle";
        }
        final JSONObject notifObject = new JSONObject();
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        opened_time = sdf.format(cal.getTime());
        //System.out.println("json object"+data);
        if (jsonObject.has("notif_id")) {
            try {
                notif_id = Integer.parseInt(jsonObject.getString("notif_id"));
                sent_time = jsonObject.getString("sent_time");

            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                notifObject.put("notif_id", notif_id);
                notifObject.put("sent_time", sent_time);
                notifObject.put("opened_time", opened_time);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        if (jsonObject.has("marketing")) {
            String action = null;
            try {
                action = jsonObject.getString("action");
            } catch (JSONException e) {
                action="";
                e.printStackTrace();
            }

            switch (action) {
                case "product":
                    async = false;
                    if (notif_id > 0) {
                        new MyAsyncTask().execute(String.valueOf(notifObject));
                    }
                    String sale = null;
                    try {
                        sale = jsonObject.getString("product_sale");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    String sale_id = null;
                    try {
                        sale_id = jsonObject.getString("product_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    following_intent = new Intent(this, ProductPage.class);
                    following_intent.putExtra("album_id", Integer.parseInt(sale_id));
                    following_intent.putExtra("pta", false);
                    following_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


                    break;
                case "referrel":
                    async = true;
                    if (ExternalFunctions.referalmsg.equals("1")) {
                        try {
                            ExternalFunctions.referalmsg = jsonObject.getString("msg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        following_intent = new Intent(this, EarnCash.class);
                        following_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    } else if (ExternalFunctions.referalmsg.length() < 1) {
//                    ExternalFunctions.referalmsg=data.getString("msg");
                        following_intent = new Intent(this, SplashScreen.class);
                        following_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        try {
                            following_intent.putExtra("msg", jsonObject.getString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    break;

                case "earn_cash":
                    async = true;

                    following_intent = new Intent(this, EarnCash.class);
                    break;

                case "disapproved":
                    async = true;
                    //System.out.println("disapp"+ExternalFunctions.disapprovemsg);
                    if (ExternalFunctions.disapprovemsg.equals("1")) {
                        try {
                            ExternalFunctions.disapprovemsg = jsonObject.getString("msg");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        following_intent = new Intent(this, Notifications.class);
                        following_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    } else if (ExternalFunctions.disapprovemsg.length() < 1) {
//                    ExternalFunctions.disapprovemsg=data.getString("msg");
                        following_intent = new Intent(this, SplashScreen.class);
                        following_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        try {
                            following_intent.putExtra("msg", jsonObject.getString("msg"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "newsfeed":
                    async = false;
                    if (notif_id > 0) {
                        new MyAsyncTask().execute(String.valueOf(notifObject));
                    }
                    following_intent = new Intent(this, HomePageNew.class);
                    following_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;


                case "profile":
                    async = false;
                    if (notif_id > 0) {
                        new MyAsyncTask().execute(String.valueOf(notifObject));
                    }
                    String profile_id = null;
                    try {
                        profile_id = jsonObject.getString("profile_id");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    String profile_name = null;
                    try {
                        profile_name = jsonObject.getString("profile_name");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    following_intent = new Intent(this, ProfilePage.class);
                    following_intent.putExtra("user_id", Integer.parseInt(profile_id));
                    following_intent.putExtra("p_username", profile_name);
                    following_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;


                case "upload":
                    async = false;

                    if (notif_id > 0) {
                        System.out.println("TASK CHECK:" + notifObject.toString());
                        new MyAsyncTask().execute(String.valueOf(notifObject));
                    }
                    following_intent = new Intent(this, Upload1.class);
                    ExternalFunctions.uploadbackcheck = 1;
                    break;

                case "filtered":
                    async = false;

                    // if(!notif_id.isEmpty())
                    if (notif_id > 0) {
                        System.out.println("TASK CHECK:" + notifObject.toString());
                        new MyAsyncTask().execute(String.valueOf(notifObject));

                    }
                    ExternalFunctions.FilterStatus = true;
                    ExternalFunctions.ActivityParam = "base";
                    ExternalFunctions.BroadCastedActivity = "";
                    ExternalFunctions.BroadCastedUrl = "";

                    ExternalFunctions.blfiteropen = false;
                    System.out.println("NOTIF : "+jsonObject);
                    try {
                        ExternalFunctions.strfilter = "&" + jsonObject.getString("args");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    ExternalFunctions.ActivityParam = "base";
                    ExternalFunctions.BroadCastedActivity = "";
                    ExternalFunctions.BroadCastedUrl = "";

                    ExternalFunctions.blfiteropen = true;
                    following_intent = new Intent(this, FilteredFeed.class);
                    following_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;

                default:
                    async = false;

                    if (notif_id > 0) {
                        new MyAsyncTask().execute(String.valueOf(notifObject));
                    }
                    following_intent = new Intent(this, HomePageNew.class);
                    following_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    break;
            }

        } else {
            async = false;

            if (notif_id > 0) {
                new MyAsyncTask().execute(String.valueOf(notifObject));
            }
            following_intent = new Intent(this, Notifications.class);
            following_intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);


        }


        NotificationCompat.BigPictureStyle notiStyle = new NotificationCompat.BigPictureStyle();
        if (jsonObject.has("image")) {
            notifImageStatus = true;
            try {
                remote_picture = getBitmapFromURL(jsonObject.getString("image"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            float multiplier= getImageFactor(getResources());
            remote_picture=Bitmap.createScaledBitmap(remote_picture, (int)(remote_picture.getWidth()*multiplier), (int)(remote_picture.getHeight()*multiplier), false);
            if(remote_picture!=null){
                notiStyle.bigPicture(remote_picture);
            }

            notiStyle.setSummaryText(msgToShow);
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) (Math.random() * 100) /* Request code */, following_intent,
                PendingIntent.FLAG_ONE_SHOT);

        if (notifImageStatus) {
            Log.d(TAG, "From: " + "inside image" + "---" + title + "__" + msgToShow);
            try {
                sendBigPictureStyleNotification(pendingIntent, title, msgToShow, getBitmapFromURL(jsonObject.getString("image")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "From: " + "inside else" + "---" + title + "__" + msgToShow);
            sendBigTextStyleNotification(pendingIntent, title, msgToShow);
        }
    }

    public void sendBasicNotification(PendingIntent pendingIntent, String title, String msgToShow) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentText(msgToShow)
                .setSmallIcon(R.drawable.icon);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) (Math.random() * 100), notification.build());
    }

    public void sendBigTextStyleNotification(PendingIntent pendingIntent, String title, String msgToShow) {

        System.out.println("CHECK: " + title + "__" + msgToShow);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(
                this.getResources(),
                R.drawable.icon);
        builder.setContentTitle(title)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setContentText(msgToShow)
                .setPriority(Notification.PRIORITY_HIGH)
                .setTicker(message)
                .setVibrate(new long[]{100, 250, 100, 250, 100, 250})
                .setStyle(new NotificationCompat.BigTextStyle().bigText(msgToShow))
                .setColor(Color.BLACK);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.iconsmall);
        } else {
            builder.setSmallIcon(R.drawable.iconsmall)
                    .setLargeIcon(notificationLargeIconBitmap);
        }
        System.out.println("INISDE NOTIF: ");

        notificationManager.notify((int) (Math.random() * 100), builder.build());
    }

    public void sendBigPictureStyleNotification(PendingIntent pendingIntent, String title, String msgToShow, Bitmap bitmap) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        Bitmap notificationLargeIconBitmap = BitmapFactory.decodeResource(
                this.getResources(),
                R.drawable.icon);
        builder.setContentTitle(title)
                .setAutoCancel(true)
                .setContentText(msgToShow)
                .setPriority(Notification.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
//                .setTicker(message)
                .setVibrate(new long[]{100, 250, 100, 250, 100, 250})
                .setStyle(new NotificationCompat.BigPictureStyle().bigPicture(bitmap).setSummaryText(msgToShow));
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            builder.setSmallIcon(R.drawable.iconsmall);
        } else {
            builder.setSmallIcon(R.drawable.iconsmall)
                    .setLargeIcon(notificationLargeIconBitmap);
        }

//        builder.flags |= Notification.FLAG_AUTO_CANCEL;
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify((int) (Math.random() * 100), builder.build());
    }


    public Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
           // myBitmap= Bitmap.createScaledBitmap(myBitmap, 120, 120, true);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }
    public static float getImageFactor(Resources r){
        DisplayMetrics metrics = r.getDisplayMetrics();
        float multiplier=metrics.density/3f;
        return multiplier;
    }
//
//    public void sendInboxStyleNotification(PendingIntent pendingIntent, String title, String msgToShow) {
//        PendingIntent pi = getPendingIntent();
//        Builder builder = new Notification.Builder(this)
//                .setContentTitle("IS Notification")
//                .setContentText("Inbox Style notification!!")
//                .setSmallIcon(R.drawable.ic_launcher)
//                .addAction(R.drawable.ic_launcher_web, "show activity", pi);
//
//        Notification notification = new Notification.InboxStyle(builder)
//                .addLine("First message").addLine("Second message")
//                .addLine("Thrid message").addLine("Fourth Message")
//                .setSummaryText("+2 more").build();
//        // Put the auto cancel notification flag
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        NotificationManager notificationManager = getNotificationManager();
//        notificationManager.notify(0, notification);
//    }


    private int getNotificationIcon() {


        boolean useWhiteIcon = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
        return useWhiteIcon ? R.drawable.icon : R.drawable.icon;
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }


    private class MyAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            System.out.println("params:" + params[0]);
            PostData(params[0]);


            return null;
        }

        protected void onPostExecute(Double result) {
//            pb.setVisibility(View.GONE);
//            Toast.makeText(getApplicationContext(), "command sent", Toast.LENGTH_LONG).show();
        }

        protected void onProgressUpdate(Integer... progress) {
//            pb.setProgress(progress[0]);
        }
    }


    private void PostData(String json) {
        JSONObject data = null;
        try {
            data = new JSONObject(json);
            data.put("sessionid", GetSharedValues.GetSessionId(getApplicationContext()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        InputStream inputStream = null;
        String result = "";
        try {
            System.out.println("rajeeff" + data);

            ApiService.getInstance(getApplicationContext(), 1).postData(MyGcmListenerService.this, EnvConstants.APP_BASE_URL + "/marketing/track/", data, "GCM", "signup");


        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
    }


}
