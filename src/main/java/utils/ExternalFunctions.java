package utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * Created by haseeb on 18/9/15.
 */
public class ExternalFunctions {
    public static boolean tutorialStatus = false;
    Context context;
    public static int csrfStatus = 0;
    public String SCREEN_NAME = "STATICDATA";

    public static String dicActivity = "";
    public static String ActivityParam = "base";
    public static String BroadCastedActivity;
    public static String DiffParam="";
    public static String BroadCastedUrl;
    public static String FilteredUrl;
    public static String FilteredString="";
    public static Boolean SortStatus = false;
    public static Boolean FilterStatus = false;


    //
//    public static  JSONObject ob1;
//    public static  JSONObject ob3;
//
    public static boolean blfiteropen = false;
    //        public static String strupdated="";
//    public static String strpriceupdated="";
//    public static String[] selectedcat;
//    public static String[] selectedsize;
//    public static String[] selecteddiscount=new String[1];
//    public static String[] selectedprice;
//    public static int intclick=0;
//    public static  String strfilter="";
    public static int sort = 0;
    public static int sortsearch = 0;
    public static int intcat = 0;
    public static int intsize = 0;
    public static int intprice = 0;


    public static int uploadbackcheck = 0;
    //
    public static String referalmsg = "";
    public static String disapprovemsg = "";
    public static String selsugest = "a";

    public static Context[] cContextArray = {null, null, null, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null, null, null, null, null, null, null};

    public static String strfilter = "";
    public static JSONObject jsonObjsearch;
    public static String strOverlayurl = "";
    public static String strOverlayactivity = "";
    public static JSONObject datasearch;
    public static JSONObject datasearchcloset;
    public static boolean blsearch = false;
    public static boolean blsearch1 = false;
    public static boolean blapplysearch = false;
    public static boolean bloverlay = false;
    public static boolean blapplysug = false;
    public static String strsearch = "";

    public static String feedfilter = "";
    public static int cartcount = 0;
    public static Boolean overlaystatus = false;
    public static String[] strFilterdata;
    public static String[] tmpfilterdata;


    public static String DiffParamCheck(String url) {
        String param = null;
        if (url.contains("zap_market")) {
            param = "marketplace";
        } else if (url.contains("zap_curated")) {
            param = "vintage";
        } else if (url.contains("designer")) {
            param = "designer";
        }
        return param;
    }

    public static void expand(final View v) {
        v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        final int targetHeight = v.getMeasuredHeight();

        // Older versions of android (pre API 21) cancel animations for views with a height of 0.
        v.getLayoutParams().height = 1;
        v.setVisibility(View.VISIBLE);
        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                v.getLayoutParams().height = interpolatedTime == 1
                        ? ViewGroup.LayoutParams.WRAP_CONTENT
                        : (int)(targetHeight * interpolatedTime);
                v.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(targetHeight / v.getContext().getResources().getDisplayMetrics().density) + 100);
        v.startAnimation(a);
    }

    public static void collapse(final View v) {
        final int initialHeight = v.getMeasuredHeight();

        Animation a = new Animation()
        {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation t) {
                if(interpolatedTime == 1){
                    v.setVisibility(View.GONE);
                }else{
                    v.getLayoutParams().height = initialHeight - (int)(initialHeight * interpolatedTime);
                    v.requestLayout();
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // 1dp/ms
        a.setDuration((int)(initialHeight / v.getContext().getResources().getDisplayMetrics().density) + 100);
        v.startAnimation(a);
    }



    public static void ImageParams(ImageView imageView){
        float[] f = new float[9];
        imageView.getImageMatrix().getValues(f);

        // Extract the scale values using the constants (if aspect ratio maintained, scaleX == scaleY)
        final float scaleX = f[Matrix.MSCALE_X];
        final float scaleY = f[Matrix.MSCALE_Y];

        // Get the drawable (could also get the bitmap behind the drawable and getWidth/getHeight)
        final Drawable d = imageView.getDrawable();
        final int origW = d.getIntrinsicWidth();
        final int origH = d.getIntrinsicHeight();

        // Calculate the actual dimensions
        final int actW = Math.round(origW * scaleX);
        final int actH = Math.round(origH * scaleY);

        System.out.println("IMAGEPARAM INTRINSIC : "+origW+"===="+origH);
        System.out.println("IMAGEPARAM ACTUAL : "+actW+"===="+actH);
        System.out.println("IMAGEPARAM SCALE : "+scaleX+"===="+scaleY);
    }


    public static Animation FadeIn(){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(600);
        return fadeIn;
    }


    public static Animation FadeOut(){
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(600);
        fadeOut.setDuration(600);
        return fadeOut;
    }





    public static Bitmap rotateBitmap(String src, Bitmap bitmap) {
        try {
            int orientation = getExifOrientation(src);

            if (orientation == 1) {
                return bitmap;
            }

            Matrix matrix = new Matrix();
            switch (orientation) {
                case 2:
                    matrix.setScale(-1, 1);
                    break;
                case 3:
                    matrix.setRotate(180);
                    break;
                case 4:
                    matrix.setRotate(180);
                    matrix.postScale(-1, 1);
                    break;
                case 5:
                    matrix.setRotate(90);
                    matrix.postScale(-1, 1);
                    break;
                case 6:
                    matrix.setRotate(90);
                    break;
                case 7:
                    matrix.setRotate(-90);
                    matrix.postScale(-1, 1);
                    break;
                case 8:
                    matrix.setRotate(-90);
                    break;
                default:
                    return bitmap;
            }

            try {
                Bitmap oriented = Bitmap.createBitmap(bitmap, 0, 0,
                        bitmap.getWidth(), bitmap.getHeight(), matrix, true);
//                bitmap.recycle();
                return oriented;
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
                return bitmap;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bitmap;
    }


    private static int getExifOrientation(String src) throws IOException {
        int orientation = 1;

        try {
            /**
             * if your are targeting only api level >= 5 ExifInterface exif =
             * new ExifInterface(src); orientation =
             * exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
             */
            if (Build.VERSION.SDK_INT >= 5) {
                Class<?> exifClass = Class
                        .forName("android.media.ExifInterface");
                Constructor<?> exifConstructor = exifClass
                        .getConstructor(String.class);
                Object exifInstance = exifConstructor
                        .newInstance(src);
                Method getAttributeInt = exifClass.getMethod("getAttributeInt",
                        String.class, int.class);
                Field tagOrientationField = exifClass
                        .getField("TAG_ORIENTATION");
                String tagOrientation = (String) tagOrientationField.get(null);
                orientation = (Integer) getAttributeInt.invoke(exifInstance,
                        tagOrientation, 1);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        return orientation;
    }


    public static void showOverlay(final Context ctx, String strtilte, String str_description, final String strbutton, String activityname, boolean bl_fullscreen, boolean bl_canclose, String imgpath) {
        LayoutInflater layoutInflater = LayoutInflater.from(ctx);
        View promptView;

        if (bl_fullscreen) {
            promptView = layoutInflater.inflate(R.layout.popup, null);
        } else {
            promptView = layoutInflater.inflate(R.layout.popupsmall, null);
        }

        System.out.println("asasasasa");
        //AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(new ContextThemeWrapper(ctx,  R.style.full_screen_dialog));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ctx);
        alertDialogBuilder.setView(promptView);

        TextView txtclick;
        RelativeLayout rl = (RelativeLayout) promptView.findViewById(R.id.rlbotttm);
        RelativeLayout rlparent = (RelativeLayout) promptView.findViewById(R.id.rlparent);
        TextView txttitle = (TextView) promptView.findViewById(R.id.txttitle);
        TextView txtdescription = (TextView) promptView.findViewById(R.id.txtdescription);
        txtdescription.setGravity(Gravity.CENTER);
        ImageView imagepath = (ImageView) promptView.findViewById(R.id.image);
        ImageView imgclose = (ImageView) promptView.findViewById(R.id.imgclose);
        txtclick = (TextView) promptView.findViewById(R.id.btclick);

        if (bl_canclose) {
            imgclose.setVisibility(View.VISIBLE);
        } else {
            imgclose.setVisibility(View.GONE);
        }
        System.out.println("overlay image   " + EnvConstants.APP_MEDIA_URL + imgpath);

        Glide.with(ctx)
                .load(EnvConstants.APP_MEDIA_URL + imgpath)
                .fitCenter()
                .placeholder(R.drawable.playholderscreen)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(imagepath);


        final AlertDialog alert = alertDialogBuilder.create();

        alertDialogBuilder.setCancelable(false);

        alert.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                // Prevent dialog close on back press button
                return keyCode == KeyEvent.KEYCODE_BACK;
            }
        });

        imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                strOverlayurl = "";
                alert.dismiss();
            }
        });

        if (strtilte.isEmpty() || strtilte.equals("") || strtilte.equals(null)) {
            txttitle.setVisibility(View.GONE);
        } else {
            txttitle.setText(strtilte);
        }
        if (str_description.isEmpty() || str_description.equals("") || str_description.equals(null)) {
            txtdescription.setVisibility(View.GONE);
        } else {
            txtdescription.setText(str_description);
        }
        if (strbutton.isEmpty() || strbutton.equals("") || strbutton.equals(null)) {
            txtclick.setVisibility(View.GONE);


        } else {
            txtclick.setText(strbutton);
        }


        if (activityname.contains("FeedPage")) {
            if (ExternalFunctions.strOverlayurl.contains("zap_market") || ExternalFunctions.strOverlayurl.contains("zap_curated") || ExternalFunctions.strOverlayurl.contains("designer")) {
                activityname = "activity.BuySecondPage";
                ExternalFunctions.ActivityParam = "Broadcasted";
                ExternalFunctions.BroadCastedActivity = activityname;
                ExternalFunctions.BroadCastedUrl = ExternalFunctions.strOverlayurl.substring(EnvConstants.APP_BASE_URL.length(), ExternalFunctions.strOverlayurl.length());
            } else {
                ExternalFunctions.FilteredString = "";
                activityname = "activity.FilteredFeed";
            }
        }

        // imagepath.setScaleType(ImageView.ScaleType.FIT_XY);
        final String finalActivityname = activityname;
        txtclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                overlaystatus = true;


                Intent newintent = null;
                try {
                    alert.dismiss();
                    newintent = new Intent(ctx, Class.forName(finalActivityname));
                    //System.out.println("overlay --"+activityname);
                    ctx.startActivity(newintent);
                    ((Activity) ctx).finish();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

        imagepath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (strbutton.isEmpty() || strbutton.equals("") || strbutton.equals(null)) {


                    overlaystatus = true;


                    Intent newintent = null;
                    try {
                        alert.dismiss();
                        newintent = new Intent(ctx, Class.forName(finalActivityname));
                        //System.out.println("overlay --"+activityname);
                        ctx.startActivity(newintent);
                        ((Activity) ctx).finish();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }

            }
        });

        // create an alert dialog
        alert.show();

    }

    public static Boolean UploadCheck(Context context, String AlbumId) {
        String AlbumIdString;
        SharedPreferences settings = context.getSharedPreferences("UploadServiceSession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        AlbumIdString = settings.getString("AlbumIdString", "");
        ArrayList<String> array1 = RealmString.convertStringToArrayList(AlbumIdString);
        return array1.contains(AlbumId);
    }

    public static int UploadImageCount(Context context, String AlbumId) {
        String AlbumIdString;
        String AlbumCountString;
        SharedPreferences settings = context.getSharedPreferences("UploadServiceSession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        AlbumIdString = settings.getString("AlbumIdString", "");
        ArrayList<String> array1 = RealmString.convertStringToArrayList(AlbumIdString);
        AlbumCountString = settings.getString("AlbumCountString", "");
        ArrayList<String> array2 = RealmString.convertStringToArrayList(AlbumCountString);
        return Integer.parseInt(array2.get(array1.indexOf(AlbumId)));

    }

    public static void nullViewDrawablesRecursive(View view) {
        if (view != null) {
            try {
                ViewGroup viewGroup = (ViewGroup) view;

                int childCount = viewGroup.getChildCount();
                for (int index = 0; index < childCount; index++) {
                    View child = viewGroup.getChildAt(index);
                    nullViewDrawablesRecursive(child);
                }
            } catch (Exception e) {
            }

            nullViewDrawable(view);
        }
    }

    private static void nullViewDrawable(View view) {
        try {
            view.setBackgroundDrawable(null);
        } catch (Exception e) {
        }

        try {
            ImageView imageView = (ImageView) view;
            imageView.setImageDrawable(null);
            imageView.setBackgroundDrawable(null);
        } catch (Exception e) {
        }
    }

    public static ArrayList<String> GetImageUrisFromInternalStorage(Context context, String AlbumId, int Count) {

        ArrayList<String> AlbumUri = new ArrayList<String>();
        for (int i = 0; i < Count; i++) {
            AlbumUri.add(readFromFile(context.getFilesDir().getAbsolutePath() + "/" + "ZAPYLE" + String.valueOf(AlbumId) + "imguri" + i + ".txt"));
        }
        return AlbumUri;
    }


    public static String readFromFile(String filepath) {
        String ret = "";
        try {
            FileInputStream inputStream = new FileInputStream(new File(filepath));
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ((receiveString = bufferedReader.readLine()) != null) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                ret = stringBuilder.toString();

            }
        } catch (FileNotFoundException e) {
            ////Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            ////Log.e("login activity", "Can not read file: " + e.toString());
        }
        ////System.out.println("fileeeeeee:" + ret);
        return ret;
    }


    public static void AlbumCRUD_Shared(Context context, String albumid, String Count, Boolean Add) {
        String AlbumIdString;
        String AlbumCountString;

        SharedPreferences settings = context.getSharedPreferences("UploadServiceSession",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();

        AlbumIdString = settings.getString("AlbumIdString", "");
        AlbumCountString = settings.getString("AlbumCountString", "");

        if (Add) {
            if (RealmString.convertStringToArrayList(AlbumIdString).size() == 0) {
                editor.putString("AlbumIdString", albumid);
                editor.putString("AlbumCountString", Count);
                editor.apply();
            } else {
                editor.putString("AlbumIdString", AlbumIdString + "," + albumid);
                editor.putString("AlbumCountString", AlbumCountString + "," + Count);
                editor.apply();
            }
        } else {
            if (Arrays.asList(AlbumIdString.split(",")).size() > 0) {
                ArrayList<String> array1 = RealmString.convertStringToArrayList(AlbumIdString);
                ArrayList<String> array2 = RealmString.convertStringToArrayList(AlbumCountString);
                array1.remove(array1.indexOf(albumid));
                array2.remove(array2.indexOf(Count));
                editor.putString("AlbumIdString", RealmString.convertArrayListToString(array1));
                editor.putString("AlbumCountString", RealmString.convertArrayListToString(array2));
                editor.apply();
            }

        }

    }


    public static String AddComma(String price) {
        double amount = Double.parseDouble(price);
        DecimalFormat formatter = new DecimalFormat("#,###.00");

        ////System.out.println(formatter.format(amount));
        return formatter.format(amount);
    }


    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }


    public static JSONObject displaymetrics(Context context) {
        int height, width;
        JSONObject metrics = null;
        try {
            DisplayMetrics displaymetrics = new DisplayMetrics();
            ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            height = displaymetrics.heightPixels;
            width = displaymetrics.widthPixels;


            metrics = new JSONObject();
            metrics.put("height", height);
            metrics.put("width", width);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return metrics;
    }


    public static void hideKeyboard(Context context) {
        View view = ((Activity) context).getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


//    public static void notifClick(Context context, JSONObject object) {
//        ApiService.getInstance(ProductPage.this, 1).postData(ProductPage.this, EnvConstants.APP_BASE_URL + "/zapcart/addtocart/", buyObject, SCREEN_NAME, "buy");
//
//    }


    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {
        }
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile())
            return dir.delete();
        else {
            return false;
        }
    }


    public static void trimCache(Context context) {
        try {
            File dir = context.getCacheDir();
            if (dir != null && dir.isDirectory()) {
                boolean cache = deleteDirct(dir);
                Log.i("TAG", "**************** File /data/data/APP_PACKAGE/" +
                        " DELETED *******************");
                ////Log.e("After pressing Exit ", "cache memory clear result is::  " +
//                        cache);
            }
        } catch (Exception e) {
// TODO: handle exception
        }
    }

    public static boolean deleteDirct(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDirct(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
        }
        return true;
    }
}
