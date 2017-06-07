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
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.GetSharedValues;

/**
 * Created by haseeb on 9/3/16.
 */
public class RecyclerViewProductAdapter extends RecyclerView.Adapter<RecyclerViewProductAdapter.ViewHolder> {
    private static final int TYPE_LOAD_MORE = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    ArrayList<HomeFeedItem> feeds;
    static Context context;
    final String SCREEN_NAME = "FEED";
    static Date today;
    int screenHeight = 0, screenWidth = 0;

    String param;
    private int lastPosition = -1;
    String activityName;

    CleverTapAPI cleverTap;


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        int Holderid;

        ImageView image, like;
        TextView title, usertype;
        TextView price, orgPrice, discount, brand;
        RelativeLayout BottomtextLayout;
        ProgressBar customProgressBar;
        int screenHeight = 0, screenWidth = 0;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            JSONObject screenSize = ExternalFunctions.displaymetrics(context);
            screenWidth = screenSize.optInt("width");
//            screenHeight = screenSize.optInt("width");

            if (ViewType == TYPE_ITEM) {
                Holderid = 1;
                mView = itemView;
                today = new Date();
                image = (ImageView) itemView.findViewById(R.id.feedgrid);
                title = (TextView) itemView.findViewById(R.id.feedgridtitle);
                like = (ImageView) itemView.findViewById(R.id.imageview_like);
                price = (TextView) itemView.findViewById(R.id.feedgridprice);
                brand = (TextView) itemView.findViewById(R.id.brand);
                usertype = (TextView) itemView.findViewById(R.id.feeduser);
                orgPrice = (TextView) itemView.findViewById(R.id.feedgridOrgprice);
                orgPrice.setPaintFlags(orgPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                orgPrice.setTextColor(Color.GRAY);
                discount = (TextView) itemView.findViewById(R.id.feedgriddiscount);
                discount.getLayoutParams().width = (int) Double.parseDouble(String.valueOf(((screenWidth - 6) / 2) * 0.4));
                image.getLayoutParams().height = (int) (((screenWidth - 6) / 2) * 1.333);
                image.getLayoutParams().width = (screenWidth - 6) / 2;
                //  FontUtils.setCustomFont(itemView, context.getAssets());

            } else if (ViewType == TYPE_LOAD_MORE) {
                System.out.println("Load more new");
                Holderid = 0;
                customProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
                customProgressBar.setVisibility(View.GONE);
            }

        }

        public void clearAnimation() {
            if (mView != null) {
                mView.clearAnimation();
            }
        }
    }


    public RecyclerViewProductAdapter(ArrayList<HomeFeedItem> feeds, Context context, String param, RecyclerView rv, String activityName) { // MyAdapter Constructor with titles and icons parameter
        this.feeds = feeds;
        RecyclerViewProductAdapter.context = context;
        this.activityName = activityName;
        JSONObject screenSize = ExternalFunctions.displaymetrics(context);
        screenHeight = screenSize.optInt("width");
        screenWidth = screenSize.optInt("width");
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
        ViewHolder vhItem = null;
        View v = null;
        if (viewType == TYPE_ITEM) {
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_griditem_new, parent, false); //Inflating the layout
            vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhItem;
            // Returning the created object
        } else {
            System.out.println("Load more");
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false); //Inflating the layout
            vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhItem;
            // Returning the created object
        }

    }


    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final HomeFeedItem feed = feeds.get(position);
        //   System.out.println("PRICES : " + Double.parseDouble(feed.getNewPrice().replace(",", "")) + "____" + Double.parseDouble(feed.getOldPrice().replace(",", "")));
        //YoYo.with(Techniques.FadeIn).duration(600).playOn(holder.itemView);
        if (holder.Holderid == 1) {

            holder.title.setText(feed.getProductName());
            holder.brand.setText(feed.getBrand());


            // System.out.println("ssss1"+feed.getUsertype().toString());
            if (!feed.getUsertype().contains("designer")) {
                System.out.println("ssss");
                holder.usertype.setText("PRE OWNED");
            } else {
                holder.usertype.setText("Brand New");
            }


            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {


                    try {
                        Glide.with(context)
                                .load(EnvConstants.APP_MEDIA_URL + feed.getProductImage().get(0).toString())
                                .placeholder(R.drawable.playholderscreen)
                                .dontAnimate()
                                .override((screenHeight - 6) / 2, (int) (((screenHeight - 6) / 2) * 1.333))
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .into(holder.image);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            });


            final int likes = feed.getLikes();
            if (feed.isLiked()) {
                //////System.out.println("inside is liked");
                holder.like.setImageDrawable(context.getResources().getDrawable(R.drawable.orangeheart));
            } else {
                //////System.out.println("inside is unliked");
                holder.like.setImageDrawable(context.getResources().getDrawable(R.drawable.heartblack));
            }
            holder.like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckConnectivity.isNetworkAvailable(context)) {
                        if (GetSharedValues.LoginStatus(context)) {
                            if (feed.isLiked()) {
                                feed.setIsLiked(false);
                                final JSONObject likeObject = new JSONObject();
                                try {
                                    likeObject.put("product_id", feed.getId());
                                    likeObject.put("action", "unlike");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApiService.getInstance(context, 1).postData((ApiCommunication) context, EnvConstants.APP_BASE_URL + "/user/like_product/", likeObject, SCREEN_NAME, "unlike");
                                feed.setLikes(likes - 1);

                            } else {
                                feed.setIsLiked(true);

                                final JSONObject likeObject = new JSONObject();
                                try {
                                    likeObject.put("product_id", feed.getId());
                                    likeObject.put("action", "like");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApiService.getInstance(context, 1).postData((ApiCommunication) context, EnvConstants.APP_BASE_URL + "/user/like_product/", likeObject, SCREEN_NAME, "like");
                                HashMap<String, Object> love = new HashMap<String, Object>();
                                love.put("product_id", feed.getId());
                                cleverTap.event.push("love", love);
                                feed.setLikes(likes + 1);

                            }
                            notifyDataSetChanged();
                        } else {
                            Alerts.loginAlert(context);
                        }
                    } else {
                        CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                    }
                }
            });


            if (feed.getSale().equals("SALE")) {
                if (feed.getSold_out()) {
                    holder.price.setVisibility(View.VISIBLE);
                    holder.orgPrice.setVisibility(View.VISIBLE);
                    holder.discount.setVisibility(View.VISIBLE);
                    holder.price.setText(context.getResources().getString(R.string.Rs) + feed.getNewPrice());
                    if (Integer.parseInt(feed.getOldPrice().replace(",", "")) > Integer.parseInt(feed.getNewPrice().replace(",", ""))) {
                        holder.orgPrice.setVisibility(View.VISIBLE);
                        holder.orgPrice.setText(context.getResources().getString(R.string.Rs) + feed.getOldPrice());
                    } else {
                        holder.orgPrice.setVisibility(View.GONE);
                    }
                    holder.discount.setText("Sold Out");
                } else {
                    holder.price.setVisibility(View.VISIBLE);
                    holder.orgPrice.setVisibility(View.VISIBLE);
                    holder.discount.setVisibility(View.VISIBLE);
                    holder.discount.setTextColor(Color.parseColor("#ff7477"));
                    holder.price.setText(context.getResources().getString(R.string.Rs) + feed.getNewPrice());
                    if (Double.parseDouble(feed.getOldPrice().replace(",", "")) > Double.parseDouble(feed.getNewPrice().replace(",", ""))) {
                        holder.orgPrice.setText(context.getResources().getString(R.string.Rs) + feed.getOldPrice());
                        holder.discount.setText("(" + feed.getDiscount() + "% Off".toUpperCase() + ")");
                        holder.discount.setBackgroundColor(Color.TRANSPARENT);
                    } else {
                        holder.orgPrice.setVisibility(View.GONE);
                        holder.discount.setVisibility(View.GONE);
                    }
                }
            } else if (feed.getSale().equals("SOCIAL")) {
                holder.price.setText("StyleInspiration");
                holder.orgPrice.setVisibility(View.GONE);
                holder.discount.setVisibility(View.GONE);
            } else {

            }

            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExternalFunctions.strOverlayactivity = "";
                    ExternalFunctions.strOverlayurl = "";
                    if (CheckConnectivity.isNetworkAvailable(context)) {
                        SharedPreferences FeedSession = context.getSharedPreferences("FeedSession",
                                Context.MODE_PRIVATE);
                        SharedPreferences.Editor FeedSessioneditor = FeedSession.edit();
                        FeedSessioneditor.putString("FeedBackAction", activityName);
                        FeedSessioneditor.putBoolean("FeedActionStatus", true);
                        FeedSessioneditor.apply();
                        System.out.println("firstnew"+ExternalFunctions.prevActivity);
                        // ExternalFunctions.prevActivity = SCREEN_NAME;
                        System.out.println("firstnewsecond"+ExternalFunctions.prevActivity);
                        Intent styleinspiration = new Intent(context, product.class);
                        styleinspiration.putExtra("album_id", feed.getId());
                        styleinspiration.putExtra("activity", activityName);
                        styleinspiration.putExtra("sale", false);
                        styleinspiration.putExtra("pta", false);
                        context.startActivity(styleinspiration);
                        ExternalFunctions.strOverlayurl = "";
                        Runtime.getRuntime().gc();
                    } else {
                        CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                    }
                }


            });

        }else if (holder.Holderid == 0) {
            System.out.println("xyz");
            if (CheckConnectivity.isNetworkAvailable(context)) {
                holder.customProgressBar.setVisibility(View.VISIBLE);
            } else {
                holder.customProgressBar.setVisibility(View.GONE);
            }
        }

    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return feeds.size();
    }

    @Override
    public int getItemViewType(int position) {
        if (feeds.get(position) == null) {
            ////System.out.println("inside load more viewtype grid");
            return TYPE_LOAD_MORE;
        } else {
            ////System.out.println("inside item viewtype grid");
            return TYPE_ITEM;
        }
    }


    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.clearAnimation();
    }
}