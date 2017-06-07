package adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.zapyle.zapyle.Alerts;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;

import activity.ProductPage;
import activity.searchFeedPage;
import models.HomeFeedItem;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by haseeb on 9/3/16.
 */
public class searchGridAdaptor extends RecyclerView.Adapter<searchGridAdaptor.ViewHolder> {
    private static final int TYPE_LOAD_MORE = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    ArrayList<HomeFeedItem> feeds;
    static Context context;
    final String SCREEN_NAME = "FEED";
    static MixpanelAPI mixpanel;
    static Date today;
    int lastPosition = -1;
    int screenHeight = 0, screenWidth = 0;


    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        int Holderid;


        ImageView image, like;
        TextView title, usertype,brand;
        TextView price, orgPrice, discount;
        ProgressBar customProgressBar;
        int screenHeight = 0, screenWeight = 0;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            JSONObject screenSize = ExternalFunctions.displaymetrics(context);
            screenHeight = screenSize.optInt("width");
            screenWeight = screenSize.optInt("height");

            if (ViewType == TYPE_ITEM) {
                Holderid = 1;
                mView = itemView;
                today = new Date();
                mixpanel = MixpanelAPI.getInstance(context, context.getResources().getString(R.string.mixpanelToken));
                image = (ImageView) itemView.findViewById(R.id.feedgrid);
                title = (TextView) itemView.findViewById(R.id.feedgridtitle);
                like = (ImageView) itemView.findViewById(R.id.imageview_like);
                price = (TextView) itemView.findViewById(R.id.feedgridprice);
                usertype = (TextView) itemView.findViewById(R.id.feeduser);
                brand = (TextView) itemView.findViewById(R.id.brand);
                orgPrice = (TextView) itemView.findViewById(R.id.feedgridOrgprice);
                orgPrice.setPaintFlags(orgPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                orgPrice.setTextColor(Color.GRAY);
                discount = (TextView) itemView.findViewById(R.id.feedgriddiscount);
                discount.getLayoutParams().width = (int) Double.parseDouble(String.valueOf(((screenHeight - 6) / 2) * 0.4));
                image.getLayoutParams().height = ((screenHeight - 6) / 2) + 30;
                image.getLayoutParams().width = (screenHeight - 6) / 2;
            } else if (ViewType == TYPE_LOAD_MORE) {
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


    public searchGridAdaptor(ArrayList<HomeFeedItem> feeds, Context context) { // MyAdapter Constructor with titles and icons parameter
        this.feeds = feeds;
        searchGridAdaptor.context = context;
        JSONObject screenSize = ExternalFunctions.displaymetrics(context);
        screenHeight = screenSize.optInt("height");
        screenWidth = screenSize.optInt("width");
    }


    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feeds_griditem_new, parent, false); //Inflating the layout
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
        final HomeFeedItem feed = feeds.get(position);



        if (holder.Holderid == 1) {
//      Main content
//            --------------------------------------------------------------
            holder.title.setText(feed.getProductName());
            holder.brand.setText(feed.getBrand());

            if (!feed.getUsertype().contains("designer")) {
                holder.usertype.setText("PRE OWNED");
            }
            else {
                holder.usertype.setText("Brand New");
            }

            try {

                Glide.with(context)
                        .load(EnvConstants.APP_MEDIA_URL + feed.getProductImage().get(0).toString())
                        .fitCenter()
                        .placeholder(R.drawable.playholderscreen)
                        .dontAnimate()
                        .override((screenWidth - 6) / 2 , (int) (((screenWidth - 6) / 2) * 1.333))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .into(holder.image);

            } catch (JSONException e) {
                e.printStackTrace();
            }

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
                                JSONObject superprop = new JSONObject();
                                try {
                                    superprop.put("product title", feed.getProductName());
                                    superprop.put("Event Name", "Love a product");
                                    mixpanel.track("Love a product", superprop);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                final JSONObject likeObject = new JSONObject();
                                try {
                                    likeObject.put("product_id", feed.getId());
                                    likeObject.put("action", "like");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                ApiService.getInstance(context, 1).postData((ApiCommunication) context, EnvConstants.APP_BASE_URL + "/user/like_product/", likeObject, SCREEN_NAME, "like");
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
                    if (Double.parseDouble(feed.getOldPrice()) > Double.parseDouble(feed.getNewPrice())) {
                        holder.orgPrice.setText(context.getResources().getString(R.string.Rs) + feed.getOldPrice());
                    }
                    else {
                        holder.orgPrice.setVisibility(View.VISIBLE);
                    }
                    holder.discount.setText("SOLD OUT");
                    holder.discount.setTextColor(Color.WHITE);
                    holder.discount.setBackgroundColor(Color.parseColor("#ff7477"));
                } else {
                    holder.price.setVisibility(View.VISIBLE);
                    holder.orgPrice.setVisibility(View.VISIBLE);
                    holder.discount.setVisibility(View.VISIBLE);
                    holder.price.setText(context.getResources().getString(R.string.Rs) + feed.getNewPrice());
                    if (Double.parseDouble(feed.getOldPrice().replace(",","")) > Double.parseDouble(feed.getNewPrice().replace(",",""))) {
                        holder.orgPrice.setText(context.getResources().getString(R.string.Rs) + feed.getOldPrice());
                    }
                    else {
                        holder.orgPrice.setVisibility(View.GONE);
                    }
                    if (Double.parseDouble(feed.getDiscount()) > 0) {
                        holder.discount.setText(feed.getDiscount() + "% Off".toUpperCase());
                        holder.discount.setTextColor(Color.BLACK);
                        holder.discount.setBackgroundColor(Color.TRANSPARENT);
                    }
                    else {
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
                        Intent productPage = new Intent(context, ProductPage.class);
                        productPage.putExtra("album_id", feed.getId());
                        productPage.putExtra("pta", false);
                        productPage.putExtra("activity", "searchFeedPage");
                        productPage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        Runtime.getRuntime().gc();
                        context.startActivity(productPage);

                        ExternalFunctions.strOverlayurl = "";

                    } else {
                       CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                    }
                }


            });

//            setAnimation(holder.itemView, position);
        }
       //setAnimation(holder.itemView, position);

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

    private void setAnimation(View viewToAnimate, int position)
    {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition)
        {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.swing_up);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }



    @Override
    public void onViewDetachedFromWindow(ViewHolder holder) {
        super.onViewDetachedFromWindow(holder);
        holder.clearAnimation();
    }
}