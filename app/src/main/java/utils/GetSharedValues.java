package utils;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;


/**
 * Created by haseeb on 4/1/16.
 */
public class GetSharedValues {


    public static String GetSessionId(Context context){
        String SessionId;
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        SessionId = settings.getString("session_id","");
        return SessionId;
    }

    public static String GetgcmId(Context context){
        String gcm_id;
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        gcm_id = settings.getString("GcMToken", "");
        return gcm_id;
    }

    public static String GetCsrf(Context context){
        String csrfToken;
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        csrfToken = settings.getString("csrfToken","");
        return csrfToken;
    }

    public static Boolean LoginStatus(Context context){
        Boolean status;
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        status = settings.getBoolean("LOGIN_STATUS", false);
        return status;
    }


    public static int getuserId(Context context){
        //
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        int id = settings.getInt("USER_ID", 0);
        return id;
    }

    public static String getZapname(Context context){
        //
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        String zapname = settings.getString("USER_ZAPNAME", "");
        return zapname;
    }

    public static String getUsername(Context context){
        //
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        String zapname = settings.getString("USER_NAME", "");
        return zapname;
    }

    public static String getUserprofilepic(Context context){
        //
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        String zapname = settings.getString("USER_PROFILEPIC", "");
        return zapname;
    }

    public static String getUseremail(Context context){
        //
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        String zapname = settings.getString("USER_EMAIL", "");
        return zapname;
    }

    public static String getUserfullname (Context context){
        //
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        String zapname = settings.getString("USER_FULLNAME", "");
        return zapname;
    }

    public static String getUserephonenumber (Context context){
        //
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        String zapname = settings.getString("USER_PHONENUMBER", "");
        return zapname;
    }

    public static String getUsertype (Context context){
        //
        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                Context.MODE_PRIVATE);
        String zapname = settings.getString("USER_TYPE", "");
        return zapname;
    }

    public static int getScreenWidth (Context context){
        //
        JSONObject screenSize = ExternalFunctions.displaymetrics(context);
        int screenWidth = screenSize.optInt("width");
        return screenWidth;
    }



    public static int getScreenHeight (Context context){
        //
        JSONObject screenSize = ExternalFunctions.displaymetrics(context);
        int screenHeight = screenSize.optInt("height");
        return screenHeight;
    }

}
