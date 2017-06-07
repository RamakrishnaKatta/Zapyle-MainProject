package adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.R;

import java.util.ArrayList;

import models.BrandModel;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;

/**
 * Created by haseeb on 12/12/16.
 */

public class MainBrandAdapter extends RecyclerView.Adapter<MainBrandAdapter.ViewHolder> {
    private static final int TYPE_LOAD_MORE = 0;
    private static final int TYPE_ITEM = 1;
    ArrayList<BrandModel> data;
    static Context context;

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textview_brand;
        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            FontUtils.setCustomFont(itemView, context.getAssets());
            textview_brand = (TextView) itemView.findViewById(R.id.textview_brand);

        }

    }


    public MainBrandAdapter(ArrayList<BrandModel> data, Context context) { // MyAdapter Constructor with titles and icons parameter
        this.data = data;
        this.context = context;

    }


    //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
    //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
    // if the viewType is TYPE_HEADER
    // and pass it to the view holder

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.brand_item_placeholder, parent, false); //Inflating the layout
        ViewHolder vhItem = new ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view

        return vhItem; // Returning the created object


    }

    //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
    // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
    // which view type is being created 1 for item row


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final BrandModel item = data.get(position);
        holder.textview_brand.setText(item.getName());
        holder.textview_brand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckConnectivity.isNetworkAvailable(context)) {

                    System.out.println("URLLLL : "+item.getTarget());

                    Intent intent=  ExternalFunctions.routeActivity(context,"filtered",item.getTarget());
                    context.startActivity(intent);


                } else {
                    CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                }
              //  System.out.println("Brand target selected : "+item.getTarget());
            }
        });
    }

    // This method returns the number of items present in the list
    @Override
    public int getItemCount() {
        return data.size();
    }

    @Override
    public int getItemViewType(int position) {
//        if (data.get(position) == null) {
//            ////System.out.println("inside load more viewtype grid");
//            return TYPE_LOAD_MORE;
//        } else {
//            ////System.out.println("inside item viewtype grid");
            return TYPE_ITEM;
//        }
    }

}