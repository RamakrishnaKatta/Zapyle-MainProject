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

public class eventList extends AppCompatActivity implements ApiCommunication {
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
        heading.setText( "EVENTS");

        GetData();
    }
    private void GetData() {
        ApiService.getInstance(getBaseContext(), 1).getData(eventList.this, true, SCREEN_NAME, EnvConstants.URL_HOME+"?page=events", "home");
    }

    @Override
    public void onResponseCallback(JSONObject response, String flag) {
        System.out.println("Generic_new"+response);
        if (flag.equals("home")) {
            JSONObject resp = network.JsonParser.getInstance().parserJsonObject(response);
            if (resp != null) {
                try {
                    String status = resp.getString("status");
                    LinearLayout ln = (LinearLayout) findViewById(R.id.generic_main);
                    manager = getSupportFragmentManager();

                    if (status.contains("success")) {
                        progressBar.setVisibility(View.GONE);


                        data = resp.getJSONArray("data");


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
