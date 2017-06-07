package adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.zapyle.zapyle.EnvConstants;
import com.zapyle.zapyle.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

//import activity.HomePage;
import activity.HomePageNew;
import activity.ProductPage;
import activity.ZoomActivity;
import utils.GetSharedValues;

/**
 * Created by haseeb on 8/9/16.
 */
public class ClosetPagerAdaptr extends PagerAdapter{

    Context context;
    JSONArray data = new JSONArray();
    LayoutInflater inflater;


    public ClosetPagerAdaptr(Context context, JSONArray data) {
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
        View layout = inflater.inflate(R.layout.home_closet_collection_inside, view, false);
        ImageView imageView = (ImageView) layout.findViewById(R.id.inside_closet_image);
        try {
            Glide.with(context)
                    .load(data.getJSONObject(position).getString("image"))
                    .fitCenter()
                    .placeholder(R.drawable.playholderscreen)
                    .crossFade()
                    .override(HomePageNew.screenWidth - 150, HomePageNew.screenWidth - 150)
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .into(imageView);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent productPage = new Intent(context, ProductPage.class);
                    productPage.putExtra("album_id", data.getJSONObject(position).getInt("id"));
                    productPage.putExtra("pta", false);
                    productPage.putExtra("activity", "HomePageNew");
                    context.startActivity(productPage);
//                                    finish();
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
