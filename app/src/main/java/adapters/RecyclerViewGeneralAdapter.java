package adapters;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import activity.product;
import models.HomeFeedItem;
import models.genericData;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by Rajeesh on 9/3/16.
 */
public class RecyclerViewGeneralAdapter extends RecyclerView.Adapter<RecyclerViewGeneralAdapter.ViewHolder> {
    private static final int TYPE_LOAD_MORE = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    ArrayList<genericData> feeds;

    static Context context;
    final String SCREEN_NAME = "FEED";
    String activityName;

    CleverTapAPI cleverTap;


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        int Holderid;

        ImageView image;
        RelativeLayout rv;

        ProgressBar customProgressBar;
        int screenWidth = 0;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            JSONObject screenSize = ExternalFunctions.displaymetrics(context);
            screenWidth = screenSize.optInt("width");
            RelativeLayout.LayoutParams param=new RelativeLayout.LayoutParams((screenWidth/2)-10, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (ViewType == TYPE_ITEM) {
                mView = itemView;
                image = (ImageView) itemView.findViewById(R.id.img_generic);
                rv=(RelativeLayout)itemView.findViewById(R.id.rv);
                rv.setLayoutParams(param);
                 FontUtils.setCustomFont(itemView, context.getAssets());

            }

        }

        public void clearAnimation() {
            if (mView != null) {
                mView.clearAnimation();
            }
        }
    }


    public RecyclerViewGeneralAdapter(ArrayList<genericData> feeds, Context context,  String activityName) { // MyAdapter Constructor with titles and icons parameter
        this.feeds = feeds;
        RecyclerViewGeneralAdapter.context = context;
        this.activityName = activityName;

        try {
            cleverTap = CleverTapAPI.getInstance(context);
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }

    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        ViewHolder vhItem = null;
        View v = null;
        if (viewType == TYPE_ITEM) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.genericitem, parent, false); //Inflating the layout
            vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhItem;
            // Returning the created object
        }
        return vhItem;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final genericData feed = feeds.get(position);
        //   System.out.println("PRICES : " + Double.parseDouble(feed.getNewPrice().replace(",", "")) + "____" + Double.parseDouble(feed.getOldPrice().replace(",", "")));

      System.out.println("zzzzznb"+EnvConstants.APP_MEDIA_URL + feed.getImage());
                    try {
                        Glide.with(context)
                                .load( feed.getImage())
                                .placeholder(R.drawable.playholderscreen)
                                .dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(holder.image);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }




            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (CheckConnectivity.isNetworkAvailable(context)) {

                        System.out.println("URLLLL : "+feed.getTarget());

                        Intent intent=  ExternalFunctions.routeActivity(context,"filtered",feed.getTarget());
                        context.startActivity(intent);


                    } else {
                        CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                    }
                }


            });

    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return feeds.size();
    }

    @Override
    public int getItemViewType(int position) {

            return TYPE_ITEM;

    }


    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.clearAnimation();
    }
}