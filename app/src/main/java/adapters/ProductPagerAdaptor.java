package adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;

//import activity.HomePage;
import activity.discover;
import activity.product;
import utils.GetSharedValues;

/**
 * Created by haseeb on 8/9/16.
 */
public class ProductPagerAdaptor extends PagerAdapter{

    Context context;
    JSONArray data = new JSONArray();
    LayoutInflater inflater;


    public ProductPagerAdaptor(Context context, JSONArray data) {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((LinearLayout) object);
    }


    @Override
    public int getCount() {
        return data.length();
    }

    @Override
    public Object instantiateItem(ViewGroup view, final int position) {
        final View layout;
        layout = inflater.inflate(R.layout.home_product_collection_inside, null, false);

        try {
            System.out.println("INSIDEEE PRoduct pagr adaptor : "+data +"___"+String.valueOf(data.getJSONObject(position).getInt("listing_price")));
        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("INSIDEEE PRoduct pagr adaptor ck: "+e);
        }

//        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(discover.screenWidth - 150, discover.screenWidth);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int)(discover.screenWidth/2.5) - 30, (int)((discover.screenWidth/2.5)*1.333)+50);
        lp.setMargins(23, 23, 23, 23);
        layout.setLayoutParams(lp);
        layout.setBackgroundColor(Color.WHITE);
        final ImageView product_collection_image = (ImageView) layout.findViewById(R.id.product_collection_image);
        product_collection_image.setPadding(1, 1, 1, 1);
        product_collection_image.setBackgroundResource(R.drawable.home_img_border);
        TextView product_collection_name = (TextView) layout.findViewById(R.id.product_collection_name);
        TextView product_collection_lprice = (TextView) layout.findViewById(R.id.product_collection_lprice);
        TextView product_collection_oprice = (TextView) layout.findViewById(R.id.product_collection_oprice);
        try {
            product_collection_name.setText(data.getJSONObject(position).getString("title"));
            product_collection_name.setTextColor(Color.parseColor("#4A4A4A"));
            product_collection_lprice.setText(context.getResources().getString(R.string.Rs) + String.valueOf(data.getJSONObject(position).getInt("listing_price")));
            product_collection_lprice.setTextColor(Color.parseColor("#4A4A4A"));
            product_collection_oprice.setVisibility(View.VISIBLE);
            product_collection_oprice.setTextColor(Color.parseColor("#4A4A4A"));
            if (data.getJSONObject(position).getInt("original_price") > data.getJSONObject(position).getInt("listing_price")) {
                product_collection_oprice.setText(context.getResources().getString(R.string.Rs) + String.valueOf(data.getJSONObject(position).getInt("original_price")));
                product_collection_oprice.setPaintFlags(product_collection_oprice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            }
            else {
                product_collection_oprice.setVisibility(View.GONE);
            }

            Glide.with(context)
                    .load(data.getJSONObject(position).getString("image"))
                    .asBitmap()
                    .placeholder(R.drawable.playholderscreen)
                    .dontAnimate()
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {

                            int width = resource.getWidth(); //prints 0
                            int height = resource.getHeight(); //prints 0


                            System.out.println("WIDTH : "+width+"------"+height);
                            if (width == height && width > 0) {
                                resource.recycle();
                                System.out.println(GetSharedValues.getScreenWidth(context) + "inside sqaure" + String.valueOf(width));
                                try {
                                    Glide.with(context)
                                            .load(data.getJSONObject(position).getString("image"))
                                            .placeholder(R.drawable.playholderscreen)
                                            .crossFade()
                                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                            .override((int)(discover.screenWidth/2.5) - 30,(int)(discover.screenWidth/2.5) - 30)
                                            .into(product_collection_image);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                product_collection_image.setScaleType(ImageView.ScaleType.FIT_CENTER);

                            } else {
                                resource.recycle();
                                System.out.println("outside sqaure");
                                try {
                                    Glide.with(context)
                                            .load(data.getJSONObject(position).getString("image"))
                                            .fitCenter()
                                            .placeholder(R.drawable.playholderscreen)
                                            .crossFade()
                                            .override((int)(discover.screenWidth/2.5) - 30, (int)((discover.screenWidth/2.5)*1.333))
                                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                                            .into(product_collection_image);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }

                        }


                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }





//        try {
//            Glide.with(context)
//                    .load(data.getJSONObject(position).getString("image"))
//                    .fitCenter()
//                    .placeholder(R.drawable.playholderscreen)
//                    .dontAnimate()
////                    .override(discover.screenWidth - 150, discover.screenWidth - 150)
//                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//                    .into(product_collection_image);
//        } catch (JSONException e) {
//            e.printStackTrace();
//            System.out.println("INSIDEEE PRoduct pagr adaptor : "+e);
//        }
////
//


        final int finalJ = position;
        product_collection_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //System.out.println("ALBUMID click:" + discoverData.getJSONArray("product").getJSONObject(finalJ).getInt("id"));
                    Intent productPage = new Intent(context, product.class);
                    productPage.putExtra("activity", "discover");
                    productPage.putExtra("album_id", data.getJSONObject(finalJ).getInt("id"));
                    productPage.putExtra("pta", false);
                    context.startActivity(productPage);
                    ((Activity) context).finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        view.addView(layout, 0);
        return layout;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        System.out.println("IMAGES DATA check4: ");
        return (view == object);
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

}
