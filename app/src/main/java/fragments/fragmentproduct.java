package fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import activity.product;
import activity.searchFeedPage;
import activity.searchnew;
import utils.ExternalFunctions;
import utils.GetSharedValues;

public class fragmentproduct extends Fragment {
    private static final String SCREEN_NAME = "product";
    Context ctx;
    ListView lvsug,lvprod;

    LinearLayout l1,l3;

    String strPrevMax;
    int check=0;
    int check1=0;

    private String[] product_listing_price;
    private int[] product_id;
    private String[] product_original_price;
    private String[] product_image_url;
    private String[] product_name;
    private boolean []blsoldout;
    private boolean []blstyle;
    private String[] product_description;
    private int[] product_user_id;

    public static  CustomAdapter adapt;
    public static  CustomAdapter1 adapt1;
    public static ProgressDialog pd1;
    public JSONArray Category=new JSONArray();
    public JSONArray Style=new JSONArray();
    public JSONArray Color=new JSONArray();
    public JSONArray Brand=new JSONArray();
    public JSONArray Occasion=new JSONArray();
    public JSONArray SubCategory=new JSONArray();
    private ArrayList<String> product_suggestion = new ArrayList<String>();

    JSONArray filter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        ctx=fragmentproduct.this.getActivity();
        View v = inflater.inflate(R.layout.searchproduct, null);
        lvprod = (ListView) v.findViewById(R.id.ListViewproduct);
        lvsug = (ListView) v.findViewById(R.id.listview1);
        l3=(LinearLayout)v.findViewById(R.id.l3);
        l3.setVisibility(View.VISIBLE);
        ((searchnew)getActivity()).setFragmentRefreshListener(new searchnew.FragmentRefreshListener() {
            @Override
            public void onRefresh() {
                if( ExternalFunctions.blsearch){
                    //System.out.println("inside getsearchq0");
                    lvprod.setVisibility(View.GONE);
                    lvsug.setVisibility(View.GONE);
                    adapt=null;
                    adapt1=null;
                    GetSearch();

                }
            }
        });
        // GetFilter();

        return v;


    }
    public static class Utility {
        public static void setListViewHeightBasedOnChildren(ListView listView) {
            ListAdapter listAdapter = listView.getAdapter();
            if (listAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            for (int i = 0; i < listAdapter.getCount(); i++) {
                try {
                    View listItem = listAdapter.getView(i, null, listView);
                    listItem.measure(0, 0);
                    totalHeight += listItem.getMeasuredHeight();
                }catch (Exception e){

                }
            }

            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
    }
    public static void hideKeyboard(Context ctx) {
        InputMethodManager inputManager = (InputMethodManager) ctx
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        // check if no view has focus:
        View v = ((Activity) ctx).getCurrentFocus();
        if (v == null)
            return;

        inputManager.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser){
        super.setUserVisibleHint(isVisibleToUser);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if( ExternalFunctions.blsearch) {
            lvprod.setVisibility(View.GONE);
            lvsug.setVisibility(View.GONE);
            adapt = null;
            adapt1 = null;
            GetSearch();
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
        int count1 = lvprod.getChildCount();
        for (int i = 0; i < count1; i++) {
            ImageView rv = (ImageView) lvprod.getChildAt(i).findViewById(R.id.imgproduct);

            if (rv != null) {
                if (rv.getDrawable() != null)
                    rv.getDrawable().setCallback(null);
            }

        }
        lvprod.setVisibility(View.GONE);
        lvsug.setVisibility(View.GONE);
    }
//
//    @Override
//    public void onResume() {
//        //System.out.println("inside getsearchq0");
//        if( ExternalFunctions.blsearch) {
//            lvprod.setVisibility(View.GONE);
//            lvsug.setVisibility(View.GONE);
//            adapt = null;
//            adapt1 = null;
//            GetSearch();
//        }
//
//        super.onResume();
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ExternalFunctions.datasearch=null;

        int count1 = lvprod.getChildCount();
        for (int i = 0; i < count1; i++) {
            ImageView rv = (ImageView) lvprod.getChildAt(i).findViewById(R.id.imgproduct);

            if (rv != null) {
                if (rv.getDrawable() != null)
                    rv.getDrawable().setCallback(null);
            }

        }
        lvprod.setVisibility(View.GONE);
        lvsug.setVisibility(View.GONE);

    }

    private void GetSearch() {
        int intcheck=0;
        //System.out.println("inside getsearchq");
        ExternalFunctions.blsearch=false;
        if( ExternalFunctions.datasearch!=null) {
            try {
                final JSONArray product = ExternalFunctions.datasearch.getJSONArray("products");
                filter = ExternalFunctions.datasearch.getJSONArray("filters");
                try {
                    if (filter.length() > 0) {
                        intcheck = 1;
                        lvsug.setVisibility(View.VISIBLE);
                        product_suggestion = new ArrayList<String>();
                        for (int i = 0; i < filter.length(); i++) {
                            JSONObject filter_obj = filter.getJSONObject(i).getJSONObject("filter");
                            Category = filter_obj.getJSONArray("Category");
                            Style = filter_obj.getJSONArray("Style");
                            Color = filter_obj.getJSONArray("Color");
                            Brand = filter_obj.getJSONArray("Brand");
                            Occasion = filter_obj.getJSONArray("Occasion");
                            SubCategory = filter_obj.getJSONArray("SubCategory");
                            product_suggestion.add(filter_obj.getString("string"));
                        }
                        adapt = new CustomAdapter(product_suggestion);
                        lvsug.setAdapter(adapt);
                        adapt.notifyDataSetChanged();
                        Utility.setListViewHeightBasedOnChildren(lvsug);
                    } else {
                        //System.out.println("inside getsearchq0=" + intcheck);
                        adapt = null;
                        lvsug.setAdapter(adapt);
                        //adapt.notifyDataSetChanged();
                        //  lvsug.setVisibility(View.GONE);
                    }
                } catch (Exception e) {


                }
                if (product.length() > 0) {

                    lvprod.setVisibility(View.VISIBLE);
                    product_id = new int[product.length()];
                    product_description = new String[product.length()];
                    product_listing_price = new String[product.length()];
                    product_original_price = new String[product.length()];
                    product_user_id = new int[product.length()];
                    product_name = new String[product.length()];
                    blsoldout = new boolean[product.length()];
                    blstyle = new boolean[product.length()];
                    product_image_url = new String[product.length()];

                    for (int i = 0; i < product.length(); i++) {
                        product_id[i] = product.getJSONObject(i).getInt("product_id");
                        product_description[i] = product.getJSONObject(i).getString("description_excerpt");
                        product_listing_price[i] = product.getJSONObject(i).getString("product_listing_price");
                        product_original_price[i] = product.getJSONObject(i).getString("product_original_price");
                        product_name[i] = product.getJSONObject(i).getString("product_name");
                        product_user_id[i] = product.getJSONObject(i).getInt("product_user_id");
                        product_image_url[i] = product.getJSONObject(i).getString("product_image_url");
                        blsoldout[i] = product.getJSONObject(i).getBoolean("sold_out");
                        blstyle[i] = product.getJSONObject(i).getBoolean("style_inspiration");
                        //System.out.println("bbb" + product_description[i]);
                    }
                    intcheck = 1;
                    l3.setVisibility(View.GONE);
                    adapt1 = new CustomAdapter1(product_description);
                    lvprod.setAdapter(adapt1);
                    adapt1.notifyDataSetChanged();
                    Utility.setListViewHeightBasedOnChildren(lvprod);
                } else {
                    //System.out.println("inside getsearch ivide=" + intcheck);

                    if (intcheck == 0) {
                        //System.out.println("inside getsearch ivide=" + intcheck);
                        l3.setVisibility(View.VISIBLE);
                    } else {
                        l3.setVisibility(View.GONE);
                    }
                    adapt1 = null;
                    lvprod.setAdapter(adapt1);
//                adapt1.notifyDataSetChanged();

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }




    public class CustomAdapter extends BaseAdapter {
        ArrayList <String> result;
        Context context;

        private  LayoutInflater inflater=null;
        public CustomAdapter( ArrayList<String> prgmNameList) {
            // TODO Auto-generated constructor stub
            result=prgmNameList;
            context=fragmentproduct.this.getActivity();

            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return result.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder
        {
            TextView tv;
            ImageView imgadd;

        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final Holder holder=new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.prodsuggestion, null);
            holder.tv=(TextView) rowView.findViewById(R.id.tvsug);
            holder.imgadd=(ImageView) rowView.findViewById(R.id.imageadd);
            holder.tv.setText(result.get(position));
            ////System.out.println("staging" + response);
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    try {

                        searchnew.objMainList=filter.getJSONObject(position).getJSONObject("filter");
                        ExternalFunctions.jsonObjsearch .put("filter",  searchnew.objMainList);
                        // searchnew.objMainList.put("string", result.get(position));
                        ExternalFunctions.jsonObjsearch .put("query_string", "");
                        ExternalFunctions.blapplysearch=true;
                        ExternalFunctions.blapplysug=false;
                        ExternalFunctions.strsearch=result.get(position);
                        Intent feed = new Intent(context, searchFeedPage.class);
                        feed.putExtra("activity", "SplashScreen");
                        Runtime.getRuntime().gc();
                        startActivity(feed);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            });
            holder.imgadd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        searchnew.objMainList=filter.getJSONObject(position).getJSONObject("filter");

                        ExternalFunctions.jsonObjsearch .put("filter",  searchnew.objMainList);
                        ExternalFunctions.jsonObjsearch .put("query_string", "");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    // searchnew.edtsearch.setText( "");
                    searchnew.edtsearch.setText( holder.tv.getText());
                    searchnew.edtsearch.setSelection(searchnew.edtsearch.length());
                    ExternalFunctions.selsugest=holder.tv.getText().toString();
                    result.remove(position);
                    adapt.notifyDataSetChanged();
                    Utility.setListViewHeightBasedOnChildren(lvsug);

                }
            });

            return rowView;
        }

    }

    public class CustomAdapter1 extends BaseAdapter {
        String [] result;
        Context context;

        private  LayoutInflater inflater=null;
        public CustomAdapter1( String[] prgmNameList) {

            //System.out.println("hsd"+prgmNameList[0]);
            // TODO Auto-generated constructor stub
            result=prgmNameList;
            //System.out.println("hsd11"+result.length);
            context=fragmentproduct.this.getActivity();

            inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return result.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        public class Holder
        {
            ImageView imgproduct;
            TextView tvname,tvdesc,tvprice,tvofferprice;


        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //System.out.println("hsd11");
            final Holder holder=new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.searchproduct_list, null);
            holder.imgproduct=(ImageView) rowView.findViewById(R.id.imgproduct);
            holder.tvname=(TextView) rowView.findViewById(R.id.tvpname);
            holder.tvdesc=(TextView) rowView.findViewById(R.id.tvdescription);
            holder.tvprice=(TextView) rowView.findViewById(R.id.tvamount);
            holder.tvofferprice=(TextView) rowView.findViewById(R.id.tvofferprice);
            //System.out.println("hshs" + product_id[position]);
            holder.tvname.setText(product_name[position].toUpperCase());
            holder.tvdesc.setText(product_description[position]);
            if (blsoldout[position]){
                holder.tvprice.setText("Sold Out");

            }else {
                if (blstyle[position]){
                    holder.tvprice.setText("StyleInspiration");
                }else {
                    holder.tvprice.setText(getResources().getString(R.string.Rs) + product_listing_price[position]);
                    if (Integer.parseInt(product_original_price[position]) > Integer.parseInt(product_listing_price[position])) {
                        holder.tvofferprice.setVisibility(View.VISIBLE);
                        holder.tvofferprice.setText(getResources().getString(R.string.Rs) + product_original_price[position]);
                        holder.tvofferprice.setPaintFlags(holder.tvofferprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                    }
                    else {
                        holder.tvofferprice.setVisibility(View.GONE);
                    }
                }
            }


            Glide.with(fragmentproduct.this.getActivity())
                    .load(EnvConstants.APP_MEDIA_URL + product_image_url[position])
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.imgproduct);


            rowView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExternalFunctions.blapplysug=true;
                    ExternalFunctions.blapplysearch=false;
                    if (product_user_id[position] == GetSharedValues.getuserId(context)) {
                        Intent productPage = new Intent(context, product.class);
                        productPage.putExtra("album_id", product_id[position]);
                        productPage.putExtra("sale", true);
                        productPage.putExtra("pta", false);
                        productPage.putExtra("activity","searchnew");
                        productPage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        Runtime.getRuntime().gc();
                        context.startActivity(productPage);
                        // ((Activity) context).finish();
                    } else {

                        Intent productPage = new Intent(context, product.class);
                        productPage.putExtra("album_id", product_id[position]);
                        productPage.putExtra("pta", false);
                        productPage.putExtra("activity","searchnew");
                        productPage.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        Runtime.getRuntime().gc();
                        context.startActivity(productPage);
//                            ((Activity) context).finish();
                    }
                }
            });
            return rowView;
        }

    }
}