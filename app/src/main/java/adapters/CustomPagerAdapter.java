package adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import activity.HomePage;
import activity.discover;
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
        Glide.clear(holder.custom_collection_image);
    }
    public static class holder{
        static ImageView  custom_collection_image;
    }

    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        final View layout;
        layout = inflater.inflate(R.layout.home_custom_collection_inside, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(discover.screenWidth - 150, discover.screenWidth - 350);
        lp.setMargins(10, 10, 10, 10);
        layout.setLayoutParams(lp);
        try {
            System.out.println("cccc"+data.getJSONObject(position).getString("image"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
//                            view_mid_custom.setBackgroundResource(R.drawable.home_img_border);
        holder.custom_collection_image = (ImageView) layout.findViewById(R.id.custom_collection_image);
//        TextView custom_collection_text = (TextView) layout.findViewById(R.id.custom_collection_text);
        try {
            Glide.with(context)
                    .load(data.getJSONObject(position).getString("image"))
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .override(discover.screenWidth - 150, discover.screenWidth - 350)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.custom_collection_image);


            holder.custom_collection_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        JSONObject Action = data.getJSONObject(position).getJSONObject("action");
                        System.out.println("zzzq"+ data.getJSONObject(position).getString("title").toUpperCase());

                            SharedPreferences FeedSession = context.getSharedPreferences("FeedSession",
                                    Context.MODE_PRIVATE);
                            SharedPreferences.Editor FeedSessioneditor = FeedSession.edit();
                            FeedSessioneditor.putString("FilteredHeading", data.getJSONObject(position).getString("title").toUpperCase());
                            FeedSessioneditor.apply();


                        System.out.println("DISCOVERDATA : " + Action.getString("target") + "____" + Action.getString("android_activity"));
                        String fwd_url = Action.getString("target");
                        String fwd_action_type = Action.getString("action_type");

                       Intent intent= ExternalFunctions.routeActivity(context,fwd_action_type,fwd_url);
                       context.startActivity(intent);



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
//            custom_collection_text.setText(data.getJSONObject(position).getString("title").toUpperCase());

        } catch (JSONException e) {
            System.out.println("cccceroor"+e.getMessage());
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
