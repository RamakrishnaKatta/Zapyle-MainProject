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

import adapters.RecyclerViewGeneralAdapter;

import models.genericData;
import utils.ExternalFunctions;
import utils.FontUtils;
import utils.ItemDecorationAlbumColumns;




/**
 * Created by zapyle on 22/10/16.
 */

public class generalfragement extends Fragment {
    JSONArray objectData;
    RecyclerView rv;
    GridLayoutManager mGridLayoutManager;
    String heading,first,second;
    TextView tvhead,tv1,tv2;
    String strredirect="";
    @Override
    public void onDestroy() {
        super.onDestroy();
        System.out.println("banner destroyed custom");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        final Bundle bundle =  getArguments();


        View view=inflater.inflate(R.layout.fragment_general, container, false);
        rv=(RecyclerView) view.findViewById(R.id.main_recyclerview);
        tvhead=(TextView) view.findViewById(R.id.tv_heading);
        tv1=(TextView) view.findViewById(R.id.tv1);
        tv2=(TextView) view.findViewById(R.id.tv2);
        heading=bundle.getString("heading");
        strredirect=bundle.getString("redirect");
        first=bundle.getString("first");
        second=bundle.getString("second");

        if(heading.isEmpty()) {
            tvhead.setVisibility(View.GONE);
        }else{
            tvhead.setVisibility(View.VISIBLE);

            tvhead.setText(heading);
        }
        if(first.isEmpty()) {
           tv1.setVisibility(View.GONE);
        }else{
            tv1.setVisibility(View.VISIBLE);
            tv1.setPaintFlags(tv1.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            tv1.setText(first);
        }
        if(second.isEmpty()) {
            tv2.setVisibility(View.GONE);
        }else{
            tv2.setVisibility(View.VISIBLE);
            tv2.setPaintFlags(tv2.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);
            tv2.setText(second);
        }

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= null;
                try {
                    intent = new Intent(getActivity(), Class.forName("activity."+strredirect));
                    intent.putExtra("type", bundle.getString("type"));
                    intent.putExtra("ForwardUrl",bundle.getString("list-link"));
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

            }
        });

        tv2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=  ExternalFunctions.routeActivity(getActivity(),"filtered",bundle.getString("target"));
                startActivity(intent);
            }
        });

        if(bundle.getBoolean("showrecycle")){
            try {
                objectData= new JSONArray(bundle.getString("object"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            System.out.println("zzzznew"+objectData.toString());
            rv.setVisibility(View.VISIBLE);
            ArrayList<genericData> dataList = new ArrayList<genericData>();
            mGridLayoutManager = new GridLayoutManager(getActivity(), 2);
            for(int i=0;i<objectData.length();i++){
                try {
                    JSONObject obj=objectData.getJSONObject(i);
                    genericData genericData = new genericData();
                    System.out.println("zzzzznb0"+ EnvConstants.APP_MEDIA_URL + obj.getString("image"));
                    genericData.setImage(obj.getString("image"));
                    genericData.set_Target(obj.getString("target"));
                    dataList.add(genericData);

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
            RecyclerViewGeneralAdapter generic_adapter = new RecyclerViewGeneralAdapter(dataList,getActivity(), "filtered");
            rv.removeAllViews();
            rv.setAdapter(generic_adapter);                              // Setting the adapter to RecyclerView
            rv.setLayoutManager(mGridLayoutManager);
            rv.addItemDecoration(new ItemDecorationAlbumColumns(
                    getResources().getDimensionPixelSize(R.dimen.margin_10),
                    getResources().getInteger(R.integer.photo_list_preview_columns)));
        }else{
            rv.setVisibility(View.GONE);
        }
        FontUtils.setCustomFont(view, getActivity().getAssets());
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();


    }
}
