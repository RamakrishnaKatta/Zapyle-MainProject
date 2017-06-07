package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import models.ClosetData;
import network.AdmireTask;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by haseeb on 9/8/16.
 */
public class RecyclerViewClosetAdapter extends RecyclerView.Adapter<RecyclerViewClosetAdapter.ViewHolder> {
    private static final int TYPE_LOAD_MORE = 0;
    private static final int TYPE_ITEM = 1;
    ArrayList<ClosetData> data;
    static Context context;
    final String SCREEN_NAME = "FEED";
    static Date today;
    int lastPosition = -1;
    String param;
    CleverTapAPI cleverTap;
    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        int Holderid;

        ImageView im_profile_image;
        TextView tv_profilename, tv_admire, tv_gotocloset;
        ProgressBar customProgressBar;
        RecyclerView recyclerView;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            FontUtils.setCustomFont(itemView, context.getAssets());

            if (ViewType == TYPE_ITEM) {
                Holderid = 1;
                mView = itemView;
                today = new Date();
                im_profile_image = (ImageView) itemView.findViewById(R.id.user_image);
                tv_admire = (TextView) itemView.findViewById(R.id.admire_button);
                tv_gotocloset = (TextView) itemView.findViewById(R.id.gotocloset);
                tv_profilename = (TextView) itemView.findViewById(R.id.username);
                recyclerView = (RecyclerView) itemView.findViewById(R.id.h_recyclerview);

            } else if (ViewType == TYPE_LOAD_MORE) {
                Holderid = 0;
                customProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
                customProgressBar.setVisibility(View.GONE);
            }

        }

    }


    public RecyclerViewClosetAdapter(ArrayList<ClosetData> data, Context context, String param) { // MyAdapter Constructor with titles and icons parameter
        this.data = data;
        RecyclerViewClosetAdapter.context = context;
        this.param = param;
        try {
            cleverTap = CleverTapAPI.getInstance(context);
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
    }


    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_closet_placeholder, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        }

    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final ClosetData item = data.get(position);
        System.out.println("DATAALLLL: inside adaptor 1 out");
        ExternalFunctions.dicActivity = "MainFeed";
        if (holder.Holderid == 1) {
            System.out.println("DATAALLLL: inside adaptor 1");
            holder.tv_profilename.setText(item.getUserName());
            if (item.getAdmire()) {
                holder.tv_admire.setText("Admiring");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.tv_admire.setBackground(ContextCompat.getDrawable(context,R.drawable.brand_follow_bg));
                    holder.tv_admire.setTextColor(Color.WHITE);
                }
            } else {
                holder.tv_admire.setText("Admire");
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    holder.tv_admire.setBackground(ContextCompat.getDrawable(context,R.drawable.brand_unfollow_bg));
                    holder.tv_admire.setTextColor(Color.parseColor("#ff7477"));
                }
            }


            System.out.println("DATAALLLL: inside adaptor 1 1" + item.getUserImage());
//            ((Activity) context).runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
            try {
//                Glide.with(context)
//                        .load(item.getUserImage())
//                        .fitCenter()
//                        .placeholder(R.drawable.prof_placeholder)
//                        .error(R.drawable.prof_placeholder)
//                        .crossFade()
//                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                        .into(holder.im_profile_image);
                Glide.with(context)
                        .load(item.getUserImage())
                        .asBitmap()
                        .placeholder(R.drawable.prof_placeholder)
                        .error(R.drawable.prof_placeholder)
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(new BitmapImageViewTarget(holder.im_profile_image) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                holder.im_profile_image.setImageDrawable(circularBitmapDrawable);
                            }
                        });


            } catch (IllegalArgumentException e) {


            }


//                }
//            });

            ClosetAsync obj = new ClosetAsync(holder.recyclerView, item);
            obj.execute(null, null, null);

            holder.tv_profilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profile = new Intent(context, activity.profile.class);
                    profile.putExtra("activity", "MainFeed");
                    profile.putExtra("user_id", item.getUserId());
                    profile.putExtra("p_username", item.getUserName());
                    context.startActivity(profile);
                    ((Activity) context).finish();

                }
            });


            holder.im_profile_image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profile = new Intent(context, activity.profile.class);
                    profile.putExtra("user_id", item.getUserId());
                    profile.putExtra("p_username", item.getUserName());
                    context.startActivity(profile);
                    ((Activity) context).finish();

                }
            });


            holder.tv_admire.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (GetSharedValues.LoginStatus(context)) {
                        if (item.getAdmire()) {
                            holder.tv_admire.setText("Admire");
                            holder.tv_admire.setTextColor(Color.parseColor("#ff7477"));
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                holder.tv_admire.setBackground(ContextCompat.getDrawable(context,R.drawable.brand_unfollow_bg));
                            }
                            item.setAdmire(false);
                            JSONObject admireObject = null;
                            try {
                                admireObject = new JSONObject();
                                admireObject.put("action", "unadmire");
                                admireObject.put("user", item.getUserId());
                            } catch (Exception e) {

                            }
                            new AdmireTask(context).execute(admireObject);
                        } else {
                            holder.tv_admire.setText("Admiring");
                            holder.tv_admire.setTextColor(Color.WHITE);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                                holder.tv_admire.setBackground(ContextCompat.getDrawable(context,R.drawable.brand_follow_bg));
                            }
                            item.setAdmire(true);
                            JSONObject admireObject = null;
                            try {
                                admireObject = new JSONObject();
                                admireObject.put("action", "admire");
                                admireObject.put("user", item.getUserId());
                            } catch (Exception e) {

                            }
                            new AdmireTask(context).execute(admireObject);
                            HashMap<String, Object> admire = new HashMap<String, Object>();
                            admire.put("admired_user_id", item.getUserId());
                            cleverTap.event.push("admire", admire);
                        }
                    } else {
                        Alerts.loginAlert(context);
                    }

                }
            });

            if (param.equals("designer")) {
                holder.tv_gotocloset.setText("View Collection");
            } else {
                holder.tv_gotocloset.setText("View Closet");
            }

            holder.tv_gotocloset.setPaintFlags(holder.tv_gotocloset.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            holder.tv_gotocloset.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent profile = new Intent(context, activity.profile.class);
                    profile.putExtra("user_id", item.getUserId());
                    profile.putExtra("p_username", item.getUserName());
                    profile.putExtra("activity", "MainFeed");
                    context.startActivity(profile);
                }
            });


        }
// else if (holder.Holderid == 0) {
//            System.out.println("xyz");
//
//            if (CheckConnectivity.isNetworkAvailable(context)) {
//                holder.customProgressBar.setVisibility(View.VISIBLE);
//            } else {
//                holder.customProgressBar.setVisibility(View.GONE);
//            }
//        }
        //setAnimation(holder.itemView, position);

    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (data.get(position) == null) {
            ////System.out.println("inside load more viewtype grid");
            return TYPE_LOAD_MORE;
        } else {
            ////System.out.println("inside item viewtype grid");
            return TYPE_ITEM;
        }
    }

    public class ClosetAsync extends AsyncTask<String, String, String> {
        RecyclerView rv;
        HorizontalItemAdaptor adaptor;
        ClosetData item;

        ClosetAsync(RecyclerView v, ClosetData item) {
            this.rv = v;
            this.item = item;

        }

        @Override
        protected String doInBackground(String... params) {
            System.out.println("sdf1");
            adaptor = new HorizontalItemAdaptor(item.getProducts(), context);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            rv.getLayoutParams().height = (int) ((GetSharedValues.getScreenWidth(context) * 0.35) * 1.8);
            System.out.println("sdf2");
            // rv.setHasFixedSize(true);
            rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            rv.setAdapter(adaptor);
            rv.setNestedScrollingEnabled(false);

        }


    }


}