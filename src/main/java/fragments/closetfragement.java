package fragments;



import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import activity.HomePageNew;
import activity.ProfilePage;
import adapters.ClosetPagerAdaptr;

import network.ApiCommunication;
import network.ApiService;


/**
 * Created by zapyle on 22/10/16.
 */

public class closetfragement extends Fragment implements ApiCommunication {
    TextView title,description,admire;
    ViewPager viewPager;

    JSONObject contentData = null;
    JSONObject discoverData = null;
   
    JSONObject objectData;
  

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle data =  getArguments();
        try {
            objectData= new JSONObject(data.getString("object"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        // TODO Auto-generated method stub
        View view=inflater.inflate(R.layout.home_closet, container, false);

        title = (TextView) view.findViewById(R.id.closet_title);
        description = (TextView) view.findViewById(R.id.closet_description);
        admire = (TextView) view.findViewById(R.id.closet_admire);
        viewPager = (ViewPager) view.findViewById(R.id.closet_viewpager);
        try {
            contentData = objectData.getJSONObject("content_data");
            discoverData = contentData.getJSONObject("discover_data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            title.setText(discoverData.getString("title"));
            title.setTextColor(Color.parseColor("#4A4A4A"));
            title.setText(discoverData.getString("description"));
            description.setTextColor(Color.parseColor("#4A4A4A"));
           viewPager.setBackgroundColor(Color.WHITE);
            LinearLayout linearLayout = new LinearLayout(getActivity());
            
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            //System.out.println("ADMIRE: closet :" + discoverData.getJSONObject("user").getBoolean("admiring"));
            if (discoverData.getJSONObject("user").getBoolean("admiring")) {
                admire.setText("Admiring");
                admire.setTag("Admiring");
            } else {
                admire.setText("Admire");
                admire.setTag("Admire");
            }
            admire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //System.out.println("ADMIRE: closet clicked");
                    if (admire.getTag().toString().contains("Admiring")) {
                        //System.out.println("ADMIRE :Admiring closet click");
                        admire.setText("Admire");
                        JSONObject admireObject = null;
                        try {
                            admireObject = new JSONObject();
                            admireObject.put("action", "unadmire");
                            admireObject.put("user", discoverData.getJSONObject("user").getInt("id"));
                        } catch (Exception e) {

                        }
                        ApiService.getInstance(getActivity(), 1).postData((ApiCommunication) getActivity(), EnvConstants.APP_BASE_URL + "/user/admire/", admireObject, "HomePageNew", "unadmire");
                        admire.setEnabled(false);

                    } else {
                        //System.out.println("ADMIRE :Admire closet click");
                        admire.setText("Admiring");
                        JSONObject admireObject = null;
                        try {
                            admireObject = new JSONObject();
                            admireObject.put("action", "admire");
                            admireObject.put("user", discoverData.getJSONObject("user").getInt("id"));
                        } catch (Exception e) {

                        }
                        ApiService.getInstance(getActivity(), 1).postData((ApiCommunication) getActivity(), EnvConstants.APP_BASE_URL + "/user/admire/", admireObject, "HomePageNew", "admire");
                        //System.out.println("after admire:" + admireObject);
                        admire.setEnabled(false);
                    }
                }
            });

//                    ImageView img = new ImageView(this);
            LayoutInflater inflater_inside_closet = LayoutInflater.from(getActivity());
            final View view_inside_closet;
            view_inside_closet = inflater_inside_closet.inflate(R.layout.home_closet_collection_inside, null, false);
            LinearLayout.LayoutParams closet_image_params = new LinearLayout.LayoutParams(HomePageNew.screenWidth - 150, HomePageNew.screenWidth - 150);
            closet_image_params.setMargins(23, 23, 23, 23);
            view_inside_closet.setLayoutParams(closet_image_params);
            view_inside_closet.setPadding(1, 1, 1, 1);
            view_inside_closet.setBackgroundResource(R.drawable.home_img_border);
            ImageView inside_closet_image = (ImageView) view_inside_closet.findViewById(R.id.inside_closet_image);

            Glide.with(getActivity())
                    .load(discoverData.getString("image"))
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .override(HomePageNew.screenWidth - 150, (int) ((HomePageNew.screenWidth - 150) * 1.333))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(inside_closet_image);


            linearLayout.addView(view_inside_closet);
            view_inside_closet.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Intent pro_page = new Intent(getActivity(), ProfilePage.class);
                        pro_page.putExtra("activity", "HomePageNew");
                        pro_page.putExtra("user_id", discoverData.getJSONObject("user").getInt("id"));
                        getActivity().startActivity(pro_page);
                        getActivity().finish();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
//            ---==============================

            final JSONArray closetPagerData = discoverData.getJSONArray("product");
            ClosetPagerAdaptr closetPagerAdaptr = new ClosetPagerAdaptr(getActivity(), closetPagerData);
           viewPager.setAdapter(closetPagerAdaptr);
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
                    } else if (position > 0 && position < closetPagerData.length() - 1) {
                       viewPager.setPadding((int) (HomePageNew.screenWidth * 0.075), 0, (int) (HomePageNew.screenWidth * 0.075), 0);
                    } else if (position == closetPagerData.length() - 1) {
                       viewPager.setPadding((int) (HomePageNew.screenWidth * 0.15), 0, 0, 0);
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });

            linearLayout.setDrawingCacheEnabled(false);
           // viewCallback.getViewHandle(linearLayout);

        } catch (Exception e) {

        }

        return view;
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }
}
