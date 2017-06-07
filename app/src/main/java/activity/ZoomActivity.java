package activity;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import Viewholder.ExtendedViewPager;
import Viewholder.TouchImageView;
import utils.ExternalFunctions;
import utils.GetSharedValues;

/**
 * Created by haseeb on 13/7/16.
 */
public class ZoomActivity extends Activity {


    ArrayList<String> Images = new ArrayList<String>();
    ExtendedViewPager viewPager;
    FrameLayout zoom_layout;
    TextView pinchToZoom;
    public LinearLayout indicator_layout;
    public ImageButton[] imageButtons;
    int indicatorcount = 0, screenHeight = 0;
    int currentPosition = 0;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;
    public String SCREEN_NAME = "ZOOM_PRODUCT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoomlayout);
        Images = getIntent().getStringArrayListExtra("data");
        currentPosition = getIntent().getIntExtra("currentItem", 0);
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
        zoom_layout = (FrameLayout) findViewById(R.id.zoom_layout);
        zoom_layout.setForegroundGravity(Gravity.CENTER);

        pinchToZoom = (TextView) findViewById(R.id.pinchToZoom);
        indicator_layout = (LinearLayout) findViewById(R.id.indicator_layout);

        AddIndicators();

        viewPager = (ExtendedViewPager) findViewById(R.id.zoom_viewpager);
        TouchImageAdapter touchImageAdapter = new TouchImageAdapter(ZoomActivity.this, Images);
        viewPager.setAdapter(touchImageAdapter);
        viewPager.setCurrentItem(currentPosition);


        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (int i = 0; i < Images.size(); i++) {
                            imageButtons[i].clearAnimation();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                imageButtons[i].setBackground(ContextCompat.getDrawable(ZoomActivity.this, R.drawable.white_round_bg));
                            }
                        }
                        Animation logoMoveAnimation = AnimationUtils.loadAnimation(ZoomActivity.this, R.anim.indicator_anim);
                        imageButtons[position].startAnimation(logoMoveAnimation);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                            imageButtons[position].setBackground(ContextCompat.getDrawable(ZoomActivity.this, R.drawable.black_round_bg));
                        }
                    }
                });


            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    public void closeZoom(View v) {
        finish();
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

    public void AddIndicators() {
        indicatorcount = Images.size();
        imageButtons = new ImageButton[indicatorcount];
        indicator_layout.removeAllViews();
        for (int i = 0; i < Images.size(); i++) {
            imageButtons[i] = new ImageButton(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 8, 8, 8);
            imageButtons[i].setLayoutParams(params);
            if (i == currentPosition) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageButtons[i].setBackground(ContextCompat.getDrawable(ZoomActivity.this, R.drawable.black_round_bg));
                }
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    imageButtons[i].setBackground(ContextCompat.getDrawable(ZoomActivity.this, R.drawable.white_round_bg));
                }
            }
            indicator_layout.addView(imageButtons[i]);
        }
    }


    public class TouchImageAdapter extends PagerAdapter {


        private ArrayList<String> IMAGES;
        private LayoutInflater inflater;
        private Context context;
        int screenWidth = 0, screenHeight = 0;


        public TouchImageAdapter(Context context, ArrayList<String> IMAGES) {
            this.context = context;
            this.IMAGES = IMAGES;
            inflater = LayoutInflater.from(context);

            JSONObject screenSize = ExternalFunctions.displaymetrics(context);
            screenWidth = screenSize.optInt("width");
            screenHeight = screenSize.optInt("height");
        }


        @Override
        public int getCount() {
            return IMAGES.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            final TouchImageView imageView = new TouchImageView(context);
            Glide.with(context)
                    .load(EnvConstants.APP_MEDIA_URL + IMAGES.get(position))
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .override(GetSharedValues.getScreenWidth(context), (int) (GetSharedValues.getScreenWidth(context) * 1.333))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);


            container.addView(imageView, ViewPager.LayoutParams.WRAP_CONTENT, ViewPager.LayoutParams.WRAP_CONTENT);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((TouchImageView) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

    }


//    public int getImageHeight(String url) {
//        try {
//            InputStream is = (InputStream) new URL(url).getContent();
//            Drawable d = Drawable.createFromStream(is, "src name");
//            return d.getIntrinsicHeight();
//        } catch (Exception e) {
//            return 0;
//        }
//    }
//    public int getDiff() {
//        System.out.println("SIZESSSSS: "+getImageHeight(EnvConstants.APP_MEDIA_URL + Images.get(0)));
//        JSONObject screenSize = ExternalFunctions.displaymetrics(ZoomActivity.this);
//        screenHeight = screenSize.optInt("height");
//        int diff = ((screenHeight - getImageHeight(EnvConstants.APP_MEDIA_URL + Images.get(0))) / 2) - 490;
//        System.out.println("DIFF: "+diff+"==="+screenHeight+"==="+getImageHeight(EnvConstants.APP_MEDIA_URL + Images.get(0)));
//        return diff;
//    }


}