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

import activity.BuyPage;
//import activity.HomePage;
import activity.HomePageNew;
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

//    public bannerfragement(getActivity() ctx, JSONObject objectData){
//        this.getActivity()=ctx;
//        this.objectData=objectData;
//    }


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
        bannerImage = (ImageView) view.findViewById(R.id.banner_image);
        try {

            bannerImage.layout(0, 0, 0, 0);
          //  JSONObject objectData = HomePageNew.dataList.get(pos);

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
                    .override(HomePageNew.screenWidth, HomePageNew.screenHieght)
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
                                    String fwd_url = Action.getString("target");
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


                                    String fwd_action_type = Action.getString("action_type");
                                    try {
                                        Intent forward = new Intent(getActivity(), Class.forName(activity));
                                        forward.putExtra("ForwardUrl", fwd_url);
                                        forward.putExtra("ActionType", fwd_action_type);
                                        forward.putExtra("activity", "HomePageNew");
                                        forward.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        getActivity().startActivity(forward);
                                        Runtime.getRuntime().gc();
                                        //System.exit(0);
                                         getActivity().finish();

                                    } catch (ClassNotFoundException e) {
                                        e.printStackTrace();
                                        Intent forward = new Intent(getActivity(), BuyPage.class);
                                        getActivity().startActivity(forward);
                                        Runtime.getRuntime().gc();
                                        getActivity().finish();
                                    }

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
