package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import activity.BuyPage;
//import activity.HomePage;
import activity.HomePageNew;
import activity.ProfilePage;
import network.ApiService;
import utils.ExternalFunctions;

/**
 * Created by haseeb on 8/9/16.
 */
public class CustomPagerAdapter extends PagerAdapter {

    Context context;
    JSONArray data = new JSONArray();
    LayoutInflater inflater;


    public CustomPagerAdapter(Context context, JSONArray data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }


    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        final View layout;
        layout = inflater.inflate(R.layout.home_custom_collection_inside, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(HomePageNew.screenWidth - 150, HomePageNew.screenWidth - 350);
        lp.setMargins(23, 23, 23, 23);
        layout.setLayoutParams(lp);
//                            view_mid_custom.setBackgroundResource(R.drawable.home_img_border);
        ImageView custom_collection_image = (ImageView) layout.findViewById(R.id.custom_collection_image);
//        TextView custom_collection_text = (TextView) layout.findViewById(R.id.custom_collection_text);
        try {
            Glide.with(context)
                    .load(data.getJSONObject(position).getString("image"))
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .override(HomePageNew.screenWidth - 150, HomePageNew.screenWidth - 350)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(custom_collection_image);


            custom_collection_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject Action = data.getJSONObject(position).getJSONObject("action");
                        ExternalFunctions.FilteredString = data.getJSONObject(position).getString("title").toUpperCase();
                        String activity;
                        System.out.println("DISCOVERDATA : " + Action.getString("target") + "____" + Action.getString("android_activity"));
                        if (Action.getString("android_activity").contains("FeedPage") || Action.getString("android_activity").equals("BuySecondPage")) {
                            activity = "activity.BuySecondPage";
                            ExternalFunctions.ActivityParam = "Broadcasted";
                            ExternalFunctions.BroadCastedActivity = activity;
                            ExternalFunctions.BroadCastedUrl = Action.getString("target");
                        } else {
                            activity = "activity." + Action.getString("android_activity");
                        }
                        String fwd_url = Action.getString("target");
                        if (!fwd_url.contains("zap_market") && !fwd_url.contains("zap_curated") && !fwd_url.contains("designer")) {
                            activity = "activity.FilteredFeed";
                            ExternalFunctions.ActivityParam = "base";
                            ExternalFunctions.BroadCastedActivity = "";
                            ExternalFunctions.BroadCastedUrl = "";
                        }
                        String fwd_action_type = Action.getString("action_type");


                        Intent forward = null;
                        try {
                            forward = new Intent(context, Class.forName(activity));
                            forward.putExtra("ForwardUrl", fwd_url);
                            forward.putExtra("ActionType", fwd_action_type);
                            forward.putExtra("activity", "HomePageNew");
                            context.startActivity(forward);
                            Runtime.getRuntime().gc();
                            //System.exit(0);
                          //  ((Activity) context).finish();

                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                            forward = new Intent(context, BuyPage.class);
                            context.startActivity(forward);
                            Runtime.getRuntime().gc();
                            //System.exit(0);
                            ((Activity) context).finish();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
//            custom_collection_text.setText(data.getJSONObject(position).getString("title").toUpperCase());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        view.addView(layout, 0);
        return layout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        System.out.println("IMAGES DATA check4: ");
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
