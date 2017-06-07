

package activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
//import com.uxcam.UXCam;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import utils.FontUtils;
import utils.GetSharedValues;
//import com.//Appsee.//Appsee;

/**
 * Created by haseeb on 8/10/15.
 */
public class ForceUpdate extends Activity {
    ViewPager viewPager;
    SliderAdaptor sliderAdaptor;
    int indicatorcount = 0, currentPage = 0;
    LinearLayout lvIndicator;
    ImageButton[] imageButtons;
    JSONArray data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.force_update);
        View view = findViewById(R.id.forceUpdatelayout);
        FontUtils.setCustomFont(view, getAssets());
        String dataString = getIntent().getStringExtra("Features");
        try {
            data = new JSONArray(dataString);
        } catch (JSONException e) {
            e.printStackTrace();
        }


        lvIndicator = (LinearLayout) findViewById(R.id.update_indicator_layout);

        //UXCam.startWithKey("1dfb25141864376");
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        AddIndicators();
        viewPager = (ViewPager) findViewById(R.id.update_pager);
        sliderAdaptor = new SliderAdaptor(ForceUpdate.this, data);
        viewPager.setAdapter(sliderAdaptor);
        viewPager.setCurrentItem(0);

        AutomaticScrolling();
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(final int position) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        currentPage = position;
                        for (int i = 0; i < data.length(); i++) {
                            imageButtons[i].clearAnimation();
                            imageButtons[i].setBackground(getResources().getDrawable(R.drawable.white_round_bg));
                        }
                        Animation logoMoveAnimation = AnimationUtils.loadAnimation(ForceUpdate.this, R.anim.indicator_anim);
                        imageButtons[position].startAnimation(logoMoveAnimation);
                        imageButtons[position].setBackground(getResources().getDrawable(R.drawable.black_round_bg));
                    }
                });
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        TextView update = (TextView) findViewById(R.id.AppUpdate);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearApplicationData();
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
            }
        });
    }

    public void AddIndicators() {
        indicatorcount = data.length();
        imageButtons = new ImageButton[indicatorcount];
        lvIndicator.removeAllViews();
        for (int i = 0; i < data.length(); i++) {
            imageButtons[i] = new ImageButton(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(20, 20);
            params.setMargins(8, 8, 8, 8);
            imageButtons[i].setLayoutParams(params);
            if (i == 0) {
                imageButtons[i].setBackground(getResources().getDrawable(R.drawable.black_round_bg));
            } else {
                imageButtons[i].setBackground(getResources().getDrawable(R.drawable.white_round_bg));
            }
            lvIndicator.addView(imageButtons[i]);
        }
    }

    public void AutomaticScrolling() {
        final Handler handler = new Handler();

        final Runnable update = new Runnable() {
            public void run() {
                if (currentPage == indicatorcount) {
                    currentPage = 0;
                }
                viewPager.setCurrentItem(currentPage++, true);
            }
        };


        new Timer().schedule(new TimerTask() {

            @Override
            public void run() {
                handler.post(update);
            }
        }, 100, 3000);
    }


    public void clearApplicationData() {
        File cache = getCacheDir();
        File appDir = new File(cache.getParent());
        if (appDir.exists()) {
            String[] children = appDir.list();
            for (String s : children) {
                if (!s.equals("lib")) {
                    deleteDir(new File(appDir, s));

                }
            }
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
        }

        return dir.delete();
    }

    public class SliderAdaptor extends PagerAdapter {


        private JSONArray data;
        private LayoutInflater inflater;
        private Context context;


        public SliderAdaptor(Context context, JSONArray data) {
            try {
                this.context = context;
                this.data = data;
                inflater = LayoutInflater.from(context);
            } catch (IndexOutOfBoundsException e) {

            }

        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((LinearLayout) object);
        }

        @Override
        public int getCount() {
            return data.length();
        }

        @Override
        public Object instantiateItem(ViewGroup view, final int position) {
            View imageLayout = inflater.inflate(R.layout.update_pager_item, view, false);
            TextView updateText = (TextView) imageLayout.findViewById(R.id.updateText);
            TextView header = (TextView) imageLayout.findViewById(R.id.headerText);
            header.setText(data.optJSONObject(position).optString("title").toUpperCase());
            updateText.setText(data.optJSONObject(position).optString("description"));
            view.addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

    }


}







