package activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import utils.ExternalFunctions;

//import com.uxcam.UXCam;

/**
 * Created by haseeb on 13/2/16.
 */
public class SizeGuide extends Activity {

    Integer album_id = 0;
    String strActivity;
    ImageView guide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examplesizeguide);
        //Appsee.start("4fb55809ab45411c909521bda720f8e6");
        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        album_id = getIntent().getIntExtra("album_id", 0);
        strActivity = getIntent().getStringExtra("activity");
        String type = getIntent().getStringExtra("TYPE");

        JSONObject measure = ExternalFunctions.displaymetrics(this);
        int screenWidth = 0;
        int screenHeight = 0;
        try {
            screenWidth = measure.getInt("width");
            screenHeight = measure.getInt("height");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        guide = (ImageView) findViewById(R.id.imageguide);
        if (type.contains("C")) {
            Glide.with(SizeGuide.this)
                    .load(EnvConstants.APP_MEDIA_URL + "/zapstatic/frontend/images/size_guide.jpg")
                    .fitCenter()
                    .override(screenWidth,screenHeight)
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(guide);

        } else {
            Glide.with(SizeGuide.this)
                    .load(EnvConstants.APP_MEDIA_URL + "/zapstatic/frontend/images/size_guide_shoe.jpg")
                    .fitCenter()
                    .override(screenWidth,screenHeight)
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(guide);


        }
        guide.getLayoutParams().height = screenHeight;
        guide.getLayoutParams().width = screenWidth;
        guide.setScaleType(ImageView.ScaleType.FIT_XY);


    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.activity_open_scale, R.anim.activity_close_translate);
    }

    @Override
    public void onBackPressed() {
        if (strActivity.equals("product")) {
            Intent mydialog = new Intent(SizeGuide.this, product.class);
            mydialog.putExtra("activity", "1");
            mydialog.putExtra("album_id", album_id);
            mydialog.putExtra("pta", false);
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();
        }
        if (strActivity.equals("filteractivity")) {
            finish();
        } else {
            Intent mydialog = new Intent(SizeGuide.this, product.class);
            mydialog.putExtra("activity", "1");
            mydialog.putExtra("album_id", album_id);
            mydialog.putExtra("pta", false);
            mydialog.putExtra("sale", true);
            mydialog.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(mydialog);
            finish();
        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (guide != null) {
            if (guide.getDrawable() != null)
                guide.getDrawable().setCallback(null);
            Glide.clear(guide);
        }

    }
}
