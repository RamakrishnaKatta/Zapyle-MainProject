package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import activity.Onboarding2;
import activity.Onboarding3;
import models.BrandListmodel;

/**
 * Created by haseeb on 14/1/16.
 */
public class NewBrandAdaptor extends BaseAdapter {
    ArrayList<BrandListmodel> strBranList = new ArrayList<BrandListmodel>();
    Context context;
    // String [] strUnselected;
    private static LayoutInflater inflater = null;
    private int intshowsel, intselcount, intunselcount;

    public NewBrandAdaptor(Context mainactivity, ArrayList<BrandListmodel> strBrandList, int intselcount, int intUnselcount, int intshowsel) {
        // TODO Auto-generated constructor stub
        this.strBranList = strBrandList;
        this.context = mainactivity;
        this.intshowsel = intshowsel;
        this.intselcount = intselcount;
        this.intunselcount = intUnselcount;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return strBranList.size();
    }

    @Override
    public BrandListmodel getItem(int position) {
        // TODO Auto-generated method stub
        return strBranList.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        // TODO Auto-generated method stub
        final MyViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.branditemnew, null);
            holder = new MyViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        final BrandListmodel data = getItem(position);

        if(!Onboarding3.seemoreStatus) {

            if (position < intshowsel) {
                holder.rlsel.setVisibility(View.VISIBLE);
                holder.lnseeAll.setVisibility(View.GONE);
                holder.tvselBrand.setText(data.getBrandname());
                if(!Onboarding3.dataToserver.contains(String.valueOf(data.getBrand_id()))){
                    holder.lnfollowing.setVisibility(View.GONE);
                    holder.lnfollow.setVisibility(View.VISIBLE);
                }
                else{
                    holder.lnfollowing.setVisibility(View.VISIBLE);
                    holder.lnfollow.setVisibility(View.GONE);
                }

            } else if (position == intshowsel) {
                holder.rlsel.setVisibility(View.GONE);
                holder.lnseeAll.setVisibility(View.VISIBLE);
                holder.rlline.setVisibility(View.GONE);
            } else {
                holder.rlsel.setVisibility(View.VISIBLE);
                holder.rlline.setVisibility(View.VISIBLE);
                holder.lnseeAll.setVisibility(View.GONE);
                holder.tvselBrand.setText(data.getBrandname());

                if (!Onboarding3.dataToserver.contains(String.valueOf(data.getBrand_id()))) {
                    holder.lnfollowing.setVisibility(View.GONE);
                    holder.lnfollow.setVisibility(View.VISIBLE);
                } else {
                    holder.lnfollowing.setVisibility(View.VISIBLE);
                    holder.lnfollow.setVisibility(View.GONE);
                }
            }
        }

        else {
            holder.rlsel.setVisibility(View.VISIBLE);
            holder.lnseeAll.setVisibility(View.GONE);
            holder.tvselBrand.setText(data.getBrandname());

            if (!Onboarding3.dataToserver.contains(String.valueOf(data.getBrand_id()))) {
                holder.lnfollowing.setVisibility(View.GONE);
                holder.lnfollow.setVisibility(View.VISIBLE);
            } else {
                holder.lnfollowing.setVisibility(View.VISIBLE);
                holder.lnfollow.setVisibility(View.GONE);
            }

        }

        holder.lnfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lnfollowing.setVisibility(View.VISIBLE);
                holder.lnfollow.setVisibility(View.GONE);
                if (!Onboarding3.dataToserver.contains(String.valueOf(data.getBrand_id()))) {
                    Onboarding3.dataToserver.add(String.valueOf(data.getBrand_id()));
                }
            }
        });

        holder.lnfollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.lnfollow.setVisibility(View.VISIBLE);
                holder.lnfollowing.setVisibility(View.GONE);
                if (Onboarding3.dataToserver.contains(String.valueOf(data.getBrand_id()))) {
                    Onboarding3.dataToserver.remove(String.valueOf(data.getBrand_id()));
                }
            }
        });



        return convertView;
    }





    public class MyViewHolder {
        public TextView tvselBrand;
        public RelativeLayout rlsel,rlseeClick,rlline;
        public LinearLayout lnseeAll, lnfollowing, lnfollow;


        public MyViewHolder(View item) {
            tvselBrand = (TextView) item.findViewById(R.id.tvselected);
            rlsel = (RelativeLayout) item.findViewById(R.id.rl);
            rlseeClick = (RelativeLayout) item.findViewById(R.id.rl1);
            rlline = (RelativeLayout) item.findViewById(R.id.rlline);
            lnseeAll = (LinearLayout) item.findViewById(R.id.seemoreLayout);
            lnfollowing = (LinearLayout) item.findViewById(R.id.followinglayout);
            lnfollow = (LinearLayout) item.findViewById(R.id.followlayout);
        }
    }
}