package activity;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fragments.bannerfragement;
import fragments.closetfragement;
import fragments.customfragement;
import fragments.generalfragement;
import fragments.genericfragement;
import fragments.messagefragement;
import fragments.productcollectionfragement;
import fragments.productfragement;
import fragments.userfragement;
import network.ApiCommunication;
import network.ApiService;
import utils.FontUtils;

public class GenericMain extends AppCompatActivity implements ApiCommunication {
    String SCREEN_NAME="FEED_PAGE";
    JSONArray data = null;
    JSONArray category = null;
    JSONArray brand1 = null;
    JSONArray brand2 = null;
    FragmentManager manager;
    String type="";
    ProgressBar   progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.generic_main);
        getSupportActionBar().setDisplayOptions(android.support.v7.app.ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        final View customView = getLayoutInflater().inflate(R.layout.product_bar, null);
        getSupportActionBar().setCustomView(customView);
        Toolbar parent = (Toolbar) customView.getParent();
        parent.setContentInsetsAbsolute(0, 0);
        progressBar = (ProgressBar) findViewById(R.id.buy_progress);
        FontUtils.setCustomFont(customView, getAssets());
        ImageView iv_back = (ImageView) findViewById(R.id.productfeedButton);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onBackPressed();
            }
        });
        TextView heading = (TextView) findViewById(R.id.product_title_text);
        heading.setText( getIntent().getStringExtra("heading"));

        GetData();
    }
    private void GetData() {
        ApiService.getInstance(GenericMain.this, 1).getData(GenericMain.this, true, SCREEN_NAME, EnvConstants.APP_BASE_URL + "/catalogue/browse/"+ getIntent().getStringExtra("type")+"/", "getData");
    }
    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        System.out.println("Generic_new"+response);
        if (flag.equals("getData")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                String status = resp.getString("status");
                    LinearLayout ln = (LinearLayout) findViewById(R.id.generic_main);
                    manager = getSupportFragmentManager();

                    if (status.contains("success")) {
                        progressBar.setVisibility(View.GONE);
                    try {
                        category = resp.getJSONObject("data").getJSONObject("categories").getJSONArray("items");
                        final FragmentTransaction transaction_cat = manager.beginTransaction();//.setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                        final FrameLayout frameLayout_cat = new FrameLayout(this);
                        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        frameLayout_cat.setLayoutParams(params);
                        frameLayout_cat.setId(100);
                        ln.addView(frameLayout_cat);
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("object",category.toString());
                        bundle1.putString("heading", "");
                        bundle1.putString("first","View all Categories");

                        if(getIntent().getStringExtra("heading").equals("Shop Brand New")){
                            bundle1.putString("second", "Browse Brand New");
                        }else{
                            bundle1.putString("second", "Browse Pre owned");
                        }
                        bundle1.putString("redirect","category_list");
                        bundle1.putBoolean("showrecycle",true);
                        bundle1.putString("list-link", resp.getJSONObject("data").getJSONObject("categories").getString("list_link"));
                        bundle1.putString("target", resp.getJSONObject("data").getJSONObject("categories").getString("target"));
                        final generalfragement gfrag1 = new generalfragement();
                        gfrag1.setArguments(bundle1);

                        new Thread(new Runnable() {
                            public void run() {
                                transaction_cat.add(frameLayout_cat.getId(), gfrag1, "Frag_Top_tag");
                                transaction_cat.commit();
                            }
                        }).start();

                    }catch(Exception e){

                    }
                    try {
                        brand1 = resp.getJSONObject("data").getJSONObject("brands1").getJSONArray("items");
                        final FragmentTransaction transaction_brand1 = manager.beginTransaction();//.setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                        final FrameLayout frameLayout_brand = new FrameLayout(this);
                        FrameLayout.LayoutParams paramsbrand = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        frameLayout_brand.setLayoutParams(paramsbrand);
                        frameLayout_brand.setId(101);
                        ln.addView(frameLayout_brand);
                        Bundle bundle2 = new Bundle();
                        bundle2.putString("object",brand1.toString());
                        bundle2.putString("heading",resp.getJSONObject("data").getJSONObject("brands1").getString("name"));
                        bundle2.putString("first","List of International Brands: A-Z");
                        bundle2.putString("second","Browse International");
                        bundle2.putBoolean("showrecycle",true);
                        bundle2.putString("redirect","MainBrandList");
                        bundle2.putString("type","INTERNATIONAL");
                        bundle2.putString("list-link", resp.getJSONObject("data").getJSONObject("brands1").getString("list_link"));
                        bundle2.putString("target", resp.getJSONObject("data").getJSONObject("brands1").getString("target"));
                        final generalfragement gfrag2 = new generalfragement();
                        gfrag2.setArguments(bundle2);

                        new Thread(new Runnable() {
                            public void run() {
                                transaction_brand1.add(frameLayout_brand.getId(), gfrag2, "Frag_Top_tag");
                                transaction_brand1.commit();
                            }
                        }).start();

                        brand2 = resp.getJSONObject("data").getJSONObject("brands2").getJSONArray("items");
                        final FragmentTransaction transaction_brand2 = manager.beginTransaction();//.setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                        final FrameLayout frameLayout_brand1 = new FrameLayout(this);
                        FrameLayout.LayoutParams paramsbrand1 = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                        frameLayout_brand1.setLayoutParams(paramsbrand1);
                        frameLayout_brand1.setId(105);
                        ln.addView(frameLayout_brand1);
                        Bundle bundle3 = new Bundle();
                        bundle3.putString("object",brand2.toString());
                        bundle3.putString("heading",resp.getJSONObject("data").getJSONObject("brands2").getString("name"));
                        bundle3.putString("first","List of Indian Brands: A-Z");
                        bundle3.putString("second","Browse Indian");
                        bundle3.putBoolean("showrecycle",true);
                        bundle3.putString("redirect","MainBrandList");
                        bundle3.putString("type","INDIAN");
                        bundle3.putString("list-link", resp.getJSONObject("data").getJSONObject("brands2").getString("list_link"));
                        bundle3.putString("target", resp.getJSONObject("data").getJSONObject("brands2").getString("target"));
                        final generalfragement gfrag3 = new generalfragement();
                        gfrag3.setArguments(bundle3);

                        new Thread(new Runnable() {
                            public void run() {
                                transaction_brand2.add(frameLayout_brand1.getId(), gfrag3, "Frag_Top_tag");
                                transaction_brand2.commit();
                            }
                        }).start();

                    }catch (Exception e){

                    }


                       //try{
                            data = resp.getJSONObject("data").getJSONObject("collections").getJSONArray("items");
                            final FragmentTransaction transaction_brand1 = manager.beginTransaction();//.setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                            final FrameLayout frameLayout_brand = new FrameLayout(this);
                            FrameLayout.LayoutParams paramsbrand = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            frameLayout_brand.setLayoutParams(paramsbrand);
                            frameLayout_brand.setId(103);
                            ln.addView(frameLayout_brand);
                            Bundle bundle2 = new Bundle();
                            bundle2.putString("object","");
                            bundle2.putString("heading",resp.getJSONObject("data").getJSONObject("collections").getString("name"));
                            bundle2.putString("first","");
                            bundle2.putString("second","");
                            bundle2.putBoolean("showrecycle",false);
                            bundle2.putString("list-link", "");
                            bundle2.putString("target", "");
                            final generalfragement gfrag2 = new generalfragement();
                            gfrag2.setArguments(bundle2);

                            new Thread(new Runnable() {
                                public void run() {
                                    transaction_brand1.add(frameLayout_brand.getId(), gfrag2, "Frag_Top_tag");
                                    transaction_brand1.commit();
                                }
                            }).start();


                            String discoverType = null;
                            for (int i = 0; i < data.length(); i++) {
                                System.out.println("xxxx" + data.getJSONObject(i));
                                //dataList.add(data.getJSONObject(i));
                                try {
                                    discoverType = data.getJSONObject(i).getJSONObject("content_data").getString("discover_type");
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                System.out.println("check x" + discoverType);
                                if (discoverType.contains("banner")) {
                                    System.out.println("check x" + discoverType);
                                    final FragmentTransaction transaction1 = manager.beginTransaction();//.setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                                    final FrameLayout frameLayout = new FrameLayout(this);
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    frameLayout.setLayoutParams(params);
                                    frameLayout.setId(i + 1);
                                    ln.addView(frameLayout);
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("object", data.getJSONObject(i).toString());
                                    final bannerfragement bfrag1 = new bannerfragement();
                                    bfrag1.setArguments(bundle1);



                                    new Thread(new Runnable() {
                                        public void run() {
                                            try {
                                                transaction1.add(frameLayout.getId(), bfrag1, "Frag_Top_tag");
                                                transaction1.commit();

                                            } catch (IllegalStateException e) {

                                            }

                                        }
                                    }).start();

                                } else if (discoverType.contains("message")) {
                                    final FragmentTransaction transaction2 = manager.beginTransaction();//.setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                                    final FrameLayout frameLayout = new FrameLayout(this);
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    frameLayout.setLayoutParams(params);
                                    frameLayout.setId(i + 1);
                                    ln.addView(frameLayout);
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("object", data.getJSONObject(i).toString());
                                    final messagefragement frag = new messagefragement();
                                    frag.setArguments(bundle1);


                                    new Thread(new Runnable() {
                                        public void run() {

                                            try {
                                                transaction2.add(frameLayout.getId(), frag, "Frag_Top_tag");
                                                transaction2.commit();

                                            } catch (IllegalStateException e) {

                                            }
                                        }
                                    }).start();


                                } else if (discoverType.contains("closet")) {
                                    final FragmentTransaction transaction3 = manager.beginTransaction();//.setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                                    final FrameLayout frameLayout = new FrameLayout(this);
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    frameLayout.setLayoutParams(params);
                                    frameLayout.setId(i + 1);
                                    ln.addView(frameLayout);
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("object", data.getJSONObject(i).toString());
                                    final closetfragement frag = new closetfragement();
                                    frag.setArguments(bundle1);


                                    new Thread(new Runnable() {
                                        public void run() {

                                            try {
                                                transaction3.add(frameLayout.getId(), frag, "Frag_Top_tag");
                                                transaction3.commit();

                                            } catch (IllegalStateException e) {

                                            }
                                        }
                                    }).start();


                                } else if (discoverType.contains("product_collection")) {
                                    final FragmentTransaction transactionp = manager.beginTransaction().setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                                    final FrameLayout frameLayout = new FrameLayout(this);
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    frameLayout.setLayoutParams(params);
                                    frameLayout.setId(i + 1);
                                    ln.addView(frameLayout);
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("object", data.getJSONObject(i).toString());
                                    final productcollectionfragement frag = new productcollectionfragement();
                                    frag.setArguments(bundle1);


                                    new Thread(new Runnable() {
                                        public void run() {

                                            try {
                                                transactionp.add(frameLayout.getId(), frag, "Frag_Top_tag");
                                                transactionp.commit();

                                            } catch (IllegalStateException e) {

                                            }


                                        }
                                    }).start();



                                }

                                else if (discoverType.contains("product_horizontal")) {
                                    final FragmentTransaction transaction4 = manager.beginTransaction().setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                                    final FrameLayout frameLayout = new FrameLayout(this);
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    frameLayout.setLayoutParams(params);
                                    frameLayout.setId(i + 1);
                                    ln.addView(frameLayout);
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("object", data.getJSONObject(i).toString());
                                    final productfragement frag = new productfragement();
                                    frag.setArguments(bundle1);


                                    new Thread(new Runnable() {
                                        public void run() {

                                            try {
                                                transaction4.add(frameLayout.getId(), frag, "Frag_Top_tag");
                                                transaction4.commit();

                                            } catch (IllegalStateException e) {

                                            }
                                        }
                                    }).start();



                                } else if (discoverType.contains("user_collection")) {
                                    final FragmentTransaction transaction5 = manager.beginTransaction().setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                                    final FrameLayout frameLayout = new FrameLayout(this);
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    frameLayout.setLayoutParams(params);
                                    frameLayout.setId(i + 1);
                                    ln.addView(frameLayout);
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("object", data.getJSONObject(i).toString());
                                    final userfragement frag = new userfragement();
                                    frag.setArguments(bundle1);

                                    new Thread(new Runnable() {
                                        public void run() {

                                            try {
                                                transaction5.add(frameLayout.getId(), frag, "Frag_Top_tag");
                                                transaction5.commit();

                                            } catch (IllegalStateException e) {

                                            }

                                        }
                                    }).start();

                                } else if (discoverType.contains("custom_collection")) {
                                    final FragmentTransaction transaction6 = manager.beginTransaction().setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                                    final FrameLayout frameLayout = new FrameLayout(this);
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    frameLayout.setLayoutParams(params);
                                    frameLayout.setId(i + 1);
                                    ln.addView(frameLayout);
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("object", data.getJSONObject(i).toString());
                                    final customfragement frag = new customfragement();
                                    frag.setArguments(bundle1);

                                    new Thread(new Runnable() {
                                        public void run() {

                                            try {

                                                transaction6.add(frameLayout.getId(), frag, "Frag_Top_tag");
                                                transaction6.commit();
                                            } catch (IllegalStateException e) {

                                            }

                                        }
                                    }).start();

                                } else if (discoverType.contains("generic")) {
                                    final FragmentTransaction transaction7 = manager.beginTransaction().setCustomAnimations(R.anim.hotline_slide_up,R.anim.slide_out_down);//c
                                    final FrameLayout frameLayout = new FrameLayout(this);
                                    FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                    frameLayout.setLayoutParams(params);
                                    frameLayout.setId(i + 1);
                                    ln.addView(frameLayout);
                                    Bundle bundle1 = new Bundle();
                                    bundle1.putString("object", data.getJSONObject(i).toString());
                                    final genericfragement frag = new genericfragement();
                                    frag.setArguments(bundle1);
                                    new Thread(new Runnable() {
                                        public void run() {

                                            try {
                                                transaction7.add(frameLayout.getId(), frag, "Frag_Top_tag");
                                                transaction7.commit();
                                            } catch (IllegalStateException e) {

                                            }

                                        }
                                    }).start();


                                }


                            }
//                        }catch (Exception e){
//
//                        }






                }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

        }

    }

    @Override
    public void onErrorCallback(VolleyError error, String flag) {

    }
}
