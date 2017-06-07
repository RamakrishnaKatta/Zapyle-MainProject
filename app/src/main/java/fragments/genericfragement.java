package fragments;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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

//import activity.HomePage;
import activity.discover;
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
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(discover.screenWidth - 40, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                //buttontext.setBackgroundColor(Color.parseColor("#" + discoverData.getJSONObject("cta").getString("background_color")));
                JSONObject Action = discoverData.getJSONObject("action");
                System.out.println("DISCOVERDATA : " + Action.getString("android_target") + "____" + Action.getString("android_activity"));
                final String fwd_url = Action.getString("target");
                final String fwd_action_type = Action.getString("action_type");
                                buttontext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (GetSharedValues.LoginStatus(getActivity())) {


                            Intent intent=    ExternalFunctions.routeActivity(getActivity(),fwd_action_type,fwd_url);
                            getActivity().startActivity(intent);

                        } else {
                            Alerts.loginAlert(getActivity());
                        }
                    }
                });


                image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (GetSharedValues.LoginStatus(getActivity())) {

                            Intent intent=    ExternalFunctions.routeActivity(getActivity(),fwd_action_type,fwd_url);
                            getActivity().startActivity(intent);

                        } else {
                            Alerts.loginAlert(getActivity());
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
