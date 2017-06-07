package fragments;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
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

import java.util.ArrayList;

import network.ApiCommunication;
import network.ApiService;
import utils.CustomMessage;
import utils.ExternalFunctions;

/**
 * Created by zapyle on 3/8/16.
 */
public class sublistfragment extends Fragment implements ApiCommunication {

    ExpandableListView elv;
    Context ctx;
    private String[] groups;
    private int[] counts;
    private int[] countsdisable;
    private int[] countschild;
    private String[][] childrencount;
    private String[][] children;
    private int[][] selectedindex;
    private boolean[][] isselect;
    private boolean[]isselectparent;
    private boolean[][] isdisable;
    public  int catcount = 0;
    public  int catcountunsel = 0;
    public  SavedTabsListAdapter adapt;
    private View rootView;
    public  ProgressDialog pd1;
    public  int check = 0;
    private  final String SCREEN_NAME = "FILTER_TREE_MODEL";
    public  String selecteduseritem = "";
    public  String[] selecteditem;
    String filtertype, filtervalue;
    public Context context;
    int index;
    public  JSONObject ob1;
    RelativeLayout rlsearch;
    ArrayList<String> arraySearch;
    ArrayList<Integer> arraySearchid;
    ImageView imagesearch;
    EditText edsearch;
    ListView lvsearch;
    TextView tvNotFound;
    CustomAdapter searchadapter;
    RelativeLayout rl;
    ProgressBar progressBar;
    boolean blsblistselect=false;
    boolean []blindicator;
    boolean blinitialize=false;
    @Override
    public void onAttach(Activity activity){
        super.onAttach(activity);
        context = getActivity();
    }
    public void generateFilterparam(int index, String filterValue) {
        System.out.println("ddddnew11"+filterValue);
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
                if (str[i].length() > 1) {

                    System.out.println("ddddnew"+str[i]);
                    strresult = strresult + "&" + str[i];
                }
            }catch (Exception e){

            }
        }
        strresult= strresult.replace("=&","");
        if(strresult.equals("=")){
            strresult="";
        }
        return strresult.replace("=&","");
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.filtercategory, container, false);
        elv = (ExpandableListView) rootView.findViewById(R.id.laptop_list);
        rlsearch=(RelativeLayout) rootView.findViewById(R.id.relativeLayout10);
        imagesearch=(ImageView) rootView.findViewById(R.id.imagesearch);
        edsearch=(EditText) rootView.findViewById(R.id.edtsearch);
        lvsearch=(ListView)rootView.findViewById(R.id.lvsearch);
        tvNotFound=(TextView)rootView.findViewById(R.id.tvNotFound);
        tvNotFound.setVisibility(View.GONE);
        //Progress bar
        rl = (RelativeLayout) rootView.findViewById(R.id.rl);
        inflater.inflate(R.layout.custom_progressbar, rl, true);
        progressBar= (ProgressBar) rl.findViewById(R.id.custom_progress_bar);
        progressBar.setIndeterminate(true);
        progressBar.setVisibility(View.VISIBLE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(100,100);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);

        System.out.println("queex"+ ExternalFunctions.strfilter);
        Bundle bundle = getArguments();
        filtertype = bundle.getString("filtertype");
        filtervalue = bundle.getString("filtervalue");
        index=bundle.getInt("pos");
        if(filtertype.contains("ategory"))
            rlsearch.setVisibility(View.VISIBLE);
        GetFilter();
        imagesearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edsearch.getText().toString().isEmpty()) {
                    boolean blfound = false;
                    arraySearch = new ArrayList<String>();
                    arraySearchid = new ArrayList<Integer>();
                    for (int i = 0; i < children.length; i++) {
                        for (int j = 0; j < children[i].length; j++) {
                            if (children[i][j].toLowerCase().contains(edsearch.getText().toString().toLowerCase())) {
                                blfound = true;
                                System.out.println("mmmm" + children[i][j]);
                                arraySearch.add(children[i][j]);
                                arraySearchid.add(Integer.parseInt(childrencount[i][j]));
                            }
                        }
                    }
                    if (blfound) {
                        searchadapter = new CustomAdapter();
                        elv.setVisibility(View.GONE);
                        lvsearch.setAdapter(searchadapter);
                        lvsearch.setVisibility(View.VISIBLE);

                    } else {
                        CustomMessage.getInstance().CustomMessage(getActivity(), "Category not found");
                        edsearch.requestFocus();
                    }
                }else{
                    CustomMessage.getInstance().CustomMessage(getActivity(), "Enter category to search");
                    edsearch.requestFocus();
                }
            }
        });

        edsearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {

                    if(!edsearch.getText().toString().isEmpty()) {
                        boolean blfound=false;
                        arraySearch =new ArrayList<String>();
                        arraySearchid =new ArrayList<Integer>();
                        for (int i=0;i<children.length;i++){
                            for(int j=0;j<children[i].length;j++){
                                if(children[i][j].toLowerCase().contains(edsearch.getText().toString().toLowerCase())){
                                    blfound=true;
                                    System.out.println("mmmm"+children[i][j]);
                                    arraySearch.add(children[i][j]);
                                    arraySearchid.add(Integer.parseInt(childrencount[i][j]));
                                }
                            }
                        }
                        if(blfound){
                            searchadapter=new CustomAdapter();
                            elv.setVisibility(View.GONE);
                            lvsearch.setAdapter(searchadapter);
                            lvsearch.setVisibility(View.VISIBLE);

                        }else{
                            CustomMessage.getInstance().CustomMessage(getActivity(), "Category not found");
                            edsearch.requestFocus();
                        }
                    }else{
                        CustomMessage.getInstance().CustomMessage(getActivity(), "Enter category to search");
                        edsearch.requestFocus();
                    }
                }
                return false;
            }
        });

        return rootView;


    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        {
            System.out.println("ooocczzxzxz" + response);
            //  pd1.dismiss();

            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    if (status.equals("success")) {

                        ob1 = resp.getJSONObject("data");
                        final JSONArray cat = ob1.getJSONArray("value");
                        JSONArray jsSub;
                        groups = new String[cat.length()];
                        selecteditem = new String[cat.length()];
                        counts = new int[cat.length()];
                        countsdisable = new int[cat.length()];
                        countschild = new int[cat.length()];
                        childrencount = new String[cat.length()][];
                        children = new String[cat.length()][];
                        isselect = new boolean[cat.length()][];
                        isdisable = new boolean[cat.length()][];
                        isselectparent = new boolean[cat.length()];
                        blindicator= new boolean[cat.length()];
                        selectedindex=new int[cat.length()][2];
                        catcount = 0;
                        catcountunsel = 0;
                        for (int i = 0; i < cat.length(); i++) {
                            //System.out.println("xxxx1" + i);
                            JSONObject js1 = null;
                            js1 = cat.getJSONObject(i);
                            jsSub = js1.getJSONArray("value");
                            blindicator[i]=false;
                            groups[i] = cat.getJSONObject(i).getString("name").toUpperCase();
                            selecteditem[i] = "d";
                            counts[i] = 0;
                            countsdisable[i] = 0;
                            isselectparent[i] = false;
                            childrencount[i] = new String[jsSub.length()];
                            children[i] = new String[jsSub.length()];
                            isselect[i] = new boolean[jsSub.length()];
                            isdisable[i] = new boolean[jsSub.length()];

                            countschild[i] = jsSub.length();
                            for (int j = 0; j < jsSub.length(); j++) {
                                System.out.println("mlll"+filtertype);
                                if( filtertype.toLowerCase().contains("size")){
                                    childrencount[i][j] = jsSub.getJSONObject(j).getString("name").toString();
                                }else{
                                    childrencount[i][j] = jsSub.getJSONObject(j).getString("id").toString();
                                }

                                if( cat.getJSONObject(i).getString("name").toUpperCase() .contains("FOOTWEAR SIZE")){
                                    children[i][j] = jsSub.getJSONObject(j).getString("display").toString();
                                }else {
                                    children[i][j] = jsSub.getJSONObject(j).getString("name").toString();
                                }
                                if (jsSub.getJSONObject(j).getString("selected").toString().contains("true")) {
                                    isselect[i][j] = true;
                                    isselectparent[i] = true;
                                    blinitialize=true;
                                    counts[i] = counts[i] + 1;

                                    selecteduseritem = selecteduseritem + ",*" + childrencount[i][j] + "*";
                                } else {
                                    isselect[i][j] = false;
                                }
                                if (jsSub.getJSONObject(j).getString("selected").toString().contains("true") && jsSub.getJSONObject(j).getString("disabled").toString().contains("false")) {
                                    catcount = catcount + 1;
                                }
                                if (jsSub.getJSONObject(j).getString("selected").toString().contains("false") && jsSub.getJSONObject(j).getString("disabled").toString().contains("true")) {
                                    countsdisable[i] = countsdisable[i] + 1;
                                }

                                isdisable[i][j] = jsSub.getJSONObject(j).getString("disabled").toString().contains("true");

                            }

                        }


                        adapt = new SavedTabsListAdapter();
                        elv.setAdapter(adapt);
                      //  blinitialize=false;

                        elv.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
                            @Override
                            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {


                                return false;
                            }
                        });
                        // setGroupIndicatorToRight();
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

    private ExpandableListView.OnChildClickListener ExpandList_ItemClicked = new ExpandableListView.OnChildClickListener() {

        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {

            return false;
        }

    };

    private void setGroupIndicatorToRight() {
        /* Get the screen width */
        try {
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            int width = dm.widthPixels;
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
                elv.setIndicatorBounds((width / 2) - getDipsFromPixel(35),
                        -getDipsFromPixel(5));

            } else {
                elv.setIndicatorBoundsRelative((width / 2) - getDipsFromPixel(35), (width / 2) + (width / 4)
                        - getDipsFromPixel(5));
            }
        }catch(Exception e){

        }

    }

    // Convert pixel to dip
    public int getDipsFromPixel(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {


    }


    private void GetFilter() {

        try {
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

            System.out.println("onactivityresultmm" + EnvConstants.APP_BASE_URL + "/filters/getFilters/" + filtervalue + "/"+ExternalFunctions.strfilter);
            ApiService.getInstance(ctx, 1).getData(this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/filters/getFilters/" + filtervalue + "/?version=2&"+ExternalFunctions.strfilter, "getfilter");
        } catch (Exception e) {

        }
    }

    public class SavedTabsListAdapter extends BaseExpandableListAdapter {


        @Override
        public int getGroupCount() {
            return groups.length;
        }

        @Override
        public int getChildrenCount(int i) {
            return children[i].length;
        }

        @Override
        public Object getGroup(int i) {
            return groups[i];
        }

        @Override
        public Object getChild(int i, int i1) {
            return children[i][i1];
        }

        @Override
        public long getGroupId(int i) {
            return i;
        }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        public View getChildView(final int groupPosition, final int childPosition,
                                 boolean isLastChild, View convertView, final ViewGroup parent) {
            final String cat = getChild(groupPosition, childPosition).toString();

            LayoutInflater inflater = sublistfragment.this.getActivity().getLayoutInflater();

            if (convertView == null) {
                convertView = inflater.inflate(R.layout.childcategory, null);
            }
            if (blindicator[groupPosition]) {
                if (!isselect[groupPosition][childPosition]) {
                    convertView = inflater.inflate(R.layout.emptylayout, null);
                    convertView.setVisibility(View.GONE);
                    elv.setChildDivider(getResources().getDrawable(R.drawable.expandable_list_child_divider));
                }else{

                    convertView = inflater.inflate(R.layout.childcategory, null);
                    convertView.setVisibility(View.VISIBLE);

                    elv.setChildDivider(getResources().getDrawable(R.drawable.expandable_list_child_divider));

                }
            }else{
                convertView = inflater.inflate(R.layout.childcategory, null);
                convertView.setVisibility(View.VISIBLE);
                elv.setChildDivider(getResources().getDrawable(R.drawable.expandable_list_child_divider));

            }

            final CheckBox imgdot = (CheckBox) convertView.findViewById(R.id.imgcheck);
            final RelativeLayout r1 = (RelativeLayout) convertView.findViewById(R.id.r1);
            final TextView tvcname = (TextView) convertView.findViewById(R.id.tvcname);
            final View viewupper= convertView.findViewById(R.id.upperline);


            imgdot.setButtonDrawable(R.drawable.unselected_mdpi);
            tvcname.setText(cat);

           
            if (countschild[groupPosition] == counts[groupPosition]) {

                isselect[groupPosition][childPosition] = true;

            }
            if (isdisable[groupPosition][childPosition]) {

                if (childPosition > 0) {
                    imgdot.setEnabled(false);
                    tvcname.setTextColor(Color.GRAY);
                }

            } else {
                tvcname.setTextColor(Color.BLACK);
                imgdot.setEnabled(true);
            }
            if (!isselect[groupPosition][childPosition]) {


                //CustomMessage.getInstance().CustomMessage(getActivity(),selecteduseritem );
                if (selecteduseritem.contains(",*" + childrencount[groupPosition][childPosition] + "*")) {



                    String a = ",*" + childrencount[groupPosition][childPosition] + "*";
                    selecteduseritem = selecteduseritem.replace(a, "");
                    String str="";
                    if(selecteduseritem.length()>1) {
                        str = filtervalue + "=" + selecteduseritem;
                    }
                    //CustomMessage ct=new CustomMessage(getActivity(),selecteduseritem +"=1"+",*" + childrencount[groupPosition][childPosition] + "*");
                    generateFilterparam(index,str);
                    //CustomMessage ct1=new CustomMessage(getActivity(),selecteduseritem +"=2"+",*" + childrencount[groupPosition][childPosition] + "*");
                }
                imgdot.setButtonDrawable(R.drawable.unselected_mdpi);
                imgdot.setChecked(false);
                //  tvcname.setTextColor(Color.GRAY);

            } else {
                tvcname.setTextColor(Color.BLACK);
                imgdot.setChecked(true);
                imgdot.setButtonDrawable(R.drawable.selected_mdpi);

                if (!selecteditem[groupPosition].contains("," + childrencount[groupPosition][childPosition] + ",")) {
                    selecteditem[groupPosition] = selecteditem[groupPosition] + childrencount[groupPosition][childPosition] + ",";

                }

                if (!selecteduseritem.contains(",*" + childrencount[groupPosition][childPosition] + "*")) {

                    selecteduseritem = selecteduseritem + ",*" + childrencount[groupPosition][childPosition] + "*";

                    String str="";
                    if(selecteduseritem.length()>1) {
                        str = filtervalue + "=" + selecteduseritem;
                    }
                    generateFilterparam(index,str);

                }
//                System.out.println("selected new"+selecteduseritem +"=============="+",*" + childrencount[groupPosition][childPosition] + "*");
//                CustomMessage.getInstance().CustomMessage(getActivity(),selecteduseritem +"==========="+",*" + childrencount[groupPosition][childPosition] + "*");

            }

            if (countsdisable[groupPosition] == countschild[groupPosition]) {

                imgdot.setEnabled(false);
                tvcname.setTextColor(Color.GRAY);
                isdisable[groupPosition][childPosition] = true;
                isselect[groupPosition][childPosition] = false;


            }
            r1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isdisable[groupPosition][childPosition] && !isselect[groupPosition][childPosition]) {

                       CustomMessage.getInstance().CustomMessage(sublistfragment.this.getContext(), "There is no " + children[groupPosition][childPosition] + " in your current selection");


                    } else {


                        if (!isdisable[groupPosition][childPosition]) {

                            if (!isselect[groupPosition][childPosition]) {
                                isselect[groupPosition][childPosition] = true;
                                counts[groupPosition] = counts[groupPosition] + 1;
                                selecteditem[groupPosition] = ",";
                                catcount = catcount + 1;
                                adapt.notifyDataSetChanged();

                            } else {

                               // isselect[groupPosition][0] = false;
                                isselect[groupPosition][childPosition] = false;
                                counts[groupPosition] = counts[groupPosition] - 1;
                                selecteditem[groupPosition] = ",";
                                catcount = catcount - 1;
                                adapt.notifyDataSetChanged();


                            }
                        }

                    }
                }
            });


            imgdot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    blsblistselect=false;
                    if (!isselect[groupPosition][childPosition]) {
                        isselect[groupPosition][childPosition] = true;
                        counts[groupPosition] = counts[groupPosition] + 1;
                        selecteditem[groupPosition] = ",";
                        blsblistselect=true;
                        catcount = catcount + 1;

                        adapt.notifyDataSetChanged();

                        //adapt.notifyDataSetChanged();


                    } else {


                       // isselect[groupPosition][0] = false;
                        isselect[groupPosition][childPosition] = false;
                        counts[groupPosition] = counts[groupPosition] - 1;
                        selecteditem[groupPosition] = ",";
                        catcount = catcount - 1;
                        adapt.notifyDataSetChanged();


                        //adapt.notifyDataSetChanged();


                    }
                }
            });

            return convertView;
        }



        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public View getGroupView(final int groupPosition, final boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            String subact = (String) getGroup(groupPosition);



            if (convertView == null) {
                LayoutInflater infalInflater = (LayoutInflater) sublistfragment.this.getActivity()
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = infalInflater.inflate(R.layout.gropupcategory,
                        null);

            }

            if (countschild[groupPosition] == counts[groupPosition]) {

            } else {

            }
            TextView item = (TextView) convertView.findViewById(R.id.tvcategory);
            final CheckBox checkparent = (CheckBox) convertView.findViewById(R.id.checkparent);
            RelativeLayout rlparent = (RelativeLayout) convertView.findViewById(R.id.rlparent);
            final View viewupper= convertView.findViewById(R.id.upperline);
            final ImageView imggroupindicator=(ImageView)convertView.findViewById(R.id.imggroupindicator);
            checkparent.setButtonDrawable(R.drawable.unselected_mdpi);
            if(groupPosition>0){
                viewupper.setVisibility(View.GONE);
            }else{
                viewupper.setVisibility(View.INVISIBLE);
            }
            if(filtervalue.contains("size")){
                checkparent.setVisibility(View.GONE);
            }else{
                item.setVisibility(View.GONE);
            }

            boolean bl=false;

            int a=0;
            for(int i=0;i< isselect[groupPosition].length;i++){
                if (isselect[groupPosition][i]){
                    bl=true;
                    a=a+1;

                }


            }
            if(bl){

                checkparent.setChecked(true);
                if(a==isselect[groupPosition].length)
                    checkparent.setButtonDrawable(R.drawable.selected_mdpi);
                else
                    checkparent.setButtonDrawable(R.drawable.half_selected_mdpi);

            }else {

                checkparent.setChecked(false);
                checkparent.setButtonDrawable(R.drawable.unselected_mdpi);
            }
            if( blinitialize) {
              //  System.out.println("xcxx insidenew" + isExpanded);
                boolean bl1, bn = false;
                int a1 = 0;

                    a1 = 0;
                    bl1 = false;
                    int k = 0;
                    for (int i = 0; i < isselect[groupPosition].length; i++) {
                        if (isselect[groupPosition][i]) {
                            bl1 = true;
                            bn = true;
                            if (a1 == 0) {
                                selectedindex[groupPosition][0] = i;
                            }
                            a1 = a1 + 1;
                            k = i;
                        }
                    }
                    if (bl1) {
                        selectedindex[groupPosition][1] = k;
                        blindicator[groupPosition] = true;
                        // adapt.notifyDataSetChanged();
                    } else {
                        blindicator[groupPosition] = false;
                    }

                if (!bn) {
                    blindicator[groupPosition]=false;
                    System.out.println("xcxx insidebnnew"+isExpanded);


                   // imggroupindicator.animate().rotationBy(-180).withEndAction(runnable).setDuration(150).setInterpolator(new LinearInterpolator()).start();
                    elv.collapseGroup(groupPosition);

                }
                else {

                    System.out.println("xcxx outsidebnlllnew" + isExpanded);
                    boolean blcheck = false;
                    for (int i = 0; i < isselect[groupPosition].length; i++) {
                        if (isselect[groupPosition][i]) {
                            blcheck = true;
                            break;
                        }

                    }
                    if (!blcheck) {


//                        imggroupindicator.animate().rotationBy(-180).withEndAction(runnable).setDuration(150).setInterpolator(new LinearInterpolator()).start();
                        elv.collapseGroup(groupPosition);


                    }else{
                        //adapt.notifyDataSetChanged();
//
                        elv.expandGroup(groupPosition);

                    }
                }
                if(groupPosition==groups.length-1){
                    blinitialize=false;
                  //blindicator=new boolean[groups.length];
                }

            }
            rlparent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    notifyDataSetChanged();


                }
            });

            imggroupindicator.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    System.out.println("xcxx"+isExpanded);

                    if(isExpanded){
                      //blindicator=new boolean[groups.length];
                        if( !blindicator[groupPosition]) {
                            System.out.println("xcxx inside"+isExpanded);
                            boolean bl,bn = false;
                            int a=0;
                            for(int j=0;j<groups.length;j++) {
                                a=0;
                                bl = false;
                                int k=0;
                                for (int i = 0; i < isselect[j].length; i++) {
                                    if (isselect[j][i]) {
                                        bl = true;
                                        bn=true;
                                        if (a == 0) {
                                            selectedindex[j][0] = i;
                                        }
                                        a = a + 1;
                                        k=i;
                                    }
                                }
                                if (bl) {
                                    selectedindex[j][1]=k;
                                    blindicator[j] = true;
                                   // adapt.notifyDataSetChanged();
                                }
                                else{
                                    blindicator[j] = false;
                                }
                            }

                            if (!bn) {
                                blindicator[groupPosition]=false;
                                System.out.println("xcxx insidebn"+isExpanded);
                                Runnable runnable = new Runnable() {
                                    @Override
                                    public void run() {

                                    }
                                };

                                imggroupindicator.animate().rotationBy(180).withEndAction(runnable).setDuration(150).setInterpolator(new LinearInterpolator()).start();
                                elv.collapseGroup(groupPosition);

                          }
                             else {

                                System.out.println("xcxx outsidebnlll"+isExpanded);
                                boolean blcheck=false;
                                for (int i = 0; i < isselect[groupPosition].length; i++) {
                                    if (isselect[groupPosition][i]) {
                                        blcheck=true;
                                        break;
                                    }

                                    }
                                if(!blcheck) {
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    };


                                    imggroupindicator.animate().rotationBy(180).withEndAction(runnable).setDuration(150).setInterpolator(new LinearInterpolator()).start();
                                    elv.collapseGroup(groupPosition);

                                }else{
                                    adapt.notifyDataSetChanged();
                                    Runnable runnable = new Runnable() {
                                        @Override
                                        public void run() {

                                        }
                                    };

                                    imggroupindicator.animate().rotationBy(180).withEndAction(runnable).setDuration(150).setInterpolator(new LinearInterpolator()).start();
                                    elv.expandGroup(groupPosition);
                                }
                            }
                        }else{
                            System.out.println("xcxx outsidemain"+isExpanded);
                            blindicator[groupPosition]=false;
                            adapt.notifyDataSetChanged();
                            Runnable runnable = new Runnable() {
                                @Override
                                public void run() {
//
                                }
                            };
                            imggroupindicator.animate().rotationBy(180).withEndAction(runnable).setDuration(150).setInterpolator(new LinearInterpolator()).start();
                            elv.expandGroup(groupPosition);


//                           // blindicator[groupPosition] = false;
//                            blindicator=new boolean[groups.length];
//                            adapt.notifyDataSetChanged();
//                            Runnable runnable = new Runnable() {
//                                @Override
//                                public void run() {
//
//                                }
//                            };
//
//                            imggroupindicator.animate().rotationBy(-180).withEndAction(runnable).setDuration(150).setInterpolator(new LinearInterpolator()).start();
//                            elv.collapseGroup(groupPosition);
                        }
                    }else{
                        System.out.println("xcxx ---"+isExpanded);
                        blinitialize=false;
                        blindicator[groupPosition]=false;
                        adapt.notifyDataSetChanged();
                        Runnable runnable = new Runnable() {
                            @Override
                            public void run() {
//
                            }
                        };
                        imggroupindicator.animate().rotationBy(180).withEndAction(runnable).setDuration(150).setInterpolator(new LinearInterpolator()).start();
                        elv.expandGroup(groupPosition);
                    }
                }
            });

//            elv.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
//                @Override
//                public void onGroupCollapse(int groupPosition) {
//
//                    elv.setSelectedGroup(groupPosition);
//                }
//
//
//
//            });
            elv.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
                @Override
                public void onGroupExpand(int groupPosition) {
                    elv.setSelectedGroup(groupPosition);
                }
            });
            checkparent.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (((CheckBox) v).isChecked()) {

                        isselectparent[groupPosition]=true;
                        counts[groupPosition] = childrencount[groupPosition].length - 1;
                        selecteditem[groupPosition] = "";
                        blsblistselect=true;

                        System.out.println("newz0"+selecteduseritem);
                        for (int i = 0; i < childrencount[groupPosition].length; i++) {


                            if (!selecteditem[groupPosition].contains("," + childrencount[groupPosition][i] + ",")) {
                                selecteditem[groupPosition] = selecteditem[groupPosition] + childrencount[groupPosition][i] + ",";

                            }


                            if (!selecteduseritem.contains(",*" + childrencount[groupPosition][i] + "*")) {

                                selecteduseritem = selecteduseritem + ",*" + childrencount[groupPosition][i] + "*";

                            }

                            System.out.println("newz1"+selecteduseritem);
                            String str="";
                            if(selecteduseritem.length()>1) {
                                str = filtervalue + "=" + selecteduseritem;
                            }
                            generateFilterparam(index,str);
                            if (isselect[groupPosition][i]) {
                                catcount = catcount - 1;

                            }

                            isselect[groupPosition][i] = true;
                            System.out.println("newz2"+selecteduseritem);


                            //catcount=catcount+1;
                        }


                        isselect[groupPosition][childrencount[groupPosition].length - 1] = true;

                        catcount = catcount + (childrencount[groupPosition].length);
                        selecteditem[groupPosition] = ",";
                        adapt.notifyDataSetChanged();
                    }


                    else{
                        isselectparent[groupPosition]=false;
                        counts[groupPosition] = 0;
                        selecteditem[groupPosition] = "";
                        blsblistselect=false;
                        int t;
                        System.out.println("newz00"+selecteduseritem);
                        for(t=0;t<groups.length;t++) {


                            if (groups[t].equals(groups[groupPosition])) {

                                for (int r = 0; r < childrencount[t].length; r++) {

                                    if (selecteduseritem.contains(",*" + childrencount[t][r] + "*")) {
                                        String a = ",*" + childrencount[t][r] + "*";

                                        selecteduseritem = selecteduseritem.replace(a, "");

                                    }
                                }
                            }
                            for (int i = 0; i < childrencount[groupPosition].length; i++) {

                                if (selecteduseritem.contains(",*" + childrencount[groupPosition][i] + "*")) {

                                    String a = ",*" + childrencount[groupPosition][i] + "*";
                                    //System.out.println("cccc" + a + "=" + ExternalFunctions.selectedusercat);

                                    selecteduseritem = selecteduseritem.replace(a, "");
                                }

                                if (!isselect[groupPosition][i]) {
                                    catcount = catcount + 1;

                                }
                                isselect[groupPosition][i] = false;
                                //catcount=catcount-1;
                            }
                        }
                        System.out.println("newz01"+selecteduseritem);
                        String str="";
                        if(selecteduseritem.length()>1) {
                            str = filtervalue + "=" + selecteduseritem;
                        }
                        generateFilterparam(index,str);
                        System.out.println("newz01"+selecteduseritem);
                        catcount = catcount - (childrencount[groupPosition].length - 1);
                        selecteditem[groupPosition] = ",";
                        adapt.notifyDataSetChanged();

                    }

                }
            });


            item.setTypeface(null, Typeface.BOLD);
            item.setText(subact);
            checkparent.setTypeface(null, Typeface.BOLD);
            checkparent.setText(subact);
            convertView.setTag(groupPosition);

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
            return true;
        }

    }

    public class CustomAdapter extends BaseAdapter {

        Context context;

        private LayoutInflater inflater = null;

        public CustomAdapter() {
            // TODO Auto-generated constructor stub

            context = sublistfragment.this.getActivity();

            inflater = (LayoutInflater) context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return arraySearch.size();
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
            holder.tv.setText(arraySearch.get(position));
           // holder.tv.setButtonDrawable(R.drawable.unselected_mdpi);
            boolean bl=false;

                String a=",*" + arraySearchid.get(position) + "*";
                if (selecteduseritem.contains(a)) {
                    System.out.println(selecteduseritem+" qqqqq1 "+a);
                    bl = true;

                }


            if(bl) {
                holder.tv.setButtonDrawable(R.drawable.selected_mdpi);
            } else {
                holder.tv.setButtonDrawable(R.drawable.unselected_mdpi);
            }
//            if (isselect1[position]) {
//                holder.tv.setChecked(true);
//            } else {
//                holder.tv.setChecked(false);
//            }


            holder.tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (holder.tv.isChecked()) {
                        if (!selecteduseritem.contains(",*" + arraySearchid.get(position) + "*")) {
                            selecteduseritem = selecteduseritem + ",*" + arraySearchid.get(position) + "*";
                        }

                        String str="";
                        if(selecteduseritem.length()>1) {
                            str = filtervalue + "=" + selecteduseritem;
                        }
                        generateFilterparam(index,str);
                        holder.tv.setButtonDrawable(R.drawable.selected_mdpi);
//                        fprice = fprice + 1;

                    } else {

                        selecteduseritem=selecteduseritem.replace(",*" + arraySearchid.get(position) + "*","");
                        String str="";
                        if(selecteduseritem.length()>1) {
                            str = filtervalue + "=" + selecteduseritem;
                        }
                        generateFilterparam(index,str);
                        holder.tv.setButtonDrawable(R.drawable.unselected_mdpi);
                    }


                }
            });

            return rowView;
        }

    }

}
