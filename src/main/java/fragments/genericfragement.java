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

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import activity.BuyPage;
//import activity.HomePage;
import activity.HomePageNew;
import adapters.UserPagerAdapter;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by zapyle on 22/10/16.
 */

public class genericfragement extends Fragment {
    TextView title,description,buttontext;
    ImageView image;

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
        View view=inflater.inflate(R.layout.home_generic, container, false);

        title = (TextView) view.findViewById(R.id.generic_title);
        description = (TextView) view.findViewById(R.id.generic_description);
        buttontext = (TextView) view.findViewById(R.id.generic_buttontext);
        image = (ImageView) view.findViewById(R.id.generic_image);
        title.setTextColor(Color.parseColor("#4A4A4A"));
        try {
            contentData = objectData.getJSONObject("content_data");
            discoverData = contentData.getJSONObject("discover_data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            buttontext.setVisibility(View.GONE);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(HomePageNew.screenWidth - 40, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(20, 20, 20, 20);
            image.setLayoutParams(lp);
            image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            Glide.with(getActivity())
                    .load(discoverData.getString("image"))
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(image);

            title.setText(discoverData.getString("title"));
            title.setTextColor(Color.parseColor("#4A4A4A"));
            description.setText(discoverData.getString("description"));
            description.setTextColor(Color.parseColor("#4A4A4A"));
            if (discoverData.has("cta")) {
                buttontext.setVisibility(View.VISIBLE);
                buttontext.setText(discoverData.getJSONObject("cta").getString("text"));
                buttontext.setTextColor(Color.parseColor("#" + discoverData.getJSONObject("cta").getString("text_color")));
                buttontext.setBackgroundColor(Color.parseColor("#" + discoverData.getJSONObject("cta").getString("background_color")));
                JSONObject Action = discoverData.getJSONObject("action");
                final String fwd_url = Action.getString("target");
                final String fwd_action_type = Action.getString("action_type");
                String activity;
                if (Action.getString("android_activity").contains("FeedPage") || Action.getString("android_activity").equals("BuySecondPage")) {
                    activity = "activity.BuySecondPage";
                    ExternalFunctions.ActivityParam = "Broadcasted";
                    ExternalFunctions.BroadCastedActivity = activity;
                    ExternalFunctions.BroadCastedUrl = Action.getString("target");
                    if (!fwd_url.contains("zap_market") && !fwd_url.contains("zap_curated") && !fwd_url.contains("designer")) {
                        activity = "activity.FilteredFeed";
                        ExternalFunctions.ActivityParam = "base";
                        ExternalFunctions.BroadCastedActivity = "";
                        ExternalFunctions.BroadCastedUrl = "";
                    }
                } else {
                    activity = "activity." + Action.getString("android_activity");
                }

                final String finalActivity = activity;
                buttontext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (GetSharedValues.LoginStatus(getActivity())) {

                            Intent forward = null;
                            try {
                                forward = new Intent(getActivity(), Class.forName(finalActivity));
                                forward.putExtra("ForwardUrl", fwd_url);
                                forward.putExtra("ActionType", fwd_action_type);
                                forward.putExtra("activity", "HomePageNew");
                                getActivity().startActivity(forward);
                                Runtime.getRuntime().gc();
                                getActivity().finish();

                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                                forward = new Intent(getActivity(), BuyPage.class);
                                getActivity().startActivity(forward);
                                Runtime.getRuntime().gc();
                                getActivity().finish();

                            }
                        } else {
                            Alerts.loginAlert(getActivity());
                        }
                    }
                });

                final String finalActivity1 = activity;
                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(finalActivity.toLowerCase().contains("earncash")){
                            if (GetSharedValues.LoginStatus(getActivity())) {

                                Intent forward = null;
                                try {
                                    forward = new Intent(getActivity(), Class.forName(finalActivity));
                                    forward.putExtra("ForwardUrl", fwd_url);
                                    forward.putExtra("ActionType", fwd_action_type);
                                    forward.putExtra("activity", "HomePageNew");
                                    getActivity().startActivity(forward);
                                    Runtime.getRuntime().gc();
                                    getActivity().finish();

                                } catch (ClassNotFoundException e) {
                                    e.printStackTrace();
                                    forward = new Intent(getActivity(), BuyPage.class);
                                    getActivity().startActivity(forward);
                                    Runtime.getRuntime().gc();
                                    getActivity().finish();

                                }
                            } else {
                                Alerts.loginAlert(getActivity());
                            }

                        }else {

                            Intent forward = null;
                            try {
                                forward = new Intent(getActivity(), Class.forName(finalActivity1));
                                forward.putExtra("ForwardUrl", fwd_url);
                                forward.putExtra("ActionType", fwd_action_type);
                                forward.putExtra("activity", "HomePageNew");
                                getActivity().startActivity(forward);
                                Runtime.getRuntime().gc();
                                //System.exit(0);
                                getActivity().finish();

                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                                forward = new Intent(getActivity(), BuyPage.class);
                                getActivity().startActivity(forward);
                                Runtime.getRuntime().gc();
                                //System.exit(0);
                                getActivity().finish();
                            }
                        }
                    }
                });

            }

        } catch (Exception e) {

        }
        FontUtils.setCustomFont(view, getActivity().getAssets());
        return view;
    }
}
