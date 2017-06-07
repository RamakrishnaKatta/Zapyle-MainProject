package adapters;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import activity.discover;
import activity.product;
import models.genericData;
import models.productData;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;

/**
 * Created by Rajeesh on 9/3/16.
 */
public class RecyclerViewCollectionAdapter extends RecyclerView.Adapter<RecyclerViewCollectionAdapter.ViewHolder> {
    private static final int TYPE_LOAD_MORE = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;
    ArrayList<productData> feeds;

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

        int screenWidth = 0;
        TextView tvLprice,tvOprice,tvprodtitle,tvbrand;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);

            JSONObject screenSize = ExternalFunctions.displaymetrics(context);
            screenWidth = screenSize.optInt("width");
            RelativeLayout.LayoutParams param=new RelativeLayout.LayoutParams((screenWidth/2)-10, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (ViewType == TYPE_ITEM) {
                mView = itemView;
                image = (ImageView) itemView.findViewById(R.id.product_collection_image);
                tvLprice=(TextView) itemView.findViewById(R.id.product_collection_lprice);
                tvbrand=(TextView) itemView.findViewById(R.id.product_collection_name);
                tvOprice=(TextView) itemView.findViewById(R.id.product_collection_oprice);
                tvprodtitle=(TextView) itemView.findViewById(R.id.prodgridtitle);

                rv=(RelativeLayout) itemView.findViewById(R.id.rv);
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


    public RecyclerViewCollectionAdapter(ArrayList<productData> feeds, Context context, String activityName) { // MyAdapter Constructor with titles and icons parameter
        this.feeds = feeds;
        RecyclerViewCollectionAdapter.context = context;
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
            v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_product_collection_inside_new, parent, false); //Inflating the layout
            vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
            return vhItem;
            // Returning the created object
        }
        return vhItem;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final productData feed = feeds.get(position);
        //   System.out.println("PRICES : " + Double.parseDouble(feed.getNewPrice().replace(",", "")) + "____" + Double.parseDouble(feed.getOldPrice().replace(",", "")));

        holder.tvprodtitle.setText(feed.getproductname());
        holder.tvbrand.setText(feed.getBrandname());
        holder.tvLprice.setText(feed.getLprice());
        holder.tvOprice.setText(feed.getOprice());

        holder.tvbrand.setTextColor(Color.parseColor("#4A4A4A"));
        holder.tvLprice.setText(context.getResources().getString(R.string.Rs) + feed.getLprice());
        holder.tvLprice.setTextColor(Color.parseColor("#4A4A4A"));
        holder.tvOprice.setTextColor(Color.parseColor("#4A4A4A"));
        try {
            if (Integer.parseInt(feed.getLprice()) == Integer.parseInt(feed.getOprice())) {
                holder.tvOprice.setText("");

            } else {
                holder.tvOprice.setText(context.getResources().getString(R.string.Rs) + feed.getOprice());
                holder.tvOprice.setPaintFlags(holder.tvOprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
        }catch(Exception e){

        }

        holder.image.getLayoutParams().height =  (int) (((discover.screenWidth - 6) / 2) * 1.333);
        holder.image.getLayoutParams().width =(discover.screenWidth - 6) / 2;
        holder.image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
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
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Intent productPage = new Intent(context, product.class);
                                    productPage.putExtra("activity", "discover");
                                    productPage.putExtra("album_id",Integer.parseInt(feed.getId()));
                                    productPage.putExtra("pta", false);
                                    productPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    context.startActivity(productPage);
                                    // getActivity().finish();
                                }catch(Exception e){

                                }
                            }
                        }, 1000);


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