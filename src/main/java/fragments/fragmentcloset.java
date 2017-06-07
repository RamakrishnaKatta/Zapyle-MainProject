package fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.CheckConnectivity;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import activity.HomePageNew;
import activity.ProfilePage;
import activity.searchnew;
import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;
import utils.GetSharedValues;

public class fragmentcloset extends Fragment implements ApiCommunication {
    private static final String SCREEN_NAME = "Product";
    Context ctx;
    ListView lvcloset;

    LinearLayout l1,l3;

    String strPrevMax;
    int check=0;
    int check1=0;

    private String []profile_pic;
    private String[] full_name;
    private boolean []verified;
    private boolean []bladmired;
    private Integer[] closet_size;
    private Integer[] user_id;
    private Integer[] admirers;
    private String[] zap_username;



    public static  CustomAdapter1 adapt;
    public static ProgressDialog pd1;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setRetainInstance(true);
        ctx=fragmentcloset.this.getActivity();
        View v = inflater.inflate(R.layout.searchcloset, null);
        lvcloset = (ListView) v.findViewById(R.id.listview1);
        l3=(LinearLayout)v.findViewById(R.id.l3);
        l3.setVisibility(View.VISIBLE);



        ((searchnew)getActivity()).setFragmentRefreshListener1(new searchnew.FragmentRefreshListener1() {
            @Override
            public void onRefresh() {
                if( ExternalFunctions.blsearch1) {
                    //System.out.println("resume hit1");
                    lvcloset.setVisibility(View.GONE);
                    adapt = null;
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
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
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
        //System.out.println("visible"+ExternalFunctions.blsearch1);
        if (isVisibleToUser) {
            if (ExternalFunctions.blsearch1) {
                lvcloset.setVisibility(View.GONE);
                adapt = null;
                GetSearch();

            }        }

    }

    private void GetSearch() {
        int intcheck=0;
        //System.out.println("inside getsearch");
        ExternalFunctions.blsearch1=false;
        l3.setVisibility(View.VISIBLE);
        if ( ExternalFunctions.datasearchcloset!=null) {
            try {
                final JSONArray users = ExternalFunctions.datasearchcloset.getJSONArray("users");
                try {
                    if (users.length() > 0) {
                        intcheck = 1;
                        lvcloset.setVisibility(View.VISIBLE);
                        l3.setVisibility(View.GONE);
                        profile_pic = new String[users.length()];
                        full_name = new String[users.length()];
                        verified = new boolean[users.length()];
                        bladmired = new boolean[users.length()];
                        zap_username = new String[users.length()];
                        closet_size = new Integer[users.length()];
                        user_id = new Integer[users.length()];
                        admirers = new Integer[users.length()];


                        for (int i = 0; i < users.length(); i++) {

                            profile_pic[i] = users.getJSONObject(i).getString("profile_pic");
                            full_name[i] = users.getJSONObject(i).getString("full_name");
                            verified[i] = users.getJSONObject(i).getBoolean("verified");
                            bladmired[i] = users.getJSONObject(i).getBoolean("admired");
                            zap_username[i] = users.getJSONObject(i).getString("zap_username");
                            closet_size[i] = users.getJSONObject(i).getInt("closet_size");
                            user_id[i] = users.getJSONObject(i).getInt("user_id");
                            admirers[i] = users.getJSONObject(i).getInt("admirers");

                            //System.out.println("bbb" + profile_pic[i]);


                        }

                        adapt = new CustomAdapter1(profile_pic);
                        lvcloset.setAdapter(adapt);

                        adapt.notifyDataSetChanged();
                        Utility.setListViewHeightBasedOnChildren(lvcloset);


                    } else {
                        //System.out.println("inside getsearch1");
                        adapt = null;
                        lvcloset.setAdapter(adapt);

                        adapt.notifyDataSetChanged();
                        if (intcheck == 0) {
                            l3.setVisibility(View.VISIBLE);
                        } else {
                            l3.setVisibility(View.GONE);
                        }
                        //  lvprod.setVisibility(View.GONE);


                    }
                } catch (Exception e) {

                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void onDetach() {
        super.onDetach();
        int count1 = lvcloset.getChildCount();
        for (int i = 0; i < count1; i++) {
            ImageView rv = (ImageView) lvcloset.getChildAt(i).findViewById(R.id.imgprofile);

            if (rv != null) {
                if (rv.getDrawable() != null)
                    rv.getDrawable().setCallback(null);
            }

        }
        lvcloset.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ExternalFunctions.datasearchcloset=null;
        int count1 = lvcloset.getChildCount();
        for (int i = 0; i < count1; i++) {
            ImageView rv = (ImageView) lvcloset.getChildAt(i).findViewById(R.id.imgprofile);

            if (rv != null) {
                if (rv.getDrawable() != null)
                    rv.getDrawable().setCallback(null);
            }

        }

        lvcloset.setVisibility(View.GONE);

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
            context=fragmentcloset.this.getActivity();

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
            ImageView imgprofile;
            TextView tvzapname,tvfullname,tvcloset,tvadmire,tvadmirebtn;


        }
        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            //System.out.println("hsd11");
            final Holder holder=new Holder();
            View rowView;

            rowView = inflater.inflate(R.layout.searchcloset_list, null);
            holder.imgprofile=(ImageView) rowView.findViewById(R.id.imgprofile);
            holder.tvzapname=(TextView) rowView.findViewById(R.id.tvzapname);
            holder.tvfullname=(TextView) rowView.findViewById(R.id.tvfullname);
            holder.tvcloset=(TextView) rowView.findViewById(R.id.tvcloset);
            holder.tvadmire=(TextView) rowView.findViewById(R.id.tvadmires);
            holder.tvadmirebtn=(TextView) rowView.findViewById(R.id.tvadmire);

            //System.out.println("hshs" + position);
            holder.tvzapname.setText(zap_username[position].toUpperCase());
            holder.tvfullname.setText(full_name[position]);
            holder.tvcloset.setText(closet_size[position]+" OUTFITS");
            holder.tvadmire.setText(admirers[position]+" ADMIRERS");
            holder.tvadmirebtn.setText("Admire");

            if (bladmired[position]) {


                holder.tvadmirebtn.setText("ADMIRING");

            } else {

                holder.tvadmirebtn.setText("ADMIRE");




            }


            Glide.with(fragmentcloset.this.getActivity())
                    .load(profile_pic[position])
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(holder.imgprofile);


            holder.tvadmirebtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckConnectivity.isNetworkAvailable(context)) {

                        //System.out.println("check"+bladmired[position]);

                        if (bladmired[position]) {
                            admirers[position]=admirers[position]-1;
                            holder.tvadmire.setText(admirers[position]+" ADMIRERS");
                            holder.tvadmirebtn.setText("ADMIRE");
                            bladmired[position]=false;
                            JSONObject admireObject = null;
                            try {
                                admireObject = new JSONObject();
                                admireObject.put("action", "unadmire");
                                admireObject.put("user", user_id[position]);
                            } catch (Exception e) {

                            }
                            ApiService.getInstance(fragmentcloset.this.getActivity(), 1).postData((ApiCommunication) fragmentcloset.this.getActivity(), EnvConstants.APP_BASE_URL + "/user/admire/", admireObject, SCREEN_NAME, "unadmire");
                            //  holder.tvadmirebtn.setEnabled(false);



                        } else {
                            admirers[position]=admirers[position]+1;
                            holder.tvadmire.setText(admirers[position]+" ADMIRERS");
                            holder.tvadmirebtn.setText("ADMIRING");
                            bladmired[position]=true;
                            JSONObject admireObject = null;
                            try {
                                admireObject = new JSONObject();
                                admireObject.put("action", "admire");
                                admireObject.put("user",  user_id[position]);
                            } catch (Exception e) {

                            }
                            ApiService.getInstance(fragmentcloset.this.getActivity(), 1).postData((ApiCommunication) fragmentcloset.this.getActivity(), EnvConstants.APP_BASE_URL + "/user/admire/", admireObject, SCREEN_NAME, "admire");
                            // holder.tvadmirebtn.setEnabled(false);

                        }
                    }


                }
            });



            holder.imgprofile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CheckConnectivity.isNetworkAvailable(context)) {

                        if (  user_id[position] == GetSharedValues.getuserId(context)) {
                            Intent pro_page = new Intent(context, ProfilePage.class);
                            pro_page.putExtra("user_id", GetSharedValues.getuserId(context));
                            pro_page.putExtra("p_username",GetSharedValues.getUsername(context));
                            pro_page.putExtra("activity", "searchnew");
                            context.startActivity(pro_page);
                            Runtime.getRuntime().gc();
                        } else {
                            Intent pro_page = new Intent(context, ProfilePage.class);
                            pro_page.putExtra("user_id", user_id[position]);
                            pro_page.putExtra("activity", "searchnew");
                            pro_page.putExtra("p_username", zap_username[position]);
//                          pro_page.putExtra("p_Fullname", feed.getFullname());
                            context.startActivity(pro_page);
                            Runtime.getRuntime().gc();
//                        ((Activity) context).finish();
                        }
                    }
                    else {
                       CustomMessage.getInstance().CustomMessage(context,"Internet is not available!");
                    }

                }
            });


            return rowView;
        }

    }

//    @Override
//    public void onResume() {
//        //System.out.println("resume hit0");
//       if( ExternalFunctions.blsearch1) {
//            //System.out.println("resume hit1");
//            lvcloset.setVisibility(View.GONE);
//            adapt = null;
//            GetSearch();
//        }
//
//        super.onResume();
//    }
//    Server callbacks
//    ------------------------------------------------------------

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        //System.out.println("response:"+response);
        if (flag.equals("admire")){

        }
        else if (flag.equals("unadmire")){

        }
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }

}