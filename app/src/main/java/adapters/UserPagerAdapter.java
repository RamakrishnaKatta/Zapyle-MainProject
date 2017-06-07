package adapters;

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
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

//import activity.HomePage;
import activity.discover;
import activity.profile;
import network.ApiCommunication;
import network.ApiService;

/**
 * Created by haseeb on 8/9/16.
 */
public class UserPagerAdapter extends PagerAdapter implements ApiCommunication{

    Context context;
    JSONArray data = new JSONArray();
    LayoutInflater inflater;


    public UserPagerAdapter(Context context, JSONArray data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);

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
        final View layout;
        layout = inflater.inflate(R.layout.home_user_collection_inside, null, false);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams( discover.screenWidth - 100,  discover.screenWidth / 3);
        lp.setMargins(23, 23, 23, 23);
        layout.setLayoutParams(lp);
        layout.setBackgroundResource(R.drawable.home_img_border);
        LinearLayout user_collection_linear_layout = (LinearLayout) layout.findViewById(R.id.user_collection_linear_layout);
        user_collection_linear_layout.getLayoutParams().height = ( discover.screenWidth / 3) - 20;
        ImageView user_collection_image = (ImageView) layout.findViewById(R.id.user_collection_image);
        TextView user_collection_fullname = (TextView) layout.findViewById(R.id.user_collection_fullname);
        TextView user_collection_username = (TextView) layout.findViewById(R.id.user_collection_username);
        TextView user_collection_outfits = (TextView) layout.findViewById(R.id.user_collection_outfits);
        TextView user_collection_admirers = (TextView) layout.findViewById(R.id.user_collection_admirers);
        final TextView user_collection_admire_button = (TextView) layout.findViewById(R.id.user_collection_admire_button);
//                        user_collection_admire_button.setBackground(getResources().getDrawable(R.drawable.discover_admire_bg));

        //System.out.println("ADMIRE: user :" + discoverData.getJSONArray("user").getJSONObject(j).getBoolean("admiring"));
        try {
            if (data.getJSONObject(position).getBoolean("admiring")) {
                user_collection_admire_button.setText("Admiring");
                user_collection_admire_button.setTag("Admiring");
            } else {
                user_collection_admire_button.setText("Admire");
                user_collection_admire_button.setTag("Admire");
            }


            user_collection_admire_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //System.out.println("ADMIRE: user : clicked");
                    if (user_collection_admire_button.getText().toString().contains("Admiring")) {
                        user_collection_admire_button.setText("Admire");
                        JSONObject admireObject = null;
                        try {
                            admireObject = new JSONObject();
                            admireObject.put("action", "unadmire");
                            admireObject.put("user", data.getJSONObject(position).getInt("id"));
                        } catch (Exception e) {

                        }
                        ApiService.getInstance(context, 1).postData(UserPagerAdapter.this, EnvConstants.APP_BASE_URL + "/user/admire/", admireObject, "discover", "userunadmire");
//                                    user_collection_admire_button.setEnabled(false);


                    } else {
                        //System.out.println("ADMIRE: user :Admire click");
                        user_collection_admire_button.setText("Admiring");
                        JSONObject admireObject = null;
                        try {
                            admireObject = new JSONObject();
                            admireObject.put("action", "admire");
                            admireObject.put("user", data.getJSONObject(position).getInt("id"));
                        } catch (Exception e) {

                        }
                        ApiService.getInstance(context, 1).postData(UserPagerAdapter.this, EnvConstants.APP_BASE_URL + "/user/admire/", admireObject, "discover", "useradmire");
//                                    user_collection_admire_button.setEnabled(false);
                    }
                }
            });

            try {
                Glide.with(context)
                        .load(data.getJSONObject(position).getString("profile_image"))
                        .fitCenter()
                        .placeholder(R.drawable.playholderscreen)
                        .crossFade()
                        .override(( discover.screenWidth / 3) - 20, ( discover.screenWidth / 3) - 20)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(user_collection_image);
            } catch (JSONException e) {
                e.printStackTrace();
            }


            user_collection_fullname.setText(data.getJSONObject(position).getString("full_name"));
            user_collection_username.setText(String.valueOf(data.getJSONObject(position).getString("zap_username")));
            user_collection_outfits.setText(String.valueOf(data.getJSONObject(position).getInt("outfits")) + " OUTFITS");
            user_collection_admirers.setText(String.valueOf(data.getJSONObject(position).getInt("admirers")) + " ADMIRERS");
            user_collection_admirers.setTextColor(Color.parseColor("#4A4A4A"));
            user_collection_fullname.setTextColor(Color.parseColor("#4A4A4A"));
            user_collection_username.setTextColor(Color.parseColor("#4A4A4A"));
            user_collection_outfits.setTextColor(Color.parseColor("#4A4A4A"));
        }
        catch (Exception e){

        }
        user_collection_fullname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent pro_page = new Intent(context, profile.class);
                    pro_page.putExtra("activity", "discover");
                    pro_page.putExtra("user_id", data.getJSONObject(position).getInt("id"));
                    context.startActivity(pro_page);
             //((Activity) context).finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        user_collection_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent pro_page = new Intent(context, profile.class);
                    pro_page.putExtra("activity", "discover");
                    pro_page.putExtra("user_id", data.getJSONObject(position).getInt("id"));
                    context.startActivity(pro_page);
                   // ((Activity) context).finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

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

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }
}
