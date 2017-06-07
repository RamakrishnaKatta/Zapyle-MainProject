package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.appvirality.wom.RoundedImageView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.mixpanel.android.mpmetrics.MixpanelAPI;
import com.squareup.picasso.Picasso;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import activity.ProductPage;
import models.ClosetData;
import models.SingleItemData;
import utils.ExternalFunctions;
import utils.FontUtils;

/**
 * Created by haseeb on 9/8/16.
 */
public class HorizontalItemAdaptor  extends RecyclerView.Adapter<HorizontalItemAdaptor.ViewHolder> {
    private static final int TYPE_LOAD_MORE = 0;
    private static final int TYPE_ITEM = 1;
    ArrayList<SingleItemData> data;
    static Context context;
    int screenWidth;

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        int Holderid;

        TextView tv_albumname;
        ImageView im_albumimage;

        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
          //  FontUtils.setCustomFont(itemView, context.getAssets());

            if (ViewType == TYPE_ITEM) {
                Holderid = 1;
                mView = itemView;
                im_albumimage = (ImageView) itemView.findViewById(R.id.hv_image);
                tv_albumname = (TextView) itemView.findViewById(R.id.hv_album_name);

            } else if (ViewType == TYPE_LOAD_MORE) {
                Holderid = 0;
//                customProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
//                customProgressBar.setVisibility(View.GONE);
            }

        }

    }


    public HorizontalItemAdaptor(ArrayList<SingleItemData> data, Context context) { // MyAdapter Constructor with titles and icons parameter
        this.data = data;
        HorizontalItemAdaptor.context = context;
        JSONObject measures = ExternalFunctions.displaymetrics(context);
        try {
            screenWidth = measures.getInt("width");
        } catch (JSONException e) {
            e.printStackTrace();
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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.horizontal_item_placeholder, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        } else {
//            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false); //Inflating the layout
//            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return null; // Returning the created object
        }

    }


    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final SingleItemData item = data.get(position);
        if (holder.Holderid == 1) {
            System.out.println("DATAALLLL: inside adaptor 2");

            try{
                holder.im_albumimage.getLayoutParams().height = (int) ((screenWidth * 0.35) * 1.333);
                holder.im_albumimage.getLayoutParams().width = (int) (screenWidth * 0.35);

            }
            catch (Exception e){
                holder.im_albumimage.getLayoutParams().height = (int) ((screenWidth * 0.35) * 1.333);
                holder.im_albumimage.getLayoutParams().width = (int) (screenWidth * 0.35);
            }

            System.out.println("SIZESSSS : "+(int) ((screenWidth * 0.35) * 1.333) +"____"+(int) (screenWidth * 0.35));

            if (holder.im_albumimage.getDrawable()==null) {

//                ((Activity) context).runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//
                        Glide.with(context)
                                .load(item.getImageUrl())
                                .fitCenter()
                                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                .placeholder(R.drawable.playholderscreen)
                                .dontAnimate()
                                .override((int) (screenWidth * 0.35), (int) ((screenWidth * 0.35) * 1.333))
                                .into(holder.im_albumimage);



//                        Picasso
//                                .with(context)
//                                .load(item.getImageUrl())
//                                .placeholder(R.drawable.playholderscreen)
//                                .resize((int) (screenWidth * 0.35), (int) ((screenWidth * 0.35) * 1.333))
//                                .into(holder.im_albumimage);



//                    }
//                });

            }
            holder.tv_albumname.getLayoutParams().width = (int) (screenWidth * 0.35);
            holder.tv_albumname.setText(item.getTitle());


            holder.im_albumimage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences FeedSession = context.getSharedPreferences("FeedSession",
                            Context.MODE_PRIVATE);
                    SharedPreferences.Editor FeedSessioneditor = FeedSession.edit();
                    FeedSessioneditor.putBoolean("FeedBackAction", true);
                    FeedSessioneditor.apply();
                    Intent product = new Intent(context, ProductPage.class);
                    product.putExtra("album_id",item.getProductId());
                    context.startActivity(product);

                }
            });
            System.out.println("DATAALLLL: inside adaptor 2 1");

        } else if (holder.Holderid == 0) {

        }

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
}
