package adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zapyle.zapyle.R;

import java.util.ArrayList;

import activity.Onboarding2;
import activity.Onboarding3;
import models.BrandListmodel;

/**
 * Created by haseeb on 14/1/16.
 */
public class BrandAdapterSkip extends BaseAdapter {
    ArrayList<BrandListmodel> strBranList = new ArrayList<BrandListmodel>();
    Context context;
    // String [] strUnselected;
    private static LayoutInflater inflater = null;
    private int intshowsel, intselcount, intunselcount;

    public BrandAdapterSkip(Context mainactivity, ArrayList<BrandListmodel> strBrandList, int intselcount, int intUnselcount, int intshowsel) {
        // TODO Auto-generated constructor stub
        this.strBranList = strBrandList;
        this.context = mainactivity;
        this.intshowsel = intshowsel;
        this.intselcount = intselcount;
        this.intunselcount = intUnselcount;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
       if( Onboarding2.isskip){
            Onboarding3.dataToserver.clear();
        }

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
            convertView = inflater.inflate(R.layout.branditemskip, null);
            holder = new MyViewHolder(convertView);

            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }

        final BrandListmodel data = getItem(position);
//         if (Onboarding2.isskip==true){
//             //////System.out.println("dddddddddddddddddd"+Onboarding2.isskip);
//             intshowsel=0;
//         }

        if(!Onboarding3.seemoreStatus) {

            //////System.out.println("datatoserver"+Onboarding3.dataToserver.size());

            //////System.out.println("datatoserver1"+String.valueOf(data.getBrand_id()));

                holder.tvselBrand.setText(data.getBrandname());
                if(!Onboarding3.dataToserver.contains(String.valueOf(data.getBrand_id()))){
                    holder.lnfollowing.setVisibility(View.GONE);
                    holder.lnfollow.setVisibility(View.VISIBLE);
                }
                else{
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
                else if(Onboarding3.dataToserver.size()<=0){
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
                else if(Onboarding3.dataToserver.size()<=0){
                    Onboarding3.dataToserver.add(String.valueOf(data.getBrand_id()));
                }
            }
        });



        return convertView;
    }





    public class MyViewHolder {
        public TextView tvselBrand,txline;
        public RelativeLayout rlsel,rlseeClick,rlline;
        public LinearLayout lnseeAll, lnfollowing, lnfollow;


        public MyViewHolder(View item) {
            tvselBrand = (TextView) item.findViewById(R.id.tvselected);
            txline = (TextView) item.findViewById(R.id.textView10);
            rlsel = (RelativeLayout) item.findViewById(R.id.rl);
            rlline = (RelativeLayout) item.findViewById(R.id.rlline);
            lnfollowing = (LinearLayout) item.findViewById(R.id.followinglayout);
            lnfollow = (LinearLayout) item.findViewById(R.id.followlayout);
        }
    }
}