package adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.R;

import org.json.JSONObject;

import java.util.ArrayList;

import activity.LikersActivity;
import activity.ProfilePage;
import models.ClosetData;
import models.LikeData;
import network.AdmireTask;
import utils.FontUtils;
import utils.GetSharedValues;

/**
 * Created by haseeb on 7/9/16.
 */
public class LikeAdaptor extends RecyclerView.Adapter<LikeAdaptor.ViewHolder>{

    private static final int TYPE_LOAD_MORE = 0;
    private static final int TYPE_ITEM = 1;
    ArrayList<LikeData> data;
    static Context context;


    public LikeAdaptor(ArrayList<LikeData> data, Context context) { // MyAdapter Constructor with titles and icons parameter
        this.data = data;
        LikeAdaptor.context = context;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public View mView;
        int Holderid;
        ProgressBar customProgressBar;
        RoundedImageView profileImage;
        TextView profilename, admire;



        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            FontUtils.setCustomFont(itemView, context.getAssets());

            if (ViewType == TYPE_ITEM) {
                Holderid = 1;
                mView = itemView;
                profileImage = (RoundedImageView) itemView.findViewById(R.id.l_pic);
                profilename = (TextView) itemView.findViewById(R.id.l_username);
                admire = (TextView) itemView.findViewById(R.id.l_admire);


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
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.like_list, parent, false); //Inflating the layout
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
        final LikeData item = data.get(position);

        if (holder.Holderid == 1) {

            holder.profilename.setText(item.getLikersname());
            Glide.with(context)
                    .load(item.getprofilePic())
                    .fitCenter()
                    .placeholder(R.drawable.prof_placeholder)
                    .error(R.drawable.prof_placeholder)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .crossFade()
                    .into(holder.profileImage);

            holder.profilename.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pro_page = new Intent(context, ProfilePage.class);
                    pro_page.putExtra("activity", "LikersActivity");
                    pro_page.putExtra("product_id", Integer.parseInt(LikersActivity.albumId));
                    pro_page.putExtra("user_id", item.getUserid());
                    pro_page.putExtra("p_username", item.getLikersname());
                    pro_page.putExtra("p_usertype", "");
                    pro_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(pro_page);
                }
            });

            holder.profileImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pro_page = new Intent(context, ProfilePage.class);
                    pro_page.putExtra("activity", "LikersActivity");
                    pro_page.putExtra("product_id", Integer.parseInt(LikersActivity.albumId));
                    pro_page.putExtra("user_id", item.getUserid());
                    pro_page.putExtra("p_username", item.getLikersname());
                    pro_page.putExtra("p_usertype", "");
                    pro_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(pro_page);
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
}
