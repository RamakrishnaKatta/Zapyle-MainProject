package utils;

import android.app.Activity;
import android.content.Context;
import android.support.design.widget.Snackbar;
import android.view.View;

/**
 * Created by zapyle on 25/7/16.
 */
public class CustomMessage {
    private static CustomMessage singleton =new CustomMessage();
    private Snackbar skbar;
    public CustomMessage(){

    }
    public static CustomMessage getInstance( ) {
        return singleton;
    }
    public void CustomMessage(Context context, String message) {
        try {
            View v = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            Snackbar.make(v, message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }catch (Exception e)
        {

        }


    }
}

