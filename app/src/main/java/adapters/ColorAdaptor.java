package adapters;

import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.zapyle.zapyle.R;

import java.util.ArrayList;

import models.ColorData;
import utils.FontUtils;

/**
 * Created by haseeb on 23/1/16.
 */
public class ColorAdaptor implements ListAdapter {
    ArrayList<ColorData> myColorList = new ArrayList<ColorData>();
    LayoutInflater l_inflater;
    Context context;


    public ColorAdaptor(Context context, ArrayList<ColorData> myColorList) {

        this.myColorList = myColorList;
        this.context = context;
        l_inflater = LayoutInflater.from(this.context);

    }

    customButtonListener customListner;

    public interface customButtonListener {

        //public void onButtonClickListner(int position, String temp, int id);

        void onButtonClickListner(int position, Integer value, String time, View v, MyColorViewHolder viewHolder);
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
        return myColorList.size();
    }

    @Override
    public ColorData getItem(int position) {
        return myColorList.get(position);
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
    public View getView(final int position, View convertView1, ViewGroup parent) {
        final MyColorViewHolder myViewHolder;
        if (convertView1 == null) {
            convertView1 = l_inflater.inflate(R.layout.color_code, parent, false);
            myViewHolder = new MyColorViewHolder(convertView1);
            convertView1.setTag(myViewHolder);
        } else {
            myViewHolder = (MyColorViewHolder) convertView1.getTag();
        }
        FontUtils.setCustomFont(convertView1, context.getAssets());

        final ColorData colorData = getItem(position);

        try {
            if(colorData.getColor().contains("Multi")){
//                myViewHolder.color_holder.setImageResource(R.drawable.multi);
                Glide.with(context)
                        .load(R.drawable.multi)
                        .asBitmap()
                        .fitCenter()
                        .placeholder(R.drawable.prof_placeholder)
                        .error(R.drawable.prof_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(new BitmapImageViewTarget(myViewHolder.color_holder) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                myViewHolder.color_holder.setImageDrawable(circularBitmapDrawable);
                            }
                        });
                myViewHolder.colorname.setText(colorData.getColor());
            }
            else {
                myViewHolder.colorname.setText(colorData.getColor());
//                myViewHolder.color_holder.setBackgroundColor(Color.parseColor(colorData.getColorCode()));
                Glide.with(context)
                        .load(Color.parseColor(colorData.getColorCode()))
                        .asBitmap()
                        .fitCenter()
                        .placeholder(R.drawable.prof_placeholder)
                        .error(R.drawable.prof_placeholder)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop()
                        .into(new BitmapImageViewTarget(myViewHolder.color_holder) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                RoundedBitmapDrawable circularBitmapDrawable =
                                        RoundedBitmapDrawableFactory.create(context.getResources(), resource);
                                circularBitmapDrawable.setCircular(true);
                                myViewHolder.color_holder.setImageDrawable(circularBitmapDrawable);
                            }
                        });

            }

        } catch (Exception e) {
//             CustomMessage.getInstance().CustomMessage(context,"Unable to load colors!");
        }

        convertView1.setOnClickListener(new View.OnClickListener() {
            //
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        myViewHolder.color_layout_holder.setBackground(ContextCompat.getDrawable(context,R.drawable.background_feed_icons));
                    }
                    customListner.onButtonClickListner(position, colorData.getId(), colorData.getColor(), v, myViewHolder);
                }

            }
        });


        return convertView1;
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

    public static class MyColorViewHolder {
        public TextView colorname;
        public ImageView color_holder;
        public LinearLayout color_layout_holder;

        public MyColorViewHolder(View item) {
            colorname = (TextView) item.findViewById(R.id.colorname);
            color_holder = (ImageView) item.findViewById(R.id.color_holder);
            color_layout_holder = (LinearLayout) item.findViewById(R.id.color_layout_holder);

        }
    }
}
