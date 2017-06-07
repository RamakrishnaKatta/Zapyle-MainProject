package fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import network.ApiCommunication;
import network.ApiService;
import utils.ExternalFunctions;

/**
 * Created by zapyle on 3/8/16.
 */
public class listfragment extends Fragment implements ApiCommunication {
    private static final String SCREEN_NAME = "FILTER_LIST_MODEL";
    Context ctx;
    ListView elv1;
    private String[] groups1;
    public  int fprice = 0;
    public  boolean[] isselect1;
    public  CustomAdapter1 adapt1;
    public  String[] selecteditem;
    public  String selecteduseritem= "";
    String filtertype, filtervalue;
    TextView tvNotFound;
    int []id;
    public Context context;
    int index;
    public  JSONObject ob3;
    ProgressBar progressBar;
    RelativeLayout rl;
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = getActivity();
        //activityCommunicator =(ActivityCommunicator)context;
    }


    public void generateFilterparam(int index, String filterValue) {
        System.out.println("ddddnew11"+filterValue);
        if (filterValue.length() > 1) {
            ExternalFunctions.strFilterdata[index] = filterValue.replace("*", "").replace("=,", "=");
        } else {
            ExternalFunctions.strFilterdata[index] = "";
        }

        ExternalFunctions.strfilter = makeFilter(ExternalFunctions.strFilterdata);

        //Toast.makeText(getApplicationContext(),ExternalFunctions.strfilter,Toast.LENGTH_LONG).show();
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
        ctx = listfragment.this.getActivity();
        View v = inflater.inflate(R.layout.filtersingle, null);
        elv1 = (ListView) v.findViewById(R.id.expandablelistview1);
        tvNotFound=(TextView)v.findViewById(R.id.tvNotFound);
        tvNotFound.setVisibility(View.GONE);
        //Progress bar
        rl=(RelativeLayout) v.findViewById(R.id.rl);
        inflater.inflate(R.layout.custom_progressbar, rl, true);
        progressBar= (ProgressBar) rl.findViewById(R.id.custom_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        Bundle bundle = getArguments();
        filtertype = bundle.getString("filtertype");
        filtervalue = bundle.getString("filtervalue");
        index=bundle.getInt("pos");
        System.out.println("xxx" + ExternalFunctions.strfilter);
        TextView tvtitle = (TextView) v.findViewById(R.id.textView38);
        tvtitle.setText("Select " + filtertype);
        GetFilter();
        return v;
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {

        if (flag.equals("getfilter")) {


            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    System.out.println("ooo" + resp);
                    String status = resp.getString("status");
                    if (status.equals("success")) {

                        ob3 = resp.getJSONObject("data");
                        JSONArray jsSub =ob3.getJSONArray("value");
                        fprice = 0;
                        groups1 = new String[jsSub.length()];
                        selecteditem = new String[jsSub.length()];
                        isselect1 = new boolean[jsSub.length()];
                        id=new int[jsSub.length()];

                        for (int j = 0; j < jsSub.length(); j++) {

                            groups1[j] = jsSub.getJSONObject(j).getString("value").toUpperCase();
                            id[j] = Integer.parseInt(jsSub.getJSONObject(j).getString("id"));
                            selecteditem[j] = "d";

                            if (jsSub.getJSONObject(j).getString("selected").contains("true")) {
                                isselect1[j] = true;
                                fprice = fprice + 1;
                                selecteditem[j] = "," + id[j];
                            } else {
                                isselect1[j] = false;
                            }
                        }

                        int m = 0;
                        for (int i = 0; i < isselect1.length; i++) {
                            if (isselect1[i]) {
                                m = m + 1;
                            }
                        }
                        fprice = m;


                        adapt1 = new CustomAdapter1(groups1);
                        elv1.setAdapter(adapt1);


                    }

                    progressBar.setVisibility(View.GONE);
                } catch (JSONException e) {
                    tvNotFound.setVisibility(View.VISIBLE);
                    tvNotFound.setText("Not Found");
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);

                }

            }

        }


    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }


    public class CustomAdapter1 extends BaseAdapter {
        String[] result;
        Context context;

        private LayoutInflater inflater = null;

        public CustomAdapter1(String[] prgmNameList) {
            // TODO Auto-generated constructor stub
            result = prgmNameList;
            context = listfragment.this.getActivity();
            try {
                inflater = (LayoutInflater) context.
                        getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            }catch(Exception e){

            }
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

        public class Holder {
            CheckBox tv;


        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            final Holder holder = new Holder();
            View rowView;
            rowView = inflater.inflate(R.layout.childprice, null);
            holder.tv = (CheckBox) rowView.findViewById(R.id.imgcheck);
            holder.tv.setText(result[position]);


            if (isselect1[position]) {
                holder.tv.setChecked(true);
                holder.tv.setButtonDrawable(R.drawable.selected_mdpi);
                selecteditem[position] = "," + id[position];
                if (!selecteduseritem.contains("," + id[position])) {
                    selecteduseritem = selecteduseritem + "," + id[position];
                    String str="";
                    if(selecteduseritem.length()>1) {
                        str = filtervalue + "=" + selecteduseritem;
                    }
                    generateFilterparam(index,str);
                }
            } else {
                holder.tv.setChecked(false);
                holder.tv.setButtonDrawable(R.drawable.unselected_mdpi);
                String str = "," + id[position];
                if (selecteduseritem.contains(str)) {
                    selecteduseritem = selecteduseritem.replace(str, "");
                    String str1="";
                    if(selecteduseritem.length()>1) {
                        str1 = filtervalue + "=" + selecteduseritem;
                    }
                    generateFilterparam(index,str1);

                }
            }
            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.tv.isChecked()) {
                        selecteditem[position] = "," + id[position];
                        if (!selecteduseritem.contains("," + id[position])) {
                            selecteduseritem = selecteduseritem + "," + id[position];
                            String str="";
                            if(selecteduseritem.length()>1) {
                                str = filtervalue + "=" + selecteduseritem;
                            }
                            generateFilterparam(index,str);
                        }

                        isselect1[position] = true;
                        fprice = fprice + 1;


                       // activityCommunicator.passDataToActivity(index,str);

                    } else {

                        selecteditem[position] = "";
                        String str = "," + id[position];
                        if (selecteduseritem.contains(str)) {
                            selecteduseritem = selecteduseritem.replace(str, "");
                            String str1="";
                            if(selecteduseritem.length()>1) {
                                str1 = filtervalue + "=" + selecteduseritem;
                            }
                            generateFilterparam(index,str1);

                        }
                        isselect1[position] = false;
                        fprice = fprice - 1;


                      //  activityCommunicator.passDataToActivity(index,str);

                    }
                    adapt1.notifyDataSetChanged();


                }
            });
            return rowView;
        }

    }

    private void GetFilter() {
        boolean blchk=false;
        for(int i=0;i<ExternalFunctions.strFilterdata.length;i++){
            if(ExternalFunctions.strFilterdata[i]!=null) {
                blchk=true;
                System.out.println("xxxxxxxx");

            }
        }
        if(blchk){
            ExternalFunctions.strfilter = makeFilter(ExternalFunctions.strFilterdata);
        }

        ApiService.getInstance(ctx, 1).getData(this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/filters/getFilters/" + filtervalue + "/?version=2&"+ExternalFunctions.strfilter, "getfilter");

    }


}
