package fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

//import activity.HomePage;
import activity.discover;
import utils.CustomMessage;
import utils.ExternalFunctions;

/**
 * Created by zapyle on 21/10/16.
 */

public class bannerfragement extends Fragment {
    ImageView bannerImage;
    JSONObject contentData = null;
    JSONObject discoverData = null;

    JSONObject objectData;


    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("banner destroyed");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        System.out.println("banner destroyed1");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //getActivity()= getActivity().getApplicationgetActivity()();
        // TODO Auto-generated method stub
        Bundle data =  getArguments();
        try {
            objectData= new JSONObject(data.getString("object"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View view=inflater.inflate(R.layout.home_banner, container, false);
       //// YoYo.with(Techniques.Landing).duration(600).playOn(view);
        bannerImage = (ImageView) view.findViewById(R.id.banner_image);
        try {

            bannerImage.layout(0, 10, 0, 10);
          //  JSONObject objectData = discover.dataList.get(pos);
            try {
                contentData = objectData.getJSONObject("content_data");
                discoverData = contentData.getJSONObject("discover_data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("bannerqqq" + discoverData.getString("image"));

            Glide.with(getActivity())
                    .load(discoverData.getString("image"))
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .dontAnimate()
                    .placeholder(R.drawable.playholderscreen)
                    .override(discover.screenWidth, discover.screenHieght)
                    .into(bannerImage);

            bannerImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckConnectivity.isNetworkAvailable(getActivity())) {
                        if (discoverData.has("action")) {
                            if (discoverData.has("action")) {
                                try {
                                    JSONObject Action = discoverData.getJSONObject("action");
//                                ExternalFunctions.FilteredString = discoverData.getJSONArray("collection").getJSONObject(finalJ).getString("title").toUpperCase();
                                    String activity;
                                    System.out.println("DISCOVERDATA : " + Action.getString("android_target") + "____" + Action.getString("android_activity"));
                                    String fwd_url = Action.getString("target");
                                    String fwd_action_type = Action.getString("action_type");

                                    Intent intent=  ExternalFunctions.routeActivity(getActivity(),fwd_action_type,fwd_url);
                                    getActivity().startActivity(intent);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    } else {
                        CustomMessage.getInstance().CustomMessage(getActivity(), "Internet is not available!");
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return view;
    }
}
