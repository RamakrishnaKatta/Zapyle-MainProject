package activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;

import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.naver.android.helloyako.imagecrop.view.ImageCropView;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.R;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import utils.ExternalFunctions;
//import com.//Appsee.////Appsee;


public class CropActivity extends Activity {
    public static final String TAG = "CropActivity";
    public String filepath;

    private ImageCropView imageCropView;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;
    String SCREEN_NAME="UPLOAD_CROP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        //////Thread.currentThread().setDefaultUncaughtExceptionHandler(new SplashScreen.MyUncaughtExceptionHandler());
        imageCropView = (ImageCropView) findViewById(R.id.product_image);
        JSONObject displayParams = ExternalFunctions.displaymetrics(this);
        int screenwidth = displayParams.optInt("width");

        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
//
//        imageCropView.getLayoutParams().width = screenwidth;
//        imageCropView.getLayoutParams().height = (int) (screenwidth * (1.333));

        ////Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        Intent i = getIntent();
        Uri uri = i.getData();
        imageCropView.setImageFilePath(uri.toString());
        imageCropView.setAspectRatio(3, 4);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        findViewById(R.id.crop_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!imageCropView.isChangingScale()) {
                    Bitmap b = imageCropView.getCroppedImage();

                    getResizedBitmap(b, b.getWidth(), b.getHeight(), b.getWidth());

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endtime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        HashMap<String, Object> page_change = new HashMap<String, Object>();
        page_change.put("new_page", SCREEN_NAME);
        page_change.put("old_page", ExternalFunctions.prevActivity);
        page_change.put("page_view_starttime", stime);
        page_change.put("page_view_endtime", endtime);
        cleverTap.event.push("page_change", page_change);
        ExternalFunctions.prevActivity = SCREEN_NAME;
    }

    public void getResizedBitmap(Bitmap image, int width, int height, int maxSize) {

        float bitmapRatio = (float) width / (float) height;
        if (bitmapRatio > 0) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        Bitmap b = Bitmap.createScaledBitmap(image, width, height, true);
        bitmapConvertToFile(b);
    }

    private boolean isPossibleCrop(int widthRatio, int heightRatio) {
        ////////System.out.println(imageCropView.getViewBitmap().getWidth()+ "___"+imageCropView.getViewBitmap().getHeight());
        int bitmapWidth = imageCropView.getViewBitmap().getWidth();
        int bitmapHeight = imageCropView.getViewBitmap().getHeight();
        return !(bitmapWidth < widthRatio && bitmapHeight < heightRatio);
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    public File bitmapConvertToFile(Bitmap bitmap) {
        FileOutputStream fileOutputStream = null;
        File bitmapFile = null;
        try {
            File file = new File(Environment.getExternalStoragePublicDirectory("image_crop_sample"), "");
            if (!file.exists()) {
                file.mkdir();
            }

            bitmapFile = new File(file, "IMG_" + (new SimpleDateFormat("yyyyMMddHHmmss")).format(Calendar.getInstance().getTime()) + ".jpg");
            fileOutputStream = new FileOutputStream(bitmapFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, fileOutputStream);
            filepath = bitmapFile.getAbsolutePath();
            MediaScannerConnection.scanFile(this, new String[]{bitmapFile.getAbsolutePath()}, null, new MediaScannerConnection.MediaScannerConnectionClient() {
                @Override
                public void onMediaScannerConnected() {

                }

                @Override
                public void onScanCompleted(String path, Uri uri) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            Intent newintent = new Intent(getApplicationContext(), upload.class);
                            newintent.putExtra("imgUrl", filepath);
                            setResult(RESULT_OK, newintent);
                            finish();
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.flush();
                    fileOutputStream.close();
                } catch (Exception e) {
                }
            }
        }

        return bitmapFile;
    }

}
