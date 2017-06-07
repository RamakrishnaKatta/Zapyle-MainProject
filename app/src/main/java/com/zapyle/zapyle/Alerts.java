package com.zapyle.zapyle;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.freshdesk.hotline.Hotline;

import activity.Parallax;
import activity.profile;
import activity.upload;
import activity.SplashScreen;
import utils.ExternalFunctions;
import utils.GetSharedValues;

/**
 * Created by haseeb on 26/8/15.
 */
public class Alerts {
    //    public static final Context context = null;
    public static void loginAlert(final Context context) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("You are not logged in!");

        // set dialog message
        alertDialogBuilder
                .setMessage("Go to login page?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Hotline.clearUserData(context);
                        SharedPreferences settings = context.getSharedPreferences("LoginSession",
                                Context.MODE_PRIVATE);

                        SharedPreferences.Editor editor = settings.edit();
                        editor.putBoolean("GuestLoginStatus", false);
                        editor.apply();
                        Intent login = new Intent(context, Parallax.class);
                        login.putExtra("booltype", false);
                        context.startActivity(login);
                        ((Activity) context).finish();

                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();

                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


    public static void InternetAlert(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setTitle("No Internet Connection!");

        // set dialog message
        alertDialogBuilder
                .setMessage("Check Your Internet connection.")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        ExternalFunctions.strfilter = "";
                        ExternalFunctions.sort = 0;



                        ExternalFunctions.intprice = 0;
                        ExternalFunctions.intcat = 0;
                        ExternalFunctions.intsize = 0;


                        ActivityCompat.finishAffinity((Activity)context);



                    }
                });


        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }

    public static void listingAlert(final Context context, final String zapname) {

//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
////        alertDialogBuilder.setTitle("Thanks for your listing. Our stylist will review i and get back to youwith the status real soon!");
//
//        // set dialog message
//        alertDialogBuilder
//                .setCancelable(false)
//                .setMessage("Thanks for your listing. Our stylist will review it and get back to you with the status real soon!")
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        Intent login = new Intent(context, MyProfile.class);
//                        login.putExtra("zap_user", zapname);
//                        context.startActivity(login);
//                        ((Activity)context).finish();
//
//                    }
//                });
//
//        // create alert dialog
//        AlertDialog alertDialog = alertDialogBuilder.create();
//
//        // show it
//        alertDialog.show();


        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_listing_alert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setView(promptView);
        TextView touploadClick = (TextView) promptView.findViewById(R.id.touploadClick);
        TextView toprofile = (TextView) promptView.findViewById(R.id.toprofile);

        touploadClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(context, upload.class);
                context.startActivity(login);
                ((Activity) context).finish();
            }
        });

        toprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent login = new Intent(context, profile.class);
                login.putExtra("user_id", GetSharedValues.getuserId(context));
                login.putExtra("p_username",GetSharedValues.getUsername(context));
                login.putExtra("zap_user", zapname);
                context.startActivity(login);
                ((Activity) context).finish();
            }
        });

        // setup a dialog window
        alertDialogBuilder.setCancelable(false);
        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }


    public static void InternetSpeedAlert(final Context context) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
//        alertDialogBuilder.setTitle("Your internet connection is slow!");

        // set dialog message
        alertDialogBuilder
                .setMessage("Your internet connection is slow!")
                .setCancelable(false)
                .setPositiveButton("Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
//                        System.exit(0);
//                        dialog.cancel();
                        Intent splashScreen = new Intent(context, SplashScreen.class);
                        context.startActivity(splashScreen);

                    }
                });
//                .setNegativeButton("No", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // if this button is clicked, just close
//                        // the dialog box and do nothing
//                        dialog.cancel();
//
//                    }
//                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }


}
