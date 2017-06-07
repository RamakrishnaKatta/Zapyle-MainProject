package adapters;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import activity.CommentActivity;
import activity.profile;
import models.CommentData;
import utils.FontUtils;
import utils.GetSharedValues;

import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.zapyle.zapyle.R;

import java.util.ArrayList;

/**
 * Created by hitech on 11/7/15.
 */
public class CommentAdaptor extends BaseAdapter {
    ArrayList<CommentData> myList = new ArrayList<CommentData>();
    LayoutInflater inflater;
    Context context;


    public CommentAdaptor(Context context, ArrayList<CommentData> myList) {
        this.myList = myList;
        this.context = context;
        inflater = LayoutInflater.from(this.context);

    }

    customButtonListener customListner;

    public interface customButtonListener {

        //public void onButtonClickListner(int position, String temp, int id);

        void onButtonClickListner(int position, Integer value, String time, View v, MyViewHolder viewHolder);
    }

    public void setCustomButtonListner(customButtonListener listener) {
        this.customListner = listener;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public CommentData getItem(int position) {
        return myList.get(position);
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        final MyViewHolder myViewHolder;
        final CommentData currentCommentData = getItem(position);

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.comment_list_item, parent, false);
            myViewHolder = new MyViewHolder(convertView);
//            myViewHolder.delete_comment.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if(myList.size() > position) {
//                        ////System.out.println("inside delete click"+"__"+myList.size()+"___"+position);
//                        myViewHolder.delete_comment.setEnabled(false);
//                        myList.remove(position);
//                        notifyDataSetChanged();
//                        notifyDataSetInvalidated();
//                        myViewHolder.delete_comment.setEnabled(true);
//                    }
//                }
//            });


            myViewHolder.delete_comment.setOnClickListener(new View.OnClickListener() {
                //
                @Override
                public void onClick(View v) {
                    if (customListner != null) {
                        customListner.onButtonClickListner(position, currentCommentData.getCommentId(), currentCommentData.getCommenttext(), v, myViewHolder);
                    }

                }
            });
            convertView.setTag(myViewHolder);
        } else {
            myViewHolder = (MyViewHolder) convertView.getTag();
        }
        FontUtils.setCustomFont(convertView, context.getAssets());
        if (GetSharedValues.getuserId(context) == Integer.parseInt(currentCommentData.getUserId())) {
            myViewHolder.delete_comment.setVisibility(View.VISIBLE);

        } else {
            myViewHolder.delete_comment.setVisibility(View.GONE);
        }


        myViewHolder.tvTitle.setText(currentCommentData.getUsername());
        myViewHolder.tvDesc.setText(currentCommentData.getCommenttext());
        myViewHolder.tvTime.setText(currentCommentData.getTime());
        myViewHolder.tvTitle.setTag(currentCommentData.getUserId());
        myViewHolder.tvDesc.setTag(currentCommentData.getCommentId());


        myViewHolder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pro_page = new Intent(context, profile.class);
                pro_page.putExtra("activity", "CommentActivity");
                pro_page.putExtra("product_id", CommentActivity.album_id);
                pro_page.putExtra("user_id", Integer.parseInt(currentCommentData.getUserId()));
                pro_page.putExtra("p_username", currentCommentData.getUsername());
                pro_page.putExtra("p_usertype", "");
                pro_page.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(pro_page);
            }
        });

//        Glide.with(context)
//                .load(currentCommentData.getImgResId())
//                .fitCenter()
//                .placeholder(R.drawable.prof_placeholder)
//                .crossFade()
//                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                .into(myViewHolder.ivIcon);

        Glide.with(context)
                .load(currentCommentData.getImgResId())
                .asBitmap()
                .placeholder(R.drawable.prof_placeholder)
                .error(R.drawable.prof_placeholder)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(new BitmapImageViewTarget(myViewHolder.ivIcon) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        myViewHolder.ivIcon.setImageDrawable(circularBitmapDrawable);
                    }
                });

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

    @Override
    public boolean areAllItemsEnabled() {
        return false;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    public class MyViewHolder {
        public TextView tvTitle, tvDesc, tvTime;
        public ImageView ivIcon;
        public ImageView delete_comment;

        public MyViewHolder(View item) {
            tvTitle = (TextView) item.findViewById(R.id.username);
            tvDesc = (TextView) item.findViewById(R.id.commenttext);
            tvTime = (TextView) item.findViewById(R.id.time);
            ivIcon = (ImageView) item.findViewById(R.id.ivIcon);
            delete_comment = (ImageView) item.findViewById(R.id.delete_comment);
        }
    }
}
