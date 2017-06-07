package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import java.util.ArrayList;

import activity.MyLoves;
import activity.product;
import models.ProfileData;

/**
 * Created by haseeb on 2/2/16.
 */
public class MyLoveAdaptor extends BaseAdapter {
    ArrayList<ProfileData> profiledata = new ArrayList<ProfileData>();
    LayoutInflater inflater;
    Context context;


    public MyLoveAdaptor(Context context, ArrayList<ProfileData> profiledata) {
        this.profiledata = profiledata;
        this.context = context;
        inflater = LayoutInflater.from(this.context);
    }

    @Override
    public int getCount() {
        return profiledata.size();
    }

    @Override
    public ProfileData getItem(int position) {
        return profiledata.get(position);
    }


    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return 0;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ViewHolder holder = null;


        if (row == null) {
            row = inflater.inflate(R.layout.myloves_griditem, parent, false);
            holder = new ViewHolder();
            holder.image = (ImageView) row.findViewById(R.id.gimage2);
            try{
                holder.image.getLayoutParams().height = (int) (((MyLoves.screenHeight - 6) / 3) * 1.333);
                holder.image.getLayoutParams().width = (MyLoves.screenHeight - 6) / 3;
            }
            catch (Exception e){
                holder.image.getLayoutParams().height = (int) (((MyLoves.screenHeight - 6) / 3) * 1.333);
                holder.image.getLayoutParams().width = (MyLoves.screenHeight - 6) / 3;
            }
            row.setTag(holder);
        } else {
            holder = (ViewHolder) row.getTag();
        }

        final ProfileData item = profiledata.get(position);

        Glide.with(context)
                .load(EnvConstants.APP_MEDIA_URL + item.getImage())
                .fitCenter()
                .placeholder(R.drawable.playholderscreen)
                .crossFade()
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .override((MyLoves.screenHeight - 6) / 3,(int) (((MyLoves.screenHeight - 6) / 3) * 1.333))
                .into(holder.image);



        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent productPage = new Intent(context, product.class);
                    productPage.putExtra("pta", false);
                    productPage.putExtra("activity", "MyLoves");
//                    productPage.putExtra("sale", true);
                    productPage.putExtra("album_id", Integer.parseInt(item.getAlbumid()));
                    productPage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    context.startActivity(productPage);
                    ((Activity) context).finish();
            }
        });
        return row;
    }

    static class ViewHolder {
        //        TextView imageTitle;
        ImageView image;
    }
}
