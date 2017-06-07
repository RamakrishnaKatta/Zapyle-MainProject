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

import activity.HomePageNew;
import adapters.CustomPagerAdapter;
import utils.FontUtils;


/**
 * Created by zapyle on 22/10/16.
 */

public class customfragement extends Fragment {
    TextView title;
    ViewPager viewPager;

    JSONObject contentData = null;
    JSONObject discoverData = null;
    JSONObject objectData;
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("banner destroyed custom");
    }

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
        View view=inflater.inflate(R.layout.home_custom_collection, container, false);

        title = (TextView) view.findViewById(R.id.custom_collection_title);
        viewPager = (ViewPager) view.findViewById(R.id.custom_viewpager);
        title.setTextColor(Color.parseColor("#4A4A4A"));
        try {
            contentData = objectData.getJSONObject("content_data");
            discoverData = contentData.getJSONObject("discover_data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {

            if (!discoverData.getString("title").isEmpty()) {
                System.out.println("DISSSSS : inside" + discoverData.getString("title").length());
                title.setVisibility(View.VISIBLE);
                title.setText(discoverData.getString("title"));
                title.setTextColor(Color.parseColor("#4A4A4A"));
            } else {
                System.out.println("DISSSSS : out" + discoverData.getString("title"));
                title.setVisibility(View.GONE);
            }
            viewPager.setBackgroundColor(Color.WHITE);
            viewPager.getLayoutParams().height = HomePageNew.screenWidth - 350;

            LinearLayout custom_inearLayout = new LinearLayout(getActivity());
            custom_inearLayout.setOrientation(LinearLayout.HORIZONTAL);

            final JSONArray data = discoverData.getJSONArray("collection");

            CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(getActivity(), discoverData.getJSONArray("collection"));
            viewPager.setAdapter(pagerAdapter);
            viewPager.setCurrentItem(0);

            viewPager.setClipToPadding(false);
            viewPager.setPadding(0, 0, (int) (HomePageNew.screenWidth * 0.15), 0);
            viewPager.setPageMargin(10);

            viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                }

                @Override
                public void onPageSelected(int position) {
                    if (position == 0) {
                        viewPager.setPadding(0, 0, (int) (HomePageNew.screenWidth * 0.15), 0);
                    } else if (position > 0 && position < data.length() - 1) {
                        viewPager.setPadding((int) (HomePageNew.screenWidth * 0.075), 0, (int) (HomePageNew.screenWidth * 0.075), 0);
                    } else if (position == data.length() - 1) {
                        viewPager.setPadding((int) (HomePageNew.screenWidth * 0.15), 0, 0, 0);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

           // viewCallback.getViewHandle(custom_inearLayout);
        } catch (Exception e) {

        }
        FontUtils.setCustomFont(view, getActivity().getAssets());
        return view;
    }
}
