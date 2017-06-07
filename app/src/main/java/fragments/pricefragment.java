package fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import network.ApiCommunication;
import network.ApiService;
import utils.ActivityCommunicator;
import utils.CustomMessage;
import utils.ExternalFunctions;

/**
 * Created by zapyle on 3/8/16.
 */
public class pricefragment extends Fragment implements ApiCommunication {
    private static final String SCREEN_NAME = "FILTER_PRICE_MODEL";
    Context ctx;
    Spinner sp_min, sp_max;
    public String selecteduseritem = "";
    String filtertype, filtervalue;
    public Context context;
    int index;
    ArrayList<String> min = new ArrayList<String>();
    ArrayList<String> max = new ArrayList<String>();
    public JSONObject obj;
    public String str_max, str_min;
    LinearLayout lnprice;
    ProgressBar progressBar;
    RelativeLayout rl;
    boolean []blmax,blmin;
    ArrayAdapter<String> adapter,adapter1;
    boolean bl=false;
    boolean bl1=false;
    TextView tvNotFound;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        context = getActivity();

    }



    public void generateFilterparam(int index, String filterValue) {

        if (filterValue.length() > 1) {
            ExternalFunctions.strFilterdata[index] = filterValue.replace("*", "").replace("=,", "=");
        } else {
            ExternalFunctions.strFilterdata[index] = "";
        }

        ExternalFunctions.strfilter = makeFilter(ExternalFunctions.strFilterdata);

    }

    public String makeFilter(String[] str){
        String strresult="=";
        for(int i=0;i<str.length;i++){
            try {
                if (str[i].length() > 1)
                    strresult = strresult + "&" + str[i];
            }catch (Exception e){

            }
        }
        strresult= strresult.replace("=&","");
        if(strresult.equals("=")){
            strresult="";
        }
        return strresult.replace("=&","");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        ctx = pricefragment.this.getActivity();
        View v = inflater.inflate(R.layout.filter_price, null);
        sp_min = (Spinner) v.findViewById(R.id.sp_min);
        sp_max = (Spinner) v.findViewById(R.id.sp_max);
        lnprice = (LinearLayout) v.findViewById(R.id.lnprice);
        tvNotFound=(TextView)v.findViewById(R.id.tvNotFound);
        tvNotFound.setVisibility(View.GONE);
        lnprice.setVisibility(View.GONE);
        //Progress bar
        rl = (RelativeLayout) v.findViewById(R.id.rl);
        inflater.inflate(R.layout.custom_progressbar, rl, true);
        progressBar= (ProgressBar) rl.findViewById(R.id.custom_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        Bundle bundle = getArguments();
        filtertype = bundle.getString("filtertype");
        filtervalue = bundle.getString("filtervalue");
        index = bundle.getInt("pos");
        System.out.println("xxx" + ExternalFunctions.strfilter);
        GetFilter();
        sp_max.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {




                    if (bl) {
                        if(arg2>0) {
                        str_max = sp_max.getSelectedItem().toString();
                        String str = "";
                        if (str_max != null && str_min != null) {
                            selecteduseritem = str_min + "," + str_max;
                            str = filtervalue + "=" + selecteduseritem;
                        }
                        generateFilterparam(index, str);
                        int m = 0, intselmin = 0;
                        for (int i = 1; i < min.size(); i++) {
                            if (Integer.parseInt(min.get(i)) >= Integer.parseInt(str_max)) {
                                blmin[i] = true;


                            } else {
                                blmin[i] = false;
                                if (m == 0) {
                                    intselmin = i;
                                    m = m + 1;
                                }


                            }
                        }

                        adapter.setDropDownViewResource(R.layout.spinner_item);
                        adapter.notifyDataSetChanged();
                        }else{
                            str_max="x";
                            String str = "";

                            generateFilterparam(index, str);
                                    blmax=new boolean[max.size()];
                            blmin=new boolean[min.size()];
                    adapter.setDropDownViewResource(R.layout.spinner_item);
                    adapter.notifyDataSetChanged();
                            sp_min.setSelection(0);
                        }
                        //  sp_min.setSelection(intselmin);
                    } else {
                        bl = true;
                    }


                }




            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        sp_min.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int arg2, long arg3) {



                  if (bl1) {
                      if(arg2>0) {

                          str_min = sp_min.getSelectedItem().toString();
                      String str = "";
                      if (str_max != null && str_min != null) {
                          selecteduseritem = str_min + "," + str_max;
                          str = filtervalue + "=" + selecteduseritem;
                      }
                      generateFilterparam(index, str);
                      int m = 0, intselmax = 0;
                      for (int i = 1; i < max.size(); i++) {
                          if (Integer.parseInt(max.get(i)) <= Integer.parseInt(str_min)) {

                              blmax[i] = true;

                          } else {
                              if (m == 0) {
                                  intselmax = i;
                                  m = m + 1;
                              }
                              blmax[i] = false;


                          }
                      }

                      adapter1.setDropDownViewResource(R.layout.spinner_item);
                      adapter1.notifyDataSetChanged();
                      sp_max.setSelection(intselmax);
                      }else{
                          str_min="x";

                          String str = "";

                          generateFilterparam(index, str);
                          blmax=new boolean[max.size()];
                          blmin=new boolean[min.size()];
                  adapter1.setDropDownViewResource(R.layout.spinner_item);
                  adapter1.notifyDataSetChanged();
                          sp_max.setSelection(0);
                      }

                  } else {
                      bl1 = true;
                  }
                }




            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub

            }
        });
        return v;

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        if (flag.equals("getfilter")) {


            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    System.out.println("ooo11" + resp);
                    String status = resp.getString("status");
                    if (status.equals("success")) {


                        obj = resp.getJSONObject("data");
                        JSONObject jsprice = obj.getJSONObject("price");
                        JSONArray jsmin = jsprice.getJSONArray("min_price_list");
                        JSONArray jsmax = jsprice.getJSONArray("max_price_list");
                        blmin=new boolean[jsmin.length()+1];
                        blmax=new boolean[jsmax.length()+1];
                        min.add("Minimum price");
                        blmax[0]=false;
                        blmin[0]=false;
                        for (int j = 0; j < jsmin.length(); j++) {
                            min.add(jsmin.get(j).toString());

                        }
                        max.add("Maximum price");
                        for (int j =0; j <jsmax.length(); j++) {
                            max.add(jsmax.get(j).toString());

                        }
                        str_max = jsprice.get("max_selected_price").toString();
                        str_min = jsprice.get("min_selected_price").toString();
                        setSpinners();


                    }
                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    tvNotFound.setVisibility(View.VISIBLE);
                    tvNotFound.setText("Not Found");
                    progressBar.setVisibility(View.GONE);
                    e.printStackTrace();

                }

            }


        }


    }

    public void setSpinners() {
        try {
               adapter = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, min){
                @Override
                public boolean isEnabled(int position){
                    return !blmin[position];
                }
                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(blmin[position]){
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
            adapter.setDropDownViewResource(R.layout.spinner_item);
            sp_min.setAdapter(adapter);
             adapter1 = new ArrayAdapter<String>(getActivity(), R.layout.simple_spinner_item, max){
                @Override
                public boolean isEnabled(int position){
                    return !blmax[position];
                }
                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if(blmax[position]){
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    }
                    else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
            adapter1.setDropDownViewResource(R.layout.spinner_item);
            sp_max.setAdapter(adapter1);

            if (max.contains(str_max) && min.contains(str_min)) {
                System.out.println("oooz" + str_max + "ooo" + str_min + "ind=" + min.indexOf(str_min) + "ind=" + max.indexOf(str_max));
                sp_max.setSelection(max.indexOf(str_max));
                sp_min.setSelection(min.indexOf(str_min));

            }
            lnprice.setVisibility(View.VISIBLE);


        } catch (Exception e) {

        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }


    private void GetFilter() {
        boolean blchk=false;
        if(ExternalFunctions.strFilterdata!=null) {
            for (int i = 0; i < ExternalFunctions.strFilterdata.length; i++) {
                if (ExternalFunctions.strFilterdata[i] != null) {
                    blchk = true;
                    System.out.println("xxxxxxxx");

                }
            }
            if (blchk) {
                ExternalFunctions.strfilter = makeFilter(ExternalFunctions.strFilterdata);
            }
            ApiService.getInstance(ctx, 1).getData(this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/filters/getFilters/" + filtervalue + "/?version=2&" + ExternalFunctions.strfilter, "getfilter");
        }
    }


}