package adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import java.util.ArrayList;

import activity.OrderTrack;
import models.MyorderListmodel;
import utils.FontUtils;

/**
 * Created by haseeb on 7/9/16.
 */

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>  {
    ArrayList<MyorderListmodel> data = new ArrayList<MyorderListmodel>();
    Context context;
    private static final int TYPE_LOAD_MORE = 0;
    private static final int TYPE_ITEM = 1;
    int lastPosition = -1;



    public OrderAdapter(ArrayList<MyorderListmodel> data, Context context) { // MyAdapter Constructor with titles and icons parameter
        this.data = data;
        context = context;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        int Holderid;
        ProgressBar customProgressBar;
        ImageView im_productImage,orderTrack;
        TextView title,originalPrice, listingPrice, orderDate, orderStatus;





        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            FontUtils.setCustomFont(itemView, context.getAssets());

            if (ViewType == TYPE_ITEM) {
                Holderid = 1;
                mView = itemView;
                im_productImage = (ImageView) itemView.findViewById(R.id.imgproduct);
                title = (TextView) itemView.findViewById(R.id.order_product_title);
                originalPrice = (TextView) itemView.findViewById(R.id.order_original_price);
                listingPrice = (TextView) itemView.findViewById(R.id.order_listing_price);
                orderDate = (TextView) itemView.findViewById(R.id.order_date);
                orderStatus = (TextView) itemView.findViewById(R.id.order_status);
                orderTrack = (ImageView) itemView.findViewById(R.id.track_order);


            } else if (ViewType == TYPE_LOAD_MORE) {
                Holderid = 0;
                customProgressBar = (ProgressBar) itemView.findViewById(R.id.progressBar);
                customProgressBar.setVisibility(View.GONE);
            }

        }

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.myorders_list, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_load_more, parent, false); //Inflating the layout
            ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

            return vhItem; // Returning the created object
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final MyorderListmodel item = data.get(position);

        if (holder.Holderid == 1) {
            Glide.with(context)
                    .load(EnvConstants.APP_MEDIA_URL+item.getimagepath())
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .error(R.drawable.playholderscreen)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .crossFade()
                    .into(holder.im_productImage);

            holder.title.setText(item.getproductname().toUpperCase());
//            System.out.println("Original pric : "+item.getamount());
            holder.originalPrice.setText(context.getResources().getString(R.string.Rs)+String.valueOf(item.getamount()));
            if (Integer.parseInt(item.getOriginalPrice()) > item.getamount()) {
                holder.listingPrice.setVisibility(View.GONE);
                holder.listingPrice.setText(context.getResources().getString(R.string.Rs) + item.getOriginalPrice());
            }
            else {
                holder.listingPrice.setVisibility(View.GONE);
            }

            System.out.println("Status: "+item.getstatus() + "___" + item.getReturn());
            holder.orderStatus.setText(item.getstatus());
            holder.orderDate.setText(item.getPlaced_at());


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent tracker = new Intent(context, OrderTrack.class);
                    tracker.putExtra("orderID",item.getorderid());
                    context.startActivity(tracker);
                }
            });

        } else if (holder.Holderid == 0) {
            if (CheckConnectivity.isNetworkAvailable(context)) {
                holder.customProgressBar.setVisibility(View.VISIBLE);
            }
            else {
                holder.customProgressBar.setVisibility(View.GONE);
            }
        }

        setAnimation(holder.itemView, position);
    }

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
}
