package activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.clevertap.android.sdk.CleverTapAPI;
import com.clevertap.android.sdk.exceptions.CleverTapMetaDataNotFoundException;
import com.clevertap.android.sdk.exceptions.CleverTapPermissionsNotSatisfied;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.Calendar;
import java.util.HashMap;

import fragments.listfragment;
import fragments.pricefragment;
import fragments.sublistfragment;
import network.ApiCommunication;
import network.ApiService;
import utils.ExternalFunctions;

public class filter_activity extends AppCompatActivity implements ApiCommunication {
    public String SCREEN_NAME = "REFINE";
    ListView lvfiltertype;
    FilterTypeAdapter adapter;
    boolean[] blselect;
    int intselectedposition = 0;

    TextView tvcancel, tvclear, tvapply;
    String strfvalue, strftype, strgtype, filtercancel;
    int pos;
    boolean blclear = false;
    String activity;
    String stime = "";
    String endtime = "";
    CleverTapAPI cleverTap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_filter);
        overridePendingTransition(R.anim.activity_open_translate, R.anim.activity_close_scale);
        System.out.println("xxxx" + ExternalFunctions.strfilter);

        lvfiltertype = (ListView) findViewById(R.id.lvfiltertype);
        tvcancel = (TextView) findViewById(R.id.tvcancel);
        tvclear = (TextView) findViewById(R.id.tvclear);
        tvapply = (TextView) findViewById(R.id.tvapply);
        filtercancel = ExternalFunctions.strfilter;
        try {
            activity = "activity." + getIntent().getStringExtra("activity");
        } catch (Exception e) {
            activity = "";
        }
        stime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());

        try {
            cleverTap = CleverTapAPI.getInstance(getApplicationContext());
        } catch (CleverTapMetaDataNotFoundException e) {
            // thrown if you haven't specified your CleverTap Account ID or Token in your AndroidManifest.xml
        } catch (CleverTapPermissionsNotSatisfied e) {
            // thrown if you haven’t requested the required permissions in your AndroidManifest.xml
        }
//        System.out.println("urlzzz"+ExternalFunctions.DiffParam);

        GetFilter();
        tvcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvcancel.setBackgroundColor(Color.parseColor("#ff7477"));
                tvclear.setBackgroundColor(Color.parseColor("#f5f5f5"));
                tvapply.setBackgroundColor(Color.parseColor("#f5f5f5"));
                tvcancel.setTextColor(Color.parseColor("#ffffff"));
                tvclear.setTextColor(Color.parseColor("#9b9b9b"));
                tvapply.setTextColor(Color.parseColor("#9b9b9b"));

                //                    FilterStatus = true;
//                    FilterStatus = false;
                ExternalFunctions.blfiteropen = ExternalFunctions.strfilter.length() > 1;

                ExternalFunctions.strFilterdata = null;

                ExternalFunctions.strfilter = filtercancel;
                System.out.println("onactivityresultmm" + ExternalFunctions.strfilter);
                if (!activity.contains("null")) {
                    if (ExternalFunctions.strfilter.length() > 1) {
                        ExternalFunctions.FilterStatus = true;
                    }
                    Intent newintent = new Intent(getApplicationContext(), filtered.class);
                    newintent.putExtra("ForwardUrl", ExternalFunctions.FilteredUrl);
                    startActivity(newintent);
                    finish();
                } else {
                    if (ExternalFunctions.strfilter.length() > 1) {
                        ExternalFunctions.FilterStatus = true;
                    }
                    Intent newintent = new Intent(getApplicationContext(), MainFeed.class);
                    startActivity(newintent);
                    finish();
                }
            }
        });
        tvclear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvclear.setBackgroundColor(Color.parseColor("#ff7477"));
                tvcancel.setBackgroundColor(Color.parseColor("#f5f5f5"));
                tvapply.setBackgroundColor(Color.parseColor("#f5f5f5"));
                tvclear.setTextColor(Color.parseColor("#ffffff"));
                tvcancel.setTextColor(Color.parseColor("#9b9b9b"));
                tvapply.setTextColor(Color.parseColor("#9b9b9b"));
                blclear = true;

                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction transaction = fm.beginTransaction();

                ExternalFunctions.strfilter = "";
                for (int i = 0; i < ExternalFunctions.strFilterdata.length; i++) {
                    ExternalFunctions.strFilterdata[i] = "";
                }
                adapter.notifyDataSetChanged();
                if (strgtype.equals("2")) {

                    sublistfragment sb = new sublistfragment();
                    Bundle b = new Bundle();
                    b.putString("filtertype", strftype);
                    b.putString("filtervalue", strfvalue);
                    b.putInt("pos", pos);
                    sb.setArguments(b);
                    transaction.replace(R.id.lnfragement, sb);
                } else if (strgtype.equals("1")) {

                    listfragment sb = new listfragment();
                    Bundle b = new Bundle();
                    b.putString("filtertype", strftype);
                    b.putString("filtervalue", strfvalue);
                    b.putInt("pos", pos);
                    sb.setArguments(b);
                    transaction.replace(R.id.lnfragement, sb);

                } else if (strgtype.equals("0")) {

                    pricefragment sb = new pricefragment();
                    Bundle b = new Bundle();
                    b.putString("filtertype", strftype);
                    b.putString("filtervalue", strfvalue);
                    b.putInt("pos", pos);
                    sb.setArguments(b);
                    transaction.replace(R.id.lnfragement, sb);

                }

                transaction.commit();

            }
        });

        tvapply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvapply.setBackgroundColor(Color.parseColor("#ff7477"));
                tvcancel.setBackgroundColor(Color.parseColor("#f5f5f5"));
                tvclear.setBackgroundColor(Color.parseColor("#f5f5f5"));
                tvapply.setTextColor(Color.parseColor("#ffffff"));
                tvcancel.setTextColor(Color.parseColor("#9b9b9b"));
                tvclear.setTextColor(Color.parseColor("#9b9b9b"));
                boolean blcheck = false;
                for (int i = 0; i < ExternalFunctions.strFilterdata.length; i++) {
                    try {
                        if (!ExternalFunctions.strFilterdata[i].equals(null)) {
                            blcheck = true;
                        }
                    } catch (Exception e) {

                    }
                }
                if (blcheck)

                 //   System.out.println("onactivityresult--" + ExternalFunctions.strFilterdata.toString());
                    ExternalFunctions.strfilter = makeFilter(ExternalFunctions.strFilterdata);
               // System.out.println("onactivityresult" + ExternalFunctions.strfilter);
                ExternalFunctions.FilterStatus = ExternalFunctions.strfilter.length() > 1;

                ExternalFunctions.ActivityParam = "base";
              //  System.out.println("qax" + activity);
                if (!activity.contains("null")) {
                    Intent newintent = new Intent(getApplicationContext(), filtered.class);
                    newintent.putExtra("ForwardUrl", ExternalFunctions.strfilter);
                    startActivity(newintent);
                    finish();
                } else {
                    Intent newintent = new Intent(getApplicationContext(), MainFeed.class);
                    startActivity(newintent);
                    finish();
                }

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        endtime = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        HashMap<String, Object> page_change = new HashMap<String, Object>();
        page_change.put("new_page", SCREEN_NAME);
        page_change.put("old_page", ExternalFunctions.prevActivity);
        page_change.put("page_view_starttime", stime);
        page_change.put("page_view_endtime", endtime);
        cleverTap.event.push("page_change", page_change);
        ExternalFunctions.prevActivity = SCREEN_NAME;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        //                    FilterStatus = true;
//                    FilterStatus = false;
        ExternalFunctions.blfiteropen = ExternalFunctions.strfilter.length() > 1;

        ExternalFunctions.strFilterdata = null;

        ExternalFunctions.strfilter = filtercancel;
        if (!activity.contains("null")) {
            Intent newintent = new Intent(getApplicationContext(), filtered.class);
            newintent.putExtra("ForwardUrl", ExternalFunctions.FilteredUrl);
            startActivity(newintent);
            finish();
        } else {
            Intent newintent = new Intent(getApplicationContext(), MainFeed.class);
            startActivity(newintent);
            finish();
        }
    }

    public String makeFilter(String[] str) {
        String strresult = "=";
        HashMap<String, Object> filter  = new HashMap<String, Object>();
        filter.put("invite_channel", "");
                try {
            for (int i = 0; i < str.length; i++) {
                try {
                  //  System.out.println("xxx" + str[i]);

                    if (str[i].length() > 1) {
                        String[] a = str[i].split("=");
                        filter.put(a[0], a[1]);
                        strresult = strresult + "&" + str[i];
                    }
                } catch (Exception e) {

                }
            }
            strresult = strresult.replace("=&", "");
            if (strresult.equals("=")) {
                strresult = "";
            }
        } catch ( IllegalStateException e){
                    strresult = "";
                } catch(Exception e){
            strresult = "";
        }
        cleverTap.event.push("filter ", filter );

        return strresult.replace("=&", "");

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
        if (resp != null) {
            try {
                System.out.println("ooo" + resp);

                String status = resp.getString("status");
                if (status.equals("success")) {
                    JSONObject data = resp.getJSONObject("data");
                    final JSONArray filtertype = data.getJSONArray("filter_types");
                    if (ExternalFunctions.strFilterdata == null || filtertype.length() > ExternalFunctions.strFilterdata.length) {
                        ExternalFunctions.strFilterdata = new String[filtertype.length()];
                    }
                    blselect = new boolean[filtertype.length()];
                    adapter = new FilterTypeAdapter(getApplicationContext(), filtertype);
                    lvfiltertype.setAdapter(adapter);
                    // lvfiltertype.setAdapter(adapter);
                    strgtype = filtertype.getJSONObject(0).getString("grid_type");
                    strftype = filtertype.getJSONObject(0).getString("title");
                    strfvalue = filtertype.getJSONObject(0).getString("value");
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();

                    if (strgtype.equals("2")) {
                        blclear = false;
                        sublistfragment sb = new sublistfragment();
                        Bundle b = new Bundle();
                        b.putString("filtertype", strftype);
                        b.putString("filtervalue", strfvalue);
                        b.putInt("pos", 0);
                        sb.setArguments(b);
                        transaction.replace(R.id.lnfragement, sb);
                    } else if (strgtype.equals("1")) {
                        blclear = false;
                        listfragment sb = new listfragment();
                        Bundle b = new Bundle();
                        b.putString("filtertype", strftype);
                        b.putString("filtervalue", strfvalue);
                        b.putInt("pos", 0);
                        sb.setArguments(b);
                        transaction.replace(R.id.lnfragement, sb);

                    } else if (strgtype.equals("0")) {
                        blclear = false;
                        pricefragment sb = new pricefragment();
                        Bundle b = new Bundle();
                        b.putString("filtertype", strftype);
                        b.putString("filtervalue", strfvalue);
                        b.putInt("pos", 0);
                        sb.setArguments(b);
                        transaction.replace(R.id.lnfragement, sb);

                    }

                    transaction.commit();

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

    private void GetFilter() {
        //System.out.println(EnvConstants.APP_BASE_URL + "/filters/getFilters/product_category/");
//        if(ExternalFunctions.strfilter.length()>0)
//        ApiService.getInstance(filter_activity.this, 1).getData(this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/filters/getFilters/product_category/"+ExternalFunctions.strfilter+"/?version=2", "getfilter");
//        else
        ApiService.getInstance(filter_activity.this, 1).getData(this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/filters/getFilters/product_category/?version=2", "getfilter");
    }

//    @Override
//    public void passDataToActivity(int index, String filterValue) {
//        if(!blclear) {
//            if (filterValue.length() > 1) {
//                strFilterdata[index] = filterValue.replace("*", "").replace("=,", "=");
//            } else {
//                strFilterdata[index] = "";
//            }
//
//            ExternalFunctions.strfilter = makeFilter(strFilterdata);
//
//        }else{
//            ExternalFunctions.strfilter="";
//        }
//        //Toast.makeText(getApplicationContext(),ExternalFunctions.strfilter,Toast.LENGTH_LONG).show();
//    }

    //    public String makeFilter(String[] str){
//        String strresult="=";
//        for(int i=0;i<str.length;i++){
//            try {
//                if (str[i].length() > 1)
//                    strresult = strresult + "&" + str[i];
//            }catch (Exception e){
//
//            }
//        }
//        strresult= strresult.replace("=&","");
//        if(strresult.equals("=")){
//            strresult="";
//        }
//        return strresult.replace("=&","");
//    }
    public class FilterTypeAdapter extends BaseAdapter {
        JSONArray jsonfilter;
        Context context;

        private LayoutInflater inflater = null;

        public FilterTypeAdapter(Context context, JSONArray jsonfilter) {
            // TODO Auto-generated constructor stub
            this.jsonfilter = jsonfilter;
            this.context = context;
            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return jsonfilter.length();
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

        public class Holder {
            TextView tv;
            RelativeLayout rlfilter;


        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.filter_type_item, null);
            holder.tv = (TextView) rowView.findViewById(R.id.txt_filter_type);
            holder.rlfilter = (RelativeLayout) rowView.findViewById(R.id.rlfitertype);
            try {
                holder.tv.setText(jsonfilter.getJSONObject(position).getString("title"));
                holder.tv.setTag(jsonfilter.getJSONObject(position).getString("value"));
                holder.rlfilter.setTag(jsonfilter.getJSONObject(position).getString("grid_type"));


                if (ExternalFunctions.strfilter.contains(jsonfilter.getJSONObject(position).getString("value").toString())) {
                    holder.tv.setTextColor(Color.parseColor("#ff7477"));
                } else {
                    holder.tv.setTextColor(Color.GRAY);

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (intselectedposition == position) {
                holder.rlfilter.setBackgroundColor(Color.BLACK);
                holder.tv.setTextColor(Color.WHITE);

//               // lvfiltertype.setAdapter(adapter);
//                strgtype=holder.rlfilter.getTag().toString();
//                strftype= holder.tv.getText().toString();
//                strfvalue=holder.tv.getTag().toString();
//                FragmentManager fm=getSupportFragmentManager();
//                FragmentTransaction transaction=fm.beginTransaction();
//
//                if(holder.rlfilter.getTag().toString().equals("2")){
//                    blclear=false;
//                    sublistfragment sb=new sublistfragment();
//                    Bundle b = new Bundle();
//                    b.putString("filtertype",  holder.tv.getText().toString());
//                    b.putString("filtervalue", holder.tv.getTag().toString());
//                    b.putInt("pos", position);
//                    sb.setArguments(b);
//                    transaction.replace(R.id.lnfragement, sb);
//                }
//                else  if(holder.rlfilter.getTag().toString().equals("1")) {
//                    blclear=false;
//                    listfragment sb=new listfragment();
//                    Bundle b = new Bundle();
//                    b.putString("filtertype",  holder.tv.getText().toString());
//                    b.putString("filtervalue", holder.tv.getTag().toString());
//                    b.putInt("pos", position);
//                    sb.setArguments(b);
//                    transaction.replace(R.id.lnfragement, sb);
//
//                }else  if(holder.rlfilter.getTag().toString().equals("0")) {
//                    blclear=false;
//                    pricefragment sb=new pricefragment();
//                    Bundle b = new Bundle();
//                    b.putString("filtertype",  holder.tv.getText().toString());
//                    b.putString("filtervalue", holder.tv.getTag().toString());
//                    b.putInt("pos", position);
//                    sb.setArguments(b);
//                    transaction.replace(R.id.lnfragement, sb);
//
//                }
//
//                transaction.commit();

            }


            holder.rlfilter.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blclear = false;
                    holder.rlfilter.setBackgroundColor(Color.BLACK);
                    holder.tv.setTextColor(Color.WHITE);

                    intselectedposition = position;
                    lvfiltertype.setAdapter(adapter);
                    strgtype = holder.rlfilter.getTag().toString();
                    strftype = holder.tv.getText().toString();
                    strfvalue = holder.tv.getTag().toString();
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction transaction = fm.beginTransaction();

                    if (holder.rlfilter.getTag().toString().equals("2")) {
                        blclear = false;
                        sublistfragment sb = new sublistfragment();
                        Bundle b = new Bundle();
                        b.putString("filtertype", holder.tv.getText().toString());
                        b.putString("filtervalue", holder.tv.getTag().toString());
                        b.putInt("pos", position);
                        sb.setArguments(b);
                        transaction.replace(R.id.lnfragement, sb);
                    } else if (holder.rlfilter.getTag().toString().equals("1")) {
                        blclear = false;
                        listfragment sb = new listfragment();
                        Bundle b = new Bundle();
                        b.putString("filtertype", holder.tv.getText().toString());
                        b.putString("filtervalue", holder.tv.getTag().toString());
                        b.putInt("pos", position);
                        sb.setArguments(b);
                        transaction.replace(R.id.lnfragement, sb);

                    } else if (holder.rlfilter.getTag().toString().equals("0")) {
                        blclear = false;
                        pricefragment sb = new pricefragment();
                        Bundle b = new Bundle();
                        b.putString("filtertype", holder.tv.getText().toString());
                        b.putString("filtervalue", holder.tv.getTag().toString());
                        b.putInt("pos", position);
                        sb.setArguments(b);
                        transaction.replace(R.id.lnfragement, sb);

                    }

                    transaction.commit();

                    adapter.notifyDataSetChanged();


                }
            });
            return rowView;
        }

    }
}
