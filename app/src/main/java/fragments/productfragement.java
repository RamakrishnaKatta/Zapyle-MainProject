package fragments;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.R;

import org.json.JSONException;
import org.json.JSONObject;

//import activity.HomePage;
import activity.discover;
import activity.product;
import utils.FontUtils;

/**
 * Created by zapyle on 22/10/16.
 */

public class productfragement extends Fragment {
    TextView title;
    HorizontalScrollView scrollView;
    JSONObject contentData = null;
    JSONObject discoverData = null;
  
    JSONObject objectData;
    int pos;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle data =  getArguments();
        try {
            objectData= new JSONObject(data.getString("object"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View view=inflater.inflate(R.layout.home_product_collection, container, false);
        title = (TextView) view.findViewById(R.id.product_collection_title);
        scrollView = (HorizontalScrollView) view.findViewById(R.id.product_collection_horizontal_scrollview);
        title.setTextColor(Color.parseColor("#4A4A4A"));
        try {
            contentData = objectData.getJSONObject("content_data");
            discoverData = contentData.getJSONObject("discover_data");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
           title.setText(discoverData.getString("title"));
           title.setTextColor(Color.parseColor("#4A4A4A"));
           title.setBackgroundColor(Color.WHITE);
            
            LinearLayout product_linearLayout = new LinearLayout(getActivity());
            product_linearLayout.setOrientation(LinearLayout.HORIZONTAL);
            for (int j = 0; j < discoverData.getJSONArray("product").length(); j++) {
                LayoutInflater inflater_mid = LayoutInflater.from(getActivity());
                final View view_mid;
                view_mid = inflater_mid.inflate(R.layout.home_product_collection_inside, null, false);
//                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(discover.screenWidth - 150, discover.screenWidth);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                lp.setMargins(23, 2, 23, 2);
                view_mid.setLayoutParams(lp);
                view_mid.setBackgroundColor(Color.WHITE);
                ImageView product_collection_image = (ImageView) view_mid.findViewById(R.id.product_collection_image);
                product_collection_image.setPadding(1, 1, 1, 1);
                product_collection_image.setBackgroundResource(R.drawable.home_img_border);
                TextView product_collection_name = (TextView) view_mid.findViewById(R.id.product_collection_name);
                TextView product_collection_lprice = (TextView) view_mid.findViewById(R.id.product_collection_lprice);
                TextView product_collection_oprice = (TextView) view_mid.findViewById(R.id.product_collection_oprice);
                product_collection_name.setText(discoverData.getJSONArray("product").getJSONObject(j).getString("title"));
                product_collection_name.setTextColor(Color.parseColor("#4A4A4A"));
                product_collection_lprice.setText(getActivity().getResources().getString(R.string.Rs) + String.valueOf(discoverData.getJSONArray("product").getJSONObject(j).getInt("listing_price")));
                product_collection_lprice.setTextColor(Color.parseColor("#4A4A4A"));
                product_collection_oprice.setTextColor(Color.parseColor("#4A4A4A"));
                if(discoverData.getJSONArray("product").getJSONObject(j).getInt("listing_price")==discoverData.getJSONArray("product").getJSONObject(j).getInt("original_price"))
                {
                    product_collection_oprice.setText("");

                }else{
                    product_collection_oprice.setText(getActivity().getResources().getString(R.string.Rs) + String.valueOf(discoverData.getJSONArray("product").getJSONObject(j).getInt("original_price")));
                    product_collection_oprice.setPaintFlags(product_collection_oprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                }
                product_collection_image.getLayoutParams().height = (int)(((discover.screenWidth/2.5) - 30)*1.333);
                product_collection_image.getLayoutParams().width = (int)(discover.screenWidth/2.5) - 30;
                product_collection_image.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                try {
                    Glide.with(getActivity())
                            .load(discoverData.getJSONArray("product").getJSONObject(j).getString("image"))
                            .placeholder(R.drawable.playholderscreen)
                            .dontAnimate()
                            .override((int)(discover.screenWidth/2.5) - 30, (int)(((discover.screenWidth/2.5) - 30)*1.333))
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(product_collection_image);
                } catch (JSONException e) {
                    e.printStackTrace();
                    System.out.println("INSIDEEE PRoduct pagr adaptor : "+e);
                }
                final int finalJ = j;
                product_collection_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Intent productPage = new Intent(getActivity(), product.class);
                                    productPage.putExtra("activity", "discover");
                                    try {
                                        productPage.putExtra("album_id", discoverData.getJSONArray("product").getJSONObject(finalJ).getInt("id"));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    productPage.putExtra("pta", false);
                                    productPage.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    getActivity().startActivity(productPage);
                                   // getActivity().finish();
                                }catch(Exception e){

                                }
                            }
                        }, 1000);
                        //System.out.println("ALBUMID click:" + discoverData.getJSONArray("product").getJSONObject(finalJ).getInt("id"));

//                                    finish();
                    }
                });
                product_linearLayout.addView(view_mid);
            }

            scrollView.addView(product_linearLayout);
            FontUtils.setCustomFont(scrollView, getActivity().getAssets());
            //product_linearLayout.setDrawingCacheEnabled(false);
           // viewCallback.getViewHandle(product_linearLayout);
        }
        catch (Exception e){

        }

        return view;
    }
}
