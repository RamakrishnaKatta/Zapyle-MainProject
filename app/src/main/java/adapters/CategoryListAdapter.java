package adapters;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brandongogetap.stickyheaders.exposed.StickyHeader;
import com.brandongogetap.stickyheaders.exposed.StickyHeaderHandler;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.R;

import java.util.ArrayList;
import java.util.List;

import models.category_list_model;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.FontUtils;

import static com.zapyle.zapyle.R.id.tv;

/**
 * Created by haseeb on 9/12/16.
 */

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.ViewHolder> implements StickyHeaderHandler {

    private static final int TYPE_ITEM = 1;
    ArrayList<category_list_model> category_data = new ArrayList<category_list_model>();
    static Context context;
    LayoutInflater inflate;


    public CategoryListAdapter(ArrayList<category_list_model> category_data, Context context) { // MyAdapter Constructor with titles and icons parameter
        this.category_data = category_data;
        this.context = context;
    }

    @Override
    public List<?> getAdapterData() {
        return category_data;
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView category_text, subcategory_text;


        public ViewHolder(View itemView, int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
            super(itemView);
            FontUtils.setCustomFont(itemView, context.getAssets());
            category_text = (TextView) itemView.findViewById(R.id.category_text);
//            subcategory_text = (TextView) itemView.findViewById(R.id.subcategory_text);
        }

    }


    @Override
    public CategoryListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_list_item, parent, false); //Inflating the layout
        CategoryListAdapter.ViewHolder vhItem = new CategoryListAdapter.ViewHolder(v, viewType); //Creating ViewHolder and passing the object of type view
        return vhItem;

    }


    @Override
    public void onBindViewHolder(CategoryListAdapter.ViewHolder holder, int position) {
        final category_list_model category = category_data.get(position);

        holder.category_text.setText(category.getName());
        if (category instanceof StickyHeader) {
            holder.itemView.setBackgroundColor(Color.parseColor("#FAFAFA"));
            holder.category_text.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.category_text.setTypeface(Typeface.defaultFromStyle(Typeface.NORMAL));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckConnectivity.isNetworkAvailable(context)) {

                    System.out.println("URLLLL : "+category.getTarget());

                    Intent intent=  ExternalFunctions.routeActivity(context,"filtered",category.getTarget());
                    context.startActivity(intent);


                } else {
                    CustomMessage.getInstance().CustomMessage(context, "Internet is not available!");
                }
                //System.out.println(category.getTarget());
            }
        });

    }

    @Override
    public int getItemCount() {
        return category_data.size();
    }


    @Override
    public int getItemViewType(int position) {
        return TYPE_ITEM;
    }
}
