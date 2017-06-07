package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zapyle.zapyle.R;

import java.util.ArrayList;

import DisplayImage.ImageDownloaderTask;
import activity.Upload1;
import models.LikeData;
import models.OccasionData;
import utils.FontUtils;

/**
 * Created by haseeb on 22/1/16.
 */
public class OccasionAdaptor implements ListAdapter {
    ArrayList<OccasionData> occasionList = new ArrayList<OccasionData>();
    LayoutInflater l_inflater;
    Context context;


    public OccasionAdaptor(Context context, ArrayList<OccasionData> occasionList) {

        this.occasionList = occasionList;
        this.context = context;
        l_inflater = LayoutInflater.from(this.context);


    }


    customButtonListener customListner;

    public interface customButtonListener {

        //public void onButtonClickListner(int position, String temp, int id);

        void onButtonClickListner(int position, Integer value, String time, View v, MyOccasionViewHolder viewHolder);
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
    public int getCount(){
        return occasionList.size();
    }

    @Override
    public OccasionData getItem(int position){
        return  occasionList.get(position);
    }

    @Override
    public long getItemId(int position){
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(final int position, View convertView1, ViewGroup parent){
        final MyOccasionViewHolder myViewHolder;
        if(convertView1 == null){
            convertView1 = l_inflater.inflate(R.layout.occasion_list_item, parent, false);
            myViewHolder = new MyOccasionViewHolder(convertView1);
            convertView1.setTag(myViewHolder);
        }
        else{
            myViewHolder = (MyOccasionViewHolder) convertView1.getTag();
        }
        FontUtils.setCustomFont(convertView1, context.getAssets());
        final OccasionData occasionData = getItem(position);
        //////System.out.println(occasionData.getOccasion()+"__"+occasionData.getOccasionId());
        myViewHolder.tv_occasion.setText(occasionData.getOccasion().toUpperCase());
//        convertView1.setTag(occasionData.getOccasionId());
        convertView1.setOnClickListener(new View.OnClickListener() {
//
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position,occasionData.getOccasionId(),occasionData.getOccasion(),v, myViewHolder);
                }

            }
        });

        myViewHolder.rb_select.setOnClickListener(new View.OnClickListener() {
            //
            @Override
            public void onClick(View v) {
                if (customListner != null) {
                    customListner.onButtonClickListner(position, occasionData.getOccasionId(), occasionData.getOccasion(), v, myViewHolder);
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

    public static class MyOccasionViewHolder{
        public TextView tv_occasion;
        public RadioButton rb_select;

        public MyOccasionViewHolder(View item) {
            tv_occasion = (TextView) item.findViewById(R.id.occasion_name);
            rb_select = (RadioButton) item.findViewById(R.id.r_occasionbutton);

        }
    }
}
