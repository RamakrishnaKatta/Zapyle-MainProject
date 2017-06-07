package fragments;


import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import adapters.RecyclerViewCollectionAdapter;
import adapters.RecyclerViewGeneralAdapter;
import models.genericData;
import models.productData;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.ItemDecorationAlbumColumns;


/**
 * Created by zapyle on 22/10/16.
 */

public class productcollectionfragement extends Fragment {
    JSONObject objectData;
    RecyclerView rv;
    GridLayoutManager mGridLayoutManager;
    String heading,first,second;
    TextView tvhead,tv1;
    String strredirect="";
    JSONObject contentData = null;
    JSONObject discoverData = null;

    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("banner destroyed custom");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        final Bundle bundle = getArguments();


        View view = inflater.inflate(R.layout.fragment_product, container, false);
        rv = (RecyclerView) view.findViewById(R.id.main_recyclerview);
        tvhead = (TextView) view.findViewById(R.id.tv_heading);
        tv1 = (TextView) view.findViewById(R.id.tv1);
       // heading=bundle.getString("heading");
//        strredirect=bundle.getString("redirect");
//        first=bundle.getString("first");
//        second=bundle.getString("second");
//
//        if(heading.isEmpty()) {
//            tvhead.setVisibility(View.GONE);
//        }else{
//            tvhead.setVisibility(View.VISIBLE);
//
//            tvhead.setText(heading);
//        }
//        if(first.isEmpty()) {
//           tv1.setVisibility(View.GONE);
//        }else{
//            tv1.setVisibility(View.VISIBLE);
//            tv1.setPaintFlags(tv1.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
//            tv1.setText(first);
//        }
//        if(second.isEmpty()) {
//            tv2.setVisibility(View.GONE);
//        }else{
//            tv2.setVisibility(View.VISIBLE);
//            tv2.setPaintFlags(tv2.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
//            tv2.setText(second);
//        }

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= null;
                try {
                    intent = new Intent(getActivity(), Class.forName("activity.filtered"));
                    try {
                        intent.putExtra("ForwardUrl",discoverData.getString("target"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });
//


        try {
            objectData = new JSONObject(bundle.getString("object"));
            contentData = objectData.getJSONObject("content_data");
            discoverData = contentData.getJSONObject("discover_data");
            heading=discoverData.getString("title");
            String cta_text=discoverData.getString("cta_text");


            if(cta_text.isEmpty()) {
                tv1.setVisibility(View.GONE);
            }else{
                tv1.setVisibility(View.VISIBLE);

                tv1.setText(cta_text);
            }
        if(heading.isEmpty()) {
            tvhead.setVisibility(View.GONE);
        }else{
            tvhead.setVisibility(View.VISIBLE);

            tvhead.setText(heading);
        }
        } catch (JSONException e) {
            e.printStackTrace();
        }


        System.out.println("zzzznew" + objectData.toString());
        rv.setVisibility(View.VISIBLE);
        ArrayList<productData> dataList = new ArrayList<productData>();
        mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
        try {
            for (int i = 0; i < discoverData.getJSONArray("product").length(); i++) {
                try {

                    productData productData = new productData();
                    productData.setId(discoverData.getJSONArray("product").getJSONObject(i).getString("id"));
                    productData.setImage(discoverData.getJSONArray("product").getJSONObject(i).getString("image"));
                    productData.setproductname(discoverData.getJSONArray("product").getJSONObject(i).getString("title"));
                    productData.setBrandname(discoverData.getJSONArray("product").getJSONObject(i).getString("brand"));
                    productData.setLprice(discoverData.getJSONArray("product").getJSONObject(i).getString("listing_price"));
                    productData.setOprice(discoverData.getJSONArray("product").getJSONObject(i).getString("original_price"));
                    dataList.add(productData);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        RecyclerViewCollectionAdapter generic_adapter = new RecyclerViewCollectionAdapter(dataList, getActivity(), "filtered");
        rv.removeAllViews();
        rv.setAdapter(generic_adapter);                              // Setting the adapter to RecyclerView
        rv.setLayoutManager(mGridLayoutManager);
        rv.addItemDecoration(new ItemDecorationAlbumColumns(
                getResources().getDimensionPixelSize(R.dimen.margin_10),
                getResources().getInteger(R.integer.photo_list_preview_columns)));

//        }else{
//            rv.setVisibility(View.GONE);
//        }
        FontUtils.setCustomFont(view, getActivity().getAssets());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }
}
