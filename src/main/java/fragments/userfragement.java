package fragments;


import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import activity.HomePage;
import activity.HomePageNew;
import adapters.UserPagerAdapter;
import utils.FontUtils;

/**
 * Created by zapyle on 22/10/16.
 */

public class userfragement extends Fragment {
    TextView title;
    ViewPager viewPager;

    JSONObject contentData = null;
    JSONObject discoverData = null;
    JSONObject objectData;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle bdle =  getArguments();
        try {
            objectData= new JSONObject(bdle.getString("object"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View view=inflater.inflate(R.layout.home_user_collection, container, false);
        title = (TextView) view.findViewById(R.id.user_collection_title);
        viewPager = (ViewPager) view.findViewById(R.id.user_viewpager);
        title.setTextColor(Color.parseColor("#4A4A4A"));
        try {
            contentData = objectData.getJSONObject("content_data");
            discoverData = contentData.getJSONObject("discover_data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            title.setText("Closets to Admire");
            title.setTextColor(Color.parseColor("#4A4A4A"));
            viewPager.setBackgroundColor(Color.WHITE);
            viewPager.getLayoutParams().height = HomePageNew.screenWidth / 3;
            LinearLayout user_linearLayout = new LinearLayout(getActivity());
            user_linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            final JSONArray data = discoverData.getJSONArray("user");
            UserPagerAdapter pagerAdapter = new UserPagerAdapter(getActivity(), discoverData.getJSONArray("user"));
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(0);
            viewPager.setClipToPadding(false);
            viewPager.setPadding(0, 0, (int) ( HomePageNew.screenWidth * 0.15), 0);
            viewPager.setPageMargin(10);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        viewPager.setPadding(0, 0, (int) ( HomePageNew.screenWidth * 0.15), 0);
                    } else if (position > 0 && position < data.length() - 1) {
                        viewPager.setPadding((int) ( HomePageNew.screenWidth * 0.075), 0, (int) ( HomePageNew.screenWidth * 0.075), 0);
                    } else if (position == data.length() - 1) {
                        viewPager.setPadding((int) ( HomePageNew.screenWidth * 0.15), 0, 0, 0);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

//
//            user_linearLayout.setDrawingCacheEnabled(false);
//            viewCallback.getViewHandle(user_linearLayout);


        } catch (Exception e) {

        }
        FontUtils.setCustomFont(view, getActivity().getAssets());
        return view;
    }
}
