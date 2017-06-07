package notifications;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.makeramen.roundedimageview.RoundedImageView;
import com.zapyle.zapyle.R;

import activity.BuyPage;
import activity.CommentActivity;
import activity.LikersActivity;
import activity.ProfilePage;

import java.util.ArrayList;

/**
 * Created by haseeb on 15/8/15.
 */
public class NotificationAdaptor implements ListAdapter {
    ArrayList<NotificationData> myNotifiersList = new ArrayList<NotificationData>();
    LayoutInflater n_inflater;

    Context context;

    public NotificationAdaptor(Context context, ArrayList<NotificationData> myNotifiersList) {

        this.myNotifiersList = myNotifiersList;
        this.context = context;
        n_inflater = LayoutInflater.from(this.context);

    }

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return myNotifiersList.size();
    }

    @Override
    public NotificationData getItem(int position) {
        return myNotifiersList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        MyNotifViewHolder holder;
        if (convertView == null) {
            convertView = n_inflater.inflate(R.layout.notif_list, parent, false);
            holder = new MyNotifViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (MyNotifViewHolder) convertView.getTag();
        }
        final NotificationData currentNotifData = getItem(position);

        if (currentNotifData.getAction().contains("love")) {
            holder.first_layout.setVisibility(View.VISIBLE);
            holder.second_layout.setVisibility(View.GONE);

            Glide.with(context)
                    .load(currentNotifData.getProfile_pic())
                    .fitCenter()
                    .placeholder(R.drawable.prof_placeholder)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.first_profile_pic);


            holder.first_message.setText(currentNotifData.getMessage());
            holder.first_time.setText(currentNotifData.getNotif_time());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent gotoLike = new Intent(context, LikersActivity.class);
                    gotoLike.putExtra("album_user", currentNotifData.getUsername());
                    gotoLike.putExtra("album_id", String.valueOf(currentNotifData.getProductid()));
                    context.startActivity(gotoLike);
                    ((Activity) context).finish();
                }
            });

        } else if (currentNotifData.getAction().contains("comment")) {
            holder.first_layout.setVisibility(View.VISIBLE);
            holder.second_layout.setVisibility(View.GONE);

            Glide.with(context)
                    .load(currentNotifData.getProfile_pic())
                    .fitCenter()
                    .placeholder(R.drawable.prof_placeholder)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.first_profile_pic);


            holder.first_message.setText(currentNotifData.getMessage());
            holder.first_time.setText(currentNotifData.getNotif_time());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent comment = new Intent(context, CommentActivity.class);
                    comment.putExtra("album_id", String.valueOf(currentNotifData.getProductid()));
                    context.startActivity(comment);
                    ((Activity) context).finish();
                }
            });
        } else if (currentNotifData.getAction().contains("admire")) {
            holder.first_layout.setVisibility(View.VISIBLE);
            holder.second_layout.setVisibility(View.GONE);

            Glide.with(context)
                    .load(currentNotifData.getProfile_pic())
                    .fitCenter()
                    .placeholder(R.drawable.prof_placeholder)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.first_profile_pic);


            holder.first_message.setText(currentNotifData.getMessage());
            holder.first_time.setText(currentNotifData.getNotif_time());
            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent pro_page = new Intent(context, ProfilePage.class);
                    pro_page.putExtra("user_id", Integer.parseInt(currentNotifData.getUser_id()));
                    pro_page.putExtra("p_username", currentNotifData.getUsername());
                    pro_page.putExtra("p_usertype", currentNotifData.getUser_type());
                    context.startActivity(pro_page);
                    ((Activity) context).finish();
                }
            });
        } else if (currentNotifData.getAction().contains("approve")) {

            holder.first_layout.setVisibility(View.GONE);
            holder.second_layout.setVisibility(View.VISIBLE);
            holder.second_profile_pic.setImageResource(R.drawable.icon);
            holder.second_message.setText(currentNotifData.getMessage());
            holder.second_time.setText(currentNotifData.getNotif_time());

            Glide.with(context)
                    .load(currentNotifData.getProductImage())
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.product_image);


            convertView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent feed = new Intent(context,                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       BuyPage.class);
                    context.startActivity(feed);
                    ((Activity) context).finish();
                }
            });
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private class MyNotifViewHolder {
        LinearLayout first_layout;
        RelativeLayout second_layout;
        TextView first_message, second_message;
        RoundedImageView first_profile_pic, second_profile_pic;
        TextView first_time, second_time;
        ImageView product_image;

        public MyNotifViewHolder(View item) {
            first_layout = (LinearLayout) item.findViewById(R.id.first_layout);
            second_layout = (RelativeLayout) item.findViewById(R.id.second_layout);
            first_message = (TextView) item.findViewById(R.id.first_notif_message);
            second_message = (TextView) item.findViewById(R.id.second_notif_message);
            first_profile_pic = (RoundedImageView) item.findViewById(R.id.first_profile_pic);
            second_profile_pic = (RoundedImageView) item.findViewById(R.id.second_profile_pic);
            first_time = (TextView) item.findViewById(R.id.first_notif_time);
            second_time = (TextView) item.findViewById(R.id.second_notif_time);
            product_image = (ImageView) item.findViewById(R.id.second_product_image);
        }
    }
}
